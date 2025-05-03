package com.banco.serviciocuentas.controller;

import com.banco.serviciocuentas.model.Cliente;
import com.banco.serviciocuentas.model.Cuenta;
import com.banco.serviciocuentas.repository.ClienteRepository;
import com.banco.serviciocuentas.repository.CuentaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap; // Importar HashMap
import java.util.List;
import java.util.Map;     // Importar Map
import java.util.Optional;

@RestController
@RequestMapping("/api/cuentas")
@CrossOrigin(origins = "http://localhost:4200") // Permite peticiones desde tu aplicación Angular
public class CuentaController {

    private final ClienteRepository clienteRepo;
    private final CuentaRepository cuentaRepo;

    public CuentaController(ClienteRepository clienteRepo,
                            CuentaRepository cuentaRepo) {
        this.clienteRepo = clienteRepo;
        this.cuentaRepo = cuentaRepo;
    }

    // Listar todas las cuentas
    // Este método ya devuelve una lista, lo cual es estándar.
    // Una lista vacía es el caso de "no encontradas".
    @GetMapping
    public List<Cuenta> findAll() {
        return cuentaRepo.findAll();
    }

    // Listar cuentas de un cliente por DUI
    // GET /api/cuentas/cliente/{dui}
    @GetMapping("/cliente/{dui}")
    public ResponseEntity<?> findByClienteDui(@PathVariable String dui) {
        Optional<Cliente> optCliente = clienteRepo.findByDui(dui);
        if (optCliente.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Cliente con DUI " + dui + " no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        List<Cuenta> cuentas = optCliente.get().getCuentas();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Cuentas encontradas para cliente con DUI " + dui);
        response.put("data", cuentas); // Incluimos la lista de cuentas en 'data'
        return ResponseEntity.ok(response);
    }

    // Crear cuenta para un cliente (recibe JSON con número y saldo)
    // POST /api/cuentas/cliente/{dui}
    @PostMapping("/cliente/{dui}")
    public ResponseEntity<?> create(
            @PathVariable String dui,
            @RequestBody Cuenta cuentaData
    ) {
        Optional<Cliente> optCliente = clienteRepo.findByDui(dui);
        if (optCliente.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Cliente con DUI " + dui + " no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Cliente cliente = optCliente.get();

        Cuenta nueva = new Cuenta();
        nueva.setNumero(cuentaData.getNumero());
        nueva.setSaldo(cuentaData.getSaldo());
        nueva.setCliente(cliente);

        Cuenta saved = cuentaRepo.save(nueva);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Cuenta creada exitosamente");
        response.put("data", saved); // Incluimos la cuenta creada en 'data'

        // Usamos 201 Created, que es estándar para creaciones exitosas
        return ResponseEntity
                .created(URI.create("/api/cuentas/" + saved.getNumero()))
                .body(response);
    }

    // Borrar cuenta por DUI y número
    // DELETE /api/cuentas/cliente/{dui}/{numero}
    @DeleteMapping("/cliente/{dui}/{numero}")
    public ResponseEntity<Map<String, Object>> delete(
            @PathVariable String dui,
            @PathVariable String numero
    ) {
        Optional<Cuenta> opt = cuentaRepo.findByClienteDuiAndNumero(dui, numero);
        Map<String, Object> response = new HashMap<>();

        if (opt.isEmpty()) {
            response.put("success", false);
            response.put("message", String.format(
                    "No se encontró la cuenta %s para el cliente %s", numero, dui));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        cuentaRepo.delete(opt.get());
        response.put("success", true);
        response.put("message", "Cuenta eliminada correctamente");
        return ResponseEntity.ok(response);
    }


    // Abonar efectivo (recibe JSON con número de cuenta y monto)
    // POST /api/cuentas/cliente/{dui}/abonarefectivo
    @PostMapping("/cliente/{dui}/abonarefectivo")
    public ResponseEntity<?> abonar( // Cambiamos a <?>
                                     @PathVariable String dui,
                                     @RequestBody CuentaAbonoRequest req
    ) {
        Optional<Cliente> optCliente = clienteRepo.findByDui(dui);
        if (optCliente.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Cliente con DUI " + dui + " no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Cliente cliente = optCliente.get();

        Optional<Cuenta> optCuenta = cliente.getCuentas().stream()
                .filter(c -> c.getNumero().equals(req.getNumero()))
                .findFirst();
        if (optCuenta.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Cuenta con número " + req.getNumero() + " no encontrada para el cliente con DUI " + dui);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Cuenta cuenta = optCuenta.get();
        cuenta.setSaldo(cuenta.getSaldo() + req.getMonto());
        Cuenta updatedCuenta = cuentaRepo.save(cuenta); // Guardamos y obtenemos la cuenta actualizada

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Abono exitoso.");
        response.put("newSaldo", updatedCuenta.getSaldo()); // Opcional: devolver el nuevo saldo
        response.put("data", updatedCuenta); // Opcional: devolver la cuenta actualizada

        return ResponseEntity.ok(response);
    }

    // Retirar efectivo (recibe JSON con número de cuenta y monto)
    // POST /api/cuentas/cliente/{dui}/retirarefectivo
    @PostMapping("/cliente/{dui}/retirarefectivo")
    public ResponseEntity<?> retirar( // Cambiamos a <?>
                                      @PathVariable String dui,
                                      @RequestBody CuentaAbonoRequest req
    ) {
        Optional<Cliente> optCliente = clienteRepo.findByDui(dui);
        if (optCliente.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Cliente con DUI " + dui + " no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Cliente cliente = optCliente.get();

        Optional<Cuenta> optCuenta = cliente.getCuentas().stream()
                .filter(c -> c.getNumero().equals(req.getNumero()))
                .findFirst();
        if (optCuenta.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Cuenta con número " + req.getNumero() + " no encontrada para el cliente con DUI " + dui);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Cuenta cuenta = optCuenta.get();
        if (cuenta.getSaldo() < req.getMonto()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Saldo insuficiente. Saldo actual: " + cuenta.getSaldo());
            // Usamos 400 Bad Request para errores de lógica de negocio (saldo insuficiente)
            return ResponseEntity.badRequest().body(response);
        }

        cuenta.setSaldo(cuenta.getSaldo() - req.getMonto());
        Cuenta updatedCuenta = cuentaRepo.save(cuenta); // Guardamos y obtenemos la cuenta actualizada

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Retiro exitoso.");
        response.put("newSaldo", updatedCuenta.getSaldo()); // Opcional: devolver el nuevo saldo
        response.put("data", updatedCuenta); // Opcional: devolver la cuenta actualizada

        return ResponseEntity.ok(response);
    }

    // DTO para abono/retiro (sin cambios)
    public static class CuentaAbonoRequest {
        private String numero;
        private double monto;

        public String getNumero() {
            return numero;
        }

        public void setNumero(String numero) {
            this.numero = numero;
        }

        public double getMonto() {
            return monto;
        }

        public void setMonto(double monto) {
            this.monto = monto;
        }
    }
}