package ModeloDAO;

import Modelo.Conexion;
import Modelo.UsuarioCitas; 
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioCitaRecepDAO {

    // ¡AJUSTADO! Nombre de la clase en el logger
    private static final Logger LOGGER = Logger.getLogger(UsuarioCitaRecepDAO.class.getName());

    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    
    // Si usas CallableStatement para actualizar, también necesitas una referencia para cerrarlo.
    CallableStatement cs;

    /**
     * Lista todas las citas, incluyendo el nombre del cliente y el veterinario asociado.
     * Esta función construye el objeto UsuarioCitas a partir de JOINs.
     * Asume:
     * - Tabla 'Citas' con columnas: idCita, idCliente (FK a UsuarioCliente), idVeterinario (FK a Veterinario), fecha, hora, estado.
     * - Tabla 'UsuarioCliente' con columnas: idUsuario, nombreCliente (o similar).
     * - Tabla 'Veterinario' con columnas: idVeterinario, nombre (o similar).
     * @return Una lista de objetos UsuarioCitas.
     */
    public List<UsuarioCitas> listarCitasConDetalles() {
        List<UsuarioCitas> lista = new ArrayList<>();
        // Asumo que 'nombreCliente' es el campo que deseas mostrar como 'motivo' en tu JSP
        String sql = "SELECT c.idCita, uc.nombreCliente, c.fecha, c.hora, v.nombre AS nombreVeterinario, c.estado " +
                     "FROM Citas c " +
                     "JOIN UsuarioCliente uc ON c.idCliente = uc.idUsuario " + 
                     "LEFT JOIN Veterinario v ON c.idVeterinario = v.idVeterinario " + 
                     "ORDER BY c.fecha DESC, c.hora DESC";

        try {
            con = Conexion.getConnection();
            if (con == null) {
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudieron listar las citas con detalles.");
                return lista;
            }
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                UsuarioCitas uc = new UsuarioCitas();
                uc.setIdCita(rs.getInt("idCita"));
                uc.setNombreCliente(rs.getString("nombreCliente")); // Mapea a nombreCliente
                uc.setFecha(rs.getDate("fecha"));
                uc.setHora(rs.getTime("hora"));
                uc.setVeterinario(rs.getString("nombreVeterinario")); 
                uc.setEstado(rs.getString("estado"));
                lista.add(uc);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar citas con detalles: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, con); // Usar el método de cierre general
        }
        return lista;
    }

    /**
     * Obtiene una cita específica con detalles (nombre del cliente, veterinario) por su ID de Cita.
     * @param idCita El ID de la cita a buscar.
     * @return El objeto UsuarioCitas si se encuentra, o null si no existe.
     */
    public UsuarioCitas obtenerCitaConDetallesPorId(int idCita) {
        UsuarioCitas uc = null;
        String sql = "SELECT c.idCita, uc.nombreCliente, c.fecha, c.hora, v.nombre AS nombreVeterinario, c.estado, " +
                     "c.idCliente, c.idVeterinario " + // ¡IMPORTANTE! Necesitamos idCliente e idVeterinario para actualizar
                     "FROM Citas c " +
                     "JOIN UsuarioCliente uc ON c.idCliente = uc.idUsuario " +
                     "LEFT JOIN Veterinario v ON c.idVeterinario = v.idVeterinario " +
                     "WHERE c.idCita = ?";
        try {
            con = Conexion.getConnection();
            if (con == null) {
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudo obtener la cita con detalles.");
                return null;
            }
            ps = con.prepareStatement(sql);
            ps.setInt(1, idCita);
            rs = ps.executeQuery();
            if (rs.next()) {
                uc = new UsuarioCitas();
                uc.setIdCita(rs.getInt("idCita"));
                uc.setNombreCliente(rs.getString("nombreCliente"));
                uc.setFecha(rs.getDate("fecha"));
                uc.setHora(rs.getTime("hora"));
                uc.setVeterinario(rs.getString("nombreVeterinario"));
                uc.setEstado(rs.getString("estado"));
                // ¡IMPORTANTE! Almacenar los IDs para una posible actualización posterior
                uc.setIdUsuario(rs.getInt("idCliente")); 
                uc.setIdVeterinario(rs.getInt("idVeterinario"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener cita con detalles por ID: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, con);
        }
        return uc;
    }
    
    /**
     * Busca citas por un término dado (ej. por nombre de cliente, nombre de veterinario o estado).
     * Utiliza un Stored Procedure para mayor flexibilidad de búsqueda.
     * Asume un SP llamado `sp_buscar_citas_detalles` que toma un parámetro de búsqueda.
     * El SP debe devolver las mismas columnas que los métodos de listado/obtención.
     * @param busqueda El término de búsqueda.
     * @return Una lista de objetos UsuarioCitas que coinciden con la búsqueda.
     */
    public List<UsuarioCitas> buscarCitasConDetalles(String busqueda) {
        List<UsuarioCitas> lista = new ArrayList<>();
        String sql = "{CALL sp_buscar_citas_detalles(?)}"; // Asume un SP que hace el JOIN y filtra.
        // No instanciamos cs aquí, lo hacemos dentro del try para un manejo seguro.

        try {
            con = Conexion.getConnection();
            if (con == null) {
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudieron buscar citas con detalles.");
                return lista;
            }
            cs = con.prepareCall(sql); // Ahora se instancia aquí
            cs.setString(1, busqueda);
            rs = cs.executeQuery();

            while (rs.next()) {
                UsuarioCitas uc = new UsuarioCitas();
                uc.setIdCita(rs.getInt("idCita"));
                uc.setNombreCliente(rs.getString("nombreCliente"));
                uc.setFecha(rs.getDate("fecha"));
                uc.setHora(rs.getTime("hora"));
                uc.setVeterinario(rs.getString("nombreVeterinario"));
                uc.setEstado(rs.getString("estado"));
                lista.add(uc);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar citas con detalles mediante SP: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            // Asegúrate de cerrar CallableStatement también
            closeResources(rs, cs, con); 
        }
        return lista;
    }


    /**
     * Elimina una cita por su ID.
     * Asume un Stored Procedure `sp_eliminar_cita` que elimina de la tabla Citas.
     * Este DAO no manipula directamente 'UsuarioCitas' como tabla.
     * @param idCita El ID de la cita a eliminar.
     * @return 1 para éxito, -1 si no existe, 0 para error de conexión, -2 para error SQL.
     */
    public int eliminarCita(int idCita) {
        String sql = "{CALL sp_eliminar_cita(?, ?)}"; // Asume que el SP devuelve un INT como resultado
        // No instanciamos cs aquí, lo hacemos dentro del try para un manejo seguro.

        try {
            con = Conexion.getConnection();
            if (con == null) {
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudo eliminar la cita.");
                return 0; // Código 0: error de conexión
            }

            cs = con.prepareCall(sql); // Ahora se instancia aquí
            cs.setInt(1, idCita);
            cs.registerOutParameter(2, java.sql.Types.INTEGER); // Para capturar el resultado del SP

            cs.execute();
            int resultado = cs.getInt(2);
            return resultado; // 1: éxito, -1: no existe, etc. (según la lógica de tu SP)

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar cita mediante SP: " + e.getMessage(), e);
            return -2; // Código -2: error SQL
        } finally {
            closeResources(null, cs, con); // rs no se usa aquí
        }
    }

    /**
     * Actualiza la fecha, hora y estado de una cita existente.
     * Se usa para el formulario de edición. No se actualiza cliente ni veterinario desde aquí,
     * ya que UsuarioCitas es una vista. Si se necesitan actualizar, se pasaría sus IDs.
     * Asume un Stored Procedure `sp_actualizar_cita`.
     * @param cita El objeto UsuarioCitas con los datos actualizados (idCita, fecha, hora, estado).
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarCita(UsuarioCitas cita) {
        // Asumo un SP que actualiza la tabla Citas. Podrías pasar más parámetros si es necesario.
        // Se asume que el SP actualizará fecha, hora y estado, y quizás otros campos de Citas.
        String sql = "{CALL sp_actualizar_cita(?, ?, ?, ?, ?)}"; // Ejemplo: idCita, fecha, hora, estado, resultado_out
        CallableStatement csUpdate = null; // Usar un nombre diferente para evitar conflicto con 'cs' global
        boolean exito = false;

        try {
            con = Conexion.getConnection();
            if (con == null) {
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudo actualizar la cita.");
                return false;
            }

            csUpdate = con.prepareCall(sql);
            csUpdate.setInt(1, cita.getIdCita());
            csUpdate.setDate(2, cita.getFecha());
            csUpdate.setTime(3, cita.getHora());
            csUpdate.setString(4, cita.getEstado());
            csUpdate.registerOutParameter(5, java.sql.Types.BOOLEAN); // Asumo que el SP devuelve un BOOLEAN

            csUpdate.execute();
            exito = csUpdate.getBoolean(5); // Obtener el valor de retorno del SP

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar cita mediante SP: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            // Cierra recursos específicos de esta operación
            try {
                if (csUpdate != null) csUpdate.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error al cerrar recursos en actualizarCita: " + e.getMessage(), e);
            }
        }
        return exito;
    }

    /**
     * Método auxiliar para cerrar los recursos de la base de datos de manera segura.
     * Se puede llamar con null para recursos que no se usaron en una operación particular.
     */
    private void closeResources(ResultSet rs, PreparedStatement ps, Connection con) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close(); // ps o cs
            if (con != null) con.close(); 
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al cerrar recursos de la BD: " + e.getMessage(), e);
        }
    }
    
    // Sobrecarga para CallableStatement, ya que ps y cs son diferentes tipos
    private void closeResources(ResultSet rs, CallableStatement cs, Connection con) {
        try {
            if (rs != null) rs.close();
            if (cs != null) cs.close(); 
            if (con != null) con.close(); 
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al cerrar recursos de la BD: " + e.getMessage(), e);
        }
    }
}