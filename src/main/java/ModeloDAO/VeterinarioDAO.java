package ModeloDAO; // Asume que esta es la ruta correcta del paquete

import Modelo.Conexion;
import Modelo.Veterinario; // Asegúrate de que esta importación sea correcta y la clase Veterinario exista

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VeterinarioDAO {

    private static final Logger LOGGER = Logger.getLogger(VeterinarioDAO.class.getName());

    public VeterinarioDAO() {
        // Constructor vacío, puedes añadir inicialización si es necesaria
    }

    public List<Veterinario> listarVeterinarios() {
        List<Veterinario> listaVeterinarios = new ArrayList<>();
        // Asegúrate de que los nombres de las columnas en tu DB son exactos:
        // idVeterinario, V_Nombre, V_Apellido, V_Numero, V_Dni, V_Especialidad
        String sql = "SELECT idVeterinario, V_Nombre, V_Apellido, V_Numero, V_Dni, V_Especialidad FROM veterinario ORDER BY V_Nombre, V_Apellido";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Error: No se pudo obtener la conexión a la base de datos en listarVeterinarios.");
                return listaVeterinarios; // Retorna lista vacía si no hay conexión
            }

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Veterinario vet = new Veterinario();
                vet.setIdVeterinario(rs.getInt("idVeterinario"));
                
                // **** CLAVE: Concatenar nombre y apellido para el setter 'nombre' ****
                // También establecemos el apellido y la especialidad por separado si se necesitan
                vet.setNombre(rs.getString("V_Nombre")); 
                vet.setApellido(rs.getString("V_Apellido")); 
                vet.setNumero(rs.getString("V_Numero"));
                vet.setDni(rs.getString("V_Dni"));
                vet.setEspecialidad(rs.getString("V_Especialidad"));
                
                listaVeterinarios.add(vet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error SQL al listar veterinarios: " + e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception e) { // Captura cualquier otra excepción
            LOGGER.log(Level.SEVERE, "Error inesperado al listar veterinarios: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al cerrar recursos en listarVeterinarios: " + e.getMessage(), e);
            }
        }
        return listaVeterinarios;
    }

    public Veterinario obtenerVeterinarioPorId(int id) {
        Veterinario vet = null;
        String sql = "SELECT idVeterinario, V_Nombre, V_Apellido, V_Numero, V_Dni, V_Especialidad FROM veterinario WHERE idVeterinario = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Error: No se pudo obtener la conexión a la base de datos en obtenerVeterinarioPorId.");
                return null;
            }

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                vet = new Veterinario();
                vet.setIdVeterinario(rs.getInt("idVeterinario"));
                vet.setNombre(rs.getString("V_Nombre")); 
                vet.setApellido(rs.getString("V_Apellido")); 
                vet.setNumero(rs.getString("V_Numero"));
                vet.setDni(rs.getString("V_Dni"));
                vet.setEspecialidad(rs.getString("V_Especialidad"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error SQL al obtener veterinario por ID: " + e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al obtener veterinario por ID: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al cerrar recursos en obtenerVeterinarioPorId: " + e.getMessage(), e);
            }
        }
        return vet;
    }

    public boolean agregarVeterinario(Veterinario vet) {
        String sql = "INSERT INTO veterinario (V_Nombre, V_Apellido, V_Numero, V_Dni, V_Especialidad) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        boolean exito = false;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Error: No se pudo obtener la conexión a la base de datos en agregarVeterinario.");
                return false;
            }

            ps = conn.prepareStatement(sql);
            ps.setString(1, vet.getNombre()); 
            ps.setString(2, vet.getApellido()); 
            ps.setString(3, vet.getNumero());
            ps.setString(4, vet.getDni());
            ps.setString(5, vet.getEspecialidad());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error SQL al agregar veterinario: " + e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al agregar veterinario: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al cerrar recursos en agregarVeterinario: " + e.getMessage(), e);
            }
        }
        return exito;
    }

    public boolean actualizarVeterinario(Veterinario vet) {
        String sql = "UPDATE veterinario SET V_Nombre=?, V_Apellido=?, V_Numero=?, V_Dni=?, V_Especialidad=? WHERE idVeterinario=?";
        Connection conn = null;
        PreparedStatement ps = null;
        boolean exito = false;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Error: No se pudo obtener la conexión a la base de datos en actualizarVeterinario.");
                return false;
            }

            ps = conn.prepareStatement(sql);
            ps.setString(1, vet.getNombre()); 
            ps.setString(2, vet.getApellido()); 
            ps.setString(3, vet.getNumero());
            ps.setString(4, vet.getDni());
            ps.setString(5, vet.getEspecialidad());
            ps.setInt(6, vet.getIdVeterinario());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error SQL al actualizar veterinario: " + e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al actualizar veterinario: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al cerrar recursos en actualizarVeterinario: " + e.getMessage(), e);
            }
        }
        return exito;
    }

    public boolean eliminarVeterinario(int id) {
        String sql = "DELETE FROM veterinario WHERE idVeterinario = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        boolean exito = false;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Error: No se pudo obtener la conexión a la base de datos en eliminarVeterinario.");
                return false;
            }

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error SQL al eliminar veterinario: " + e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al eliminar veterinario: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al cerrar recursos en eliminarVeterinario: " + e.getMessage(), e);
            }
        }
        return exito;
    }
}