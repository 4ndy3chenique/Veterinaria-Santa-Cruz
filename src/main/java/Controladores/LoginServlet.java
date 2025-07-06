package Controladores;

import Modelo.Administrador;
import Modelo.Recepcionista;
import Modelo.UsuarioCliente;
import ModeloDAO.AdministradorDAO;
import ModeloDAO.RecepcionistaDAO;
import ModeloDAO.UsuarioClienteDAO;
import java.io.IOException;
import java.io.PrintWriter;
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

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");
        String rol = request.getParameter("rol");

        HttpSession session = request.getSession();
        String jsonResponseString = ""; // Aquí construiremos la cadena JSON

        try {
            switch (rol) {
                case "Administrador":
                    AdministradorDAO adminDAO = new AdministradorDAO();
                    Administrador admin = adminDAO.validarAdministrador(correo, contrasena);
                    if (admin != null) {
                        session.setAttribute("username", admin);
                        String redirectUrl = request.getContextPath() + "/VistasWeb/VistasAdmin/AdminDash.jsp";
                        jsonResponseString = "{\"success\": true, \"redirect\": \"" + redirectUrl + "\"}";
                    } else {
                        jsonResponseString = "{\"success\": false, \"message\": \"Correo o contraseña incorrectos para Administrador.\"}";
                    }
                    break;

                case "Recepcionista":
                    RecepcionistaDAO recepDAO = new RecepcionistaDAO();
                    Recepcionista recepcionista = recepDAO.validarRecepcionista(correo, contrasena);
                    if (recepcionista != null) {
                        session.setAttribute("recepcionista", recepcionista);
                        String redirectUrl = request.getContextPath() + "/ClienteRServlet";
                        jsonResponseString = "{\"success\": true, \"redirect\": \"" + redirectUrl + "\"}";
                    } else {
                        jsonResponseString = "{\"success\": false, \"message\": \"Correo o contraseña incorrectos para Recepcionista.\"}";
                    }
                    break;

                case "Cliente":
                    UsuarioClienteDAO clienteDAO = new UsuarioClienteDAO();
                    UsuarioCliente cliente = clienteDAO.validarUsuario(correo, contrasena);
                    if (cliente != null) {
                        session.setAttribute("usuario", cliente);
                        String redirectUrl = request.getContextPath() + "/VistasWeb/VistasCliente/indexCliente.jsp";
                        jsonResponseString = "{\"success\": true, \"redirect\": \"" + redirectUrl + "\"}";
                    } else {
                        jsonResponseString = "{\"success\": false, \"message\": \"Correo o contraseña incorrectos para Cliente.\"}";
                    }
                    break;

                default:
                    jsonResponseString = "{\"success\": false, \"message\": \"Rol no válido seleccionado.\"}";
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponseString = "{\"success\": false, \"message\": \"Ocurrió un error en el servidor. Intente de nuevo.\"}";
        } finally {
            out.print(jsonResponseString);
            out.flush();
        }
    }
}