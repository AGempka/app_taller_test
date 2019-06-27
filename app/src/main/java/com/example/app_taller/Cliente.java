package com.example.app_taller;

public class Cliente {
    private Integer idcliente;
    private String password_cliente;
    private String nombre_cliente;
    private String correo_cliente;
    private String direccion_cliente;
    private String telefono_cliente;

    public Cliente() {
    }

    public Cliente(Integer idcliente, String password_cliente, String nombre_cliente, String correo_cliente, String direccion_cliente, String telefono_cliente) {
        this.idcliente = idcliente;
        this.password_cliente = password_cliente;
        this.nombre_cliente = nombre_cliente;
        this.correo_cliente = correo_cliente;
        this.direccion_cliente = direccion_cliente;
        this.telefono_cliente = telefono_cliente;
    }

    public Integer getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(Integer idcliente) {
        this.idcliente = idcliente;
    }

    public String getPassword_cliente() {
        return password_cliente;
    }

    public void setPassword_cliente(String password_cliente) {
        this.password_cliente = password_cliente;
    }


    public String getNombre_cliente() {
        return nombre_cliente;
    }

    public void setNombre_cliente(String nombre_cliente) {
        this.nombre_cliente = nombre_cliente;
    }

    public String getCorreo_cliente() {
        return correo_cliente;
    }

    public void setCorreo_cliente(String correo_cliente) {
        this.correo_cliente = correo_cliente;
    }

    public String getDireccion_cliente() {
        return direccion_cliente;
    }

    public void setDireccion_cliente(String direccion_cliente) {
        this.direccion_cliente = direccion_cliente;
    }

    public String getTelefono_cliente() {
        return telefono_cliente;
    }

    public void setTelefono_cliente(String telefono_cliente) {
        this.telefono_cliente = telefono_cliente;
    }
}
