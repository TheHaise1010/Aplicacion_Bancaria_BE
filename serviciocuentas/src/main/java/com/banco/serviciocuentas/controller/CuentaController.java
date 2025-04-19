package com.banco.serviciocuentas.controller;

import com.banco.serviciocuentas.model.Cliente;
import com.banco.serviciocuentas.model.Cuenta;
import com.banco.serviciocuentas.repository.ClienteRepository;
import com.banco.serviciocuentas.repository.CuentaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final ClienteRepository clienteRepo;
    private final CuentaRepository  cuentaRepo;

    public CuentaController(ClienteRepository clienteRepo,
                            CuentaRepository cuentaRepo) {
        this.clienteRepo = clienteRepo;
        this.cuentaRepo  = cuentaRepo;
    }

    // Listar todas las cuentas
    @GetMapping
    public List<Cuenta> findAll() {
        return cuentaRepo.findAll();
    }

    // Listar cuentas de un cliente por DUI
    @GetMapping("/cliente/{dui}")
    public ResponseEntity<List<Cuenta>> findByClienteDui(@PathVariable String dui) {
        Optional<Cliente> optCliente = clienteRepo.findByDui(dui);
        if (optCliente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(optCliente.get().getCuentas());
    }

    // Crear cuenta para un cliente (recibe JSON con número y saldo)
    @PostMapping("/cliente/{dui}")
    public ResponseEntity<Cuenta> create(
            @PathVariable String dui,
            @RequestBody Cuenta cuentaData
    ) {
        Optional<Cliente> optCliente = clienteRepo.findByDui(dui);
        if (optCliente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cliente cliente = optCliente.get();

        Cuenta nueva = new Cuenta();
        nueva.setNumero(cuentaData.getNumero());
        nueva.setSaldo(cuentaData.getSaldo());
        nueva.setCliente(cliente);

        Cuenta saved = cuentaRepo.save(nueva);
        return ResponseEntity
                .created(URI.create("/api/cuentas/" + saved.getNumero()))
                .body(saved);
    }

    // Borrar cuenta por DUI y número
    @DeleteMapping("/cliente/{dui}/{numero}")
    public ResponseEntity<Void> delete(
            @PathVariable String dui,
            @PathVariable String numero
    ) {
        Optional<Cliente> optCliente = clienteRepo.findByDui(dui);
        if (optCliente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cliente cliente = optCliente.get();

        Optional<Cuenta> optCuenta = cliente.getCuentas().stream()
                .filter(c -> c.getNumero().equals(numero))
                .findFirst();
        if (optCuenta.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        cuentaRepo.delete(optCuenta.get());
        return ResponseEntity.noContent().build();
    }

    // Abonar efectivo (recibe JSON con número de cuenta y monto)
    @PostMapping("/cliente/{dui}/abonarefectivo")
    public ResponseEntity<String> abonar(
            @PathVariable String dui,
            @RequestBody CuentaAbonoRequest req
    ) {
        Optional<Cliente> optCliente = clienteRepo.findByDui(dui);
        if (optCliente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cliente no encontrado");
        }
        Cliente cliente = optCliente.get();

        Optional<Cuenta> optCuenta = cliente.getCuentas().stream()
                .filter(c -> c.getNumero().equals(req.getNumero()))
                .findFirst();
        if (optCuenta.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cuenta no encontrada");
        }

        Cuenta cuenta = optCuenta.get();
        cuenta.setSaldo(cuenta.getSaldo() + req.getMonto());
        cuentaRepo.save(cuenta);
        return ResponseEntity.ok("Abono exitoso. Nuevo saldo: " + cuenta.getSaldo());
    }

    // Retirar efectivo (recibe JSON con número de cuenta y monto)
    @PostMapping("/cliente/{dui}/retirarefectivo")
    public ResponseEntity<String> retirar(
            @PathVariable String dui,
            @RequestBody CuentaAbonoRequest req
    ) {
        Optional<Cliente> optCliente = clienteRepo.findByDui(dui);
        if (optCliente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cliente no encontrado");
        }
        Cliente cliente = optCliente.get();

        Optional<Cuenta> optCuenta = cliente.getCuentas().stream()
                .filter(c -> c.getNumero().equals(req.getNumero()))
                .findFirst();
        if (optCuenta.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cuenta no encontrada");
        }

        Cuenta cuenta = optCuenta.get();
        if (cuenta.getSaldo() < req.getMonto()) {
            return ResponseEntity.badRequest()
                    .body("Saldo insuficiente");
        }
        cuenta.setSaldo(cuenta.getSaldo() - req.getMonto());
        cuentaRepo.save(cuenta);
        return ResponseEntity.ok("Retiro exitoso. Nuevo saldo: " + cuenta.getSaldo());
    }

    // DTO para abono/retiro
    public static class CuentaAbonoRequest {
        private String numero;
        private double monto;

        public String getNumero() { return numero; }
        public void setNumero(String numero) { this.numero = numero; }
        public double getMonto() { return monto; }
        public void setMonto(double monto) { this.monto = monto; }
    }
}
