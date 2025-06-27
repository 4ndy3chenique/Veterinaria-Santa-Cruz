package ModeloDAO;

import Modelo.Cliente;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteDAO {
    private Connection conexion;
    private static final Logger LOGGER = Logger.getLogger(ClienteDAO.class.getName());

    public ClienteDAO(Connection conexion) {
        this.conexion = conexion;
    }

    // SP: Insertar cliente
    public boolean insertarCliente(Cliente cliente) throws SQLException {
        String sql = "{CALL sp_insertar_cliente(?, ?, ?, ?)}";

        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApellido());
            stmt.setString(3, cliente.getDni());
            stmt.setString(4, cliente.getTelefono());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al insertar cliente", e);
            throw e;
        }
    }

    // SP: Actualizar cliente
    public boolean actualizarCliente(Cliente cliente) throws SQLException {
        String sql = "{CALL sp_actualizar_cliente(?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setInt(1, cliente.getIdCliente());
            stmt.setString(2, cliente.getNombre());
            stmt.setString(3, cliente.getApellido());
            stmt.setString(4, cliente.getDni());
            stmt.setString(5, cliente.getTelefono());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar cliente", e);
            throw e;
        }
    }

    // SP: Eliminar cliente
    public boolean eliminarCliente(int id) throws SQLException {
        String sql = "{CALL sp_eliminar_cliente(?)}";

        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar cliente", e);
            throw e;
        }
    }

    // SP: Buscar clientes
    public List<Cliente> buscarClientes(String busqueda) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "{CALL sp_buscar_clientes(?)}";

        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setString(1, busqueda);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapearCliente(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar clientes", e);
            throw e;
        }

        return clientes;
    }

    // SP: Listar todos los clientes
    public List<Cliente> listarClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "{CALL sp_listar_clientes()}";

        try (CallableStatement stmt = conexion.prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar clientes", e);
            throw e;
        }

        return clientes;
    }

    // Método auxiliar
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("idCliente"));
        cliente.setNombre(rs.getString("Nombre"));
        cliente.setApellido(rs.getString("Apellido"));
        cliente.setDni(rs.getString("DNI"));
        cliente.setTelefono(rs.getString("Telefono"));
        cliente.setFechaRegistro(rs.getDate("FechaRegistro"));
        return cliente;
    }

    // Método directo (sin SP) si deseas obtener un cliente por ID
    public Cliente obtenerClientePorId(int id) throws SQLException {
        String sql = "SELECT * FROM Cliente WHERE idCliente = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapearCliente(rs) : null;
            }
        }
    }

    // Clientes recientes (sin SP, puedes convertirlo si quieres)
    public List<Cliente> listarClientesRecientes(int cantidad) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM Cliente ORDER BY idCliente DESC LIMIT ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, cantidad);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapearCliente(rs));
                }
            }
        }
        return clientes;
    }
}
