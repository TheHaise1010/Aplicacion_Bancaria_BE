package com.banco.serviciocuentas.controller;

import com.banco.serviciocuentas.model.Credenciales;
import com.banco.serviciocuentas.repository.CredencialesRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import java.net.URI;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/credenciales")
@CrossOrigin(origins = "http://localhost:4200") // Permite peticiones desde tu aplicación Angular
public class CredencialesController {

    private final CredencialesRepository credRepo;

    public CredencialesController(CredencialesRepository credRepo) {
        this.credRepo = credRepo;
    }

    // Listar todas las credenciales
    @GetMapping
    public List<Credenciales> findAll() {
        return credRepo.findAll();
    }

    // Obtener credenciales por DUI
    @GetMapping("/{dui}")
    public List<Credenciales> findByClienteDui(@PathVariable String dui) {
        return credRepo.findByClienteDui(dui);
    }

    // Crear credenciales (body con datos)
    // POST /api/credenciales
    @PostMapping
    public ResponseEntity<Credenciales> create(@RequestBody Credenciales cred) {
        Credenciales saved = credRepo.save(cred);
        return ResponseEntity
                .created(URI.create("/api/credenciales/" + saved.getId()))
                .body(saved);
    }

    // Actualizar credenciales por DUI (body con nuevo correo y/o contraseña)
    // PUT /api/credenciales/{dui}
    @PutMapping("/{dui}")
    public ResponseEntity<Credenciales> update(
            @PathVariable String dui,
            @RequestBody Credenciales updated
    ) {
        List<Credenciales> creds = credRepo.findByClienteDui(dui);
        if (creds.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Credenciales c = creds.get(0);
        c.setCorreo(updated.getCorreo());
        c.setContrasena(updated.getContrasena());
        return ResponseEntity.ok(credRepo.save(c));
    }

    // Borrar credenciales por DUI
    // DELETE /api/credenciales/{dui}
    @DeleteMapping("/{dui}")
    public ResponseEntity<Void> delete(@PathVariable String dui) {
        List<Credenciales> creds = credRepo.findByClienteDui(dui);
        if (creds.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        credRepo.deleteAll(creds);
        return ResponseEntity.noContent().build();
    }

    // DTO simple para login
    public static class LoginRequest {
        private String correo;
        private String contrasena;
        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
        public String getContrasena() { return contrasena; }
        public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    }

    // POST /api/credenciales/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<Credenciales> opt = credRepo.findByCorreoAndContrasena(
                req.getCorreo(), req.getContrasena()
        );
        if (opt.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", "un_token_seguro_generado"); // Para generar un token real mas adelante
            response.put("message", "Login exitoso");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Credenciales inválidas");
            return ResponseEntity.ok(response);        }
    }

}