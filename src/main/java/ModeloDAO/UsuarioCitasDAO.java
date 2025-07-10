package ModeloDAO;

import Modelo.Conexion;
import Modelo.Veterinario;
import Modelo.UsuarioCitas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Date;
import java.sql.Time;

public class UsuarioCitasDAO {
    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;
    private CallableStatement cs;

    private static final Logger LOGGER = Logger.getLogger(UsuarioCitasDAO.class.getName());

    // --- Métodos existentes (mantener) ---

    public List<Veterinario> listarVeterinariosParaUsuarioCitas() {
        List<Veterinario> lista = new ArrayList<>();
        String sql = "SELECT idVeterinario, V_Nombre, V_Apellido, V_Especialidad FROM Veterinario ORDER BY V_Nombre";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Veterinario vet = new Veterinario();
                vet.setIdVeterinario(rs.getInt("idVeterinario"));
                vet.setNombre(rs.getString("V_Nombre"));
                vet.setApellido(rs.getString("V_Apellido"));
                vet.setEspecialidad(rs.getString("V_Especialidad"));
                lista.add(vet);
            }
            LOGGER.info("Se obtuvieron " + lista.size() + " veterinarios del DAO.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error SQL al listar veterinarios: " + e.getMessage(), e);
        } finally {
            closeResources(con, ps, rs);
        }
        return lista;
    }

    public String getNombreVeterinarioById(int idVeterinario) {
        String nombreVeterinario = null;
        String sql = "SELECT V_Nombre, V_Apellido FROM Veterinario WHERE idVeterinario = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idVeterinario);
            rs = ps.executeQuery();
            if (rs.next()) {
                nombreVeterinario = rs.getString("V_Nombre") + " " + rs.getString("V_Apellido");
                LOGGER.info("Nombre de veterinario encontrado para ID " + idVeterinario + ": " + nombreVeterinario);
            } else {
                LOGGER.warning("No se encontró veterinario con ID: " + idVeterinario);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error SQL al obtener nombre de veterinario por ID: " + e.getMessage(), e);
        } finally {
            closeResources(con, ps, rs);
        }
        return nombreVeterinario;
    }

    public int getIdVeterinarioByName(String nombreCompletoVeterinario) {
        int idVeterinario = -1;
        String[] partesNombre = nombreCompletoVeterinario.split(" ", 2);
        String nombre = partesNombre[0];
        String apellido = (partesNombre.length > 1) ? partesNombre[1] : "";

        String sql = "SELECT idVeterinario FROM Veterinario WHERE V_Nombre = ? AND V_Apellido = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            rs = ps.executeQuery();
            if (rs.next()) {
                idVeterinario = rs.getInt("idVeterinario");
                LOGGER.info("ID de veterinario encontrado para '" + nombreCompletoVeterinario + "': " + idVeterinario);
            } else {
                LOGGER.warning("No se encontró ID de veterinario para el nombre: '" + nombreCompletoVeterinario + "'");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error SQL al obtener ID de veterinario por nombre: " + e.getMessage(), e);
        } finally {
            closeResources(con, ps, rs);
        }
        return idVeterinario;
    }

    public boolean registrarCita(UsuarioCitas cita) {
        // Asumiendo que tu SP 'sp_registrar_cita' espera los 7 parámetros en el orden:
        // idUsuario INT, idVeterinario INT, fecha DATE, hora TIME, veterinario VARCHAR(100), motivo VARCHAR(50), estado VARCHAR(50)
        String sql = "{CALL sp_registrar_cita(?, ?, ?, ?, ?, ?, ?)}";

        try {
            con = Conexion.getConnection();
            cs = con.prepareCall(sql);

            cs.setInt(1, cita.getIdUsuario());
            cs.setInt(2, cita.getIdVeterinario());
            cs.setDate(3, cita.getFecha());
            cs.setTime(4, cita.getHora());
            cs.setString(5, cita.getVeterinario()); // Ahora la tabla UsuarioCitas tiene esta columna
            cs.setString(6, cita.getMotivo());
            cs.setString(7, cita.getEstado());

            int filasAfectadas = cs.executeUpdate();

            if (filasAfectadas > 0) {
                LOGGER.info("Cita registrada exitosamente en la BD mediante SP para cliente ID: " + cita.getIdUsuario() + ". Filas afectadas: " + filasAfectadas);
                return true;
            } else {
                LOGGER.warning("No se insertó la cita en la BD mediante SP. Filas afectadas: " + filasAfectadas);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error SQL al registrar cita mediante SP: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(con, cs, null);
        }
    }

    /**
     * Lista las citas de un usuario específico de la tabla UsuarioCitas.
     * Se ha corregido para usar los nombres de tabla y columna de tu DDL.
     * @param idUsuario El ID del usuario cuyas citas se quieren listar.
     * @return Una lista de objetos UsuarioCitas.
     */
    public List<UsuarioCitas> listarCitasPorUsuario(int idUsuario) {
        List<UsuarioCitas> listaCitas = new ArrayList<>();
        // CORRECTO: Tabla 'UsuarioCitas', columnas 'id_cita', 'idUsuario', 'idVeterinario', 'veterinario' (VARCHAR), etc.
        String sql = "SELECT id_cita, idUsuario, idVeterinario, fecha, hora, veterinario, motivo, estado FROM UsuarioCitas WHERE idUsuario = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                UsuarioCitas cita = new UsuarioCitas();
                cita.setIdCita(rs.getInt("id_cita"));
                cita.setIdUsuario(rs.getInt("idUsuario"));
                cita.setIdVeterinario(rs.getInt("idVeterinario"));
                cita.setFecha(rs.getDate("fecha"));
                cita.setHora(rs.getTime("hora"));
                cita.setVeterinario(rs.getString("veterinario")); // Directamente del campo VARCHAR
                cita.setMotivo(rs.getString("motivo"));
                cita.setEstado(rs.getString("estado"));

                listaCitas.add(cita);
            }
            LOGGER.info("Se obtuvieron " + listaCitas.size() + " citas para el usuario con ID: " + idUsuario);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error SQL al listar citas por usuario: " + e.getMessage(), e);
        } finally {
            closeResources(con, ps, rs);
        }
        return listaCitas;
    }

    /**
     * Actualiza una cita existente en la base de datos.
     * Se ha corregido para usar los nombres de tabla y columna de tu DDL.
     * Ahora actualiza BOTH idVeterinario (INT) and veterinario (VARCHAR).
     * @param cita El objeto UsuarioCitas con los datos actualizados de la cita.
     * @return true si la cita se actualizó con éxito, false en caso contrario.
     */
    public boolean actualizarCita(UsuarioCitas cita) {
        // CORRECTO: Tabla 'UsuarioCitas', columnas 'id_cita', 'idUsuario', 'idVeterinario', 'veterinario', 'motivo', 'estado'
        // Se actualizan tanto el ID del veterinario como el nombre del veterinario (VARCHAR)
        String sql = "UPDATE UsuarioCitas SET fecha = ?, hora = ?, idVeterinario = ?, veterinario = ?, motivo = ?, estado = ? WHERE id_cita = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setDate(1, cita.getFecha());
            ps.setTime(2, cita.getHora());
            ps.setInt(3, cita.getIdVeterinario()); // Parámetro para idVeterinario (INT)
            ps.setString(4, cita.getVeterinario()); // Parámetro para veterinario (VARCHAR)
            ps.setString(5, cita.getMotivo());
            ps.setString(6, cita.getEstado());
            ps.setInt(7, cita.getIdCita()); // El ID de la cita a actualizar (id_cita)

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                 LOGGER.info("Cita con ID " + cita.getIdCita() + " actualizada exitosamente. Filas afectadas: " + filasAfectadas);
                 return true;
            } else {
                 LOGGER.warning("No se encontró cita con ID " + cita.getIdCita() + " para actualizar o no hubo cambios.");
                 return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error SQL al actualizar cita: ID " + cita.getIdCita() + " - " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(con, ps, null);
        }
    }

    /**
     * Elimina una cita de la base de datos.
     * Se ha corregido para usar los nombres de tabla y columna de tu DDL.
     * @param idCita El ID de la cita a eliminar.
     * @return true si la cita se eliminó con éxito, false en caso contrario.
     */
    public boolean eliminarCita(int idCita) {
        // CORRECTO: Tabla 'UsuarioCitas', columna 'id_cita'
        String sql = "DELETE FROM UsuarioCitas WHERE id_cita = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idCita);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                 LOGGER.info("Cita con ID " + idCita + " eliminada exitosamente. Filas afectadas: " + filasAfectadas);
                 return true;
            } else {
                 LOGGER.warning("No se encontró cita con ID " + idCita + " para eliminar.");
                 return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error SQL al eliminar cita: ID " + idCita + " - " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(con, ps, null);
        }
    }

    /**
     * Método de utilidad para cerrar los recursos de la base de datos de manera segura.
     * @param conn La conexión a cerrar.
     * @param stmt El PreparedStatement o CallableStatement a cerrar.
     * @param rs El ResultSet a cerrar.
     */
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al cerrar ResultSet: " + e.getMessage(), e);
        }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al cerrar PreparedStatement/CallableStatement: " + e.getMessage(), e);
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al cerrar Connection: " + e.getMessage(), e);
        }
    }
}