package com.banco.serviciocuentas.repository;

import com.banco.serviciocuentas.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByClienteDuiAndNumero(String clienteDui, String numero);

}
