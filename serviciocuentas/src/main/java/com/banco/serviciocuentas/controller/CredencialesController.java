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
    public ResponseEntity<?> create(@RequestBody Credenciales cred) {
        List<Credenciales> existingCreds = credRepo.findByClienteDui(cred.getClienteDui());
        if (!existingCreds.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ya existe una credencial registrada para el DUI: " + cred.getClienteDui());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409 Conflict
        }
        try {
            Credenciales saved = credRepo.save(cred);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", saved.getId());
            response.put("message", "Credenciales creadas exitosamente");
            return ResponseEntity.created(URI.create("/api/credenciales/" + saved.getId())).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al crear las credenciales: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // Actualizar credenciales por DUI (body con nuevo correo y/o contraseña)
    // PUT /api/credenciales/{dui}
    @PutMapping("/{dui}")
    public ResponseEntity<?> update(
            @PathVariable String dui,
            @RequestBody Credenciales updated
    ) {
        List<Credenciales> creds = credRepo.findByClienteDui(dui);
        if (creds.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "No se encontraron credenciales con el DUI: " + dui);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        try {
            Credenciales c = creds.get(0);
            c.setCorreo(updated.getCorreo());
            c.setContrasena(updated.getContrasena());
            Credenciales saved = credRepo.save(c);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Credenciales actualizadas exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al actualizar las credenciales: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Borrar credenciales por DUI
    // DELETE /api/credenciales/{dui}
    @DeleteMapping("/{dui}")
    public ResponseEntity<?> delete(@PathVariable String dui) {
        List<Credenciales> creds = credRepo.findByClienteDui(dui);
        if (creds.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "No se encontraron credenciales con el DUI: " + dui);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        try {
            credRepo.deleteAll(creds);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Credenciales eliminadas exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al eliminar las credenciales: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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