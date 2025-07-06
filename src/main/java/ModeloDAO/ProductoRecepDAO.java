package ModeloDAO;

import Modelo.CarritoItem;
import Modelo.Producto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRecepDAO {

    private final Connection con;

    public ProductoRecepDAO(Connection con) {
        this.con = con;
    }

    public List<Producto> listarTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE estado=1 ORDER BY nombre_producto";
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Producto p = new Producto();
                p.setIdProducto(rs.getInt("id_producto"));
                p.setNombreProducto(rs.getString("nombre_producto"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setPrecio(rs.getBigDecimal("precio"));
                p.setStock(rs.getInt("stock"));
                p.setUnidadMedida(rs.getString("unidad_medida"));
                p.setImagen(rs.getString("imagen"));
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Producto obtenerPorId(int idProducto) {
        String sql = "SELECT * FROM Producto WHERE id_producto=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("id_producto"));
                    p.setNombreProducto(rs.getString("nombre_producto"));
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setPrecio(rs.getBigDecimal("precio"));
                    p.setStock(rs.getInt("stock"));
                    p.setUnidadMedida(rs.getString("unidad_medida"));
                    p.setImagen(rs.getString("imagen"));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean venderCarrito(int idRecepcionista, List<CarritoItem> carrito) {
        boolean exito = false;
        String actualizarStockSQL = "UPDATE Producto SET stock = stock - ? WHERE id_producto = ? AND stock >= ?";
        String insertarHistorialSQL = "INSERT INTO HistorialVentas (idRecepcionista, idCliente, idProducto, cantidad, total, estado, comprobante, tipoVenta)VALUES (?, NULL, ?, ?, ?, 'Completado', NULL, 'Recepcionista')";

        try {
            con.setAutoCommit(false);

            for (CarritoItem item : carrito) {
                // Verificar stock
                Producto p = obtenerPorId(item.getIdCarrito());
                if (p == null || p.getStock() < item.getCantidad()) {
                    con.rollback();
                    return false;
                }

                // Descontar stock
                try (PreparedStatement psStock = con.prepareStatement(actualizarStockSQL)) {
                    psStock.setInt(1, item.getCantidad());
                    psStock.setInt(2, item.getIdCarrito());
                    psStock.setInt(3, item.getCantidad());
                    int rows = psStock.executeUpdate();
                    if (rows == 0) {
                        con.rollback();
                        return false;
                    }
                }

                // Insertar en historial
                try (PreparedStatement psHist = con.prepareStatement(insertarHistorialSQL)) {
                    psHist.setInt(1, idRecepcionista);
                    psHist.setInt(2, item.getIdCarrito());
                    psHist.setInt(3, item.getCantidad());
                    psHist.setBigDecimal(4, item.getSubtotal());
                    psHist.executeUpdate();
                }
            }

            con.commit();
            exito = true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return exito;
    }
}
