package ModeloDAO; // Asegúrate de que este paquete sea correcto según tu estructura de carpetas

import Modelo.Conexion;
import Modelo.Recepcionista;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level; // Importar para logging
import java.util.logging.Logger; // Importar para logging

public class RecepcionistaDAO {

    // Usar Logger en lugar de System.err.println para una mejor gestión de logs
    private static final Logger LOGGER = Logger.getLogger(RecepcionistaDAO.class.getName());

    public RecepcionistaDAO() {
        // Constructor
    }

    public List<Recepcionista> listarRecepcionistas() {
        List<Recepcionista> listaRecepcionistas = new ArrayList<>();
        // No selecciones la contraseña si no es estrictamente necesario para el listado
        String sql = "SELECT idRecepcionista, R_Nombre, R_Apellido, R_Numero, R_Dni, R_Correo FROM Recepcionista";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                 System.err.println("Error: No se pudo obtener la conexión a la base de datos en recepcionista.");
                return listaRecepcionistas;
            }

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Recepcionista rec = new Recepcionista();
                rec.setIdRecepcionista(rs.getInt("idRecepcionista"));
                rec.setNombre(rs.getString("R_Nombre"));
                rec.setApellido(rs.getString("R_Apellido"));
                rec.setNumero(rs.getString("R_Numero"));
                rec.setDni(rs.getString("R_dni"));
                rec.setCorreo(rs.getString("R_Correo"));
                listaRecepcionistas.add(rec);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar recepcionistas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
               System.err.println("Error al cerrar recursos en listarrecepcionista: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return listaRecepcionistas;
    }

    public Recepcionista validarRecepcionista(String correo, String contrasena) {
        String sql = "SELECT * FROM Recepcionista WHERE R_correo = ? AND R_Contrasena = ?";
        Recepcionista recepcionista = null;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    recepcionista = new Recepcionista();
                    recepcionista.setIdRecepcionista(rs.getInt("idRecepcionista"));
                    recepcionista.setNombre(rs.getString("R_Nombre"));
                    recepcionista.setApellido(rs.getString("R_Apellido"));
                    recepcionista.setNumero(rs.getString("R_Numero"));
                    recepcionista.setDni(rs.getString("R_Dni"));
                    recepcionista.setCorreo(rs.getString("R_Correo"));
                    recepcionista.setContrasena(rs.getString("R_Contrasena"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar recepcionista:");
            e.printStackTrace();
        }
        return recepcionista;
    }

    public Recepcionista obtenerRecepcionistaPorId(int id) {
        Recepcionista rec = null;
        String sql = "SELECT idRecepcionista, nombre, apellido, numero, dni, correo FROM recepcionista WHERE idRecepcionista = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Error: No se pudo obtener la conexión a la base de datos en obtenerRecepcionistaPorId. La conexión es nula.");
                return null;
            }

            LOGGER.log(Level.INFO, "Obteniendo recepcionista por ID: " + id);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                rec = new Recepcionista();
                rec.setIdRecepcionista(rs.getInt("idRecepcionista"));
                rec.setNombre(rs.getString("nombre"));
                rec.setApellido(rs.getString("apellido"));
                rec.setNumero(rs.getString("numero"));
                rec.setDni(rs.getString("dni"));
                rec.setCorreo(rs.getString("correo"));
                LOGGER.log(Level.INFO, "Recepcionista encontrado: " + rec.getNombre() + " " + rec.getApellido());
            } else {
                LOGGER.log(Level.INFO, "No se encontró recepcionista con ID: " + id);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener recepcionista por ID: " + e.getMessage(), e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error al cerrar recursos en obtenerRecepcionistaPorId: " + e.getMessage(), e);
            }
        }
        return rec;
    }

    public boolean agregarRecepcionista(Recepcionista rec) {
        String sql = "INSERT INTO recepcionista (nombre, apellido, numero, dni, correo, contrasena) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        boolean exito = false;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Error: No se pudo obtener la conexión a la base de datos en agregarRecepcionista. La conexión es nula.");
                return false;
            }

            LOGGER.log(Level.INFO, "Intentando agregar nuevo recepcionista: " + rec.getNombre() + " " + rec.getApellido());
            ps = conn.prepareStatement(sql);
            ps.setString(1, rec.getNombre());
            ps.setString(2, rec.getApellido());
            ps.setString(3, rec.getNumero());
            ps.setString(4, rec.getDni());
            ps.setString(5, rec.getCorreo());
            // ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡
            // ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡ ADVERTENCIA DE SEGURIDAD CRÍTICA ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡
            // NUNCA, BAJO NINGUNA CIRCUNSTANCIA, ALMACENES CONTRASEÑAS EN TEXTO PLANO EN LA BASE DE DATOS.
            // Esto es una VULNERABILIDAD GRAVE que puede comprometer la seguridad de tus usuarios.
            // Debes HASHEAR la contraseña ANTES de insertarla. Usa librerías como BCrypt, Argon2, o PBKDF2.
            // Ejemplo (necesitarías una clase PasswordHasher implementando BCrypt):
            // String hashedPassword = PasswordHasher.hash(rec.getContrasena());
            // ps.setString(6, hashedPassword);
            // ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡
            ps.setString(6, rec.getContrasena()); // Temporal e inseguro: ¡Cámbialo lo antes posible!

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                exito = true;
                LOGGER.log(Level.INFO, "Recepcionista agregado exitosamente: " + rec.getNombre() + " " + rec.getApellido());
            } else {
                LOGGER.log(Level.WARNING, "No se pudo agregar recepcionista: " + rec.getNombre());
            }
        } catch (SQLException e) {
            // Manejo de errores más específico para DNI/Correo duplicado (violación de UNIQUE constraint)
            if (e.getSQLState().startsWith("23")) {
                LOGGER.log(Level.WARNING, "Error al agregar recepcionista: DNI o correo duplicado. " + e.getMessage(), e);
            } else {
                LOGGER.log(Level.SEVERE, "Error al agregar recepcionista: " + e.getMessage(), e);
            }
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error al cerrar recursos en agregarRecepcionista: " + e.getMessage(), e);
            }
        }
        return exito;
    }

    public boolean actualizarRecepcionista(Recepcionista rec) {
        // No incluyas la contraseña en este UPDATE a menos que estés seguro de que siempre se actualizará aquí.
        // Si la contraseña se actualiza, es mejor usar el método actualizarContrasenaRecepcionista.
        String sql = "UPDATE recepcionista SET nombre=?, apellido=?, numero=?, dni=?, correo=? WHERE idRecepcionista=?";
        Connection conn = null;
        PreparedStatement ps = null;
        boolean exito = false;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Error: No se pudo obtener la conexión a la base de datos en actualizarRecepcionista. La conexión es nula.");
                return false;
            }

            LOGGER.log(Level.INFO, "Intentando actualizar recepcionista ID: " + rec.getIdRecepcionista());
            ps = conn.prepareStatement(sql);
            ps.setString(1, rec.getNombre());
            ps.setString(2, rec.getApellido());
            ps.setString(3, rec.getNumero());
            ps.setString(4, rec.getDni());
            ps.setString(5, rec.getCorreo());
            ps.setInt(6, rec.getIdRecepcionista());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                exito = true;
                LOGGER.log(Level.INFO, "Recepcionista actualizado exitosamente: " + rec.getNombre() + " " + rec.getApellido());
            } else {
                LOGGER.log(Level.WARNING, "No se pudo actualizar recepcionista con ID: " + rec.getIdRecepcionista);
            }
        } catch (SQLException e) {
            // Manejo de errores más específico para DNI/Correo duplicado en actualización
            if (e.getSQLState().startsWith("23")) {
                LOGGER.log(Level.WARNING, "Error al actualizar recepcionista: DNI o correo duplicado. " + e.getMessage(), e);
            } else {
                LOGGER.log(Level.SEVERE, "Error al actualizar recepcionista: " + e.getMessage(), e);
            }
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error al cerrar recursos en actualizarRecepcionista: " + e.getMessage(), e);
            }
        }
        return exito;
    }

    // Método específico para actualizar la contraseña (DEBE HASHEARSE)
    public boolean actualizarContrasenaRecepcionista(int idRecepcionista, String nuevaContrasena) {
        String sql = "UPDATE recepcionista SET contrasena=? WHERE idRecepcionista=?";
        Connection conn = null;
        PreparedStatement ps = null;
        boolean exito = false;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Error: No se pudo obtener la conexión a la base de datos en actualizarContrasenaRecepcionista. La conexión es nula.");
                return false;
            }

            LOGGER.log(Level.INFO, "Intentando actualizar contraseña para recepcionista ID: " + idRecepcionista);
            ps = conn.prepareStatement(sql);
            // ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡
            // ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡ ADVERTENCIA DE SEGURIDAD CRÍTICA ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡
            // NUNCA, BAJO NINGUNA CIRCUNSTANCIA, ALMACENES CONTRASEÑAS EN TEXTO PLANO EN LA BASE DE DATOS.
            // Esto es una VULNERABILIDAD GRAVE que puede comprometer la seguridad de tus usuarios.
            // Debes HASHEAR la contraseña ANTES de insertarla. Usa librerías como BCrypt, Argon2, o PBKDF2.
            // Ejemplo:
            // String hashedPassword = PasswordHasher.hash(nuevaContrasena);
            // ps.setString(1, hashedPassword);
            // ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡
            ps.setString(1, nuevaContrasena); // Temporal e inseguro: ¡Cámbialo lo antes posible!
            ps.setInt(2, idRecepcionista);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                exito = true;
                LOGGER.log(Level.INFO, "Contraseña de recepcionista ID " + idRecepcionista + " actualizada exitosamente.");
            } else {
                LOGGER.log(Level.WARNING, "No se pudo actualizar la contraseña para recepcionista ID: " + idRecepcionista);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar contraseña de recepcionista: " + e.getMessage(), e);
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error al cerrar recursos en actualizarContrasenaRecepcionista: " + e.getMessage(), e);
            }
        }
        return exito;
    }

    public boolean eliminarRecepcionista(int id) {
        String sql = "DELETE FROM recepcionista WHERE idRecepcionista = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        boolean exito = false;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Error: No se pudo obtener la conexión a la base de datos en eliminarRecepcionista. La conexión es nula.");
                return false;
            }

            LOGGER.log(Level.INFO, "Intentando eliminar recepcionista ID: " + id);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                exito = true;
                LOGGER.log(Level.INFO, "Recepcionista ID " + id + " eliminado exitosamente.");
            } else {
                LOGGER.log(Level.WARNING, "No se pudo eliminar recepcionista ID: " + id);
            }
        } catch (SQLException e) {
            // Manejo de errores más específico para eliminar si hay claves foráneas
            if (e.getSQLState().startsWith("23")) { // SQLSTATE para violación de integridad (ej. foreign key constraint)
                 LOGGER.log(Level.WARNING, "Error al eliminar recepcionista: Hay registros asociados (ej. citas, ventas). " + e.getMessage(), e);
            } else {
                 LOGGER.log(Level.SEVERE, "Error al eliminar recepcionista: " + e.getMessage(), e);
            }
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error al cerrar recursos en eliminarRecepcionista: " + e.getMessage(), e);
            }
        }
        return exito;
    }
}