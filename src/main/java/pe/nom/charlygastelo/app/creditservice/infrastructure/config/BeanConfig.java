package pe.nom.charlygastelo.app.creditservice.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import pe.nom.charlygastelo.app.creditservice.application.usecase.*;
import pe.nom.charlygastelo.app.creditservice.domain.port.*;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.cache.RedisCreditCacheAdapter;

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
    public CreateCreditUseCase createCreditUseCase(
            CreditRepositoryPort repository,
            CreditEventProducerPort producer,
            CustomerEventPort customerEventPort) {

        return new CreateCreditUseCase(
                repository,
                producer,
                customerEventPort
        );
    }

    @Bean
    public CreditCachePort creditCachePort(
            ReactiveRedisTemplate<String, String> redis,
            ObjectMapper mapper) {

        return new RedisCreditCacheAdapter(redis, mapper);
    }

    @Bean
    public GetCreditUseCase getCreditUseCase(
            CreditRepositoryPort repository, CreditCachePort cache) {

        return new GetCreditUseCase(repository, cache);
    }

    @Bean
    public ListCreditsUseCase listCreditsUseCase(
            CreditRepositoryPort repository) {

        return new ListCreditsUseCase(repository);
    }

    @Bean
    public UpdateCreditUseCase updateCreditUseCase(
            CreditRepositoryPort repository,
            CreditEventProducerPort producer) {

        return new UpdateCreditUseCase(
                repository,
                producer
        );
    }

    @Bean
    public DeleteCreditUseCase deleteCreditUseCase(
            CreditRepositoryPort repository,
            CreditEventProducerPort producer) {

        return new DeleteCreditUseCase(
                repository,
                producer
        );
    }

    @Bean
    public PayCreditUseCase payCreditUseCase(
            CreditRepositoryPort repository,
            CreditEventProducerPort producer) {

        return new PayCreditUseCase(
                repository,
                producer
        );
    }

    @Bean
    public ChargeCreditCardUseCase chargeCreditCardUseCase(
            CreditRepositoryPort repository,
            CreditEventProducerPort producer,
            CreditCachePort cache) {

        return new ChargeCreditCardUseCase(
                repository,
                producer,
                cache
        );
    }

    @Bean
    public CheckOverdueDebtUseCase checkOverdueDebtUseCase(
            CreditRepositoryPort repository) {

        return new CheckOverdueDebtUseCase(repository);
    }

    @Bean
    public ProcessCreditTransactionUseCase processCreditTransactionUseCase(
            PayCreditUseCase payCreditUseCase,
            ChargeCreditCardUseCase chargeCreditCardUseCase,
            AccountDebitEventPort accountDebitEventPort,
            TransactionEventPort transactionEventPort) {

        return new ProcessCreditTransactionUseCase(
                payCreditUseCase,
                chargeCreditCardUseCase,
                accountDebitEventPort,
                transactionEventPort
        );
    }
}