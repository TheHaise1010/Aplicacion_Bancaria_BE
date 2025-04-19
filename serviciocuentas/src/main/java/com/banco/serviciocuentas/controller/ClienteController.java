package com.banco.serviciocuentas.controller;

import com.banco.serviciocuentas.model.Cliente;
import com.banco.serviciocuentas.repository.ClienteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
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
    public ResponseEntity<Cliente> create(@RequestBody Cliente cliente) {
        if (clienteRepo.existsByDui(cliente.getDui())) {
            return ResponseEntity.badRequest().build();
        }
        Cliente saved = clienteRepo.save(cliente);
        return ResponseEntity
                .created(URI.create("/api/clientes/" + saved.getDui()))
                .body(saved);
    }

    // Actualizar cliente (campos en el body)
    // PUT /api/clientes/{dui}
    @PutMapping("/{dui}")
    public ResponseEntity<Cliente> update(
            @PathVariable String dui,
            @RequestBody Cliente updatedData
    ) {
        return clienteRepo.findByDui(dui)
                .map(c -> {
                    c.setPrimerNombre(updatedData.getPrimerNombre());
                    c.setApellido(updatedData.getApellido());
                    c.setFechaNacimiento(updatedData.getFechaNacimiento());
                    return ResponseEntity.ok(clienteRepo.save(c));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Borrar cliente por DUI
    // DELETE /api/clientes/{dui}
    @DeleteMapping("/{dui}")
    public ResponseEntity<Void> delete(@PathVariable String dui) {
        Optional<Cliente> opt = clienteRepo.findByDui(dui);
        if (opt.isEmpty()) {
            // Aquí build() devuelve ResponseEntity<Void>
            return ResponseEntity.notFound().build();
        }
        clienteRepo.delete(opt.get());
        return ResponseEntity.noContent().build();
    }

}
