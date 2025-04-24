package com.banco.serviciocuentas.controller;

import com.banco.serviciocuentas.model.Cliente;
import com.banco.serviciocuentas.repository.ClienteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus; // ¡Importa HttpStatus!

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:4200") // Permite peticiones desde tu aplicación Angular
public class ClienteController {

    private final ClienteRepository clienteRepo;

    public ClienteController(ClienteRepository clienteRepo) {
        this.clienteRepo = clienteRepo;
    }

    // Listar todos los clientes
    @GetMapping
    public List<Cliente> findAll() {
        return clienteRepo.findAll();
    }

    // Obtener cliente por DUI
    @GetMapping("/{dui}")
    public ResponseEntity<Cliente> findByDui(@PathVariable String dui) {
        return clienteRepo.findByDui(dui)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear cliente (datos en el body)
    // POST /api/clientes
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Cliente cliente) {
        if (clienteRepo.existsByDui(cliente.getDui())) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ya existe un cliente con el DUI: " + cliente.getDui());
            return ResponseEntity.badRequest().body(response);
        }
        Cliente saved = clienteRepo.save(cliente);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("dui", saved.getDui()); // Puedes incluir el DUI creado
        response.put("message", "Cliente creado exitosamente");
        return ResponseEntity.created(URI.create("/api/clientes/" + saved.getDui())).body(response);
    }

    // Actualizar cliente (campos en el body)
    // PUT /api/clientes/{dui}
    @PutMapping("/{dui}")
    public ResponseEntity<?> update(
            @PathVariable String dui,
            @RequestBody Cliente updatedData
    ) {
        return clienteRepo.findByDui(dui)
                .map(c -> {
                    c.setPrimerNombre(updatedData.getPrimerNombre());
                    c.setApellido(updatedData.getApellido());
                    c.setFechaNacimiento(updatedData.getFechaNacimiento());
                    clienteRepo.save(c);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Cliente actualizado exitosamente");
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "No se encontró cliente con el DUI: " + dui);
                    // **Corrección aquí**
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    // Borrar cliente por DUI
    // DELETE /api/clientes/{dui}
    @DeleteMapping("/{dui}")
    public ResponseEntity<?> delete(@PathVariable String dui) {
        Optional<Cliente> opt = clienteRepo.findByDui(dui);
        if (opt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "No se encontró cliente con el DUI: " + dui);
            // **Corrección aquí**
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        clienteRepo.delete(opt.get());
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Cliente eliminado exitosamente");
        // ResponseEntity.noContent() es correcto aquí porque el 204 no lleva cuerpo.
        return ResponseEntity.noContent().build();
    }

}
