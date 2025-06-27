package ModeloDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.Proveedor;
import Modelo.ContactoProveedor;
import Modelo.Producto;

public class ProveedorDAO {

    private Connection con;

    public ProveedorDAO(Connection con) {
        this.con = con;
    }

    public List<Proveedor> listar() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM Proveedor WHERE estado = 1";

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Proveedor p = mapearProveedor(rs);
                p.setContactos(obtenerContactosProveedor(p.getIdProveedor()));
                p.setProductos(obtenerProductosProveedor(p.getIdProveedor()));
                lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Proveedor> buscarPorNombreODireccion(String filtro) {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM Proveedor WHERE estado = 1 AND "
                + "(razon_social LIKE ? OR direccion LIKE ? OR ruc LIKE ? OR correo LIKE ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String criterio = "%" + filtro + "%";
            ps.setString(1, criterio);
            ps.setString(2, criterio);
            ps.setString(3, criterio);
            ps.setString(4, criterio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Proveedor p = mapearProveedor(rs);
                    p.setContactos(obtenerContactosProveedor(p.getIdProveedor()));
                    p.setProductos(obtenerProductosProveedor(p.getIdProveedor()));
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Proveedor> buscarPorNombreODireccionInactivos(String filtro) {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM Proveedor WHERE estado = 0 AND (razon_social LIKE ? OR direccion LIKE ? OR ruc LIKE ? OR correo LIKE ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String criterio = "%" + filtro + "%";
            ps.setString(1, criterio);
            ps.setString(2, criterio);
            ps.setString(3, criterio);
            ps.setString(4, criterio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Proveedor p = mapearProveedor(rs);
                    p.setContactos(obtenerContactosProveedor(p.getIdProveedor()));
                    p.setProductos(obtenerProductosProveedor(p.getIdProveedor()));
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Proveedor> listarInactivos() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM Proveedor WHERE estado = 0";

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Proveedor p = mapearProveedor(rs);
                p.setContactos(obtenerContactosProveedor(p.getIdProveedor()));
                p.setProductos(obtenerProductosProveedor(p.getIdProveedor()));
                lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean reactivar(int id) {
        String sql = "UPDATE Proveedor SET estado = 1 WHERE id_proveedor = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Proveedor obtenerPorId(int id) {
        String sql = "SELECT * FROM Proveedor WHERE id_proveedor = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Proveedor p = mapearProveedor(rs);
                    p.setContactos(obtenerContactosProveedor(p.getIdProveedor()));
                    p.setProductos(obtenerProductosProveedor(p.getIdProveedor()));
                    return p;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Proveedor obtenerPorRuc(String ruc) {
        String sql = "SELECT * FROM Proveedor WHERE ruc = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ruc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Proveedor p = mapearProveedor(rs);
                    p.setContactos(obtenerContactosProveedor(p.getIdProveedor()));
                    p.setProductos(obtenerProductosProveedor(p.getIdProveedor()));
                    return p;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean agregar(Proveedor p) {
        String sql = "INSERT INTO Proveedor (razon_social, ruc, direccion, correo, estado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getRazonSocial());
            ps.setString(2, p.getRuc());
            ps.setString(3, p.getDireccion());
            ps.setString(4, p.getCorreo());
            ps.setInt(5, p.getEstado());
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean actualizar(Proveedor p) {
        String sql = "UPDATE Proveedor SET razon_social=?, ruc=?, direccion=?, correo=?, estado=? WHERE id_proveedor=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getRazonSocial());
            ps.setString(2, p.getRuc());
            ps.setString(3, p.getDireccion());
            ps.setString(4, p.getCorreo());
            ps.setInt(5, p.getEstado());
            ps.setInt(6, p.getIdProveedor());
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE Proveedor SET estado = 0 WHERE id_proveedor = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private Proveedor mapearProveedor(ResultSet rs) throws SQLException {
        Proveedor p = new Proveedor();
        p.setIdProveedor(rs.getInt("id_proveedor"));
        p.setRazonSocial(rs.getString("razon_social"));
        p.setRuc(rs.getString("ruc"));
        p.setDireccion(rs.getString("direccion"));
        p.setCorreo(rs.getString("correo"));
        p.setEstado(rs.getInt("estado"));

        Timestamp timestamp = rs.getTimestamp("fecha_registro");
        if (timestamp != null) {
            p.setFechaRegistro(timestamp.toLocalDateTime());
        }
        return p;
    }

    private List<ContactoProveedor> obtenerContactosProveedor(int idProveedor) {
        List<ContactoProveedor> contactos = new ArrayList<>();
        String sql = "SELECT * FROM ContactoProveedor WHERE id_proveedor = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ContactoProveedor c = new ContactoProveedor();
                    c.setIdContacto(rs.getInt("id_contacto"));
                    c.setIdProveedor(rs.getInt("id_proveedor"));
                    c.setNombreContacto(rs.getString("nombre_contacto"));
                    c.setCargo(rs.getString("cargo"));
                    c.setTelefono(rs.getString("telefono"));
                    c.setCorreoContacto(rs.getString("correo_contacto"));
                    contactos.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contactos;
    }

    private List<Producto> obtenerProductosProveedor(int idProveedor) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE id_proveedor = ? AND estado = 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto prod = new Producto();
                    prod.setIdProducto(rs.getInt("id_producto"));
                    prod.setNombreProducto(rs.getString("nombre_producto"));
                    prod.setDescripcion(rs.getString("descripcion"));
                    prod.setPrecio(rs.getBigDecimal("precio"));
                    prod.setStock(rs.getInt("stock"));
                    prod.setUnidadMedida(rs.getString("unidad_medida"));
                    prod.setImagen(rs.getString("imagen"));
                    prod.setEstado(rs.getInt("estado"));
                    prod.setIdProveedor(rs.getInt("id_proveedor"));

                    Timestamp fecha = rs.getTimestamp("fecha_registro");
                    if (fecha != null) {
                        prod.setFechaRegistro(fecha); // ✅ ahora correcto
                    }

                    productos.add(prod);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return productos;
    }

}
