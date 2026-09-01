package com.banco.bank_legacy_batch.config;

import java.time.LocalDate;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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

import com.banco.bank_legacy_batch.model.CuentaAnual;
import com.banco.bank_legacy_batch.processor.CuentaAnualProcessor;

@Configuration
public class CuentasAnualesJobConfig {

    @Bean
    public FlatFileItemReader<CuentaAnual>
    cuentaAnualReader() {

        return new FlatFileItemReaderBuilder<CuentaAnual>()

                .name("cuentaAnualReader")

                .resource(
                        new ClassPathResource(
                                "data/cuentas_anuales.csv"
                        )
                )

                .linesToSkip(1)

                .delimited()

                .delimiter(",")

                .names(
                        "id",
                        "fecha",
                        "monto",
                        "tipo"
                )

                .fieldSetMapper(fieldSet -> {

                    CuentaAnual item =
                            new CuentaAnual();

                    item.setId(
                            fieldSet.readLong(
                                    "id"
                            )
                    );

                    item.setFecha(
                            LocalDate.parse(
                                    fieldSet.readString(
                                            "fecha"
                                    )
                            )
                    );

                    item.setMonto(
                            fieldSet.readBigDecimal(
                                    "monto"
                            )
                    );

                    item.setTipo(
                            fieldSet.readString(
                                    "tipo"
                            )
                    );

                    return item;
                })

                .build();
    }

    @Bean
    public CuentaAnualProcessor
    cuentaAnualProcessor() {

        return new CuentaAnualProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<CuentaAnual>
    cuentaAnualWriter(
            DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<CuentaAnual>()

                .dataSource(dataSource)

                .sql("""
                    INSERT INTO cuentas_anuales_procesadas
                    (
                        id,
                        fecha,
                        monto,
                        tipo,
                        estado
                    )
                    VALUES
                    (
                        :id,
                        :fecha,
                        :monto,
                        :tipo,
                        :estado
                    )
                    """)

                .beanMapped()

                .build();
    }

    @Bean
    public Step cuentasAnualesStep(

            JobRepository jobRepository,

            PlatformTransactionManager transactionManager,

            FlatFileItemReader<CuentaAnual>
                    cuentaAnualReader,

            CuentaAnualProcessor
                    cuentaAnualProcessor,

            JdbcBatchItemWriter<CuentaAnual>
                    cuentaAnualWriter) {

        return new StepBuilder(
                "cuentasAnualesStep",
                jobRepository
        )

                .<CuentaAnual, CuentaAnual>chunk(
                        5,
                        transactionManager
                )

                .reader(cuentaAnualReader)

                .processor(cuentaAnualProcessor)

                .writer(cuentaAnualWriter)

                .faultTolerant()

                .skip(Exception.class)

                .skipLimit(10)

                .retry(Exception.class)

                .retryLimit(3)

                .build();
    }

    @Bean
    public Job cuentasAnualesJob(

            JobRepository jobRepository,

            Step cuentasAnualesStep,

            BatchJobListener batchJobListener) {

        return new JobBuilder(
                "cuentasAnualesJob",
                jobRepository
        )

                .incrementer(
                        new RunIdIncrementer()
                )

                .start(cuentasAnualesStep)

                .listener(batchJobListener)

                .build();
    }
}