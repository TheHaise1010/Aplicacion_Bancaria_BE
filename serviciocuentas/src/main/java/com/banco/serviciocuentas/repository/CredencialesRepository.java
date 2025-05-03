package com.banco.serviciocuentas.repository;

import com.banco.serviciocuentas.model.Credenciales;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CredencialesRepository extends JpaRepository<Credenciales, Long> {
    List<Credenciales> findByClienteDui(String clienteDui);

    Optional<Credenciales> findByCorreoAndContrasena(String correo, String contrasena);
}
