package ModeloDAO; // CORREGIDO: Usando el paquete DAO según tu estructura de carpetas

import Modelo.Conexion;
import Modelo.Veterinario; // Asegúrate de que esta importación sea correcta

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VeterinarioDAO {

    public VeterinarioDAO() {
    }

    public List<Veterinario> listarVeterinarios() {
        List<Veterinario> listaVeterinarios = new ArrayList<>();
        // Asegúrate de que los nombres de las columnas en tu DB son exactos:
        // idVeterinario, V_Nombre, V_Apellido, V_Numero, V_Dni, V_Especialidad
        String sql = "SELECT idVeterinario, V_Nombre, V_Apellido, V_Numero, V_Dni, V_Especialidad FROM veterinario";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                System.err.println("Error: No se pudo obtener la conexión a la base de datos en listarVeterinarios.");
                return listaVeterinarios;
            }

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Veterinario vet = new Veterinario();
                vet.setIdVeterinario(rs.getInt("idVeterinario"));
                // ¡¡¡CORRECCIÓN AQUÍ!!! Usar los setters del modelo que coincidan con el JSP
                vet.setNombre(rs.getString("V_Nombre"));
                vet.setApellido(rs.getString("V_Apellido"));
                vet.setNumero(rs.getString("V_Numero"));
                vet.setDni(rs.getString("V_Dni"));
                vet.setEspecialidad(rs.getString("V_Especialidad"));
                listaVeterinarios.add(vet);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar veterinarios: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos en listarVeterinarios: " + e.getMessage());
                e.printStackTrace();
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
                System.err.println("Error: No se pudo obtener la conexión a la base de datos en obtenerVeterinarioPorId.");
                return null;
            }

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                vet = new Veterinario();
                vet.setIdVeterinario(rs.getInt("idVeterinario"));
                // ¡¡¡CORRECCIÓN AQUÍ!!!
                vet.setNombre(rs.getString("V_Nombre"));
                vet.setApellido(rs.getString("V_Apellido"));
                vet.setNumero(rs.getString("V_Numero"));
                vet.setDni(rs.getString("V_Dni"));
                vet.setEspecialidad(rs.getString("V_Especialidad"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener veterinario por ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos en obtenerVeterinarioPorId: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return vet;
    }

    public boolean agregarVeterinario(Veterinario vet) {
        // Los nombres de las columnas en la DB son correctos
        String sql = "INSERT INTO veterinario (V_Nombre, V_Apellido, V_Numero, V_Dni, V_Especialidad) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        boolean exito = false;

        try {
            conn = Conexion.getConnection();
            if (conn == null) {
                System.err.println("Error: No se pudo obtener la conexión a la base de datos en agregarVeterinario.");
                return false;
            }

            ps = conn.prepareStatement(sql);
            // ¡¡¡CORRECCIÓN AQUÍ!!! Usar los getters del modelo que coincidan con el JSP
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
            System.err.println("Error al agregar veterinario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos en agregarVeterinario: " + e.getMessage());
                e.printStackTrace();
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
                System.err.println("Error: No se pudo obtener la conexión a la base de datos en actualizarVeterinario.");
                return false;
            }

            ps = conn.prepareStatement(sql);
            // ¡¡¡CORRECCIÓN AQUÍ!!!
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
            System.err.println("Error al actualizar veterinario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos en actualizarVeterinario: " + e.getMessage());
                e.printStackTrace();
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
                System.err.println("Error: No se pudo obtener la conexión a la base de datos en eliminarVeterinario.");
                return false;
            }

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar veterinario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos en eliminarVeterinario: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return exito;
    }
}