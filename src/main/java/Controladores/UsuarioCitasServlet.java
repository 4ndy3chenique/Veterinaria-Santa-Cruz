package Controladores;

import ModeloDAO.UsuarioCitasDAO;
import Modelo.Veterinario;
import Modelo.UsuarioCliente;
import Modelo.UsuarioCitas;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.sql.Date;   // Para java.sql.Date
import java.sql.Time;   // Para java.sql.Time

@WebServlet("/UsuarioCitasServlet")
public class UsuarioCitasServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UsuarioCitasServlet.class.getName());

    // Instancia del DAO: se inicializa una vez cuando el servlet se carga.
    private UsuarioCitasDAO usuarioCitasDAO;

    // Método init se ejecuta una vez cuando el servlet es inicializado
    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioCitasDAO = new UsuarioCitasDAO(); // Inicializar el DAO
        LOGGER.info("UsuarioCitasServlet inicializado y UsuarioCitasDAO creado.");
    }

    public UsuarioCitasServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Entrando al doGet de UsuarioCitasServlet.");
        HttpSession session = request.getSession(false); // No crear una nueva sesión si no existe

        // Verificar si el usuario está logeado
        if (session == null || session.getAttribute("usuario") == null) {
            LOGGER.warning("Usuario no logeado intentando acceder. Redirigiendo a login.");
            response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/login.jsp");
            return;
        }

        // Obtener la lista de veterinarios para el dropdown en Citas.jsp
        List<Veterinario> listaVeterinarios = null;
        try {
            listaVeterinarios = usuarioCitasDAO.listarVeterinariosParaUsuarioCitas();

            if (listaVeterinarios != null) {
                LOGGER.info("Lista de veterinarios obtenida en doGet. Tamaño: " + listaVeterinarios.size());
                if (listaVeterinarios.isEmpty()) {
                    LOGGER.warning("La lista de veterinarios obtenida en doGet está vacía.");
                }
            } else {
                LOGGER.warning("La lista de veterinarios es NULL después de llamar al DAO en doGet.");
            }

            // Establecer la lista en el request scope para que Citas.jsp pueda acceder a ella
            request.setAttribute("listaVeterinarios", listaVeterinarios);
            LOGGER.info("Lista de veterinarios establecida en el request scope para Citas.jsp.");

            // Redirigir a la página de citas
            request.getRequestDispatcher("/VistasWeb/VistasCliente/Citas.jsp").forward(request, response);
            LOGGER.info("Redirigiendo a /VistasWeb/VistasCliente/Citas.jsp");

        } catch (Exception e) {
            LOGGER.severe("Excepción en doGet de UsuarioCitasServlet: " + e.getMessage());
            e.printStackTrace(); // Imprimir la traza completa del error
            request.setAttribute("mensaje", "error_inesperado");
            request.getRequestDispatcher("/VistasWeb/VistasCliente/Citas.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        LOGGER.info("Acción recibida en doPost: " + accion);

        if ("registrar".equals(accion)) {
            LOGGER.info("Procesando acción 'registrar' cita.");

            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuario") == null) {
                LOGGER.warning("Intento de registrar cita sin usuario logeado en sesión. Redirigiendo a login.");
                response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/login.jsp");
                return;
            }

            // Obtener el objeto de usuario de la sesión
            UsuarioCliente usuarioObj = (UsuarioCliente) session.getAttribute("usuario");
            if (usuarioObj == null) {
                LOGGER.severe("Objeto de usuario nulo en sesión. No se puede obtener idCliente.");
                response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/login.jsp?mensaje=error_sesion");
                return;
            }

            try {
                int idCliente = usuarioObj.getIdUsuario();
                String fechaStr = request.getParameter("fecha");
                String horaStr = request.getParameter("hora");
                String motivo = request.getParameter("motivo");

                int idVeterinario = 0;
                try {
                    idVeterinario = Integer.parseInt(request.getParameter("idVeterinario"));
                    LOGGER.info("ID de Veterinario recibido: " + idVeterinario);
                } catch (NumberFormatException e) {
                    LOGGER.severe("Error: idVeterinario no es un número válido: '" + request.getParameter("idVeterinario") + "'. " + e.getMessage());
                    response.sendRedirect(request.getContextPath() + "/UsuarioCitasServlet?mensaje=error_formato_numero");
                    return;
                }

                // OBTENER EL NOMBRE COMPLETO DEL VETERINARIO USANDO EL DAO
                String nombreVeterinario = usuarioCitasDAO.getNombreVeterinarioById(idVeterinario);
                if (nombreVeterinario == null) {
                    LOGGER.severe("No se pudo encontrar el nombre del veterinario con ID: " + idVeterinario + ". No se puede registrar la cita.");
                    response.sendRedirect(request.getContextPath() + "/UsuarioCitasServlet?mensaje=veterinario_no_encontrado");
                    return;
                }
                LOGGER.info("Nombre del veterinario obtenido para ID " + idVeterinario + ": " + nombreVeterinario);

                // Validar fecha y hora para evitar citas en el pasado
                LocalDate fechaParaValidacion = null;
                LocalTime horaParaValidacion = null;
                try {
                    fechaParaValidacion = LocalDate.parse(fechaStr);
                    horaParaValidacion = LocalTime.parse(horaStr);
                } catch (DateTimeParseException e) {
                    LOGGER.severe("Error de formato de fecha u hora: Fecha='" + fechaStr + "', Hora='" + horaStr + "'. " + e.getMessage());
                    response.sendRedirect(request.getContextPath() + "/UsuarioCitasServlet?mensaje=formato_invalido");
                    return;
                }

                if (fechaParaValidacion.isBefore(LocalDate.now()) || (fechaParaValidacion.isEqual(LocalDate.now()) && horaParaValidacion.isBefore(LocalTime.now()))) {
                    LOGGER.warning("Intento de registrar cita en fecha/hora pasada: Fecha=" + fechaStr + ", Hora=" + horaStr);
                    response.sendRedirect(request.getContextPath() + "/UsuarioCitasServlet?mensaje=fecha_hora_pasada");
                    return;
                }

                // Convertir LocalDate/LocalTime a java.sql.Date/Time para la base de datos
                Date fechaSQL = Date.valueOf(fechaParaValidacion);
                Time horaSQL = Time.valueOf(horaParaValidacion);

                // Crear el objeto UsuarioCitas usando el constructor con 'nombreVeterinario'
                UsuarioCitas nuevaCita = new UsuarioCitas(idCliente, idVeterinario, fechaSQL, horaSQL, nombreVeterinario, motivo, "Pendiente");
                LOGGER.info("Objeto UsuarioCitas creado para registro: " + nuevaCita.toString());

                // Llamar al DAO para registrar la cita
                boolean exito = usuarioCitasDAO.registrarCita(nuevaCita);

                if (exito) {
                    LOGGER.info("Cita registrada con éxito en la base de datos para Cliente ID: " + idCliente);
                    response.sendRedirect(request.getContextPath() + "/UsuarioCitasServlet?mensaje=registrado");
                } else {
                    LOGGER.warning("Fallo al registrar la cita en la base de datos para Cliente ID: " + idCliente);
                    response.sendRedirect(request.getContextPath() + "/UsuarioCitasServlet?mensaje=error_registro");
                }

            } catch (Exception e) {
                LOGGER.severe("Excepción inesperada al registrar cita: " + e.getMessage());
                e.printStackTrace(); // Imprimir la traza completa del error
                response.sendRedirect(request.getContextPath() + "/UsuarioCitasServlet?mensaje=error_inesperado");
            }
        } else {
            LOGGER.warning("Acción desconocida en doPost de UsuarioCitasServlet: " + accion);
            doGet(request, response); // Redirigir a doGet para mostrar la página de citas
        }
    }
}