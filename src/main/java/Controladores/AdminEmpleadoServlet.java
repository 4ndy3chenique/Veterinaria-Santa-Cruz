package Controladores;

import Modelo.Veterinario;
import Modelo.Recepcionista;
import Modelo.Conexion; // Aunque no se usa directamente aquí, se mantiene por si es necesario para otros métodos
import ModeloDAO.RecepcionistaDAO;
import ModeloDAO.VeterinarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection; // Se mantiene por si es necesario para otros métodos
import java.sql.SQLException; // Se mantiene por si es necesario para otros métodos
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Se eliminan las importaciones de Jackson

@WebServlet(name = "AdminEmpleadoServlet", urlPatterns = {"/AdminEmpleadoServlet"})
public class AdminEmpleadoServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminEmpleadoServlet.class.getName());
    // Se elimina la instancia de ObjectMapper

    @Override
    public void init() throws ServletException {
        super.init();
        // Se elimina la inicialización de ObjectMapper
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if (accion == null || accion.isEmpty()) {
            listarEmpleados(request, response);
        } else {
            switch (accion) {
                case "ver":
                    obtenerDatosEmpleadoJson(request, response);
                    break;
                case "editar":
                    // Si el JSP no usa AJAX para precargar el formulario de edición,
                    // esta acción podría redirigir a un JSP de formulario con los datos.
                    // Actualmente, el JSP usa 'ver' para AJAX.
                    listarEmpleados(request, response);
                    break;
                case "eliminar":
                    eliminarEmpleado(request, response);
                    break;
                case "nuevo":
                    mostrarFormularioNuevo(request, response);
                    break;
                case "listar": // Esta acción ahora también manejará la búsqueda
                    listarEmpleados(request, response);
                    break;
                default:
                    listarEmpleados(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String accion = request.getParameter("accion");
        String tipoEmpleado = request.getParameter("tipoEmpleado");

        if (accion != null) {
            switch (accion) {
                case "agregar":
                    agregarEmpleado(request, response, tipoEmpleado);
                    break;
                case "actualizar":
                    actualizarEmpleado(request, response, tipoEmpleado);
                    break;
                case "eliminar":
                    eliminarEmpleado(request, response);
                    break;
                default:
                    listarEmpleados(request, response);
            }
        } else {
            listarEmpleados(request, response);
        }
    }

    private void listarEmpleados(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchQuery = request.getParameter("query");
        String activeTab = request.getParameter("currentTab"); // Obtener la pestaña activa desde la URL

        // Si no se especifica una pestaña activa, por defecto es 'veterinarios'
        if (activeTab == null || activeTab.isEmpty()) {
            activeTab = "veterinarios";
        }
        request.setAttribute("activeTab", activeTab); // Pasar la pestaña activa al JSP

        VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
        List<Veterinario> allVeterinarios = veterinarioDAO.listarVeterinarios();
        List<Veterinario> filteredVeterinarios = new ArrayList<>();

        RecepcionistaDAO recepcionistaDAO = new RecepcionistaDAO();
        List<Recepcionista> allRecepcionistas = recepcionistaDAO.listarRecepcionistas();
        List<Recepcionista> filteredRecepcionistas = new ArrayList<>();

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String lowerCaseQuery = searchQuery.trim().toLowerCase();

            // Filtrar veterinarios
            for (Veterinario vet : allVeterinarios) {
                if ( (vet.getNombre() != null && vet.getNombre().toLowerCase().contains(lowerCaseQuery)) ||
                     (vet.getApellido() != null && vet.getApellido().toLowerCase().contains(lowerCaseQuery)) ||
                     (vet.getDni() != null && vet.getDni().toLowerCase().contains(lowerCaseQuery)) ||
                     (vet.getNumero() != null && vet.getNumero().toLowerCase().contains(lowerCaseQuery)) ||
                     (vet.getEspecialidad() != null && vet.getEspecialidad().toLowerCase().contains(lowerCaseQuery)) ) {
                    filteredVeterinarios.add(vet);
                }
            }

            // Filtrar recepcionistas
            for (Recepcionista rec : allRecepcionistas) {
                if ( (rec.getNombre() != null && rec.getNombre().toLowerCase().contains(lowerCaseQuery)) ||
                     (rec.getApellido() != null && rec.getApellido().toLowerCase().contains(lowerCaseQuery)) ||
                     (rec.getDni() != null && rec.getDni().toLowerCase().contains(lowerCaseQuery)) ||
                     (rec.getNumero() != null && rec.getNumero().toLowerCase().contains(lowerCaseQuery)) ||
                     (rec.getCorreo() != null && rec.getCorreo().toLowerCase().contains(lowerCaseQuery)) ) {
                    filteredRecepcionistas.add(rec);
                }
            }
            request.setAttribute("searchQuery", searchQuery); // Mantener el término de búsqueda en el input del JSP
        } else {
            filteredVeterinarios = allVeterinarios;
            filteredRecepcionistas = allRecepcionistas;
        }

        request.setAttribute("listaVeterinarios", filteredVeterinarios);
        request.setAttribute("listaRecepcionistas", filteredRecepcionistas);

        request.getRequestDispatcher("/VistasWeb/VistasAdmin/ListadoEmpleados.jsp")
                .forward(request, response);
    }

    // Método auxiliar para escapar cadenas para JSON (se vuelve a incluir)
    private String escapeJsonString(String text) {
        if (text == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }

    // Método para obtener datos del empleado y enviarlos como JSON (construido manualmente)
    private void obtenerDatosEmpleadoJson(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tipo = request.getParameter("tipo");
        int id = 0;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID de empleado inválido al intentar obtener datos JSON: " + request.getParameter("id"), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + escapeJsonString("ID de empleado inválido.") + "\"}");
            return;
        }

        StringBuilder jsonResponse = new StringBuilder();

        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            if ("veterinario".equals(tipo)) {
                VeterinarioDAO dao = new VeterinarioDAO();
                Veterinario vet = dao.obtenerVeterinarioPorId(id);
                if (vet != null) {
                    jsonResponse.append("{");
                    jsonResponse.append("\"idVeterinario\": ").append(vet.getIdVeterinario()).append(",");
                    jsonResponse.append("\"nombre\": \"").append(escapeJsonString(vet.getNombre())).append("\",");
                    jsonResponse.append("\"apellido\": \"").append(escapeJsonString(vet.getApellido())).append("\",");
                    jsonResponse.append("\"numero\": \"").append(escapeJsonString(vet.getNumero())).append("\",");
                    jsonResponse.append("\"dni\": \"").append(escapeJsonString(vet.getDni())).append("\",");
                    jsonResponse.append("\"especialidad\": \"").append(escapeJsonString(vet.getEspecialidad())).append("\"");
                    jsonResponse.append("}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"" + escapeJsonString("Veterinario no encontrado con ID: " + id) + "\"}");
                    return;
                }
            } else if ("recepcionista".equals(tipo)) {
                RecepcionistaDAO dao = new RecepcionistaDAO();
                Recepcionista rec = dao.obtenerRecepcionistaPorId(id);
                if (rec != null) {
                    jsonResponse.append("{");
                    jsonResponse.append("\"idRecepcionista\": ").append(rec.getIdRecepcionista()).append(",");
                    jsonResponse.append("\"nombre\": \"").append(escapeJsonString(rec.getNombre())).append("\",");
                    jsonResponse.append("\"apellido\": \"").append(escapeJsonString(rec.getApellido())).append("\",");
                    jsonResponse.append("\"numero\": \"").append(escapeJsonString(rec.getNumero())).append("\",");
                    jsonResponse.append("\"dni\": \"").append(escapeJsonString(rec.getDni())).append("\",");
                    jsonResponse.append("\"correo\": \"").append(escapeJsonString(rec.getCorreo())).append("\"");
                    // No incluir la contraseña aquí por seguridad
                    jsonResponse.append("}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"" + escapeJsonString("Recepcionista no encontrado con ID: " + id) + "\"}");
                    return;
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"" + escapeJsonString("Tipo de empleado no válido.") + "\"}");
                return;
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener datos JSON del empleado (ID: " + id + ", Tipo: " + tipo + "): " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + escapeJsonString("Error interno del servidor al obtener datos del empleado: " + e.getMessage()) + "\"}");
        }
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("accion", "agregar");
        request.setAttribute("tipoEmpleado", request.getParameter("tipo"));
        request.getRequestDispatcher("/VistasWeb/VistasAdmin/FormularioEmpleado.jsp").forward(request, response);
    }

    private void agregarEmpleado(HttpServletRequest request, HttpServletResponse response, String tipoEmpleado)
            throws ServletException, IOException {

        boolean exito = false;
        String mensaje = "";
        String error = "";

        try {
            if ("veterinario".equals(tipoEmpleado)) {
                Veterinario vet = new Veterinario();
                vet.setNombre(request.getParameter("nombre"));
                vet.setApellido(request.getParameter("apellido"));
                vet.setDni(request.getParameter("dni"));
                vet.setNumero(request.getParameter("numero"));
                vet.setEspecialidad(request.getParameter("especialidad"));

                VeterinarioDAO dao = new VeterinarioDAO();
                exito = dao.agregarVeterinario(vet);

                if (exito) {
                    mensaje = "Veterinario agregado exitosamente.";
                } else {
                    error = "No se pudo agregar el veterinario. Verifique los datos o si el DNI ya existe.";
                    LOGGER.log(Level.WARNING, "Fallo al agregar veterinario: " + vet.getDni());
                }
            } else if ("recepcionista".equals(tipoEmpleado)) {
                Recepcionista rec = new Recepcionista();
                rec.setNombre(request.getParameter("nombre"));
                rec.setApellido(request.getParameter("apellido"));
                rec.setDni(request.getParameter("dni"));
                rec.setNumero(request.getParameter("numero"));
                rec.setCorreo(request.getParameter("correo"));

                String contrasenaPlana = request.getParameter("contrasena");
                // **** IMPORTANTE: HASHEAR LA CONTRASEÑA ANTES DE ENVIARLA AL DAO ****
                // Si usas BCrypt, sería algo como:
                // String hashedPassword = BCrypt.hashpw(contrasenaPlana, BCrypt.gensalt());
                // rec.setContrasena(hashedPassword);
                // Por ahora, se envía en texto plano (¡CAMBIAR ESTO!).
                rec.setContrasena(contrasenaPlana); // ¡ADVERTENCIA DE SEGURIDAD!

                RecepcionistaDAO dao = new RecepcionistaDAO();
                exito = dao.agregarRecepcionista(rec);

                if (exito) {
                    mensaje = "Recepcionista agregado exitosamente.";
                } else {
                    error = "No se pudo agregar el recepcionista. Verifique los datos o si el DNI/Correo ya existe.";
                    LOGGER.log(Level.WARNING, "Fallo al agregar recepcionista: " + rec.getDni() + " / " + rec.getCorreo());
                }
            } else {
                error = "Tipo de empleado no válido para agregar.";
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al agregar empleado (" + tipoEmpleado + "): " + e.getMessage(), e);
            error = "Error interno del servidor al agregar empleado: " + e.getMessage();
        }

        if (!mensaje.isEmpty()) {
            request.setAttribute("mensaje", mensaje);
        }
        if (!error.isEmpty()) {
            request.setAttribute("error", error);
        }
        listarEmpleados(request, response);
    }

    private void actualizarEmpleado(HttpServletRequest request, HttpServletResponse response, String tipoEmpleado)
            throws ServletException, IOException {

        boolean exito = false;
        String mensaje = "";
        String error = "";
        int id = 0;

        try {
            id = Integer.parseInt(request.getParameter("idEmpleado"));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido para actualización: " + request.getParameter("idEmpleado"), e);
            manejarError(request, response, "ID de empleado inválido para actualización.");
            return;
        }

        try {
            if ("veterinario".equals(tipoEmpleado)) {
                Veterinario vet = new Veterinario();
                vet.setIdVeterinario(id);
                vet.setNombre(request.getParameter("nombre"));
                vet.setApellido(request.getParameter("apellido"));
                vet.setDni(request.getParameter("dni"));
                vet.setNumero(request.getParameter("numero"));
                vet.setEspecialidad(request.getParameter("especialidad"));

                VeterinarioDAO dao = new VeterinarioDAO();
                exito = dao.actualizarVeterinario(vet);

                if (exito) {
                    mensaje = "Veterinario actualizado exitosamente.";
                } else {
                    error = "No se pudo actualizar el veterinario. Verifique los datos o si el DNI ya existe.";
                    LOGGER.log(Level.WARNING, "Fallo al actualizar veterinario ID: " + id);
                }
            } else if ("recepcionista".equals(tipoEmpleado)) {
                Recepcionista rec = new Recepcionista();
                rec.setIdRecepcionista(id);
                rec.setNombre(request.getParameter("nombre"));
                rec.setApellido(request.getParameter("apellido"));
                rec.setDni(request.getParameter("dni"));
                rec.setNumero(request.getParameter("numero"));
                rec.setCorreo(request.getParameter("correo"));

                RecepcionistaDAO dao = new RecepcionistaDAO();
                exito = dao.actualizarRecepcionista(rec);

                String nuevaContrasenaPlana = request.getParameter("contrasena");
                if (nuevaContrasenaPlana != null && !nuevaContrasenaPlana.trim().isEmpty()) {
                    boolean contrasenaActualizada = dao.actualizarContrasenaRecepcionista(id, nuevaContrasenaPlana);
                    if (contrasenaActualizada) {
                        mensaje += " Contraseña actualizada.";
                    } else {
                        error += " No se pudo actualizar la contraseña.";
                        LOGGER.log(Level.WARNING, "Fallo al actualizar contraseña de recepcionista ID: " + id);
                    }
                }

                if (exito || (nuevaContrasenaPlana != null && !nuevaContrasenaPlana.trim().isEmpty() && exito)) {
                    mensaje = (mensaje.isEmpty() ? "" : mensaje + " ") + "Recepcionista actualizado exitosamente.";
                } else {
                    error = (error.isEmpty() ? "" : error + " ") + "No se pudo actualizar el recepcionista. Verifique los datos o si el DNI/Correo ya existe.";
                    LOGGER.log(Level.WARNING, "Fallo al actualizar recepcionista ID: " + id);
                }

            } else {
                error = "Tipo de empleado no válido para actualizar.";
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al actualizar empleado (" + tipoEmpleado + ", ID: " + id + "): " + e.getMessage(), e);
            error = "Error interno del servidor al actualizar empleado: " + e.getMessage();
        }

        if (!mensaje.isEmpty()) {
            request.setAttribute("mensaje", mensaje);
        }
        if (!error.isEmpty()) {
            request.setAttribute("error", error);
        }
        listarEmpleados(request, response);
    }

    private void eliminarEmpleado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tipo = request.getParameter("tipo");
        int id = 0;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido para eliminación: " + request.getParameter("id"), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain");
            response.getWriter().write("ID de empleado inválido.");
            return;
        }

        boolean exito = false;
        String mensajeRespuesta = "";
        int statusCode = HttpServletResponse.SC_OK;

        try {
            if ("veterinario".equals(tipo)) {
                VeterinarioDAO dao = new VeterinarioDAO();
                exito = dao.eliminarVeterinario(id);
                if (exito) {
                    mensajeRespuesta = "Veterinario eliminado exitosamente.";
                    LOGGER.log(Level.INFO, "Veterinario ID " + id + " eliminado exitosamente.");
                } else {
                    mensajeRespuesta = "No se pudo eliminar el veterinario. Puede que tenga registros asociados (ej. citas).";
                    statusCode = HttpServletResponse.SC_CONFLICT;
                    LOGGER.log(Level.WARNING, "Fallo al eliminar veterinario ID: " + id + ". Posiblemente por FK.");
                }
            } else if ("recepcionista".equals(tipo)) {
                RecepcionistaDAO dao = new RecepcionistaDAO();
                exito = dao.eliminarRecepcionista(id);
                if (exito) {
                    mensajeRespuesta = "Recepcionista eliminado exitosamente.";
                    LOGGER.log(Level.INFO, "Recepcionista ID " + id + " eliminado exitosamente.");
                } else {
                    mensajeRespuesta = "No se pudo eliminar el recepcionista. Puede que tenga registros asociados (ej. citas, ventas).";
                    statusCode = HttpServletResponse.SC_CONFLICT;
                    LOGGER.log(Level.WARNING, "Fallo al eliminar recepcionista ID: " + id + ". Posiblemente por FK.");
                }
            } else {
                mensajeRespuesta = "Tipo de empleado no válido para eliminar.";
                statusCode = HttpServletResponse.SC_BAD_REQUEST;
                LOGGER.log(Level.WARNING, "Intento de eliminar con tipo de empleado no válido: " + tipo);
            }

            response.setStatus(statusCode);
            response.setContentType("text/plain");
            response.getWriter().write(mensajeRespuesta);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al eliminar empleado (" + tipo + ", ID: " + id + "): " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            response.getWriter().write("Error interno del servidor al eliminar empleado: " + e.getMessage());
        }
    }

    private void manejarError(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws ServletException, IOException {
        request.setAttribute("error", mensaje);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }
}
