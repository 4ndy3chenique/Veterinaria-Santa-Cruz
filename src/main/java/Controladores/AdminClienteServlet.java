package Controladores;

import ModeloDAO.ClienteDAO;
import ModeloDAO.UsuarioClienteDAO;
import Modelo.Cliente;
import Modelo.UsuarioCliente;
import Modelo.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "AdminClienteServlet", urlPatterns = {"/AdminClienteServlet"})
public class AdminClienteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Connection conexion = null;
        try {
            conexion = Conexion.getConnection();
            
            // Obtener lista de clientes normales
            ClienteDAO clienteDAO = new ClienteDAO(conexion);
            List<Cliente> listaClientes = clienteDAO.listarClientes();
            request.setAttribute("listaClientes", listaClientes);
            
            // Obtener lista de usuarios clientes
            UsuarioClienteDAO usuarioDAO = new UsuarioClienteDAO();
            List<UsuarioCliente> listaUsuarios = usuarioDAO.listarUsuariosRecientes(100); // Ajusta el número según necesites
            
            request.setAttribute("listaUsuarios", listaUsuarios);
            
            request.getRequestDispatcher("/VistasWeb/VistasAdmin/ListadoCLientes.jsp")
                   .forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error de base de datos: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } finally {
            if (conexion != null) {
                try {
                    conexion.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}