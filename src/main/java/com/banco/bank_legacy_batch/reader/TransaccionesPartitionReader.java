package com.banco.bank_legacy_batch.reader;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.FlatFileItemReader;

import com.banco.bank_legacy_batch.model.Transaccion;

public class TransaccionesPartitionReader
        implements ItemReader<Transaccion>, ItemStream {

    private final FlatFileItemReader<Transaccion> delegate;
    private final int inicio;
    private final int fin;

    private int posicion;

    public TransaccionesPartitionReader(
            FlatFileItemReader<Transaccion> delegate,
            int inicio,
            int fin) {

        this.delegate = delegate;
        this.inicio = inicio;
        this.fin = fin;
        this.posicion = inicio;
    }

    @Override
    public void open(ExecutionContext executionContext)
            throws ItemStreamException {

        delegate.open(executionContext);

        try {
            // Avanzamos hasta la posición inicial de esta partición.
            for (int i = 0; i < inicio; i++) {
                if (delegate.read() == null) {
                    break;
                }
            }

            posicion = inicio;

        } catch (Exception e) {
            throw new ItemStreamException(
                    "Error al posicionar la partición "
                            + inicio + "-" + fin,
                    e
            );
        }
    }

    @Override
    public Transaccion read() throws Exception {

        if (posicion >= fin) {
            return null;
        }

        Transaccion item = delegate.read();

        if (item != null) {
            posicion++;
        }

        return item;
    }

    @Override
    public void update(ExecutionContext executionContext)
            throws ItemStreamException {

        executionContext.putInt(
                "transacciones.posicion",
                posicion
        );
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }
}