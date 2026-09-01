package com.banco.bank_legacy_batch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.banco.bank_legacy_batch.model.Interes;
import com.banco.bank_legacy_batch.processor.InteresProcessor;

@Configuration
public class InteresesJobConfig {

    // =========================================================
    // READER
    // =========================================================

    @Bean
    public FlatFileItemReader<Interes> interesReader() {

        return new FlatFileItemReaderBuilder<Interes>()
                .name("interesReader")
                .resource(
                        new ClassPathResource(
                                "data/intereses.csv"
                        )
                )
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names(
                        "cuentaId",
                        "nombre",
                        "saldo",
                        "edad",
                        "tipo"
                )
                .targetType(Interes.class)
                .build();
    }

    // =========================================================
    // PROCESSOR
    // =========================================================

    @Bean
    public InteresProcessor interesProcessor() {
        return new InteresProcessor();
    }

    // =========================================================
    // WRITER
    // =========================================================

    @Bean
    public JdbcBatchItemWriter<Interes> interesWriter(
            DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<Interes>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO intereses_procesados
                        (
                            cuenta_id,
                            nombre,
                            saldo,
                            edad,
                            tipo,
                            interes,
                            saldo_final,
                            estado
                        )
                        VALUES
                        (
                            :cuentaId,
                            :nombre,
                            :saldo,
                            :edad,
                            :tipo,
                            :interes,
                            :saldoFinal,
                            :estado
                        )
                        """)
                .beanMapped()
                .build();
    }

    // =========================================================
    // STEP
    // =========================================================

    @Bean
    public Step interesesStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Interes> interesReader,
            InteresProcessor interesProcessor,
            JdbcBatchItemWriter<Interes> interesWriter) {

        return new StepBuilder(
                "interesesStep",
                jobRepository
        )
                .<Interes, Interes>chunk(
                        5,
                        transactionManager
                )
                .reader(interesReader)
                .processor(interesProcessor)
                .writer(interesWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .retry(Exception.class)
                .retryLimit(3)
                .build();
    }

    // =========================================================
    // JOB
    // =========================================================

    @Bean
    public Job interesesJob(
            JobRepository jobRepository,
            Step interesesStep,
            BatchJobListener batchJobListener) {

        return new JobBuilder(
                "interesesJob",
                jobRepository
        )
                .start(interesesStep)
                .listener(batchJobListener)
                .build();
    }
}