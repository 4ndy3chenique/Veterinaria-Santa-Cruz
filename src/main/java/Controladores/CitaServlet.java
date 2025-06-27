package Controladores;

import Modelo.Cita;
import Modelo.Cliente; // Importar para los dropdowns
import Modelo.Veterinario; // Importar para los dropdowns
import ModeloDAO.CitaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date; // Usando java.sql.Date
import java.sql.Time; // Usando java.sql.Time
import java.text.ParseException; // Para el manejo de errores de parseo de fechas
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/CitaServlet") // Mapeo del servlet
public class CitaServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CitaServlet.class.getName());
    CitaDAO citaDAO = new CitaDAO();

    // Formateadores para parsear y formatear Date y Time
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yy-M-d");
    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("H:m");

   @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar"; // Acción por defecto
        }

        switch (accion) {
            case "listar":
                listarCitas(request, response);
                break;
            case "ver":
                verCita(request, response);
                break;
            case "eliminar":
                eliminarCita(request, response);
                break;
            case "buscar":
                buscarCita(request, response);
                break;
            default:
                listarCitas(request, response);
                break;
    }
}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");

        switch (accion) {
            case "guardar": // Usado tanto para agregar como para actualizar desde GestionCitas.jsp
                guardarCita(request, response);
                break;
            case "crearCita": // Si tienes un modal de "Crear Cita" en otra JSP (e.g., ClienteRServlet.jsp)
                crearCitaDesdeCliente(request, response);
                break;
            default:
                listarCitas(request, response);
                break;
        }
    }

    private void listarCitas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Cita> listaCitas = citaDAO.listarCitas();
        request.setAttribute("listaCitas", listaCitas);

        // Cargar las listas de clientes y veterinarios para los dropdowns en los modales
        request.setAttribute("listaClientes", citaDAO.listarClientesParaDropdown());
        request.setAttribute("listaVeterinarios", citaDAO.listarVeterinariosParaDropdown());

        request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionCitas.jsp").forward(request, response);
    }

    private void verCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idCita = Integer.parseInt(request.getParameter("id"));
        Cita citaSeleccionada = citaDAO.obtenerCitaPorId(idCita); // Usa obtenerCitaPorId
        request.setAttribute("citaSeleccionada", citaSeleccionada);

        // Cargar las listas para los dropdowns en el modal de edición
        request.setAttribute("listaClientes", citaDAO.listarClientesParaDropdown());
        request.setAttribute("listaVeterinarios", citaDAO.listarVeterinariosParaDropdown());

        listarCitas(request, response); // Redirige internamente a la lista y el modal se abrirá con JS
    }

    private void guardarCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cita cita = new Cita();
        String idCitaStr = request.getParameter("idCita"); // Será null si es una nueva cita

        cita.setIdCliente(Integer.parseInt(request.getParameter("idCliente")));
        cita.setIdVeterinario(Integer.parseInt(request.getParameter("idVeterinario")));

        String fechaStr = request.getParameter("fecha");
        String horaStr = request.getParameter("hora");

        try {
            java.util.Date parsedDate = DATE_FORMATTER.parse(fechaStr);
            cita.setFecha(new Date(parsedDate.getTime())); // Convierte util.Date a sql.Date

            java.util.Date parsedTime = TIME_FORMATTER.parse(horaStr);
            cita.setHora(new Time(parsedTime.getTime())); // Convierte util.Date a sql.Time

        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, "Error al parsear fecha/hora de la cita (Guardar): " + e.getMessage(), e);
            request.setAttribute("mensaje", "Error en el formato de fecha/hora. Por favor, verifica.");
            request.setAttribute("tipoMensaje", "error");
            listarCitas(request, response);
            return;
        }

        cita.setMotivo(request.getParameter("motivo"));
        cita.setEstado(request.getParameter("estado")); // Obtener el estado del formulario

        boolean operacionExitosa;
        if (idCitaStr != null && !idCitaStr.isEmpty()) {
            // Es una actualización
            cita.setIdCita(Integer.parseInt(idCitaStr));
            operacionExitosa = citaDAO.actualizarCita(cita);
            if (operacionExitosa) {
                request.setAttribute("mensaje", "Cita actualizada con éxito!");
                request.setAttribute("tipoMensaje", "exito");
            } else {
                request.setAttribute("mensaje", "Error al actualizar la cita.");
                request.setAttribute("tipoMensaje", "error");
            }
        } else {
            // Es una nueva cita
            operacionExitosa = citaDAO.agregarCita(cita);
            if (operacionExitosa) {
                request.setAttribute("mensaje", "Cita agregada con éxito!");
                request.setAttribute("tipoMensaje", "exito");
            } else {
                request.setAttribute("mensaje", "Error al agregar la cita.");
                request.setAttribute("tipoMensaje", "error");
            }
        }
        listarCitas(request, response); // Redirige internamente para mostrar la lista y el mensaje
    }

    // Este método es para la acción de "Crear Cita" que podría venir del modal en ClienteRServlet.jsp
    private void crearCitaDesdeCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cita cita = new Cita();
        cita.setIdCliente(Integer.parseInt(request.getParameter("idCliente")));
        cita.setIdVeterinario(Integer.parseInt(request.getParameter("idVeterinario")));

        String fechaStr = request.getParameter("fecha");
        String horaStr = request.getParameter("hora");

        try {
            java.util.Date parsedDate = DATE_FORMATTER.parse(fechaStr);
            cita.setFecha(new Date(parsedDate.getTime()));

            java.util.Date parsedTime = TIME_FORMATTER.parse(horaStr);
            cita.setHora(new Time(parsedTime.getTime()));

        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, "Error al parsear fecha/hora de la cita desde modal Cliente: " + e.getMessage(), e);
            request.getSession().setAttribute("mensaje", "Error en el formato de fecha/hora de la cita.");
            request.getSession().setAttribute("tipoMensaje", "error");
            response.sendRedirect(request.getContextPath() + "/ClienteRServlet"); // Redirige de vuelta a clientes
            return;
        }
        cita.setMotivo(request.getParameter("motivo"));
        cita.setEstado("Pendiente"); // Estado por defecto para nuevas citas creadas desde aquí

        boolean agregada = citaDAO.agregarCita(cita);
        if (agregada) {
            request.getSession().setAttribute("mensaje", "Cita creada con éxito!");
            request.getSession().setAttribute("tipoMensaje", "exito");
        } else {
            request.getSession().setAttribute("mensaje", "Error al crear la cita.");
            request.getSession().setAttribute("tipoMensaje", "error");
        }
        response.sendRedirect(request.getContextPath() + "/ClienteRServlet"); // Redirige de vuelta a clientes
    }

   private void eliminarCita(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    int idCita = Integer.parseInt(request.getParameter("id"));
    int resultado = citaDAO.eliminarCita(idCita);

    switch (resultado) {
        case 1:
            request.setAttribute("mensaje", "✅ Cita eliminada con éxito.");
            request.setAttribute("tipoMensaje", "exito");
            break;
        case -1:
            request.setAttribute("mensaje", "⚠️ No se encontró la cita a eliminar.");
            request.setAttribute("tipoMensaje", "advertencia");
            break;
        case 0:
            request.setAttribute("mensaje", "❌ No se pudo conectar a la base de datos.");
            request.setAttribute("tipoMensaje", "error");
            break;
        case -2:
        default:
            request.setAttribute("mensaje", "❌ Error inesperado al eliminar la cita.");
            request.setAttribute("tipoMensaje", "error");
            break;
    }

    listarCitas(request, response); // Muestra la lista con el mensaje
}
    
    private void buscarCita(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    String busqueda = request.getParameter("busqueda");
    List<Cita> listarCitas;
    
    if (busqueda != null && !busqueda.trim().isEmpty()) {
        listarCitas = citaDAO.buscarCitas(busqueda.trim());
    } else {
        listarCitas = citaDAO.listarCitas();
    }
    
    request.setAttribute("listaCitas", listarCitas);
    // Mantener los dropdowns cargados para los modales
    request.setAttribute("listaClientes", citaDAO.listarClientesParaDropdown());
    request.setAttribute("listaVeterinarios", citaDAO.listarVeterinariosParaDropdown());
    
    request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionCitas.jsp").forward(request, response);
}  
}