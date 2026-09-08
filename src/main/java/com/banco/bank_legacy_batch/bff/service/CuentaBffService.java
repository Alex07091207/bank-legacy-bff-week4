package com.banco.bank_legacy_batch.bff.service;

import org.springframework.stereotype.Service;

import com.banco.bank_legacy_batch.bff.repository.CuentaBffRepository;
import com.banco.bank_legacy_batch.model.Interes;

@Service
public class CuentaBffService {

    private final CuentaBffRepository cuentaBffRepository;

    public CuentaBffService(CuentaBffRepository cuentaBffRepository) {
        this.cuentaBffRepository = cuentaBffRepository;
    }

    public Interes buscarCuenta(Long cuentaId) {

        return cuentaBffRepository.buscarCuenta(cuentaId)
                .orElseThrow(() ->
                        new RuntimeException("Cuenta no encontrada: " + cuentaId)
                );
    }
}