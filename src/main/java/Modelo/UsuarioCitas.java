package Modelo;

import java.sql.Date;
import java.sql.Time;

public class UsuarioCitas {
    private int idCita; // Corresponde a 'id_cita' en la DB
    private int idUsuario;
    private int idVeterinario;
    private Date fecha;
    private Time hora;
    private String veterinario; // Corresponde a 'veterinario' (nombre completo) en la DB
    private String motivo;
    private String estado;

    // Puedes agregar campos adicionales si los necesitas para la lógica de la aplicación
    // pero no están directamente en la tabla UsuarioCitas.
    // Por ejemplo, para mostrar el nombre del cliente en una lista de citas.
    private String nombreCliente;

    // Constructor vacío: Necesario para frameworks y para instanciar objetos
    // antes de setear sus propiedades (ej. al leer de la base de datos).
    public UsuarioCitas() {
    }

    // Constructor para el REGISTRO de una nueva cita:
    // No incluye idCita porque es auto-incremental en la base de datos.
    // Incluye 'nombreVeterinario' ya que tu SP 'sp_registrar_cita' lo necesita.
    public UsuarioCitas(int idUsuario, int idVeterinario, Date fecha, Time hora, String veterinario, String motivo, String estado) {
        this.idUsuario = idUsuario;
        this.idVeterinario = idVeterinario;
        this.fecha = fecha;
        this.hora = hora;
        this.veterinario = veterinario; // Se inicializa aquí
        this.motivo = motivo;
        this.estado = estado;
        this.idCita = 0; // Se inicializa a 0, la DB le asignará un valor real
    }

    // Constructor completo: Útil cuando se recuperan datos de la base de datos,
    // donde ya se tiene el idCita asignado y el nombre del veterinario.
    public UsuarioCitas(int idCita, int idUsuario, int idVeterinario, Date fecha, Time hora, String veterinario, String motivo, String estado) {
        this.idCita = idCita;
        this.idUsuario = idUsuario;
        this.idVeterinario = idVeterinario;
        this.fecha = fecha;
        this.hora = hora;
        this.veterinario = veterinario; // Se inicializa aquí
        this.motivo = motivo;
        this.estado = estado;
    }


    // --- Getters y Setters para todos los campos ---

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

    // Getter y Setter para el nombre del veterinario
    public String getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(String veterinario) {
        this.veterinario = veterinario;
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

    // Getters y setters para campos adicionales (como nombreCliente)
    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    @Override
    public String toString() {
        return "UsuarioCitas{" +
               "idCita=" + idCita +
               ", idUsuario=" + idUsuario +
               ", idVeterinario=" + idVeterinario +
               ", fecha=" + fecha +
               ", hora=" + hora +
               ", veterinario='" + veterinario + '\'' +
               ", motivo='" + motivo + '\'' +
               ", estado='" + estado + '\'' +
               ", nombreCliente='" + nombreCliente + '\'' +
               '}';
    }
}