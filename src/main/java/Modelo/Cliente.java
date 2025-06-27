/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.sql.Date;

public class Cliente {
    private int idCliente;
     private int idVeterinario;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private Date fechaRegistro;

   private String nombreVeterinario;
    private String apellidoVeterinario;
    private String especialidadVeterinario;

    public Cliente() {
    }

    public Cliente(int idCliente, int idVeterinario, String nombre, String apellido, String dni, String telefono, Date fechaRegistro, String nombreVeterinario, String apellidoVeterinario, String especialidadVeterinario) {
        this.idCliente = idCliente;
        this.idVeterinario = idVeterinario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.fechaRegistro = fechaRegistro;
        this.nombreVeterinario = nombreVeterinario;
        this.apellidoVeterinario = apellidoVeterinario;
        this.especialidadVeterinario = especialidadVeterinario;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getNombreVeterinario() {
        return nombreVeterinario;
    }

    public void setNombreVeterinario(String nombreVeterinario) {
        this.nombreVeterinario = nombreVeterinario;
    }

    public String getApellidoVeterinario() {
        return apellidoVeterinario;
    }

    public void setApellidoVeterinario(String apellidoVeterinario) {
        this.apellidoVeterinario = apellidoVeterinario;
    }

    public String getEspecialidadVeterinario() {
        return especialidadVeterinario;
    }

    public void setEspecialidadVeterinario(String especialidadVeterinario) {
        this.especialidadVeterinario = especialidadVeterinario;
    }
    
}