package com.banco.serviciocuentas.repository;

import com.banco.serviciocuentas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDui(String dui);

    boolean existsByDui(String dui);
}
