package Controllers;

import Modelo.UsuarioCitas;
import Modelo.UsuarioCliente;
import Modelo.Veterinario; // Importar la clase Veterinario
import ModeloDAO.UsuarioCitasDAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Date;
import java.sql.Time;

@WebServlet(name = "UsuarioMisCitasServlet", urlPatterns = {"/UsuarioMisCitasServlet"})
public class UsuarioMisCitasServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UsuarioMisCitasServlet.class.getName());

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();

        Integer idUsuarioLogueado = (Integer) session.getAttribute("idUsuario");
        UsuarioCliente clienteLogueado = (UsuarioCliente) session.getAttribute("usuario"); 

        String accion = request.getParameter("accion"); 

        if (idUsuarioLogueado == null || clienteLogueado == null) {
            LOGGER.log(Level.WARNING, "Usuario no logueado intentó acceder a UsuarioMisCitasServlet.");
            request.setAttribute("mensajeNoLogueado", "No hay un usuario logueado para mostrar citas. Por favor, inicia sesión."); 
            request.getRequestDispatcher("/VistasWeb/VistasCliente/historialdecitas.jsp").forward(request, response);
            return; 
        }

        UsuarioCitasDAO dao = new UsuarioCitasDAO();
        
        try {
            // AQUI OBTENEMOS LA LISTA DE VETERINARIOS SIEMPRE QUE SE CARGUE EL JSP
            // Para que el select del modal de edición se pueda poblar
            List<Veterinario> veterinariosDisponibles = dao.listarVeterinariosParaUsuarioCitas();
            request.setAttribute("veterinariosDisponibles", veterinariosDisponibles); // Pasar la lista al JSP
            
            if (accion == null || accion.isEmpty() || accion.equals("listar")) {
                List<UsuarioCitas> citasUsuario = dao.listarCitasPorUsuario(idUsuarioLogueado);
                request.setAttribute("citasUsuario", citasUsuario); 
                request.setAttribute("nombreUsuarioLogueado", clienteLogueado.getNombre()); 
                request.getRequestDispatcher("/VistasWeb/VistasCliente/historialdecitas.jsp").forward(request, response);
            } else {
                switch (accion) {
                    case "actualizarCita": 
                        int idCita = Integer.parseInt(request.getParameter("idCita"));
                        String fechaStr = request.getParameter("fecha");
                        String horaStr = request.getParameter("hora");
                        String veterinarioNombre = request.getParameter("veterinario"); // Nombre del veterinario del SELECT
                        String motivo = request.getParameter("motivo");
                        String estado = request.getParameter("estado"); // Valor del SELECT de estado

                        // Convertir String a java.sql.Date y java.sql.Time
                        Date fechaSql = Date.valueOf(fechaStr);
                        Time horaSql = Time.valueOf(horaStr + ":00"); 

                        // OBTENER EL ID DEL VETERINARIO A PARTIR DEL NOMBRE SELECCIONADO
                        int idVeterinario = dao.getIdVeterinarioByName(veterinarioNombre);
                        if (idVeterinario == -1) {
                            session.setAttribute("mensajeError", "El veterinario seleccionado '" + veterinarioNombre + "' no es válido o no se encontró en la base de datos.");
                            response.sendRedirect(request.getContextPath() + "/UsuarioMisCitasServlet");
                            return;
                        }

                        UsuarioCitas citaActualizar = new UsuarioCitas();
                        citaActualizar.setIdCita(idCita);
                        citaActualizar.setIdUsuario(idUsuarioLogueado);
                        citaActualizar.setFecha(fechaSql);
                        citaActualizar.setHora(horaSql);
                        citaActualizar.setIdVeterinario(idVeterinario); // Setear el ID del veterinario (INT)
                        citaActualizar.setVeterinario(veterinarioNombre); // Setear el nombre del veterinario (STRING)
                        citaActualizar.setMotivo(motivo);
                        citaActualizar.setEstado(estado); // Setear el estado

                        if (dao.actualizarCita(citaActualizar)) {
                            session.setAttribute("mensajeExito", "Cita actualizada correctamente.");
                        } else {
                            session.setAttribute("mensajeError", "No se pudo actualizar la cita. Verifique los datos o permisos.");
                        }
                        response.sendRedirect(request.getContextPath() + "/UsuarioMisCitasServlet");
                        break;

                    case "eliminar":
                        int idCitaEliminar = Integer.parseInt(request.getParameter("id"));
                        if (dao.eliminarCita(idCitaEliminar)) {
                            session.setAttribute("mensajeExito", "Cita eliminada correctamente.");
                        } else {
                            session.setAttribute("mensajeError", "No se pudo eliminar la cita.");
                        }
                        response.sendRedirect(request.getContextPath() + "/UsuarioMisCitasServlet");
                        break;
                        
                    default:
                        // En caso de acción desconocida, listar citas
                        List<UsuarioCitas> citasUsuarioDefault = dao.listarCitasPorUsuario(idUsuarioLogueado);
                        request.setAttribute("citasUsuario", citasUsuarioDefault);
                        request.setAttribute("nombreUsuarioLogueado", clienteLogueado.getNombre());
                        request.getRequestDispatcher("/VistasWeb/VistasCliente/historialdecitas.jsp").forward(request, response);
                        break;
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Error de formato de número en Servlet (ID de cita): " + e.getMessage(), e);
            session.setAttribute("mensajeError", "Error en el ID de la cita. Por favor, inténtalo de nuevo.");
            response.sendRedirect(request.getContextPath() + "/UsuarioMisCitasServlet");
        } catch (IllegalArgumentException e) { 
            LOGGER.log(Level.SEVERE, "Error de formato de fecha/hora en Servlet: " + e.getMessage(), e);
            session.setAttribute("mensajeError", "Error en el formato de fecha u hora. Use AAAA-MM-DD y HH:MM.");
            response.sendRedirect(request.getContextPath() + "/UsuarioMisCitasServlet");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error general en UsuarioMisCitasServlet: " + e.getMessage(), e);
            session.setAttribute("mensajeError", "Ocurrió un error inesperado. Por favor, inténtalo de nuevo.");
            response.sendRedirect(request.getContextPath() + "/UsuarioMisCitasServlet");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}