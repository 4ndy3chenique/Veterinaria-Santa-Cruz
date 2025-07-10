package Controladores;

import Modelo.UsuarioCliente;
import ModeloDAO.UsuarioClienteDAO;
import Modelo.Conexion; // NEW: Import for Connection
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection; // NEW: Import for Connection
import java.sql.SQLException; // NEW: Import for SQLException
import java.util.logging.Level; // NEW: For Logger
import java.util.logging.Logger; // NEW: For Logger

// Renamed the servlet class name for clarity (from ClienteServlet to UsuarioClienteServlet)
// The name attribute in @WebServlet should match the class name by convention,
// or at least be unique and descriptive.
@WebServlet(name = "UsuarioClienteServlet", urlPatterns = {"/UsuarioClienteServlet"})
public class UsuarioClienteServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UsuarioClienteServlet.class.getName());

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

        Connection conexion = null; // Declare connection here
        try {
            conexion = Conexion.getConnection(); // Get the connection
            UsuarioClienteDAO dao = new UsuarioClienteDAO(conexion); // Pass the connection to the DAO

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
                request.setAttribute("error", "general"); // General registration error
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de base de datos en UsuarioClienteServlet: " + e.getMessage(), e);
            request.setAttribute("error", "database_error"); // Indicate a database error
            request.setAttribute("errorMessage", "Ocurrió un error en la base de datos al intentar registrar: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response); // Forward to a dedicated error page
        } finally {
            if (conexion != null) {
                try {
                    conexion.close(); // Ensure the connection is closed
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error al cerrar la conexión en UsuarioClienteServlet:", e);
                }
            }
        }
    }
    
}   