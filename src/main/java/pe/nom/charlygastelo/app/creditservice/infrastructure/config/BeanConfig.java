package pe.nom.charlygastelo.app.creditservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pe.nom.charlygastelo.app.creditservice.application.usecase.*;
import pe.nom.charlygastelo.app.creditservice.domain.port.*;

@Configuration
public class BeanConfig {

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
    public GetCreditUseCase getCreditUseCase(
            CreditRepositoryPort repository) {

        return new GetCreditUseCase(repository);
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
            CreditEventProducerPort producer) {

        return new ChargeCreditCardUseCase(
                repository,
                producer
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