/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controladores;

import Modelo.UsuarioCitas; // Asegúrate de que esta ruta sea correcta
import ModeloDAO.UsuarioCitasDAO; // Asegúrate de que esta ruta sea correcta
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList; // Importar ArrayList explícitamente
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger; // Importar Logger para un manejo de logs adecuado

/**
 * Servlet para la gestión de citas por parte de la recepcionista o administrador.
 * Maneja la visualización de todas las citas y podría manejar futuras operaciones como actualizar o eliminar.
 */
@WebServlet("/GestionCitasServlet") // La URL que activará este servlet
public class GestionCitasServlet extends HttpServlet {

    // Configuración del logger para esta clase
    private static final Logger LOGGER = Logger.getLogger(GestionCitasServlet.class.getName());

    /**
     * Procesa las solicitudes HTTP GET.
     * Recupera todas las citas de la base de datos y las envía al JSP para su visualización.
     * @param request El objeto HttpServletRequest que contiene la solicitud del cliente.
     * @param response El objeto HttpServletResponse que contiene la respuesta del servlet.
     * @throws ServletException Si ocurre un error específico del servlet.
     * @throws IOException Si ocurre un error de E/S.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LOGGER.log(Level.INFO, "GestionCitasServlet: doGet se ha invocado.");

        // 1. Crear una instancia del DAO para interactuar con la base de datos
        UsuarioCitasDAO dao = new UsuarioCitasDAO();

        // 2. Variable para almacenar la lista de citas
        List<UsuarioCitas> listaCitas = null;

        try {
            // 3. Llamar al método del DAO para obtener TODAS las citas
            listaCitas = dao.listarTodasLasCitas();
            
            // Loguear el tamaño de la lista obtenida para depuración
            if (listaCitas != null) {
                LOGGER.log(Level.INFO, "GestionCitasServlet: Se encontraron {0} citas desde el DAO.", listaCitas.size());
                
                // Opcional: Imprimir detalles de las primeras 5 citas para depuración profunda.
                // Esto te ayudará a verificar si los datos llegan correctamente al Servlet.
                if (!listaCitas.isEmpty()) {
                    listaCitas.stream().limit(5).forEach(cita -> 
                        LOGGER.log(Level.INFO, "Cita (en Servlet): ID={0}, Cliente={1}, Fecha={2}, Hora={3}, Veterinario={4}, Estado={5}", 
                                   new Object[]{cita.getIdCita(), cita.getNombreCliente(), cita.getFecha(), cita.getHora(), cita.getVeterinario(), cita.getEstado()}));
                }
            } else {
                LOGGER.log(Level.WARNING, "GestionCitasServlet: La lista de citas obtenida del DAO es NULA.");
                // Si la lista es null, la inicializamos como una lista vacía para evitar NullPointerException en el JSP
                listaCitas = new ArrayList<>(); 
            }
        } catch (Exception e) {
            // Capturar cualquier excepción inesperada durante la consulta a la base de datos
            LOGGER.log(Level.SEVERE, "Error crítico al listar citas en doGet: " + e.getMessage(), e);
            // Establecer un mensaje de error para mostrar en el JSP
            request.setAttribute("mensajeError", "Error interno al cargar las citas: " + e.getMessage());
            // Asegurarse de que la lista no sea null en caso de error grave
            listaCitas = new ArrayList<>(); 
        }

        // 4. Poner la lista de citas en el objeto 'request' para que el JSP pueda acceder a ella
        // El nombre del atributo ("listaDeCitas") debe coincidir exactamente con el usado en el JSP.
        request.setAttribute("listaDeCitas", listaCitas);

        // 5. Redirigir (despachar) la petición al JSP para que muestre los datos.
        // La ruta debe ser EXACTA a la ubicación de tu archivo JSP dentro de la carpeta webapp.
        // Por ejemplo, si tu proyecto se despliega como "VeterinariaSantaCruz" y el JSP está en webapp/VistasWeb/VistasRecep/
        RequestDispatcher dispatcher = request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionCitasUsuario.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Procesa las solicitudes HTTP POST.
     * Este método puede ser extendido para manejar acciones de escritura como
     * actualización o eliminación de citas, basándose en un parámetro "accion".
     * Después de cualquier acción, redirige a doGet para recargar y mostrar la lista actualizada.
     * @param request El objeto HttpServletRequest que contiene la solicitud del cliente.
     * @param response El objeto HttpServletResponse que contiene la respuesta del servlet.
     * @throws ServletException Si ocurre un error específico del servlet.
     * @throws IOException Si ocurre un error de E/S.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion"); // Parámetro para determinar la acción a realizar
        UsuarioCitasDAO dao = new UsuarioCitasDAO();

        if (accion != null && !accion.isEmpty()) {
            switch (accion) {
                case "actualizarEstado":
                    try {
                        int idCita = Integer.parseInt(request.getParameter("idCita"));
                        String nuevoEstado = request.getParameter("nuevoEstado");
                        boolean exito = dao.actualizarEstadoCita(idCita, nuevoEstado);
                        if (exito) {
                            LOGGER.log(Level.INFO, "Estado de cita {0} actualizado a {1} con éxito.", new Object[]{idCita, nuevoEstado});
                            request.setAttribute("mensajeExito", "Estado de cita actualizado correctamente.");
                        } else {
                            LOGGER.log(Level.WARNING, "Fallo al actualizar estado de cita {0}.", idCita);
                            request.setAttribute("mensajeError", "Error al actualizar el estado de la cita.");
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.SEVERE, "ID de cita inválido para actualizar estado: " + request.getParameter("idCita"), e);
                        request.setAttribute("mensajeError", "ID de cita inválido.");
                    }
                    break;
                case "eliminarCita":
                     try {
                        int idCita = Integer.parseInt(request.getParameter("idCita"));
                        boolean exito = dao.eliminarCita(idCita);
                        if (exito) {
                            LOGGER.log(Level.INFO, "Cita {0} eliminada con éxito.", idCita);
                            request.setAttribute("mensajeExito", "Cita eliminada correctamente.");
                        } else {
                            LOGGER.log(Level.WARNING, "Fallo al eliminar cita {0}.", idCita);
                            request.setAttribute("mensajeError", "Error al eliminar la cita.");
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.SEVERE, "ID de cita inválido para eliminar: " + request.getParameter("idCita"), e);
                        request.setAttribute("mensajeError", "ID de cita inválido.");
                    }
                    break;
                // Puedes añadir más casos para otras operaciones POST (ej. registrar cita desde un formulario)
                default:
                    LOGGER.log(Level.WARNING, "Acción POST desconocida: {0}", accion);
                    request.setAttribute("mensajeError", "Acción no reconocida.");
                    break;
            }
        } else {
            LOGGER.log(Level.INFO, "GestionCitasServlet: doPost invocado sin parámetro 'accion'.");
        }

        // Después de cualquier operación POST, siempre volvemos a cargar y mostrar la lista
        // para que el usuario vea los cambios reflejados y los mensajes de éxito/error.
        doGet(request, response); 
    }
}