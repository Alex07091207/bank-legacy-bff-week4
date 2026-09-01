package com.banco.bank_legacy_batch.config;

import java.time.LocalDate;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.banco.bank_legacy_batch.model.Transaccion;
import com.banco.bank_legacy_batch.processor.TransaccionProcessor;
import com.banco.bank_legacy_batch.reader.TransaccionesPartitionReader;

@Configuration
public class TransaccionesJobConfig {

    @Bean
    @StepScope
    public TransaccionesPartitionReader transaccionesReader(

            @Value("#{stepExecutionContext['inicio']}")
            Integer inicio,

            @Value("#{stepExecutionContext['fin']}")
            Integer fin) {

        FlatFileItemReader<Transaccion> reader =
                new FlatFileItemReaderBuilder<Transaccion>()

                        .name("transaccionesReader-" + inicio)

                        .resource(
                                new ClassPathResource(
                                        "data/transacciones.csv"
                                )
                        )

                        .linesToSkip(1)

                        .delimited()
                        .delimiter(",")

                        .names(
                                "cuenta_id",
                                "fecha",
                                "tipo",
                                "monto",
                                "descripcion"
                        )

                        .fieldSetMapper(fieldSet -> {

                            Transaccion item =
                                    new Transaccion();

                            item.setCuentaId(
                                    fieldSet.readLong("cuenta_id")
                            );

                            item.setFecha(
                                    LocalDate.parse(
                                            fieldSet.readString("fecha")
                                    )
                            );

                            item.setTipo(
                                    fieldSet.readString("tipo")
                            );

                            item.setMonto(
                                    fieldSet.readBigDecimal("monto")
                            );

                            item.setDescripcion(
                                    fieldSet.readString(
                                            "descripcion"
                                    )
                            );

                            return item;
                        })

                        // El estado de posición lo controla
                        // nuestro reader de partición.
                        .saveState(false)

                        .build();

        return new TransaccionesPartitionReader(
                reader,
                inicio,
                fin
        );
    }

    @Bean
    public TransaccionProcessor transaccionesProcessor() {
        return new TransaccionProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Transaccion> transaccionesWriter(
            DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<Transaccion>()

                .dataSource(dataSource)

                .sql("""
                    INSERT INTO transacciones_procesadas
                    (
                        cuenta_id,
                        fecha,
                        monto,
                        tipo,
                        descripcion,
                        estado
                    )
                    VALUES
                    (
                        :cuentaId,
                        :fecha,
                        :monto,
                        :tipo,
                        :descripcion,
                        :estado
                    )
                    """)

                .beanMapped()

                .build();
    }

    @Bean
    public Step transaccionesWorkerStep(

            JobRepository jobRepository,

            PlatformTransactionManager transactionManager,

            TransaccionesPartitionReader transaccionesReader,

            TransaccionProcessor transaccionesProcessor,

            JdbcBatchItemWriter<Transaccion> transaccionesWriter) {

        return new StepBuilder(
                "transaccionesWorkerStep",
                jobRepository
        )

                .<Transaccion, Transaccion>chunk(
                        3,
                        transactionManager
                )

                .reader(transaccionesReader)
                .processor(transaccionesProcessor)
                .writer(transaccionesWriter)

                .faultTolerant()

                .skipLimit(10)
                .skip(Exception.class)

                .retryLimit(3)
                .retry(Exception.class)

                .build();
    }

    @Bean
    public Step transaccionesMasterStep(

            JobRepository jobRepository,

            Partitioner transaccionesPartitioner,

            Step transaccionesWorkerStep,

            TaskExecutor batchTaskExecutor) {

        return new StepBuilder(
                "transaccionesMasterStep",
                jobRepository
        )

                .partitioner(
                        "transaccionesWorkerStep",
                        transaccionesPartitioner
                )

                .step(transaccionesWorkerStep)

                .gridSize(3)

                .taskExecutor(batchTaskExecutor)

                .build();
    }

    @Bean
    public Job transaccionesJob(

            JobRepository jobRepository,

            Step transaccionesMasterStep,

            BatchJobListener batchJobListener) {

        return new JobBuilder(
                "transaccionesJob",
                jobRepository
        )

                .start(transaccionesMasterStep)

                .listener(batchJobListener)

                .build();
    }
}