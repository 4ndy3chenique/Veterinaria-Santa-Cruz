package ModeloDAO;

import Modelo.Conexion;
import Modelo.UsuarioCliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UsuarioClienteDAO {
    private Connection conexion;

    // REGISTRAR CLIENTE (sin cambios)
    public boolean registrar(UsuarioCliente cliente) {
        String sql = "INSERT INTO UsuarioCliente (Nombre, Apellido, DNI, Telefono, Correo, Contrasena, FechaRegistro) "
                   + "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        try {
            conexion = Conexion.getConnection();
            PreparedStatement ps = conexion.prepareStatement(sql);
            
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getDni());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getCorreo());
            ps.setString(6, cliente.getContrasena());
            
            int resultado = ps.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario:");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Mensaje: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión:");
                e.printStackTrace();
            }
        }
    }

    // VALIDAR LOGIN CLIENTE (sin cambios)
    public UsuarioCliente validarUsuario(String correo, String contrasena) {
        String sql = "SELECT * FROM UsuarioCliente WHERE Correo = ? AND Contrasena = ?";
        UsuarioCliente usuario = null;
        
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new UsuarioCliente(
                        rs.getString("Nombre"),
                        rs.getString("Apellido"),
                        rs.getString("DNI"),
                        rs.getString("Telefono"),
                        rs.getString("Correo"),
                        rs.getString("Contrasena")
                    );
                    usuario.setIdUsuario(rs.getInt("idUsuario"));

                    Timestamp fecha = rs.getTimestamp("FechaRegistro");
                    if (fecha != null) {
                        usuario.setFechaRegistro(fecha.toLocalDateTime());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en validarUsuario:");
            e.printStackTrace();
        }
        return usuario;
    }

    // VERIFICAR DUPLICADO DE DNI (sin cambios)
    public boolean existeDNI(String dni) {
        String sql = "SELECT 1 FROM UsuarioCliente WHERE DNI = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // VERIFICAR DUPLICADO DE TELÉFONO (sin cambios)
    public boolean existeNumero(String telefono) {
        String sql = "SELECT 1 FROM UsuarioCliente WHERE Telefono = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, telefono);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // LISTAR USUARIOS RECIENTES (versión modificada sin rol)
    public List<UsuarioCliente> listarUsuariosRecientes(int cantidad) throws SQLException {
        List<UsuarioCliente> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM UsuarioCliente ORDER BY idUsuario DESC LIMIT ?";
        
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, cantidad);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UsuarioCliente usuario = new UsuarioCliente(
                        rs.getString("Nombre"),
                        rs.getString("Apellido"),
                        rs.getString("DNI"),
                        rs.getString("Telefono"),
                        rs.getString("Correo"),
                        rs.getString("Contrasena")
                    );
                    usuario.setIdUsuario(rs.getInt("idUsuario"));
                    
                    Timestamp fecha = rs.getTimestamp("FechaRegistro");
                    if (fecha != null) {
                        usuario.setFechaRegistro(fecha.toLocalDateTime());
                    }
                    
                    usuarios.add(usuario);
                }
            }
        }
        return usuarios;
    }
}