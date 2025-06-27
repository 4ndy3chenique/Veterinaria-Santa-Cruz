package ModeloDAO;

import Modelo.UsuarioCitaRecep;
import Modelo.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class UsuarioCitaRecepDAO {

    public boolean insertar(UsuarioCitaRecep cita) {
        String sql = "INSERT INTO Usuariocitas (nombre_cliente, fecha, hora, veterinario, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cita.getNombreCliente());
            ps.setDate(2, cita.getFecha());
            ps.setTime(3, cita.getHora());
            ps.setString(4, cita.getVeterinario());
            ps.setString(5, cita.getEstado());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<UsuarioCitaRecep> listar() {
        List<UsuarioCitaRecep> lista = new ArrayList<>();
        String sql = "SELECT * FROM Usuariocitas ORDER BY fecha DESC, hora DESC";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                UsuarioCitaRecep c = new UsuarioCitaRecep();
                c.setIdCita(rs.getInt("id_cita"));
                c.setNombreCliente(rs.getString("nombre_cliente"));
                c.setFecha(rs.getDate("fecha"));
                c.setHora(rs.getTime("hora"));
                c.setVeterinario(rs.getString("veterinario"));
                c.setEstado(rs.getString("estado"));
                lista.add(c);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return lista;
    }

    public UsuarioCitaRecep obtenerPorId(int id) {
        UsuarioCitaRecep cita = null;
        String sql = "SELECT * FROM Usuariocitas WHERE id_cita = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cita = new UsuarioCitaRecep();
                    cita.setIdCita(rs.getInt("id_cita"));
                    cita.setNombreCliente(rs.getString("nombre_cliente"));
                    cita.setFecha(rs.getDate("fecha"));
                    cita.setHora(rs.getTime("hora"));
                    cita.setVeterinario(rs.getString("veterinario"));
                    cita.setEstado(rs.getString("estado"));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cita;
    }

    public boolean actualizar(UsuarioCitaRecep cita) {
        String sql = "UPDATE Usuariocitas SET nombre_cliente=?, fecha=?, hora=?, veterinario=?, estado=? WHERE id_cita=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cita.getNombreCliente());
            ps.setDate(2, cita.getFecha());
            ps.setTime(3, cita.getHora());
            ps.setString(4, cita.getVeterinario());
            ps.setString(5, cita.getEstado());
            ps.setInt(6, cita.getIdCita());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM Usuariocitas WHERE id_cita=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}