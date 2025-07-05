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

    private static final Logger LOGGER = Logger.getLogger(UsuarioCitaRecepDAO.class.getName());

    /**
     * Lista todas las citas, incluyendo el nombre completo del cliente (Nombre + Apellido)
     * y el nombre del veterinario almacenado directamente en la tabla Citas, además del motivo.
     * @return Una lista de objetos UsuarioCitas.
     */
    public List<UsuarioCitas> listarCitasConDetalles() {
        List<UsuarioCitas> lista = new ArrayList<>();
        // ¡AJUSTADO! Ahora se llama al Stored Procedure
        String sql = "{CALL sp_listar_citas_detalles()}"; // Llama al SP sin parámetros

        Connection con = null;
        CallableStatement cs = null; // Usar CallableStatement para SPs
        ResultSet rs = null;

        try {
            con = Conexion.getConnection();
            if (con == null) {
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudieron listar las citas con detalles.");
                return lista;
            }
            cs = con.prepareCall(sql); // Prepara la llamada al SP
            rs = cs.executeQuery(); // Ejecuta el SP y obtiene el ResultSet

            while (rs.next()) {
                UsuarioCitas uc = new UsuarioCitas();
                uc.setIdCita(rs.getInt("id_cita"));
                uc.setNombreCliente(rs.getString("nombreCliente"));
                uc.setFecha(rs.getDate("fecha"));
                uc.setHora(rs.getTime("hora"));
                uc.setVeterinario(rs.getString("nombreVeterinario"));
                uc.setEstado(rs.getString("estado"));
                uc.setMotivo(rs.getString("motivo"));
                lista.add(uc);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar citas con detalles mediante SP: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            // Asegúrate de usar la sobrecarga correcta para CallableStatement
            closeResources(rs, cs, con);
        }
        return lista;
    }

    /**
     * Obtiene una cita específica con detalles (nombre completo del cliente,
     * nombre del veterinario de la tabla Citas, y motivo) por su ID de Cita.
     * @param idCita El ID de la cita a buscar.
     * @return El objeto UsuarioCitas si se encuentra, o null si no existe.
     */
    public UsuarioCitas obtenerCitaConDetallesPorId(int idCita) {
        UsuarioCitas uc = null;
        // ¡AJUSTADO EL NOMBRE DEL SP!
        String sql = "{CALL sp_obtener_usuario_cita_con_detalles_por_id(?)}"; // Nombre actualizado

        Connection con = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConnection();
            if (con == null) {
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudo obtener la cita con detalles.");
                return null;
            }
            cs = con.prepareCall(sql);
            cs.setInt(1, idCita);
            rs = cs.executeQuery();
            if (rs.next()) {
                uc = new UsuarioCitas();
                uc.setIdCita(rs.getInt("id_cita"));
                uc.setNombreCliente(rs.getString("nombreCliente"));
                uc.setFecha(rs.getDate("fecha"));
                uc.setHora(rs.getTime("hora"));
                uc.setVeterinario(rs.getString("nombreVeterinario"));
                uc.setEstado(rs.getString("estado"));
                uc.setMotivo(rs.getString("motivo"));
                uc.setIdUsuario(rs.getInt("idUsuario"));
                uc.setIdVeterinario(rs.getInt("idVeterinario"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener cita con detalles por ID mediante SP: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            closeResources(rs, cs, con);
        }
        return uc;
    }
    
    /**
     * Busca citas por un término dado (ej. por nombre de cliente, nombre de veterinario o estado).
     * Utiliza un Stored Procedure para mayor flexibilidad de búsqueda.
     * El SP debe devolver las mismas columnas que los métodos de listado/obtención,
     * incluyendo una columna 'nombreCliente' (concatenación de Nombre y Apellido del cliente)
     * y 'nombreVeterinario' (el campo 'veterinario' de la tabla UsuarioCitas).
     * @param busqueda El término de búsqueda.
     * @return Una lista de objetos UsuarioCitas que coinciden con la búsqueda.
     */
    // In your UsuarioCitaRecepDAO.java, inside the method buscarCitasConDetalles

    public List<UsuarioCitas> buscarCitasConDetalles(String busqueda) {
        List<UsuarioCitas> lista = new ArrayList<>();
        // ¡AJUSTADO EL NOMBRE DEL SP!
        String sql = "{CALL sp_buscar_usuario_citas_con_detalles(?)}"; // Nombre actualizado

        Connection con = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConnection();
            if (con == null) {
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudieron buscar citas con detalles.");
                return lista;
            }
            cs = con.prepareCall(sql);
            cs.setString(1, busqueda);
            rs = cs.executeQuery();

            while (rs.next()) {
                UsuarioCitas uc = new UsuarioCitas();
                uc.setIdCita(rs.getInt("id_cita"));
                uc.setNombreCliente(rs.getString("nombreCliente"));
                uc.setFecha(rs.getDate("fecha"));
                uc.setHora(rs.getTime("hora"));
                uc.setVeterinario(rs.getString("nombreVeterinario"));
                uc.setEstado(rs.getString("estado"));
                uc.setMotivo(rs.getString("motivo"));
                lista.add(uc);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar citas con detalles mediante SP: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            closeResources(rs, cs, con);
        }
        return lista;
    }


    /**
     * Elimina una cita por su ID.
     * Asume un Stored Procedure `sp_eliminar_cita` que elimina de la tabla UsuarioCitas.
     * @param idCita El ID de la cita a eliminar.
     * @return 1 para éxito, -1 si no existe, 0 para error de conexión, -2 para error SQL.
     */
    public int eliminarCita(int idCita) {
        // ¡AJUSTADO EL NOMBRE DEL SP!
        String sql = "{CALL sp_eliminar_usuario_cita(?, ?)}"; // Nombre actualizado

        Connection con = null;
        CallableStatement cs = null;

        try {
            con = Conexion.getConnection();
            if (con == null) {
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudo eliminar la cita.");
                return 0; // Código 0: error de conexión
            }

            cs = con.prepareCall(sql);
            cs.setInt(1, idCita);
            cs.registerOutParameter(2, java.sql.Types.INTEGER);

            cs.execute();
            int resultado = cs.getInt(2);
            return resultado;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar cita mediante SP: " + e.getMessage(), e);
            return -2; // Código -2: error SQL
        } finally {
            closeResources(null, cs, con);
        }
    }

    /**
     * Actualiza la fecha, hora, veterinario (nombre), motivo y estado de una cita existente.
     * Asume un Stored Procedure `sp_actualizar_cita` que actualiza la tabla UsuarioCitas.
     * @param cita El objeto UsuarioCitas con los datos actualizados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarCita(UsuarioCitas cita) {
        // ¡AJUSTADO EL NOMBRE DEL SP!
        // Asumo que el SP espera: id_cita, fecha, hora, idVeterinario, veterinario (nombre), motivo, estado
        String sql = "{CALL sp_actualizar_usuario_cita(?, ?, ?, ?, ?, ?, ?)}";

        Connection con = null;
        CallableStatement cs = null;
        boolean exito = false;

        try {
            con = Conexion.getConnection();
            if (con == null) {
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudo actualizar la cita.");
                return false;
            }

            cs = con.prepareCall(sql);
            cs.setInt(1, cita.getIdCita());
            cs.setDate(2, cita.getFecha());
            cs.setTime(3, cita.getHora());
            cs.setInt(4, cita.getIdVeterinario());
            cs.setString(5, cita.getVeterinario());
            cs.setString(6, cita.getMotivo());
            cs.setString(7, cita.getEstado());

            cs.execute();
            exito = true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar cita mediante SP: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            closeResources(null, cs, con);
        }
        return exito;
    }
    
    /**
     * Actualiza solo el estado de una cita existente.
     * Asume un Stored Procedure `sp_actualizar_estado_cita` que actualiza la tabla UsuarioCitas.
     * @param idCita El ID de la cita a actualizar.
     * @param nuevoEstado El nuevo estado de la cita.
     * @return 1 para éxito, 0 si no se encontró la cita, -1 para error SQL.
     */
    public int actualizarEstadoCita(int idCita, String nuevoEstado) {
        // ¡AJUSTADO EL NOMBRE DEL SP!
        String sql = "{CALL sp_actualizar_estado_usuario_cita(?, ?, ?)}"; // id_cita, nuevoEstado, resultado_out

        Connection con = null;
        CallableStatement cs = null;
        int resultado = 0;

        try {
            con = Conexion.getConnection();
            if (con == null) {
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudo actualizar el estado de la cita.");
                return 0;
            }

            cs = con.prepareCall(sql);
            cs.setInt(1, idCita);
            cs.setString(2, nuevoEstado);
            cs.registerOutParameter(3, java.sql.Types.INTEGER); // Asume que el SP devuelve un INT

            cs.execute();
            resultado = cs.getInt(3);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar estado de cita mediante SP: " + e.getMessage(), e);
            resultado = -1; // Error SQL
        } finally {
            closeResources(null, cs, con);
        }
        return resultado;
    }


    /**
     * Método auxiliar para cerrar los recursos de la base de datos de manera segura.
     * Se puede llamar con null para recursos que no se usaron en una operación particular.
     */
    private void closeResources(ResultSet rs, PreparedStatement ps, Connection con) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al cerrar recursos de la BD (PreparedStatement): " + e.getMessage(), e);
        }
    }
    
    // Sobrecarga para CallableStatement
    private void closeResources(ResultSet rs, CallableStatement cs, Connection con) {
        try {
            if (rs != null) rs.close();
            if (cs != null) cs.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al cerrar recursos de la BD (CallableStatement): " + e.getMessage(), e);
        }
    }
}