/*
 
Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template*/
package Modelo;
import java.sql.Date;
import java.sql.Time;
/**
 *
 
@author feroy*/
public class UsuarioCitaRecep {
    private int idCita;
    private String nombreCliente;
    private Date fecha;
    private Time hora;
    private String veterinario;
    private String estado;

    public UsuarioCitaRecep() {
    }

    public UsuarioCitaRecep(int idCita, String nombreCliente, Date fecha, Time hora, String veterinario, String estado) {
        this.idCita = idCita;
        this.nombreCliente = nombreCliente;
        this.fecha = fecha;
        this.hora = hora;
        this.veterinario = veterinario;
        this.estado = estado;
    }

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Time getHora() {
        return hora;
    }

    public void setHora(Time hora) {
        this.hora = hora;
    }

    public String getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(String veterinario) {
        this.veterinario = veterinario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


}