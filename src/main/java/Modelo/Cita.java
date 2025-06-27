package Modelo;

import java.sql.Date;
import java.sql.Time;

public class Cita {
    private int idCita;
    private int idCliente;
    private int idVeterinario;
    private Date fecha;
    private Time hora;
    private String motivo;
    private String estado; // Nuevo atributo para el estado de la cita

    // Atributos para los detalles del cliente y veterinario (no están en la tabla Citas, pero se obtienen por JOIN)
    private String nombreCliente;
    private String apellidoCliente;
    private String dniCliente; // Atributo para almacenar el DNI del cliente
    private String nombreVeterinario;
    private String apellidoVeterinario;
    private String especialidadVeterinario;

    // Constructor vacío
    public Cita() {
    }

    // Constructor con todos los parámetros
    public Cita(int idCita, int idCliente, int idVeterinario, Date fecha, Time hora, String motivo, String estado,
                String nombreCliente, String apellidoCliente, String dniCliente,
                String nombreVeterinario, String apellidoVeterinario, String especialidadVeterinario) {
        this.idCita = idCita;
        this.idCliente = idCliente;
        this.idVeterinario = idVeterinario;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.estado = estado;
        this.nombreCliente = nombreCliente;
        this.apellidoCliente = apellidoCliente;
        this.dniCliente = dniCliente;
        this.nombreVeterinario = nombreVeterinario;
        this.apellidoVeterinario = apellidoVeterinario;
        // ¡¡¡CORRECCIÓN AQUÍ!!!
        this.especialidadVeterinario = especialidadVeterinario; // Antes decía 'especialVeterinario'
    }

    // Getters y Setters

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Getters y Setters para los detalles del cliente y veterinario

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getApellidoCliente() {
        return apellidoCliente;
    }

    public void setApellidoCliente(String apellidoCliente) {
        this.apellidoCliente = apellidoCliente;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
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
