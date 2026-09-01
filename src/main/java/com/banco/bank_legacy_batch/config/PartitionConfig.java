package com.banco.bank_legacy_batch.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PartitionConfig {

    @Bean
    public Partitioner transaccionesPartitioner() {

        return gridSize -> {

            Map<String, ExecutionContext> partitions =
                    new HashMap<>();

            final int totalRegistros = 9;
            final int cantidadParticiones = 3;

            int registrosPorParticion =
                    (int) Math.ceil(
                            (double) totalRegistros
                                    / cantidadParticiones
                    );

            for (int i = 0;
                 i < cantidadParticiones;
                 i++) {

                int inicio =
                        i * registrosPorParticion;

                int fin =
                        Math.min(
                                inicio + registrosPorParticion,
                                totalRegistros
                        );

                if (inicio >= totalRegistros) {
                    break;
                }

                ExecutionContext context =
                        new ExecutionContext();

                context.putInt("inicio", inicio);
                context.putInt("fin", fin);

                partitions.put(
                        "partition-" + i,
                        context
                );
            }

            return partitions;
        };
    }
}