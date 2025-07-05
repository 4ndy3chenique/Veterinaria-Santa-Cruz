package ModeloDAO;

import Modelo.Producto;
import Modelo.Proveedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductoRecepDAO {

    private final Connection con;

    public ProductoRecepDAO(Connection con) {
        this.con = con;
    }

    // Método para listar todos los productos
    public List<Producto> listarTodos() {
        List<Producto> productos = new ArrayList<>();
        // El SP ahora debe seleccionar 'pr.razon_social AS nombre_proveedor'
        String sql = "{CALL sp_listar_productos()}";

        System.out.println("DEBUG DAO: Iniciando listarTodos(). Intentando ejecutar SP: " + sql);
        try (CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                System.out.println("DEBUG DAO: ResultSet vacío para sp_listar_productos(). No hay filas retornadas.");
                return productos;
            }

            int count = 0;
            while (rs.next()) {
                Producto p = mapearProducto(rs);
                productos.add(p);
                count++;
                System.out.println("DEBUG DAO: Producto leído del RS -> ID: " + p.getIdProducto() +
                                   ", Nombre: " + p.getNombreProducto() +
                                   ", Precio: " + p.getPrecio() +
                                   ", Stock: " + p.getStock() +
                                   ", Estado: " + p.getEstado() +
                                   ", Proveedor ID: " + p.getIdProveedor() +
                                   ", Imagen: " + p.getImagen() +
                                   ", FechaReg: " + (p.getFechaRegistro() != null ? p.getFechaRegistro().toString() : "NULL"));
            }
            System.out.println("DEBUG DAO: Finalizado listarTodos(). Total de productos mapeados: " + count);
        } catch (SQLException e) {
            System.err.println("ERROR DAO: Error SQLException al listar todos los productos: " + e.getMessage());
            e.printStackTrace();
            return productos;
        } catch (Exception e) {
            System.err.println("ERROR DAO: Error inesperado al listar todos los productos: " + e.getMessage());
            e.printStackTrace();
            return productos;
        }
        return productos;
    }

    // Método para buscar productos por nombre/descripción
    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> productos = new ArrayList<>();
        String sql = "{CALL sp_buscar_productos(?)}";

        System.out.println("DEBUG DAO: Iniciando buscarPorNombre() con búsqueda: '" + nombre + "'. Intentando ejecutar SP: " + sql);
        try (CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, "%" + nombre + "%");
            try (ResultSet rs = cs.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("DEBUG DAO: ResultSet vacío para sp_buscar_productos(). No se encontraron productos.");
                    return productos;
                }

                while (rs.next()) {
                    Producto p = mapearProducto(rs);
                    productos.add(p);
                    System.out.println("DEBUG DAO: Producto encontrado por búsqueda: ID=" + p.getIdProducto() + ", Nombre=" + p.getNombreProducto() + ", Estado=" + p.getEstado());
                }
                System.out.println("DEBUG DAO: Finalizado buscarPorNombre(). Total productos encontrados: " + productos.size());
            }
        } catch (SQLException e) {
            System.err.println("ERROR DAO: Error SQLException al buscar productos por nombre: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERROR DAO: Error inesperado al buscar productos por nombre: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    // Método para obtener un producto por ID
    public Producto obtenerPorId(int id) {
        String sql = "{CALL sp_obtener_producto(?)}";

        System.out.println("DEBUG DAO: Iniciando obtenerPorId() para ID: " + id + ". Intentando ejecutar SP: " + sql);
        try (CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Producto p = mapearProducto(rs);
                    System.out.println("DEBUG DAO: Producto obtenido por ID: ID=" + p.getIdProducto() + ", Nombre=" + p.getNombreProducto() + ", Estado=" + p.getEstado());
                    return p;
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR DAO: Error SQLException al obtener producto por ID: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERROR DAO: Error inesperado al obtener producto por ID: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("DEBUG DAO: Producto con ID " + id + " no encontrado o error en la consulta.");
        return null;
    }

    // Método para agregar un nuevo producto
    public boolean agregar(Producto producto) {
        String sql = "{CALL sp_insertar_producto(?, ?, ?, ?, ?, ?, ?, ?)}";

        System.out.println("DEBUG DAO: Iniciando agregar() para producto: " + producto.getNombreProducto());
        try (CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, producto.getNombreProducto());
            cs.setString(2, producto.getDescripcion());
            cs.setBigDecimal(3, producto.getPrecio());
            cs.setInt(4, producto.getStock());
            cs.setString(5, producto.getUnidadMedida());
            cs.setInt(6, producto.getEstado());
            cs.setInt(7, producto.getIdProveedor());
            cs.setString(8, producto.getImagen());

            boolean hasResultSet = cs.execute();
            if (hasResultSet) {
                try (ResultSet rs = cs.getResultSet()) {
                    if (rs.next()) {
                        producto.setIdProducto(rs.getInt("nuevo_id"));
                        System.out.println("DEBUG DAO: Producto agregado con nuevo ID: " + producto.getIdProducto());
                    }
                }
            }
            System.out.println("DEBUG DAO: Agregado de producto exitoso.");
            return true;
        } catch (SQLException e) {
            System.err.println("ERROR DAO: Error SQLException al agregar producto '" + producto.getNombreProducto() + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("ERROR DAO: Error inesperado al agregar producto '" + producto.getNombreProducto() + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método para actualizar un producto existente
    public boolean actualizar(Producto producto) {
        String sql = "{CALL sp_actualizar_producto(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        System.out.println("DEBUG DAO: Iniciando actualizar() para producto ID: " + producto.getIdProducto() + ", Nombre: " + producto.getNombreProducto());
        try (CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, producto.getIdProducto());
            cs.setString(2, producto.getNombreProducto());
            cs.setString(3, producto.getDescripcion());
            cs.setBigDecimal(4, producto.getPrecio());
            cs.setInt(5, producto.getStock());
            cs.setString(6, producto.getUnidadMedida());
            cs.setInt(7, producto.getEstado());
            cs.setInt(8, producto.getIdProveedor());
            cs.setString(9, producto.getImagen());

            cs.execute();
            System.out.println("DEBUG DAO: Producto ID " + producto.getIdProducto() + " actualizado correctamente.");
            return true;
        } catch (SQLException e) {
            System.err.println("ERROR DAO: Error SQLException al actualizar producto ID " + producto.getIdProducto() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("ERROR DAO: Error inesperado al actualizar producto ID " + producto.getIdProducto() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método para cambiar el estado de un producto (activar/desactivar)
    public boolean cambiarEstado(int idProducto, int nuevoEstado) {
        System.out.println("DEBUG DAO: Iniciando cambiarEstado() para producto ID " + idProducto + " a nuevo estado: " + nuevoEstado);
        if (nuevoEstado == 0) {
            String sql = "{CALL sp_eliminar_producto(?)}";
            try (CallableStatement cs = con.prepareCall(sql)) {
                cs.setInt(1, idProducto);
                cs.execute();
                System.out.println("DEBUG DAO: Producto ID " + idProducto + " desactivado a través de sp_eliminar_producto.");
                return true;
            } catch (SQLException e) {
                System.err.println("ERROR DAO: Error SQLException al desactivar producto ID " + idProducto + ": " + e.getMessage());
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                System.err.println("ERROR DAO: Error inesperado al desactivar producto ID " + idProducto + ": " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } else if (nuevoEstado == 1) {
            String sql = "UPDATE Producto SET estado = 1 WHERE id_producto = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idProducto);
                int filasAfectadas = ps.executeUpdate();
                boolean success = filasAfectadas > 0;
                System.out.println("DEBUG DAO: Producto ID " + idProducto + " activado directamente. Filas afectadas: " + filasAfectadas + ", Éxito: " + success);
                return success;
            } catch (SQLException e) {
                System.err.println("ERROR DAO: Error SQLException al reactivar producto ID " + idProducto + ": " + e.getMessage());
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                System.err.println("ERROR DAO: Error inesperado al reactivar producto ID " + idProducto + ": " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        System.out.println("DEBUG DAO: Estado no válido para cambiar. idProducto: " + idProducto + ", nuevoEstado: " + nuevoEstado);
        return false;
    }

    // Método para eliminar definitivamente un producto
    public boolean eliminarDefinitivo(int id) {
        String sql = "DELETE FROM Producto WHERE id_producto = ?";
        System.out.println("DEBUG DAO: Iniciando eliminarDefinitivo() para producto ID: " + id + ". Ejecutando: " + sql);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();
            boolean success = filasAfectadas > 0;
            System.out.println("DEBUG DAO: Producto ID " + id + " eliminado definitivamente. Filas afectadas: " + filasAfectadas + ", Éxito: " + success);
            return success;
        } catch (SQLException e) {
            System.err.println("ERROR DAO: Error SQLException al eliminar definitivamente producto ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("ERROR DAO: Error inesperado al eliminar definitivamente producto ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método para listar proveedores
    public List<Proveedor> listarProveedores() {
        List<Proveedor> proveedores = new ArrayList<>();
        // Asumo que tu sp_listar_proveedores también devuelve 'razon_social'
        String sql = "{CALL sp_listar_proveedores()}";

        System.out.println("DEBUG DAO: Iniciando listarProveedores(). Intentando ejecutar SP: " + sql);
        try (CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                System.out.println("DEBUG DAO: ResultSet vacío para sp_listar_proveedores(). No hay proveedores retornados.");
                return proveedores;
            }

            while (rs.next()) {
                Proveedor prov = new Proveedor();
                prov.setIdProveedor(rs.getInt("id_proveedor"));

                // ¡¡¡¡¡ CORRECCIÓN CLAVE AQUÍ !!!!!
                // Ahora lee 'razon_social' directamente desde el ResultSet
                prov.setRazonSocial(rs.getString("razon_social"));

                // Resto de campos opcionales del proveedor
                try { prov.setRuc(rs.getString("ruc")); } catch (SQLException e) { /* Campo 'ruc' no encontrado o NULL */ }
                try { prov.setDireccion(rs.getString("direccion")); } catch (SQLException e) { /* Campo 'direccion' no encontrado o NULL */ }
                try { prov.setCorreo(rs.getString("correo")); } catch (SQLException e) { /* Campo 'correo' no encontrado o NULL */ }
                try { prov.setEstado(rs.getInt("estado")); } catch (SQLException e) { /* Campo 'estado' no encontrado o NULL */ }
                try {
                    Timestamp ts = rs.getTimestamp("fecha_registro");
                    if (ts != null) {
                        prov.setFechaRegistro(ts.toLocalDateTime());
                    }
                } catch (SQLException e) { /* Campo 'fecha_registro' no encontrado o NULL */ }

                proveedores.add(prov);
                System.out.println("DEBUG DAO: Proveedor leído del RS -> ID: " + prov.getIdProveedor() +
                                   ", Razon Social: " + prov.getRazonSocial());
            }
            System.out.println("DEBUG DAO: Finalizado listarProveedores(). Total de proveedores mapeados: " + proveedores.size());
        } catch (SQLException e) {
            System.err.println("ERROR DAO: Error SQLException al listar proveedores: " + e.getMessage());
            e.printStackTrace();
            return proveedores;
        } catch (Exception e) {
            System.err.println("ERROR DAO: Error inesperado al listar proveedores: " + e.getMessage());
            e.printStackTrace();
            return proveedores;
        }
        return proveedores;
    }

    // Método auxiliar para mapear un ResultSet a un objeto Producto
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getInt("id_producto"));
        p.setNombreProducto(rs.getString("nombre_producto"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecio(rs.getBigDecimal("precio"));
        p.setStock(rs.getInt("stock"));
        p.setUnidadMedida(rs.getString("unidad_medida"));

        p.setImagen(rs.getString("imagen"));
        p.setEstado(rs.getInt("estado"));
        p.setIdProveedor(rs.getInt("id_proveedor"));

        Timestamp ts = rs.getTimestamp("fecha_registro");
        if (ts != null) {
            p.setFechaRegistro(ts);
        } else {
            System.err.println("Advertencia DAO: 'fecha_registro' es NULL para producto ID: " + p.getIdProducto());
            p.setFechaRegistro(null);
        }
        return p;
    }
}