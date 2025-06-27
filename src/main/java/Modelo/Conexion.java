/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author andy9
 */
public class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/VeterinariaDB";
    private static final String USER = "root";
    private static final String PASSWORD = "123456789";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String connectionURL = URL + "?useSSL=false" +
                    "&useUnicode=true" +
                    "&characterEncoding=UTF-8" +
                    "&serverTimezone=UTC" +
                    "&allowPublicKeyRetrieval=true";
            return DriverManager.getConnection(connectionURL, USER, PASSWORD);
        } catch (Exception e) {
            System.err.println("Error al conectar a la BD:");
            e.printStackTrace();
            return null;
        }
    }
}
