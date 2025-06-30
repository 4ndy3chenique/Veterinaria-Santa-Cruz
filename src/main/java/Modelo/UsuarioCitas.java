package Modelo;

import java.sql.Date;
import java.sql.Time;

public class UsuarioCitas {
    private int idCita;
    private int idUsuario;
    private int idVeterinario;
    private Date fecha;
    private Time hora;
    private String motivo;
    private String estado;

    // Campo para almacenar el nombre completo del veterinario (concatenado de V_Nombre y V_Apellido)
    private String nombreVeterinario; // <-- Asegúrate de que esta línea exista

    // Campo para almacenar el nombre del cliente (desde la tabla 'usuario')
    private String nombreCliente; // <-- Asegúrate de que esta línea exista

    public UsuarioCitas() {
        // Constructor vacío
    }

    public UsuarioCitas(int idCita, int idUsuario, int idVeterinario, Date fecha, Time hora, String motivo, String estado) {
        this.idCita = idCita;
        this.idUsuario = idUsuario;
        this.idVeterinario = idVeterinario;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.estado = estado;
    }

    // --- Getters y Setters ---

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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

    public void setMotivo(String motivo) { // ¡Ojo! Antes tenía 'void setMotivo', debe ser 'public void setMotivo'
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // --- Métodos para nombreVeterinario (se usará 'getVeterinario' en JSP) ---
    public String getVeterinario() { // Este método es el que se llamará en tu JSP (ej. ${cita.veterinario})
        return nombreVeterinario;
    }

    public void setVeterinario(String nombreVeterinario) { // Setter correspondiente
        this.nombreVeterinario = nombreVeterinario;
    }

    // --- Métodos para nombreCliente ---
    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
}