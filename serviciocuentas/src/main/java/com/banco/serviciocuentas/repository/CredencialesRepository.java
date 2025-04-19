package com.banco.serviciocuentas.repository;

import com.banco.serviciocuentas.model.Credenciales;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CredencialesRepository extends JpaRepository<Credenciales, Long> {
    /**
     * Obtiene todas las credenciales asociadas a un cliente por su DUI.
     */
    List<Credenciales> findByClienteDui(String clienteDui);
}
