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
    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;
    private static final Logger LOGGER = Logger.getLogger(UsuarioCitasDAO.class.getName());

    public List<Veterinario> listarVeterinariosParaUsuarioCitas() {
        List<Veterinario> lista = new ArrayList<>();
        String sql = "SELECT idVeterinario, V_Nombre, V_Apellido, V_Especialidad FROM veterinario ORDER BY V_Nombre";
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
            LOGGER.severe("Error SQL al listar veterinarios: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Asegúrate de cerrar los recursos en el orden inverso al que se abrieron
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                LOGGER.severe("Error al cerrar ResultSet en listarVeterinariosParaUsuarioCitas: " + e.getMessage());
            }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                LOGGER.severe("Error al cerrar PreparedStatement en listarVeterinariosParaUsuarioCitas: " + e.getMessage());
            }
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                LOGGER.severe("Error al cerrar Connection en listarVeterinariosParaUsuarioCitas: " + e.getMessage());
            }
        }
        return lista;
    }

    public boolean registrarCita(UsuarioCitas cita) {
        CallableStatement cs = null;
        // Ajusta esta cadena SQL para que coincida EXACTAMENTE con los parámetros de tu Stored Procedure.
        // Basado en tu ejemplo de `CALL sp_registrar_cita(1, 1, '2025-07-15', '10:00:00', 'Laura Gómez', 'Revisión anual', 'Pendiente');`
        // tu SP espera 7 parámetros. Si tu objeto UsuarioCitas no tiene 'nombreCliente', tendrás que obtenerlo.
        // POR AHORA, asumo que tienes 'nombreCliente' en tu objeto UsuarioCitas o lo obtendrás de algún lugar.
        // Si tu SP tiene solo 6 parámetros (sin 'nombreCliente'), usa "{CALL sp_registrar_cita(?, ?, ?, ?, ?, ?)}".
        String sql = "{CALL sp_registrar_cita(?, ?, ?, ?, ?, ?, ?)}"; // 7 placeholders para ID_Cliente, ID_Veterinario, Fecha, Hora, Nombre_Cliente, Motivo, Estado
        
        try {
            con = Conexion.getConnection();
            cs = con.prepareCall(sql);
            
            cs.setInt(1, cita.getIdUsuario());
            cs.setInt(2, cita.getIdVeterinario());
            cs.setDate(3, cita.getFecha());
            cs.setTime(4, cita.getHora());
            
            // Si el nombre del cliente se necesita, asegúrate de que esté en el objeto 'cita'.
            // Por ejemplo, si tienes un método getNombreCliente() en UsuarioCitas:
            // cs.setString(5, cita.getNombreCliente());
            // Si no lo tienes en UsuarioCitas y el SP lo necesita, deberías pasarlo desde el Servlet.
            // Para la demostración, usaré un valor fijo o ajusta según tu necesidad.
            // Si el nombre del cliente NO es un parámetro en tu SP, quita esta línea y ajusta el SQL a 6 '?'
            cs.setString(5, "NombreClienteTemporal"); // **Ajusta esto** Si tu SP necesita el nombre del cliente y no lo tienes en 'cita'. O quita si tu SP es de 6 parámetros.
            
            cs.setString(6, cita.getMotivo());
            cs.setString(7, cita.getEstado());

            int filasAfectadas = cs.executeUpdate();
            if (filasAfectadas > 0) {
                LOGGER.info("Cita registrada exitosamente en la BD mediante SP para cliente ID: " + cita.getIdUsuario());
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
            try {
                if (cs != null) cs.close();
            } catch (SQLException e) {
                LOGGER.severe("Error al cerrar CallableStatement en registrarCita (SP): " + e.getMessage());
            }
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                LOGGER.severe("Error al cerrar Connection en registrarCita (SP): " + e.getMessage());
            }
        }
    }

    // Nuevo método para listar citas por usuario (para historialdecitas.jsp)
    public List<UsuarioCitas> listarCitasPorUsuario(int idUsuario) {
        List<UsuarioCitas> listaCitas = new ArrayList<>();
        // Ajusta esta consulta SQL para seleccionar las citas del usuario.
        // Asegúrate de que las columnas coincidan con tu tabla 'Citas'.
        String sql = "SELECT idCita, idUsuario, idVeterinario, fechaCita, horaCita, motivo, estado FROM Citas WHERE idUsuario = ?"; 
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario); // Establece el ID del usuario como parámetro
            rs = ps.executeQuery();
            while (rs.next()) {
                UsuarioCitas cita = new UsuarioCitas();
                cita.setIdCita(rs.getInt("idCita"));
                cita.setIdUsuario(rs.getInt("idUsuario"));
                cita.setIdVeterinario(rs.getInt("idVeterinario"));
                cita.setFecha(rs.getDate("fechaCita"));
                cita.setHora(rs.getTime("horaCita"));
                cita.setMotivo(rs.getString("motivo"));
                cita.setEstado(rs.getString("estado"));
                // Si necesitas el nombre del veterinario o el cliente en el objeto cita,
                // deberías hacer un JOIN en la consulta SQL o cargar esos datos por separado.
                listaCitas.add(cita);
            }
            LOGGER.info("Se obtuvieron " + listaCitas.size() + " citas para el usuario con ID: " + idUsuario);
        } catch (SQLException e) {
            LOGGER.severe("Error SQL al listar citas por usuario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Asegúrate de cerrar los recursos en el orden inverso al que se abrieron
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                LOGGER.severe("Error al cerrar ResultSet en listarCitasPorUsuario: " + e.getMessage());
            }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                LOGGER.severe("Error al cerrar PreparedStatement en listarCitasPorUsuario: " + e.getMessage());
            }
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                LOGGER.severe("Error al cerrar Connection en listarCitasPorUsuario: " + e.getMessage());
            }
        }
        return listaCitas;
    }
    
    
}