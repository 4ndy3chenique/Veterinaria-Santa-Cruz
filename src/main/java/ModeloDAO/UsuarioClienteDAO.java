package ModeloDAO;

import Modelo.Conexion; // Asegúrate de que esta clase exista y funcione correctamente
import Modelo.UsuarioCliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime; // Necesario para toLocalDateTime()
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioClienteDAO {
    private Connection conexion;
    
    private static final Logger LOGGER = Logger.getLogger(UsuarioClienteDAO.class.getName());

    // Constructor para inyectar la conexión desde el servlet
    public UsuarioClienteDAO(Connection conexion) {
        this.conexion = conexion;
    }

    // Constructor por defecto (usar con precaución, abre/cierra conexión por cada método)
    public UsuarioClienteDAO() {
        // La conexión se obtendrá dentro de cada método si este constructor es usado.
    }

    // Método auxiliar para obtener la conexión
    // Esto centraliza la lógica de obtener la conexión inyectada o una nueva.
    private Connection obtenerConexion() throws SQLException {
        return (this.conexion != null) ? this.conexion : Conexion.getConnection();
    }

    // Método auxiliar para cerrar la conexión SOLO SI FUE ABIERTA POR EL DAO
    private void cerrarConexion(Connection con) {
        if (this.conexion == null && con != null) { // Solo cierra si no fue inyectada
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al cerrar conexión DAO: " + e.getMessage(), e);
            }
        }
    }

    // REGISTRAR CLIENTE
    public boolean registrar(UsuarioCliente cliente) {
        String sql = "INSERT INTO UsuarioCliente (Nombre, Apellido, DNI, Telefono, Correo, Contrasena, FechaRegistro) "
                   + "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        Connection con = null;
        try {
            con = obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getDni());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getCorreo());
            ps.setString(6, cliente.getContrasena());
            
            int resultado = ps.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar usuario: " + e.getMessage(), e);
            return false;
        } finally {
            cerrarConexion(con);
        }
    }
    
    // VALIDAR LOGIN CLIENTE
    public UsuarioCliente validarUsuario(String correo, String contrasena) throws SQLException {
        String sql = "SELECT * FROM UsuarioCliente WHERE Correo = ? AND Contrasena = ?";
        UsuarioCliente usuario = null;
        
        Connection con = null;
        try {
            con = obtenerConexion();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, correo);
                ps.setString(2, contrasena);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        usuario = mapearUsuarioCliente(rs);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error en validarUsuario: " + e.getMessage(), e);
            throw e;
        } finally {
            cerrarConexion(con);
        }
        return usuario;
    }

    // VERIFICAR DUPLICADO DE DNI
    public boolean existeDNI(String dni) throws SQLException {
        String sql = "SELECT 1 FROM UsuarioCliente WHERE DNI = ?";
        Connection con = null;
        try {
            con = obtenerConexion();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, dni);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error en existeDNI: " + e.getMessage(), e);
            throw e;
        } finally {
            cerrarConexion(con);
        }
    }

    // VERIFICAR DUPLICADO DE TELÉFONO
    public boolean existeNumero(String telefono) throws SQLException {
        String sql = "SELECT 1 FROM UsuarioCliente WHERE Telefono = ?";
        Connection con = null;
        try {
            con = obtenerConexion();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, telefono);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error en existeNumero: " + e.getMessage(), e);
            throw e;
        } finally {
            cerrarConexion(con);
        }
    }
    
    // LISTAR TODOS LOS USUARIOS (PARA EL REPORTE PDF) [NUEVO MÉTODO]
    public List<UsuarioCliente> listarUsuarioClientes() throws SQLException {
        List<UsuarioCliente> usuarios = new ArrayList<>();
        String sql = "SELECT idUsuario, Nombre, Apellido, DNI, Telefono, Correo, Contrasena, FechaRegistro FROM UsuarioCliente ORDER BY idUsuario ASC";
        
        Connection con = null;
        try {
            con = obtenerConexion();
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    usuarios.add(mapearUsuarioCliente(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar todos los usuarios clientes: " + e.getMessage(), e);
            throw e;
        } finally {
            cerrarConexion(con);
        }
        return usuarios;
    }

    // LISTAR USUARIOS RECIENTES
    public List<UsuarioCliente> listarUsuariosRecientes(int cantidad) throws SQLException {
        List<UsuarioCliente> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM UsuarioCliente ORDER BY idUsuario DESC LIMIT ?";
        
        Connection con = null;
        try {
            con = obtenerConexion();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, cantidad);
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        usuarios.add(mapearUsuarioCliente(rs));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar usuarios recientes: " + e.getMessage(), e);
            throw e;
        } finally {
            cerrarConexion(con);
        }
        return usuarios;
    }

    // Método auxiliar para mapear ResultSet a UsuarioCliente
    private UsuarioCliente mapearUsuarioCliente(ResultSet rs) throws SQLException {
        UsuarioCliente usuario = new UsuarioCliente(); // Asumo que tienes un constructor sin parámetros
        usuario.setIdUsuario(rs.getInt("idUsuario"));
        usuario.setNombre(rs.getString("Nombre"));
        usuario.setApellido(rs.getString("Apellido"));
        usuario.setDni(rs.getString("DNI"));
        usuario.setTelefono(rs.getString("Telefono"));
        usuario.setCorreo(rs.getString("Correo"));
        usuario.setContrasena(rs.getString("Contrasena"));
        
        Timestamp fecha = rs.getTimestamp("FechaRegistro");
        if (fecha != null) {
            usuario.setFechaRegistro(fecha.toLocalDateTime()); // Si FechaRegistro es LocalDateTime
        } else {
            // Manejar caso donde FechaRegistro sea nulo si es posible
            usuario.setFechaRegistro(null); // O un valor por defecto
        }
        return usuario;
    }

    // Método para obtener un UsuarioCliente por ID
    public UsuarioCliente obtenerUsuarioClientePorId(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM UsuarioCliente WHERE idUsuario = ?";
        UsuarioCliente usuario = null;
        Connection con = null;
        try {
            con = obtenerConexion();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        usuario = mapearUsuarioCliente(rs);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuario cliente por ID: " + e.getMessage(), e);
            throw e;
        } finally {
            cerrarConexion(con);
        }
        return usuario;
    }

    // Método para actualizar la información de un UsuarioCliente existente
    public boolean actualizarUsuarioCliente(UsuarioCliente usuario) throws SQLException {
        // IMPORTANTE: Considera tener dos métodos o un SP que maneje la actualización de contraseña
        // solo si se proporciona, para evitar sobrescribir con nulo o vacío por error.
        String sql = "UPDATE UsuarioCliente SET Nombre=?, Apellido=?, DNI=?, Telefono=?, Correo=?, Contrasena=? WHERE idUsuario=?";
        Connection con = null;
        try {
            con = obtenerConexion();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getApellido());
                ps.setString(3, usuario.getDni());
                ps.setString(4, usuario.getTelefono());
                ps.setString(5, usuario.getCorreo());
                ps.setString(6, usuario.getContrasena()); // ¡ATENCIÓN! Usar contraseña hasheada aquí.
                ps.setInt(7, usuario.getIdUsuario());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar usuario cliente: " + e.getMessage(), e);
            throw e;
        } finally {
            cerrarConexion(con);
        }
    }

    // Método para eliminar un UsuarioCliente por su ID
    public boolean eliminarUsuarioCliente(int idUsuario) throws SQLException {
        String sql = "DELETE FROM UsuarioCliente WHERE idUsuario = ?";
        Connection con = null;
        try {
            con = obtenerConexion();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar usuario cliente: " + e.getMessage(), e);
            throw e;
        } finally {
            cerrarConexion(con);
        }
    }

    // Método para buscar UsuarioClientes por un término de búsqueda
    public List<UsuarioCliente> buscarUsuariosClientes(String busqueda) throws SQLException {
        List<UsuarioCliente> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM UsuarioCliente WHERE Nombre LIKE ? OR Apellido LIKE ? OR DNI LIKE ? OR Correo LIKE ?";
        Connection con = null;
        try {
            con = obtenerConexion();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                String searchTerm = "%" + busqueda + "%";
                ps.setString(1, searchTerm);
                ps.setString(2, searchTerm);
                ps.setString(3, searchTerm);
                ps.setString(4, searchTerm);
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        usuarios.add(mapearUsuarioCliente(rs));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar usuarios clientes: " + e.getMessage(), e);
            throw e;
        } finally {
            cerrarConexion(con);
        }
        return usuarios;
    }
}