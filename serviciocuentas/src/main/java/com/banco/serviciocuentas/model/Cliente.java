package com.banco.serviciocuentas.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String dui;

    @JsonManagedReference
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cuenta> cuentas;

    // Getters y Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDui() {
        return dui;
    }
    public void setDui(String dui) {
        this.dui = dui;
    }
    public List<Cuenta> getCuentas() {
        return cuentas;
    }
    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }
}
