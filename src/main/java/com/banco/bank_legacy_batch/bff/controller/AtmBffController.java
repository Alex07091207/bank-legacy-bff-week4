package com.banco.bank_legacy_batch.bff.controller;

import org.springframework.web.bind.annotation.*;
import com.banco.bank_legacy_batch.bff.dto.AtmSaldoResponse;
import com.banco.bank_legacy_batch.bff.service.AtmBffService;
import com.banco.bank_legacy_batch.bff.service.BffSecurityService;

@RestController
@RequestMapping("/bff/atm")
public class AtmBffController {

    private final AtmBffService atmBffService;
    private final BffSecurityService securityService;

    public AtmBffController(
            AtmBffService atmBffService,
            BffSecurityService securityService) {

        this.atmBffService = atmBffService;
        this.securityService = securityService;
    }

    @GetMapping("/cuenta/{cuentaId}/saldo")
    public AtmSaldoResponse consultarSaldo(
            @PathVariable Long cuentaId,
            @RequestHeader("X-Canal") String canal,
            @RequestHeader("X-Operacion") String operacion) {

        securityService.validarCanal("ATM", canal);
        securityService.validarOperacion(
                "CONSULTAR_SALDO",
                operacion
        );

        return atmBffService.consultarSaldo(cuentaId);
    }
}