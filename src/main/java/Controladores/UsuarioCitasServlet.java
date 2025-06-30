package Controladores;

import ModeloDAO.UsuarioCitasDAO;
import Modelo.Veterinario;
import Modelo.UsuarioCliente;
import Modelo.UsuarioCitas; // *** CAMBIO: Usar tu clase UsuarioCitas ***
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.time.LocalDate; // Todavía se usa para la validación de entrada
import java.time.LocalTime; // Todavía se usa para la validación de entrada
import java.time.format.DateTimeParseException;
import java.sql.Date; // *** NUEVO: Importar java.sql.Date ***
import java.sql.Time; // *** NUEVO: Importar java.sql.Time ***

@WebServlet("/UsuarioCitasServlet")
public class UsuarioCitasServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UsuarioCitasServlet.class.getName());

    public UsuarioCitasServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Entrando al doGet de UsuarioCitasServlet.");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            LOGGER.warning("Usuario no logeado intentando acceder a Citas.jsp. Redirigiendo a login.");
            response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/login.jsp");
            return;
        }

        List<Veterinario> listaVeterinarios = null;
        try {
            UsuarioCitasDAO dao = new UsuarioCitasDAO();
            listaVeterinarios = dao.listarVeterinariosParaUsuarioCitas();

            if (listaVeterinarios != null) {
                LOGGER.info("Lista de veterinarios obtenida en doGet. Tamaño: " + listaVeterinarios.size());
                if (listaVeterinarios.isEmpty()) {
                    LOGGER.warning("La lista de veterinarios obtenida en doGet está vacía.");
                }
            } else {
                LOGGER.warning("La lista de veterinarios es NULL después de llamar al DAO en doGet.");
            }

            request.setAttribute("listaVeterinarios", listaVeterinarios);
            LOGGER.info("Lista de veterinarios establecida en el request scope para Citas.jsp.");

            request.getRequestDispatcher("/VistasWeb/VistasCliente/Citas.jsp").forward(request, response);
            LOGGER.info("Redirigiendo a /VistasWeb/VistasCliente/Citas.jsp");

        } catch (Exception e) {
            LOGGER.severe("Excepción en doGet de UsuarioCitasServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("mensaje", "error_inesperado");
            request.getRequestDispatcher("/VistasWeb/VistasCliente/Citas.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if ("registrar".equals(accion)) {
            LOGGER.info("Entrando al doPost de UsuarioCitasServlet para registrar cita.");

            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuario") == null) {
                LOGGER.warning("Intento de registrar cita sin usuario logeado en sesión. Redirigiendo a login.");
                response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/login.jsp");
                return;
            }

            UsuarioCliente usuarioObj = (UsuarioCliente) session.getAttribute("usuario");

            try {
                int idCliente = usuarioObj.getIdUsuario();
                String fechaStr = request.getParameter("fecha");
                String horaStr = request.getParameter("hora");
                String motivo = request.getParameter("motivo");
                
                int idVeterinario = 0;
                try {
                    idVeterinario = Integer.parseInt(request.getParameter("idVeterinario"));
                } catch (NumberFormatException e) {
                    LOGGER.severe("Error: idVeterinario no es un número válido: " + request.getParameter("idVeterinario"));
                    response.sendRedirect(request.getContextPath() + "/UsuarioCitasServlet?mensaje=error_formato_numero");
                    return;
                }

                // *** CAMBIO CRUCIAL: Validar con LocalDate/LocalTime, pero convertir a java.sql.Date/Time ***
                LocalDate fechaParaValidacion = LocalDate.parse(fechaStr);
                LocalTime horaParaValidacion = LocalTime.parse(horaStr);

                if (fechaParaValidacion.isBefore(LocalDate.now()) || (fechaParaValidacion.isEqual(LocalDate.now()) && horaParaValidacion.isBefore(LocalTime.now()))) {
                     response.sendRedirect(request.getContextPath() + "/UsuarioCitasServlet?mensaje=fecha_hora_pasada");
                     return;
                }
                
                // Convertir a java.sql.Date y java.sql.Time para el objeto UsuarioCitas
                Date fechaSQL = Date.valueOf(fechaParaValidacion);
                Time horaSQL = Time.valueOf(horaParaValidacion);

                // Instanciar tu objeto UsuarioCitas
                UsuarioCitas nuevaCita = new UsuarioCitas(0, idCliente, idVeterinario, fechaSQL, horaSQL, motivo, "Pendiente"); 

                UsuarioCitasDAO dao = new UsuarioCitasDAO();
                boolean exito = dao.registrarCita(nuevaCita); 

                if (exito) {
                    LOGGER.info("Cita registrada con éxito en la base de datos para Cliente ID: " + idCliente);
                    response.sendRedirect(request.getContextPath() + "/UsuarioCitasServlet?mensaje=registrado");
                } else {
                    LOGGER.warning("Fallo al registrar la cita en la base de datos para Cliente ID: " + idCliente);
                    response.sendRedirect(request.getContextPath() + "/UsuarioCitasServlet?mensaje=error_registro");
                }

            } catch (DateTimeParseException e) {
                LOGGER.severe("Error de formato de fecha u hora al registrar cita: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/UsuarioCitasServlet?mensaje=formato_invalido");
            } catch (Exception e) {
                LOGGER.severe("Excepción inesperada al registrar cita: " + e.getMessage());
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/UsuarioCitasServlet?mensaje=error_inesperado");
            }
        } else {
            LOGGER.warning("Acción desconocida en doPost de UsuarioCitasServlet: " + accion);
            doGet(request, response);
        }
    }
}