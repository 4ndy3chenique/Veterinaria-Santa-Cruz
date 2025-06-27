package ModeloDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.ContactoProveedor;

public class ContactoProveedorDAO {

    private Connection con;

    public ContactoProveedorDAO(Connection con) {
        this.con = con;
    }

    // Listar contactos por proveedor
    public List<ContactoProveedor> listarPorProveedor(int idProveedor) {
        List<ContactoProveedor> lista = new ArrayList<>();
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
                    lista.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Agregar contacto
    public boolean agregarContacto(ContactoProveedor c) {
        String sql = "INSERT INTO ContactoProveedor (id_proveedor, nombre_contacto, cargo, telefono, correo_contacto) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getIdProveedor());
            ps.setString(2, c.getNombreContacto());
            ps.setString(3, c.getCargo());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getCorreoContacto());
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Actualizar contacto
    public boolean actualizarContacto(ContactoProveedor c) {
        String sql = "UPDATE ContactoProveedor SET nombre_contacto = ?, cargo = ?, telefono = ?, correo_contacto = ? WHERE id_contacto = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombreContacto());
            ps.setString(2, c.getCargo());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getCorreoContacto());
            ps.setInt(5, c.getIdContacto());
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Eliminar contacto por ID
    public boolean eliminarContacto(int idContacto) {
        String sql = "DELETE FROM ContactoProveedor WHERE id_contacto = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idContacto);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
