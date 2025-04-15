package com.banco.serviciocuentas.controller;

import com.banco.serviciocuentas.model.Cliente;
import com.banco.serviciocuentas.model.Cuenta;
import com.banco.serviciocuentas.repository.ClienteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CuentaController {

    private final ClienteRepository clienteRepo;

    public CuentaController(ClienteRepository clienteRepo) {
        this.clienteRepo = clienteRepo;
    }

    // 1. Obtener las cuentas asociadas a un cliente (por DUI)
    // Ejemplo: GET /api/cuentas/12345678-9
    @GetMapping("/cuentas/{dui}")
    public List<Cuenta> obtenerCuentas(@PathVariable String dui) {
        Optional<Cliente> clienteOpt = clienteRepo.findByDui(dui);
        return clienteOpt.map(Cliente::getCuentas).orElse(Collections.emptyList());
    }

    // 2. Abonar efectivo a una cuenta mediante URL
    // La nueva URL será: /api/abonarefectivo/{dui}/{numero}/{monto}
    @PostMapping("/abonarefectivo/{dui}/{numero}/{monto}")
    public String abonar(@PathVariable String dui,
                         @PathVariable String numero,
                         @PathVariable double monto) {
        Optional<Cliente> clienteOpt = clienteRepo.findByDui(dui);
        if (clienteOpt.isEmpty()) {
            return "Cliente no encontrado";
        }
        Cliente cliente = clienteOpt.get();
        for (Cuenta cuenta : cliente.getCuentas()) {
            if (cuenta.getNumero().equals(numero)) {
                cuenta.setSaldo(cuenta.getSaldo() + monto);
                clienteRepo.save(cliente);
                return "Abono exitoso. Nuevo saldo: " + cuenta.getSaldo();
            }
        }
        return "Cuenta no encontrada";
    }

    // 3. Retirar efectivo de una cuenta mediante URL
    // La nueva URL será: /api/retirarefectivo/{dui}/{numero}/{monto}
    @PostMapping("/retirarefectivo/{dui}/{numero}/{monto}")
    public String retirar(@PathVariable String dui,
                          @PathVariable String numero,
                          @PathVariable double monto) {
        Optional<Cliente> clienteOpt = clienteRepo.findByDui(dui);
        if (clienteOpt.isEmpty()) {
            return "Cliente no encontrado";
        }
        Cliente cliente = clienteOpt.get();
        for (Cuenta cuenta : cliente.getCuentas()) {
            if (cuenta.getNumero().equals(numero)) {
                if (cuenta.getSaldo() < monto) {
                    return "Saldo insuficiente";
                }
                cuenta.setSaldo(cuenta.getSaldo() - monto);
                clienteRepo.save(cliente);
                return "Retiro exitoso. Nuevo saldo: " + cuenta.getSaldo();
            }
        }
        return "Cuenta no encontrada";
    }
    @GetMapping("/clientes")
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepo.findAll();
    }
}
