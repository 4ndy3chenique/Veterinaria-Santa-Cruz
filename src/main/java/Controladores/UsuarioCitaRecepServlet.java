package Controladores;

import Modelo.UsuarioCitas;
import ModeloDAO.UsuarioCitaRecepDAO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException; // Importa para manejar excepciones de parseo de fecha/hora
import java.text.SimpleDateFormat; // Importa para formatear fecha/hora


@WebServlet(name = "UsuarioCitaRecepServlet", urlPatterns = {"/UsuarioCitaRecepServlet"})
public class UsuarioCitaRecepServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UsuarioCitaRecepServlet.class.getName());

    String gestionCitasUsuarioJSP = "VistasWeb/VistasRecep/GestionCitasUsuario.jsp";

    UsuarioCitaRecepDAO dao = new UsuarioCitaRecepDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UsuarioCitaRecepServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UsuarioCitaRecepServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acceso = "";
        String action = request.getParameter("accion");

        if (action == null) {
            action = "listarCitaUsuarios"; // Acción por defecto
        }

        switch (action) {
            case "listarCitaUsuarios":
                List<UsuarioCitas> citas = dao.listarCitasConDetalles();
                request.setAttribute("citas", citas);
                request.setAttribute("modo", "listar"); // Para que el JSP sepa qué sección mostrar
                acceso = gestionCitasUsuarioJSP;
                break;

            case "buscarMedianteID":
                try {
                    int idBuscar = Integer.parseInt(request.getParameter("id"));
                    UsuarioCitas citaEncontrada = dao.obtenerCitaConDetallesPorId(idBuscar);
                    if (citaEncontrada != null) {
                        List<UsuarioCitas> resultados = new ArrayList<>();
                        resultados.add(citaEncontrada);
                        request.setAttribute("citas", resultados);
                        request.setAttribute("mensajeBusqueda", "Cita encontrada por ID.");
                    } else {
                        request.setAttribute("citas", new ArrayList<UsuarioCitas>());
                        request.setAttribute("mensajeBusqueda", "Cita con ID " + idBuscar + " no encontrada.");
                    }
                    request.setAttribute("modo", "listar"); // Mostrar la tabla de resultados
                    acceso = gestionCitasUsuarioJSP;
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "ID de búsqueda inválido: " + request.getParameter("id"), e);
                    request.setAttribute("mensajeBusqueda", "ID de cita inválido.");
                    request.setAttribute("citas", dao.listarCitasConDetalles());
                    request.setAttribute("modo", "listar");
                    acceso = gestionCitasUsuarioJSP;
                }
                break;
            
            // CORRECCIÓN 1: El nombre de la acción debe coincidir con el JSP (accion=eliminar)
            case "eliminar": 
                try {
                    int idEliminar = Integer.parseInt(request.getParameter("id"));
                    int resultadoEliminar = dao.eliminarCita(idEliminar);
                    if (resultadoEliminar == 1) { // Asumiendo que 1 significa éxito
                        request.setAttribute("mensaje", "Cita eliminada correctamente.");
                    } else if (resultadoEliminar == 0) {
                        request.setAttribute("mensaje", "Cita no encontrada o no se pudo eliminar.");
                    } else if (resultadoEliminar == -1) {
                         request.setAttribute("mensaje", "Error SQL al eliminar la cita.");
                    } else {
                        request.setAttribute("mensaje", "Error inesperado al eliminar la cita.");
                    }
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "ID de eliminación inválido: " + request.getParameter("id"), e);
                    request.setAttribute("mensaje", "ID de cita inválido para eliminación.");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error inesperado al eliminar cita: " + e.getMessage(), e);
                    request.setAttribute("mensaje", "Ocurrió un error inesperado al eliminar la cita.");
                }
                // Siempre redirigir a la vista de listado después de eliminar
                List<UsuarioCitas> citasDespuesEliminar = dao.listarCitasConDetalles();
                request.setAttribute("citas", citasDespuesEliminar);
                request.setAttribute("modo", "listar");
                acceso = gestionCitasUsuarioJSP;
                break;

            // CORRECCIÓN 2: El nombre de la acción debe coincidir con el JSP (accion=editar)
            case "editar": 
                try {
                    int idEditar = Integer.parseInt(request.getParameter("id"));
                    UsuarioCitas citaParaEditar = dao.obtenerCitaConDetallesPorId(idEditar);
                    if (citaParaEditar != null) {
                        request.setAttribute("cita", citaParaEditar);
                        request.setAttribute("modo", "editar"); // Para que el JSP muestre el formulario de edición
                    } else {
                        request.setAttribute("mensaje", "Cita a editar no encontrada.");
                        request.setAttribute("modo", "listar"); // Volver a la lista si no se encuentra
                    }
                    acceso = gestionCitasUsuarioJSP; // Siempre al mismo JSP
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "ID de edición inválido: " + request.getParameter("id"), e);
                    request.setAttribute("mensaje", "ID de cita inválido para edición.");
                    request.setAttribute("modo", "listar");
                    request.setAttribute("citas", dao.listarCitasConDetalles());
                    acceso = gestionCitasUsuarioJSP;
                }
                break;
                
            // NUEVO CASE: Para la acción de actualizar estado desde los dropdowns
            case "actualizarEstado":
                try {
                    int idCitaEstado = Integer.parseInt(request.getParameter("id"));
                    String nuevoEstado = request.getParameter("estado");
                    int resultadoEstado = dao.actualizarEstadoCita(idCitaEstado, nuevoEstado);
                    if (resultadoEstado == 1) { // Asumiendo que 1 significa éxito en el SP
                        request.setAttribute("mensaje", "Estado de la cita actualizado correctamente.");
                    } else if (resultadoEstado == 0) {
                         request.setAttribute("mensaje", "Cita no encontrada para actualizar estado.");
                    } else { // resultadoEstado == -1 para error SQL
                        request.setAttribute("mensaje", "Error al actualizar el estado de la cita.");
                    }
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "ID de cita inválido para actualizar estado: " + request.getParameter("id"), e);
                    request.setAttribute("mensaje", "ID de cita inválido para actualizar estado.");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error inesperado al actualizar estado de cita: " + e.getMessage(), e);
                    request.setAttribute("mensaje", "Ocurrió un error inesperado al actualizar el estado de la cita.");
                }
                // Después de actualizar el estado, redirige a la lista
                List<UsuarioCitas> citasDespuesEstado = dao.listarCitasConDetalles();
                request.setAttribute("citas", citasDespuesEstado);
                request.setAttribute("modo", "listar");
                acceso = gestionCitasUsuarioJSP;
                break;


            case "buscar": // Búsqueda general
                String terminoBusqueda = request.getParameter("terminoBusqueda");
                if (terminoBusqueda != null && !terminoBusqueda.trim().isEmpty()) {
                    List<UsuarioCitas> resultadosBusqueda = dao.buscarCitasConDetalles(terminoBusqueda);
                    request.setAttribute("citas", resultadosBusqueda);
                } else {
                    List<UsuarioCitas> todasLasCitas = dao.listarCitasConDetalles();
                    request.setAttribute("citas", todasLasCitas);
                }
                request.setAttribute("modo", "listar");
                acceso = gestionCitasUsuarioJSP;
                break;

            default:
                List<UsuarioCitas> defaultCitas = dao.listarCitasConDetalles();
                request.setAttribute("citas", defaultCitas);
                request.setAttribute("modo", "listar");
                acceso = gestionCitasUsuarioJSP;
                break;
        }

        RequestDispatcher vista = request.getRequestDispatcher(acceso);
        vista.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("accion");

        if ("guardarEdicion".equals(action)) {
            try {
                // Recuperar todos los parámetros del formulario de edición
                int idCita = Integer.parseInt(request.getParameter("idCita"));
                String fechaStr = request.getParameter("fecha");
                String horaStr = request.getParameter("hora");
                String veterinarioNombre = request.getParameter("veterinario"); // Nombre del veterinario
                String motivo = request.getParameter("motivo");
                String estado = request.getParameter("estado");
                // Recuperar idVeterinario (campo oculto en el JSP)
                int idVeterinario = Integer.parseInt(request.getParameter("idVeterinario"));

                // Convertir String a java.sql.Date y java.sql.Time
                Date fechaSQL = null;
                Time horaSQL = null;

                if (fechaStr != null && !fechaStr.isEmpty()) {
                    fechaSQL = Date.valueOf(fechaStr); // Directamente desde yyyy-MM-dd
                }
                if (horaStr != null && !horaStr.isEmpty()) {
                    horaSQL = Time.valueOf(horaStr + ":00"); // Añadir segundos si no vienen para Time.valueOf
                }

                // Crear el objeto UsuarioCitas con los datos actualizados
                UsuarioCitas citaActualizada = new UsuarioCitas();
                citaActualizada.setIdCita(idCita);
                citaActualizada.setFecha(fechaSQL);
                citaActualizada.setHora(horaSQL);
                citaActualizada.setIdVeterinario(idVeterinario); // ¡Importante!
                citaActualizada.setVeterinario(veterinarioNombre); // ¡Importante!
                citaActualizada.setMotivo(motivo);
                citaActualizada.setEstado(estado);

                // Llamar al DAO para realizar la actualización
                boolean exitoActualizacion = dao.actualizarCita(citaActualizada);

                if (exitoActualizacion) {
                    request.setAttribute("mensaje", "Cita con ID " + idCita + " actualizada correctamente.");
                } else {
                    request.setAttribute("mensaje", "Error al actualizar la cita con ID " + idCita + ".");
                }
                LOGGER.log(Level.INFO, "Intento de actualización de cita: " + idCita + " - Éxito: " + exitoActualizacion);

            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, "Error de formato de número al guardar edición de cita: " + e.getMessage(), e);
                request.setAttribute("mensaje", "Datos numéricos inválidos para la actualización.");
            } catch (IllegalArgumentException e) { // Captura errores de Date.valueOf o Time.valueOf
                LOGGER.log(Level.SEVERE, "Error de formato de fecha/hora al guardar edición de cita: " + e.getMessage(), e);
                request.setAttribute("mensaje", "Formato de fecha u hora inválido. Use AAAA-MM-DD y HH:MM.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error inesperado al guardar edición de cita: " + e.getMessage(), e);
                request.setAttribute("mensaje", "Ocurrió un error inesperado al actualizar la cita.");
            }
        } else {
            request.setAttribute("mensaje", "Acción POST no reconocida o no soportada.");
        }

        // Siempre redirigir a listar después de una operación POST
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet de Recepción para la gestión de UsuarioCitas (vista de citas extendida) con Jakarta EE";
    }
}