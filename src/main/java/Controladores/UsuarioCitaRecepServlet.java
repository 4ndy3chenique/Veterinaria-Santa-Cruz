package Controlador;

import Modelo.UsuarioCitas; // Tu modelo de vista de citas extendida
import ModeloDAO.UsuarioCitaRecepDAO; // El DAO que opera sobre este modelo

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

// Configuración del servlet para Jakarta EE
@WebServlet(name = "UsuarioCitaRecepServlet", urlPatterns = {"/UsuarioCitaRecepServlet"})
public class UsuarioCitaRecepServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UsuarioCitaRecepServlet.class.getName());
    
    // ¡AJUSTADO! La única vista para gestionar citas de usuario será GestionCitasUsuario.jsp
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

            case "EliminarCitaUsuario":
                try {
                    int idEliminar = Integer.parseInt(request.getParameter("id"));
                    int resultadoEliminar = dao.eliminarCita(idEliminar); 
                    if (resultadoEliminar > 0) {
                        request.setAttribute("mensaje", "Cita eliminada correctamente.");
                    } else if (resultadoEliminar == -1) {
                        request.setAttribute("mensaje", "Cita no encontrada o no se pudo eliminar (SP).");
                    } else {
                        request.setAttribute("mensaje", "Error al eliminar la cita.");
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

            case "EditarCitaUsuario":
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
                int idCita = Integer.parseInt(request.getParameter("idCita"));
                String fechaStr = request.getParameter("fecha");
                String horaStr = request.getParameter("hora");
                String estado = request.getParameter("estado");

                // *** IMPORTANTE: Aquí necesitas un DAO para la tabla 'Citas' ***
                // Para simplificar, si NO tienes un CitaDAO separado,
                // este UsuarioCitaRecepServlet NO puede realizar la actualización real.
                // Podrías manejar un mensaje de éxito/error simulado o redirigir a un error.
                
                // Simulación de actualización para fines de demostración si no hay CitaDAO
                boolean exitoActualizacion = true; // Cambiar a false para simular un fallo
                
                if (exitoActualizacion) {
                    request.setAttribute("mensaje", "Cita con ID " + idCita + " actualizada (simulado).");
                } else {
                    request.setAttribute("mensaje", "Error al actualizar la cita con ID " + idCita + " (simulado).");
                }
                LOGGER.log(Level.INFO, "Simulando actualización de cita: " + idCita);

            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, "Error de formato de número al guardar edición de cita: " + e.getMessage(), e);
                request.setAttribute("mensaje", "Datos numéricos inválidos para la actualización.");
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