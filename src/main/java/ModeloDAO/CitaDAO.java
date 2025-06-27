package ModeloDAO; // CORREGIDO: El paquete debe ser 'DAO'

import Modelo.Conexion;
import Modelo.Cita;
import Modelo.Cliente; // Necesario para el método listarClientesParaDropdown
import static Modelo.Conexion.getConnection;
import Modelo.Veterinario; // Necesario para el método listarVeterinariosParaDropdown
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date; // Usando java.sql.Date
import java.sql.Time; // Usando java.sql.Time
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CitaDAO {

    private static final Logger LOGGER = Logger.getLogger(CitaDAO.class.getName());

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // Método para agregar una nueva Cita
    public boolean agregarCita(Cita cita) {
        String sql = "INSERT INTO Citas (idCliente, idVeterinario, fecha, hora, motivo, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            con = Conexion.getConnection();
            if (con == null) {
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudo agregar la cita.");
                return false;
            }
            ps = con.prepareStatement(sql);
            ps.setInt(1, cita.getIdCliente());
            ps.setInt(2, cita.getIdVeterinario());
            ps.setDate(3, cita.getFecha());
            ps.setTime(4, cita.getHora());
            ps.setString(5, cita.getMotivo());
            ps.setString(6, cita.getEstado()); // Establecer el estado
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al agregar cita: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources();
        }
    }

    // Método para listar todas las Citas con detalles de cliente y veterinario
    public List<Cita> listarCitas() {
        List<Cita> lista = new ArrayList<>();
        // Consulta SQL con JOINs para obtener nombres de cliente y veterinario, y la especialidad
        // Asegúrate de que los alias (AS NombreCliente, AS DniCliente, etc.) coincidan con lo que tu objeto Cita espera en sus setters (setNombreCliente, setDniCliente)
        String sql = "SELECT c.idCita, c.idCliente, c.idVeterinario, c.fecha, c.hora, c.motivo, c.estado, " +
                     "cl.Nombre AS NombreCliente, cl.Apellido AS ApellidoCliente, cl.DNI AS DniCliente, " + // Asume cl.DNI es la columna en la tabla Cliente
                     "v.V_Nombre AS NombreVeterinario, v.V_Apellido AS ApellidoVeterinario, v.V_Especialidad " +
                     "FROM Citas c " +
                     "JOIN Cliente cl ON c.idCliente = cl.IdCliente " +
                     "JOIN Veterinario v ON c.idVeterinario = v.IdVeterinario " +
                     "ORDER BY c.fecha DESC, c.hora DESC";

        try {
            con = Conexion.getConnection();
            if (con == null) {
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudieron listar las citas.");
                return lista;
            }
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Cita cita = new Cita();
                cita.setIdCita(rs.getInt("idCita"));
                cita.setIdCliente(rs.getInt("idCliente"));
                cita.setIdVeterinario(rs.getInt("idVeterinario"));
                cita.setFecha(rs.getDate("fecha"));
                cita.setHora(rs.getTime("hora"));
                cita.setMotivo(rs.getString("motivo"));
                cita.setEstado(rs.getString("estado"));

                cita.setNombreCliente(rs.getString("NombreCliente"));
                cita.setApellidoCliente(rs.getString("ApellidoCliente"));
                cita.setDniCliente(rs.getString("DniCliente"));
                cita.setNombreVeterinario(rs.getString("NombreVeterinario"));
                cita.setApellidoVeterinario(rs.getString("ApellidoVeterinario"));
                cita.setEspecialidadVeterinario(rs.getString("V_Especialidad"));

                lista.add(cita);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar citas: " + e.getMessage(), e);
            e.printStackTrace(); // Agregado para mejor depuración
        } finally {
            closeResources();
        }
        return lista;
    }
 
    // Método para obtener una Cita por ID con detalles de cliente y veterinario
    public Cita obtenerCitaPorId(int id) {
        Cita cita = null;
        String sql = "SELECT c.idCita, c.idCliente, c.idVeterinario, c.fecha, c.hora, c.motivo, c.estado, " +
                     "cl.Nombre AS NombreCliente, cl.Apellido AS ApellidoCliente, cl.DNI AS DniCliente, " + // Asume cl.DNI es la columna en la tabla Cliente
                     "v.V_Nombre AS NombreVeterinario, v.V_Apellido AS ApellidoVeterinario, v.V_Especialidad " +
                     "FROM Citas c " +
                     "JOIN Cliente cl ON c.idCliente = cl.IdCliente " +
                     "JOIN Veterinario v ON c.idVeterinario = v.IdVeterinario " +
                     "WHERE c.idCita = ?";
        try {
            con = Conexion.getConnection();
            if (con == null) {
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudo obtener la cita.");
                return null;
            }
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                cita = new Cita();
                cita.setIdCita(rs.getInt("idCita"));
                cita.setIdCliente(rs.getInt("idCliente"));
                cita.setIdVeterinario(rs.getInt("idVeterinario"));
                cita.setFecha(rs.getDate("fecha"));
                cita.setHora(rs.getTime("hora"));
                cita.setMotivo(rs.getString("motivo"));
                cita.setEstado(rs.getString("estado"));

                cita.setNombreCliente(rs.getString("NombreCliente"));
                cita.setApellidoCliente(rs.getString("ApellidoCliente"));
                cita.setDniCliente(rs.getString("DniCliente"));
                cita.setNombreVeterinario(rs.getString("NombreVeterinario"));
                cita.setApellidoVeterinario(rs.getString("ApellidoVeterinario"));
                cita.setEspecialidadVeterinario(rs.getString("V_Especialidad"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener cita por ID: " + e.getMessage(), e);
            e.printStackTrace(); // Agregado para mejor depuración
        } finally {
            closeResources();
        }
        return cita;
    }

    // Método para actualizar una Cita existente
    public boolean actualizarCita(Cita cita) {
        String sql = "UPDATE Citas SET idCliente=?, idVeterinario=?, fecha=?, hora=?, motivo=?, estado=? WHERE idCita=?";
        try {
            con = Conexion.getConnection();
            if (con == null) {
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudo actualizar la cita.");
                return false;
            }
            ps = con.prepareStatement(sql);
            ps.setInt(1, cita.getIdCliente());
            ps.setInt(2, cita.getIdVeterinario());
            ps.setDate(3, cita.getFecha());
            ps.setTime(4, cita.getHora());
            ps.setString(5, cita.getMotivo());
            ps.setString(6, cita.getEstado());
            ps.setInt(7, cita.getIdCita());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar cita: " + e.getMessage(), e);
            e.printStackTrace(); // Agregado para mejor depuración
            return false;
        } finally {
            closeResources();
        }
    }

    // Método para eliminar una Cita con store procedure
   public int eliminarCita(int id) {
    String sql = "{CALL sp_eliminar_cita(?, ?)}";
    CallableStatement cs = null;

    try {
        con = Conexion.getConnection();
        if (con == null) {
            LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudo eliminar la cita.");
            return 0; // Código 0: error de conexión
        }

        cs = con.prepareCall(sql);
        cs.setInt(1, id);
        cs.registerOutParameter(2, java.sql.Types.INTEGER);

        cs.execute();
        int resultado = cs.getInt(2);
        return resultado; // 1: éxito, -1: no existe

    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error al eliminar cita mediante SP: " + e.getMessage(), e);
        return -2; // Código -2: error SQL
    } finally {
        try {
            if (cs != null) cs.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al cerrar recursos: " + e.getMessage(), e);
        }
    }
}

    // --- Métodos para cargar listas de Clientes y Veterinarios para los dropdowns ---
    public List<Cliente> listarClientesParaDropdown() {
        List<Cliente> lista = new ArrayList<>();
        // Asegúrate de que los nombres de las columnas en tu DB son exactos: IdCliente, Nombre, Apellido, DNI
        String sql = "SELECT IdCliente, Nombre, Apellido, DNI FROM Cliente ORDER BY Apellido, Nombre";
        try {
            con = Conexion.getConnection();
            if (con == null) { // Agregar verificación de conexión nula
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudieron listar clientes para dropdown.");
                return lista;
            }
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setIdCliente(rs.getInt("IdCliente"));
                cliente.setNombre(rs.getString("Nombre")); // Asume que Modelo.Cliente tiene setNombre
                cliente.setApellido(rs.getString("Apellido")); // Asume que Modelo.Cliente tiene setApellido
                cliente.setDni(rs.getString("DNI")); // Asume que Modelo.Cliente tiene setDni
                lista.add(cliente);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar clientes para dropdown: " + e.getMessage(), e);
            e.printStackTrace(); // Agregado para mejor depuración
        } finally {
            closeResources();
        }
        return lista;
    }

    public List<Veterinario> listarVeterinariosParaDropdown() {
        List<Veterinario> lista = new ArrayList<>();
        // Asegúrate de que los nombres de las columnas en tu DB son exactos: IdVeterinario, V_Nombre, V_Apellido, V_Especialidad
        String sql = "SELECT IdVeterinario, V_Nombre, V_Apellido, V_Especialidad FROM Veterinario ORDER BY V_Apellido, V_Nombre";
        try {
            con = Conexion.getConnection();
            if (con == null) { // Agregar verificación de conexión nula
                LOGGER.log(Level.SEVERE, "La conexión a la BD es nula. No se pudieron listar veterinarios para dropdown.");
                return lista;
            }
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Veterinario vet = new Veterinario();
                vet.setIdVeterinario(rs.getInt("IdVeterinario"));
                // ¡¡¡CORRECCIÓN AQUÍ!!! Usar los setters que coincidan con las propiedades del modelo (nombre, apellido, especialidad)
                vet.setNombre(rs.getString("V_Nombre"));
                vet.setApellido(rs.getString("V_Apellido"));
                vet.setEspecialidad(rs.getString("V_Especialidad"));
                lista.add(vet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar veterinarios para dropdown: " + e.getMessage(), e);
            e.printStackTrace(); // Agregado para mejor depuración
        } finally {
            closeResources();
        }
        return lista;
    }

    // Método auxiliar para cerrar recursos
    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close(); // Cierra la conexión si no es parte de un pool gestionado externamente
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al cerrar recursos de la BD: " + e.getMessage(), e);
        }
    }

    private Cliente mapearCita(ResultSet rs) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private static class conexion {

        private static PreparedStatement prepareStatement(String sql) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        public conexion() {
        }
    }
    
    
    
    //Buscar cita con store procedure
    public List<Cita> buscarCitas(String busqueda) {
    List<Cita> citas = new ArrayList<>();
    String sql = "{CALL sp_buscar_citas(?)}"; // Llamada al SP

    try (Connection conn = getConnection();
         CallableStatement stmt = conn.prepareCall(sql)) {

        stmt.setString(1, busqueda); // Enviamos el parámetro

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Cita cita = new Cita();
                cita.setIdCita(rs.getInt("idCita"));
                cita.setIdCliente(rs.getInt("idCliente"));
                cita.setIdVeterinario(rs.getInt("idVeterinario"));
                cita.setFecha(rs.getDate("fecha"));
                cita.setHora(rs.getTime("hora"));
                cita.setMotivo(rs.getString("motivo"));
                cita.setEstado(rs.getString("estado"));

                // Datos adicionales del JOIN
                cita.setNombreCliente(rs.getString("nombre_cliente"));
                cita.setApellidoCliente(rs.getString("apellido_cliente"));
                cita.setNombreVeterinario(rs.getString("nombre_veterinario"));
                cita.setApellidoVeterinario(rs.getString("apellido_veterinario"));
                cita.setEspecialidadVeterinario(rs.getString("especialidad_veterinario"));

                citas.add(cita);
            }
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error al buscar citas con stored procedure", e);
    }

    return citas;
}

      
}