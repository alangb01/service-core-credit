package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import pe.nom.charlygastelo.app.creditservice.domain.exception.CreditCacheException;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditCachePort;

@RequiredArgsConstructor
public class RedisCreditCacheAdapter implements CreditCachePort {

    private final ReactiveRedisTemplate<String, String> redis;
    private final ObjectMapper mapper;

    private static final String KEY_ID = "credit:id:";

    @Override
    public Maybe<Credit> getById(String id) {
        return Maybe.fromPublisher(
                        redis.opsForValue().get(KEY_ID + id)
                )
                .flatMap(this::parse)
                .onErrorResumeNext(e ->
                        Maybe.error(new CreditCacheException("Redis error", e))
                );
    }


    @Override
    public Completable save(Credit account) {
        try {
            String json = mapper.writeValueAsString(account);

            return Completable.fromPublisher(
                    redis.opsForValue().set(KEY_ID + account.id(), json)
            ).andThen(
                    Completable.fromPublisher(
                            redis.opsForValue().set(
                                    KEY_ID + account.type() + ":" + account.number(),
                                    json
                            )
                    )
            );

        } catch (Exception e) {
            return Completable.error(new CreditCacheException("Error serializing account", e));
        }
    }

    @Override
    public Completable delete(String id) {
        return Completable.fromPublisher(
                redis.opsForValue().delete(KEY_ID + id)
        );
    }

    private Maybe<Credit> parse(String json) {
        try {
            return json == null
                    ? Maybe.empty()
                    : Maybe.just(mapper.readValue(json, Credit.class));
        } catch (Exception e) {
            return Maybe.error(new CreditCacheException("Error parsing cache", e));
        }
    }
}
