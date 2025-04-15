package com.banco.serviciocuentas.repository;

import com.banco.serviciocuentas.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
}
