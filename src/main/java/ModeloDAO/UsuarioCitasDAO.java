package ModeloDAO;

import Modelo.Conexion; // Asegúrate de que esta ruta sea correcta y la clase exista
import Modelo.UsuarioCitas; // Asegúrate de que esta ruta sea correcta y la clase exista

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger; // Importar para logging

/**
 * DAO (Data Access Object) para gestionar las operaciones de la base de datos
 * relacionadas con las citas de los usuarios (UsuarioCitas).
 * Esta clase maneja la inserción, consulta, actualización y eliminación de datos en la tabla de citas.
 */
public class UsuarioCitasDAO {

    // Configuración del logger para esta clase
    private static final Logger LOGGER = Logger.getLogger(UsuarioCitasDAO.class.getName());

    /**
     * Registra una nueva cita en la base de datos.
     *
     * @param cita El objeto UsuarioCitas con toda la información a guardar.
     * @return true si la cita se registró con éxito, false en caso contrario.
     */
    public boolean registrarCita(UsuarioCitas cita) {
        // Es una buena práctica especificar las columnas en el INSERT.
        String sql = "INSERT INTO UsuarioCitas (nombre_cliente, fecha, hora, veterinario, estado) VALUES (?, ?, ?, ?, ?)";
        
        // Usamos try-with-resources para asegurar que la conexión y el PreparedStatement se cierren automáticamente.
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cita.getNombreCliente());
            ps.setDate(2, cita.getFecha());       // Usamos setDate para el tipo java.sql.Date
            ps.setTime(3, cita.getHora());        // Usamos setTime para el tipo java.sql.Time
            ps.setString(4, cita.getVeterinario());
            ps.setString(5, cita.getEstado());

            int filasAfectadas = ps.executeUpdate();
            
            LOGGER.log(Level.INFO, "Cita registrada: {0} filas afectadas.", filasAfectadas);
            return filasAfectadas > 0;

        } catch (SQLException e) {
            // Loguear el error para depuración en el servidor
            LOGGER.log(Level.SEVERE, "Error al registrar la cita: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Obtiene una lista de todas las citas pertenecientes a un cliente específico.
     * (Este método no es usado por el Servlet actual, pero se mantiene para funcionalidad futura).
     *
     * @param nombreCliente El nombre del cliente cuyas citas se quieren listar.
     * @return Una lista de objetos UsuarioCitas. La lista estará vacía si no se encuentran citas o si ocurre un error.
     */
    public List<UsuarioCitas> listarCitasPorUsuario(String nombreCliente) {
        List<UsuarioCitas> lista = new ArrayList<>();
        String sql = "SELECT id_cita, nombre_cliente, fecha, hora, veterinario, estado FROM UsuarioCitas WHERE nombre_cliente = ? ORDER BY fecha, hora";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nombreCliente);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UsuarioCitas cita = new UsuarioCitas();
                    cita.setIdCita(rs.getInt("id_cita"));
                    cita.setNombreCliente(rs.getString("nombre_cliente"));
                    cita.setFecha(rs.getDate("fecha"));
                    cita.setHora(rs.getTime("hora"));
                    cita.setVeterinario(rs.getString("veterinario"));
                    cita.setEstado(rs.getString("estado"));
                    lista.add(cita);
                }
            }
            LOGGER.log(Level.INFO, "Citas listadas por usuario '{0}': {1} encontradas.", new Object[]{nombreCliente, lista.size()});
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar citas por usuario: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Obtiene una lista de TODAS las citas registradas en el sistema.
     * Es el método principal usado por GestionCitasServlet.
     *
     * @return Una lista completa de objetos UsuarioCitas, ordenada por fecha descendente y luego por hora.
     * La lista estará vacía si no hay citas o si ocurre un error.
     */
    public List<UsuarioCitas> listarTodasLasCitas() {
        List<UsuarioCitas> lista = new ArrayList<>();
        // Ordenamos por fecha descendente (más recientes primero) y luego por hora ascendente.
        String sql = "SELECT id_cita, nombre_cliente, fecha, hora, veterinario, estado FROM UsuarioCitas ORDER BY fecha DESC, hora ASC";
        
        try (Connection conn = Conexion.getConnection(); // Obtiene la conexión a la DB
             PreparedStatement ps = conn.prepareStatement(sql); // Prepara la consulta SQL
             ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta y obtiene el ResultSet

            while (rs.next()) { // Itera sobre cada fila del ResultSet
                UsuarioCitas cita = new UsuarioCitas(); // Crea un nuevo objeto Cita
                // Mapea las columnas del ResultSet a las propiedades del objeto Cita
                cita.setIdCita(rs.getInt("id_cita"));
                cita.setNombreCliente(rs.getString("nombre_cliente"));
                cita.setFecha(rs.getDate("fecha"));
                cita.setHora(rs.getTime("hora"));
                cita.setVeterinario(rs.getString("veterinario"));
                cita.setEstado(rs.getString("estado"));
                lista.add(cita); // Añade la cita a la lista
            }
            LOGGER.log(Level.INFO, "Todas las citas listadas: {0} encontradas.", lista.size());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar todas las citas: " + e.getMessage(), e);
            // Podrías relanzar la excepción o devolver una lista vacía y manejarlo en el servlet
        }
        return lista;
    }
    
    /**
     * Actualiza el estado de una cita específica.
     *
     * @param idCita El ID de la cita a actualizar.
     * @param nuevoEstado El nuevo estado de la cita.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarEstadoCita(int idCita, String nuevoEstado) {
        String sql = "UPDATE UsuarioCitas SET estado = ? WHERE id_cita = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idCita);
            int filasAfectadas = ps.executeUpdate();
            LOGGER.log(Level.INFO, "Actualizando cita ID {0} a estado '{1}': {2} filas afectadas.", new Object[]{idCita, nuevoEstado, filasAfectadas});
            return filasAfectadas > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar el estado de la cita ID " + idCita + ": " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Elimina una cita de la base de datos por su ID.
     *
     * @param idCita El ID de la cita a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminarCita(int idCita) {
        String sql = "DELETE FROM UsuarioCitas WHERE id_cita = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            int filasAfectadas = ps.executeUpdate();
            LOGGER.log(Level.INFO, "Eliminando cita ID {0}: {1} filas afectadas.", new Object[]{idCita, filasAfectadas});
            return filasAfectadas > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar la cita ID " + idCita + ": " + e.getMessage(), e);
            return false;
        }
    }
}