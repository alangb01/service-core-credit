package pe.nom.charlygastelo.app.creditservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditCachePort;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.cache.RedisCreditCacheAdapter;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence.CreditPersistenceMapper;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence.CreditReactiveRepository;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence.CreditRepositoryAdapter;

@Configuration
public class BeanConfig {
    @Bean
    @Primary
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory) {

        RedisSerializationContext<String, String> context =
                RedisSerializationContext
                        .<String, String>newSerializationContext(
                                new StringRedisSerializer()
                        )
                        .value(new StringRedisSerializer())
                        .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }




    @Bean
    public CreditCachePort creditCachePort(
            ReactiveRedisTemplate<String, String> redis,
            ObjectMapper mapper) {

        return new RedisCreditCacheAdapter(redis, mapper);
    }

    @Bean
    public CreditRepositoryPort creditRepositoryPort(
            CreditReactiveRepository repository,
            CreditPersistenceMapper mapper
    ) {
        return new CreditRepositoryAdapter(repository, mapper);
    }
}