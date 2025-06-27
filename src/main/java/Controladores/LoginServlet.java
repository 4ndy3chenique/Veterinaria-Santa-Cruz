package Controladores;

import Modelo.Administrador;
import Modelo.Recepcionista;
import Modelo.UsuarioCliente;
import ModeloDAO.AdministradorDAO;
import ModeloDAO.RecepcionistaDAO;
import ModeloDAO.UsuarioClienteDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");
        String rol = request.getParameter("rol");

        HttpSession session = request.getSession();
        String redirectPage = "index.jsp?errorLogin=1"; 

        try {
            switch (rol) {
                case "Administrador":
                    AdministradorDAO adminDAO = new AdministradorDAO();
                    Administrador admin = adminDAO.validarAdministrador(correo, contrasena);
                    if (admin != null) {
                        session.setAttribute("usuario", admin);
                        session.setAttribute("rol", "administrador");
                        redirectPage = request.getContextPath() + "/VistasWeb/VistasAdmin/AdminDash.jsp";
                    }
                    break;

                case "Recepcionista":
                    RecepcionistaDAO recepDAO = new RecepcionistaDAO();
                    Recepcionista recepcionista = recepDAO.validarRecepcionista(correo, contrasena);
                    if (recepcionista != null) {
                        session.setAttribute("usuario", recepcionista);
                        session.setAttribute("rol", "recepcionista");
                        redirectPage = request.getContextPath() + "/VistasWeb/VistasRecep/RecepDash.jsp";
                    }
                    break;

                case "Cliente":
                    UsuarioClienteDAO clienteDAO = new UsuarioClienteDAO();
                    UsuarioCliente cliente = clienteDAO.validarUsuario(correo, contrasena);
                    if (cliente != null) {
                        session.setAttribute("usuario", cliente);
                        session.setAttribute("rol", "cliente");
                        // Redirige al indexCliente.jsp
                        redirectPage = request.getContextPath() + "/VistasWeb/VistasCliente/indexCliente.jsp";
                    }
                    break;

                default:
                    // Unknown role
                    redirectPage = "index.jsp?errorLogin=1";
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectPage = "index.jsp?errorLogin=1";
        }

        response.sendRedirect(redirectPage);
    }
}
