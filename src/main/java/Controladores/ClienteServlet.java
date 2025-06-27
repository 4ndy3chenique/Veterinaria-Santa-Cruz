package Controladores;

import Modelo.UsuarioCliente;
import ModeloDAO.UsuarioClienteDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ClienteServlet", urlPatterns = {"/ClienteServlet"})
public class ClienteServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8"); // evita errores con tildes
        String nombre = request.getParameter("nombres");
        String apellido = request.getParameter("apellidos");
        String dni = request.getParameter("dni");
        String telefono = request.getParameter("telefono");
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");

        UsuarioCliente cliente = new UsuarioCliente(nombre, apellido, dni, telefono, correo, contrasena);
        UsuarioClienteDAO dao = new UsuarioClienteDAO();

        if (dao.existeDNI(dni)) {
            request.setAttribute("error", "dni");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } else if (dao.existeNumero(telefono)) {
            request.setAttribute("error", "telefono");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } else if (dao.registrar(cliente)) {
            request.setAttribute("exito", "1");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "general");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }

    }
}
