package ModeloDAO;

import Modelo.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private final Connection con;

    public ProductoDAO(Connection con) {
        this.con = con;
    }

    public List<Producto> listarTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE estado = 1";

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    public List<Producto> listarInactivos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE estado = 0";

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    public List<Producto> buscarPorNombre(String nombre, boolean inactivos) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE nombre_producto LIKE ? AND estado = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            ps.setInt(2, inactivos ? 0 : 1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    public List<Producto> listarPorProveedor(int idProveedor) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE id_proveedor = ? AND estado = 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    public Producto obtenerPorId(int id) {
        String sql = "SELECT * FROM Producto WHERE id_producto = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean agregar(Producto producto) {
        String sql = "INSERT INTO Producto (nombre_producto, descripcion, precio, stock, unidad_medida, estado, fecha_registro, id_proveedor, imagen) VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, producto.getNombreProducto());
            ps.setString(2, producto.getDescripcion());
            ps.setBigDecimal(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            ps.setString(5, producto.getUnidadMedida());
            ps.setInt(6, producto.getEstado());
            ps.setInt(7, producto.getIdProveedor());
            ps.setString(8, producto.getImagen());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean actualizar(Producto producto) {
        String sql = "UPDATE Producto SET nombre_producto=?, descripcion=?, precio=?, stock=?, unidad_medida=?, estado=?, id_proveedor=?, imagen=? WHERE id_producto=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, producto.getNombreProducto());
            ps.setString(2, producto.getDescripcion());
            ps.setBigDecimal(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            ps.setString(5, producto.getUnidadMedida());
            ps.setInt(6, producto.getEstado());
            ps.setInt(7, producto.getIdProveedor());
            ps.setString(8, producto.getImagen());
            ps.setInt(9, producto.getIdProducto());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Eliminación lógica (estado = 0)
    public boolean eliminar(int id) {
        String sql = "UPDATE Producto SET estado = 0 WHERE id_producto = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Eliminación física permanente
    public boolean eliminarDefinitivo(int id) {
        String sql = "DELETE FROM Producto WHERE id_producto = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getInt("id_producto"));
        p.setNombreProducto(rs.getString("nombre_producto"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecio(rs.getBigDecimal("precio"));
        p.setStock(rs.getInt("stock"));
        p.setUnidadMedida(rs.getString("unidad_medida"));
        p.setEstado(rs.getInt("estado"));
        p.setIdProveedor(rs.getInt("id_proveedor"));
        p.setImagen(rs.getString("imagen"));
        p.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return p;
    }
}