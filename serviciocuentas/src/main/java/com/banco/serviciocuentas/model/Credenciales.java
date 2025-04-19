package com.banco.serviciocuentas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "credenciales")
public class Credenciales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String correo;

    @Column(nullable = false, length = 100)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cuenta", nullable = false, length = 10)
    private TipoCuenta tipoCuenta;

    @Column(name = "cliente_dui", length = 10)
    private String clienteDui;

    public Credenciales() {
    }

    public Credenciales(String correo, String contrasena, TipoCuenta tipoCuenta, String clienteDui) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.tipoCuenta = tipoCuenta;
        this.clienteDui = clienteDui;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getClienteDui() {
        return clienteDui;
    }

    public void setClienteDui(String clienteDui) {
        this.clienteDui = clienteDui;
    }
}
