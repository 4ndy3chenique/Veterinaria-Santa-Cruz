/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ModeloDAO;

import Modelo.Administrador;
import Modelo.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author andy9
 */
public class AdministradorDAO {
    public Administrador validarAdministrador(String correo, String contrasena) {
        String sql = "SELECT * FROM administrador WHERE A_Correo = ? AND A_Contrasena = ?";
        Administrador admin = null;
        
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    admin = new Administrador();
                    admin.setIdAdmin(rs.getInt("idAdmin"));
                    admin.setNombre(rs.getString("A_Nombre"));
                    admin.setApellido(rs.getString("A_Apellido"));
                    admin.setNumero(rs.getString("A_Numero"));
                    admin.setCorreo(rs.getString("A_Correo"));
                    admin.setContrasena(rs.getString("A_Contrasena"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar administrador:");
            e.printStackTrace();
        }
        return admin;
    }
}