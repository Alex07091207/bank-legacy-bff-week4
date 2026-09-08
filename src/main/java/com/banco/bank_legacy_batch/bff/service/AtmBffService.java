package com.banco.bank_legacy_batch.bff.service;

import org.springframework.stereotype.Service;

import com.banco.bank_legacy_batch.bff.dto.AtmSaldoResponse;
import com.banco.bank_legacy_batch.bff.repository.CuentaBffRepository;
import com.banco.bank_legacy_batch.model.Interes;

@Service
public class AtmBffService {

    private final CuentaBffRepository cuentaBffRepository;

    public AtmBffService(CuentaBffRepository cuentaBffRepository) {
        this.cuentaBffRepository = cuentaBffRepository;
    }

    public AtmSaldoResponse consultarSaldo(Long cuentaId) {

        Interes cuenta = cuentaBffRepository.buscarCuenta(cuentaId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cuenta no encontrada: " + cuentaId
                        )
                );

        return new AtmSaldoResponse(
                cuenta.getSaldo()
        );
    }
}