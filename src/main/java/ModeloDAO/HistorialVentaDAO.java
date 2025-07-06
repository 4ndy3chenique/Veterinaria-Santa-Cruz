package ModeloDAO;

import Modelo.HistorialVenta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistorialVentaDAO {

    private final Connection con;

    public HistorialVentaDAO(Connection con) {
        this.con = con;
    }

    // Registrar una venta
    public boolean registrarVenta(HistorialVenta venta) {
        String sql = "INSERT INTO HistorialVentas "
                + "(idRecepcionista, idCliente, idProducto, cantidad, total, estado, comprobante, tipoVenta) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, venta.getIdUsuario()); // idRecepcionista o idCliente según el flujo
            ps.setNull(2, Types.INTEGER);       // Para este flujo solo recepcionista
            ps.setInt(3, venta.getIdProducto());
            ps.setInt(4, venta.getCantidad());
            ps.setBigDecimal(5, venta.getTotal());
            ps.setString(6, venta.getEstado());
            ps.setString(7, venta.getComprobante());
            ps.setString(8, venta.getTipoVenta());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Listar todas las ventas
    public List<HistorialVenta> listarTodas() {
        List<HistorialVenta> lista = new ArrayList<>();
        String sql =
            "SELECT hv.idVenta, " +
            "       COALESCE(r.R_Nombre, u.Nombre) AS nombreUsuario, " +
            "       p.nombre_producto AS nombreProducto, " +
            "       hv.cantidad, " +
            "       hv.total, " +
            "       hv.estado, " +
            "       hv.comprobante, " +
            "       hv.tipoVenta, " +
            "       hv.fecha_venta " +
            "FROM HistorialVentas hv " +
            "LEFT JOIN Recepcionista r ON hv.idRecepcionista = r.idRecepcionista " +
            "LEFT JOIN UsuarioCliente u ON hv.idCliente = u.idUsuario " +
            "JOIN Producto p ON hv.idProducto = p.id_producto " +
            "ORDER BY hv.fecha_venta DESC";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearHistorialVenta(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Listar ventas por tipo
    public List<HistorialVenta> listarPorTipo(String tipoVenta) {
        List<HistorialVenta> lista = new ArrayList<>();
        String sql =
            "SELECT hv.idVenta, " +
            "       COALESCE(r.R_Nombre, u.Nombre) AS nombreUsuario, " +
            "       p.nombre_producto AS nombreProducto, " +
            "       hv.cantidad, " +
            "       hv.total, " +
            "       hv.estado, " +
            "       hv.comprobante, " +
            "       hv.tipoVenta, " +
            "       hv.fecha_venta " +
            "FROM HistorialVentas hv " +
            "LEFT JOIN Recepcionista r ON hv.idRecepcionista = r.idRecepcionista " +
            "LEFT JOIN UsuarioCliente u ON hv.idCliente = u.idUsuario " +
            "JOIN Producto p ON hv.idProducto = p.id_producto " +
            "WHERE hv.tipoVenta = ? " +
            "ORDER BY hv.fecha_venta DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipoVenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearHistorialVenta(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Método para mapear ResultSet a objeto
    private HistorialVenta mapearHistorialVenta(ResultSet rs) throws SQLException {
        HistorialVenta hv = new HistorialVenta();
        hv.setIdVenta(rs.getInt("idVenta"));
        hv.setNombreUsuario(rs.getString("nombreUsuario"));
        hv.setNombreProducto(rs.getString("nombreProducto"));
        hv.setCantidad(rs.getInt("cantidad"));
        hv.setTotal(rs.getBigDecimal("total"));
        hv.setEstado(rs.getString("estado"));
        hv.setComprobante(rs.getString("comprobante"));
        hv.setTipoVenta(rs.getString("tipoVenta"));
        hv.setFechaVenta(rs.getTimestamp("fecha_venta"));
        return hv;
    }
}
