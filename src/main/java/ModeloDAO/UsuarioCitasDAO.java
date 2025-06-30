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
import java.util.logging.Logger;

public class UsuarioCitasDAO {
    // Variables para la conexión a la base de datos
    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;
    private CallableStatement cs; // Para Stored Procedures

    // Logger para depuración y seguimiento de errores
    private static final Logger LOGGER = Logger.getLogger(UsuarioCitasDAO.class.getName());

    /**
     * Lista todos los veterinarios disponibles para la selección de citas.
     * @return Una lista de objetos Veterinario.
     */
    public List<Veterinario> listarVeterinariosParaUsuarioCitas() {
        List<Veterinario> lista = new ArrayList<>();
        // Consulta SQL para obtener los datos básicos de los veterinarios
        String sql = "SELECT idVeterinario, V_Nombre, V_Apellido, V_Especialidad FROM Veterinario ORDER BY V_Nombre";
        try {
            con = Conexion.getConnection(); // Obtener la conexión
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery(); // Ejecutar la consulta

            // Iterar sobre los resultados y construir objetos Veterinario
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
            LOGGER.severe("Error SQL al listar veterinarios: " + e.getMessage());
            e.printStackTrace(); // Imprimir la traza completa del error para depuración
        } finally {
            closeResources(con, ps, rs); // Cerrar los recursos
        }
        return lista;
    }

    /**
     * Obtiene el nombre completo de un veterinario dado su ID.
     * @param idVeterinario El ID del veterinario.
     * @return El nombre completo del veterinario (Nombre Apellido) o null si no se encuentra.
     */
    public String getNombreVeterinarioById(int idVeterinario) {
        String nombreVeterinario = null;
        String sql = "SELECT V_Nombre, V_Apellido FROM Veterinario WHERE idVeterinario = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idVeterinario);
            rs = ps.executeQuery();
            if (rs.next()) {
                // Concatenar nombre y apellido para obtener el nombre completo
                nombreVeterinario = rs.getString("V_Nombre") + " " + rs.getString("V_Apellido");
                LOGGER.info("Nombre de veterinario encontrado para ID " + idVeterinario + ": " + nombreVeterinario);
            } else {
                LOGGER.warning("No se encontró veterinario con ID: " + idVeterinario);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error SQL al obtener nombre de veterinario por ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(con, ps, rs);
        }
        return nombreVeterinario;
    }

    /**
     * Registra una nueva cita en la base de datos utilizando un Stored Procedure.
     * @param cita El objeto UsuarioCitas con los datos de la cita.
     * @return true si la cita se registró con éxito, false en caso contrario.
     */
    public boolean registrarCita(UsuarioCitas cita) {
        // La cadena SQL para llamar a tu Stored Procedure
        // Asegúrate de que el número y tipo de placeholders (?) coincidan con los parámetros de tu SP.
        // sp_registrar_cita(idUsuario INT, idVeterinario INT, fecha DATE, hora TIME, veterinario VARCHAR(100), motivo VARCHAR(50), estado VARCHAR(50))
        String sql = "{CALL sp_registrar_cita(?, ?, ?, ?, ?, ?, ?)}";

        try {
            con = Conexion.getConnection();
            cs = con.prepareCall(sql); // Usar prepareCall para Stored Procedures

            // Establecer los parámetros del Stored Procedure
            cs.setInt(1, cita.getIdUsuario());
            cs.setInt(2, cita.getIdVeterinario());
            cs.setDate(3, cita.getFecha());
            cs.setTime(4, cita.getHora());
            cs.setString(5, cita.getVeterinario()); // Aquí se usa el nombre del veterinario ya en el objeto cita
            cs.setString(6, cita.getMotivo());
            cs.setString(7, cita.getEstado());

            int filasAfectadas = cs.executeUpdate(); // Ejecutar el SP

            if (filasAfectadas > 0) {
                LOGGER.info("Cita registrada exitosamente en la BD mediante SP para cliente ID: " + cita.getIdUsuario() + ". Filas afectadas: " + filasAfectadas);
                return true;
            } else {
                LOGGER.warning("No se insertó la cita en la BD mediante SP. Filas afectadas: " + filasAfectadas);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.severe("Error SQL al registrar cita mediante SP: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Asegurarse de cerrar CallableStatement y Connection
            closeResources(con, cs, null); // No hay ResultSet en este caso
        }
    }

    /**
     * Lista las citas de un usuario específico de la tabla UsuarioCitas.
     * @param idUsuario El ID del usuario cuyas citas se quieren listar.
     * @return Una lista de objetos UsuarioCitas.
     */
    public List<UsuarioCitas> listarCitasPorUsuario(int idUsuario) {
        List<UsuarioCitas> listaCitas = new ArrayList<>();
        // **CORREGIDO**: La consulta debe apuntar a la tabla y columnas correctas
        // de tu esquema de BD (UsuarioCitas, id_cita, fecha, hora, veterinario, etc.)
        String sql = "SELECT id_cita, idUsuario, idVeterinario, fecha, hora, veterinario, motivo, estado FROM UsuarioCitas WHERE idUsuario = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario); // Establece el ID del usuario como parámetro
            rs = ps.executeQuery();
            while (rs.next()) {
                UsuarioCitas cita = new UsuarioCitas();
                cita.setIdCita(rs.getInt("id_cita")); // Usar el nombre de columna de la DB
                cita.setIdUsuario(rs.getInt("idUsuario"));
                cita.setIdVeterinario(rs.getInt("idVeterinario"));
                cita.setFecha(rs.getDate("fecha"));   // Usar el nombre de columna de la DB
                cita.setHora(rs.getTime("hora"));     // Usar el nombre de columna de la DB
                cita.setVeterinario(rs.getString("veterinario")); // Obtener el nombre del veterinario de la columna 'veterinario'
                cita.setMotivo(rs.getString("motivo"));
                cita.setEstado(rs.getString("estado"));

                listaCitas.add(cita);
            }
            LOGGER.info("Se obtuvieron " + listaCitas.size() + " citas para el usuario con ID: " + idUsuario);
        } catch (SQLException e) {
            LOGGER.severe("Error SQL al listar citas por usuario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(con, ps, rs);
        }
        return listaCitas;
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
            LOGGER.severe("Error al cerrar ResultSet: " + e.getMessage());
        }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            LOGGER.severe("Error al cerrar PreparedStatement/CallableStatement: " + e.getMessage());
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            LOGGER.severe("Error al cerrar Connection: " + e.getMessage());
        }
    }
}