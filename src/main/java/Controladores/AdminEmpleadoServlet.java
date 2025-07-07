package Controladores;

import Modelo.Veterinario;
import Modelo.Recepcionista;
import Modelo.Conexion; // Asegúrate de que tu clase Conexion maneje las conexiones de forma segura.
import ModeloDAO.RecepcionistaDAO;
import ModeloDAO.VeterinarioDAO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Importar HttpSession
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "AdminEmpleadoServlet", urlPatterns = {"/AdminEmpleadoServlet"})
public class AdminEmpleadoServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminEmpleadoServlet.class.getName());

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if (accion == null || accion.isEmpty() || "listar".equals(accion)) {
            listarEmpleados(request, response);
        } else {
            switch (accion) {
                case "obtener":
                    obtenerDatosEmpleadoJson(request, response);
                    break;
                case "eliminar": // Se mantiene aquí para el caso de eliminación directa por GET (ej. enlace)
                    eliminarEmpleado(request, response);
                    break;
                case "nuevo":
                    mostrarFormularioNuevo(request, response);
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
        String tipoEmpleado = request.getParameter("tipoEmpleado"); // Asegúrate de que este parámetro se envíe desde el formulario/modal

        if (accion != null) {
            switch (accion) {
                case "agregar":
                    agregarEmpleado(request, response, tipoEmpleado);
                    break;
                case "actualizar":
                    actualizarEmpleado(request, response, tipoEmpleado);
                    break;
                // Si decides manejar la eliminación por POST (ej. un modal de confirmación con submit POST),
                // la podrías habilitar aquí y eliminar el case "eliminar" de doGet.
                // case "eliminar":
                //     eliminarEmpleado(request, response);
                //     break;
                default:
                    response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar");
        }
    }

    private void listarEmpleados(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchQuery = request.getParameter("query");
        String activeTab = request.getParameter("currentTab");

        // Recuperar mensajes flash de la sesión
        HttpSession session = request.getSession();
        String mensajeFlash = (String) session.getAttribute("mensajeFlash");
        String errorFlash = (String) session.getAttribute("errorFlash");

        if (mensajeFlash != null) {
            request.setAttribute("mensaje", mensajeFlash);
            session.removeAttribute("mensajeFlash"); // Eliminar para que no se muestre de nuevo
        }
        if (errorFlash != null) {
            request.setAttribute("error", errorFlash);
            session.removeAttribute("errorFlash"); // Eliminar para que no se muestre de nuevo
        }

        if (activeTab == null || activeTab.isEmpty()) {
            activeTab = "veterinarios"; // Default tab
        }
        request.setAttribute("activeTab", activeTab);

        VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
        List<Veterinario> allVeterinarios = veterinarioDAO.listarVeterinarios();
        List<Veterinario> filteredVeterinarios = new ArrayList<>();

        RecepcionistaDAO recepcionistaDAO = new RecepcionistaDAO();
        List<Recepcionista> allRecepcionistas = recepcionistaDAO.listarRecepcionistas();
        List<Recepcionista> filteredRecepcionistas = new ArrayList<>();

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String lowerCaseQuery = searchQuery.trim().toLowerCase();

            for (Veterinario vet : allVeterinarios) {
                if ((vet.getNombre() != null && vet.getNombre().toLowerCase().contains(lowerCaseQuery)) ||
                    (vet.getApellido() != null && vet.getApellido().toLowerCase().contains(lowerCaseQuery)) ||
                    (vet.getDni() != null && vet.getDni().toLowerCase().contains(lowerCaseQuery)) ||
                    (vet.getNumero() != null && vet.getNumero().toLowerCase().contains(lowerCaseQuery)) ||
                    (vet.getEspecialidad() != null && vet.getEspecialidad().toLowerCase().contains(lowerCaseQuery))) {
                    filteredVeterinarios.add(vet);
                }
            }

            for (Recepcionista rec : allRecepcionistas) {
                if ((rec.getNombre() != null && rec.getNombre().toLowerCase().contains(lowerCaseQuery)) ||
                    (rec.getApellido() != null && rec.getApellido().toLowerCase().contains(lowerCaseQuery)) ||
                    (rec.getDni() != null && rec.getDni().toLowerCase().contains(lowerCaseQuery)) ||
                    (rec.getNumero() != null && rec.getNumero().toLowerCase().contains(lowerCaseQuery)) ||
                    (rec.getCorreo() != null && rec.getCorreo().toLowerCase().contains(lowerCaseQuery))) {
                    filteredRecepcionistas.add(rec);
                }
            }
            request.setAttribute("searchQuery", searchQuery);
        } else {
            filteredVeterinarios = allVeterinarios;
            filteredRecepcionistas = allRecepcionistas;
        }

        request.setAttribute("listaVeterinarios", filteredVeterinarios);
        request.setAttribute("listaRecepcionistas", filteredRecepcionistas);

        request.getRequestDispatcher("/VistasWeb/VistasAdmin/ListadoEmpleados.jsp")
                .forward(request, response);
    }

    private String escapeJsonString(String text) {
        if (text == null) {
            return ""; // Changed from "null" to "" for cleaner JSON in case of null values
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
                    // Basic ASCII and Latin-1 characters
                    if (c >= ' ' && c <= '~' || (c >= '\u00A0' && c <= '\u00FF')) {
                        sb.append(c);
                    } else {
                        // For other Unicode characters, escape them
                        sb.append(String.format("\\u%04x", (int) c));
                    }
                    break;
            }
        }
        return sb.toString();
    }

    private void obtenerDatosEmpleadoJson(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tipo = request.getParameter("tipoEmpleado");
        int id = 0;
        try {
            id = Integer.parseInt(request.getParameter("idEmpleado"));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID de empleado inválido al intentar obtener datos JSON: " + request.getParameter("idEmpleado"), e);
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
                    // NO incluir la contraseña aquí por seguridad, el campo de contraseña en el modal de edición
                    // debe ser para "nueva contraseña" y se maneja por separado si se desea cambiar.
                    jsonResponse.append("}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\": \"" + escapeJsonString("Recepcionista no encontrado con ID: " + id) + "\"}");
                    return;
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"" + escapeJsonString("Tipo de empleado no válido.") + "\"}");
                return;
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener datos JSON del empleado (ID: " + id + ", Tipo: " + tipo + "): " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + escapeJsonString("Error interno del servidor al obtener datos del empleado: " + e.getMessage()) + "\"}");
        }
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("accion", "agregar");
        request.setAttribute("tipoEmpleado", request.getParameter("tipo")); // Recibe 'veterinario' o 'recepcionista'
        // Forward a la JSP que contiene el formulario o el modal para agregar
        // Si tu modal está en ListadoEmpleados.jsp y se activa por JS, este forward no es estrictamente necesario,
        // pero es útil si tienes una JSP dedicada para el formulario.
        request.getRequestDispatcher("/VistasWeb/VistasAdmin/FormularioEmpleado.jsp").forward(request, response);
    }

    private void agregarEmpleado(HttpServletRequest request, HttpServletResponse response, String tipoEmpleado)
            throws ServletException, IOException {

        String mensaje = "";
        String error = "";
        String activeTab = request.getParameter("currentTab"); // Para redirigir a la pestaña correcta

        try {
            if ("veterinario".equals(tipoEmpleado)) {
                Veterinario vet = new Veterinario();
                vet.setNombre(request.getParameter("nombre"));
                vet.setApellido(request.getParameter("apellido"));
                vet.setDni(request.getParameter("dni"));
                vet.setNumero(request.getParameter("numero"));
                vet.setEspecialidad(request.getParameter("especialidad"));

                VeterinarioDAO dao = new VeterinarioDAO();
                boolean exito = dao.agregarVeterinario(vet);

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
                // *** ATENCIÓN: Contraseña en texto plano para este ejemplo. ¡INSEGURO! ***
                if (contrasenaPlana != null && !contrasenaPlana.isEmpty()) {
                    rec.setContrasena(contrasenaPlana); // Guarda la contraseña tal cual
                } else {
                    error = "La contraseña no puede estar vacía para un nuevo recepcionista.";
                    LOGGER.log(Level.WARNING, "Intento de agregar recepcionista sin contraseña.");
                    request.getSession().setAttribute("errorFlash", error);
                    response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
                    return;
                }

                RecepcionistaDAO dao = new RecepcionistaDAO();
                boolean exito = dao.agregarRecepcionista(rec);

                if (exito) {
                    mensaje = "Recepcionista agregado exitosamente.";
                } else {
                    error = "No se pudo agregar el recepcionista. Verifique los datos, DNI o Correo ya existe.";
                    LOGGER.log(Level.WARNING, "Fallo al agregar recepcionista: " + rec.getDni());
                }
            } else {
                error = "Tipo de empleado no válido para agregar.";
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al agregar empleado (" + tipoEmpleado + "): " + e.getMessage(), e);
            error = "Error interno del servidor al agregar empleado: " + e.getMessage();
        }

        // Usar patrón Post-Redirect-Get (PRG) para evitar doble envío y problemas de recarga
        HttpSession session = request.getSession();
        if (!mensaje.isEmpty()) {
            session.setAttribute("mensajeFlash", mensaje);
        }
        if (!error.isEmpty()) {
            session.setAttribute("errorFlash", error);
        }
        response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
    }

    private void actualizarEmpleado(HttpServletRequest request, HttpServletResponse response, String tipoEmpleado)
            throws ServletException, IOException {

        String mensaje = "";
        String error = "";
        int id = 0;
        String activeTab = request.getParameter("currentTab");

        try {
            id = Integer.parseInt(request.getParameter("idEmpleado"));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido para actualización: " + request.getParameter("idEmpleado"), e);
            error = "ID de empleado inválido para actualización. Recargue la página e intente de nuevo.";
            request.getSession().setAttribute("errorFlash", error);
            response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
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
                boolean exito = dao.actualizarVeterinario(vet);

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
                boolean exitoPrincipal = dao.actualizarRecepcionista(rec); // Actualiza los datos principales

                String nuevaContrasenaPlana = request.getParameter("contrasena"); // Campo de contraseña puede estar vacío
                boolean contrasenaActualizada = true; // Asumir true si no hay nueva contraseña
                // *** ATENCIÓN: Contraseña en texto plano para este ejemplo. ¡INSEGURO! ***
                if (nuevaContrasenaPlana != null && !nuevaContrasenaPlana.trim().isEmpty()) {
                    contrasenaActualizada = dao.actualizarContrasenaRecepcionista(id, nuevaContrasenaPlana); // Pasa la contraseña tal cual
                }

                if (exitoPrincipal && contrasenaActualizada) {
                    mensaje = "Recepcionista actualizado exitosamente.";
                    if (nuevaContrasenaPlana != null && !nuevaContrasenaPlana.trim().isEmpty()) {
                        mensaje += " Contraseña actualizada.";
                    }
                } else if (!exitoPrincipal) {
                    error = "No se pudo actualizar el recepcionista. Verifique los datos o si el DNI/Correo ya existe.";
                    LOGGER.log(Level.WARNING, "Fallo al actualizar datos principales de recepcionista ID: " + id);
                } else if (!contrasenaActualizada) {
                    error = "No se pudo actualizar la contraseña del recepcionista.";
                    LOGGER.log(Level.WARNING, "Fallo al actualizar contraseña de recepcionista ID: " + id);
                } else { // Caso en que exitoPrincipal es true, pero contrasenaActualizada es false, y error ya tiene mensaje
                    mensaje = "Recepcionista actualizado exitosamente, pero la contraseña no se pudo actualizar.";
                    LOGGER.log(Level.WARNING, "Fallo al actualizar contraseña de recepcionista ID: " + id + " pero datos principales actualizados.");
                }

            } else {
                error = "Tipo de empleado no válido para actualizar.";
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al actualizar empleado (" + tipoEmpleado + ", ID: " + id + "): " + e.getMessage(), e);
            error = "Error interno del servidor al actualizar empleado: " + e.getMessage();
        }

        // Usar patrón Post-Redirect-Get (PRG)
        HttpSession session = request.getSession();
        if (!mensaje.isEmpty()) {
            session.setAttribute("mensajeFlash", mensaje);
        }
        if (!error.isEmpty()) {
            session.setAttribute("errorFlash", error);
        }
        response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
    }

    private void eliminarEmpleado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tipo = request.getParameter("tipoEmpleado");
        int id = 0;
        String activeTab = request.getParameter("currentTab");

        String mensajeRespuesta = "";
        String errorRespuesta = "";

        try {
            id = Integer.parseInt(request.getParameter("idEmpleado"));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido para eliminación: " + request.getParameter("idEmpleado"), e);
            errorRespuesta = "ID de empleado inválido para eliminación. Recargue la página e intente de nuevo.";
            request.getSession().setAttribute("errorFlash", errorRespuesta);
            response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
            return;
        }

        try {
            boolean exito = false;
            if ("veterinario".equals(tipo)) {
                VeterinarioDAO dao = new VeterinarioDAO();
                exito = dao.eliminarVeterinario(id);
                if (exito) {
                    mensajeRespuesta = "Veterinario eliminado exitosamente.";
                    LOGGER.log(Level.INFO, "Veterinario ID " + id + " eliminado exitosamente.");
                } else {
                    errorRespuesta = "No se pudo eliminar el veterinario. Puede que tenga registros asociados (ej. citas).";
                    LOGGER.log(Level.WARNING, "Fallo al eliminar veterinario ID: " + id + ". Posiblemente por FK.");
                }
            } else if ("recepcionista".equals(tipo)) {
                RecepcionistaDAO dao = new RecepcionistaDAO();
                exito = dao.eliminarRecepcionista(id);
                if (exito) {
                    mensajeRespuesta = "Recepcionista eliminado exitosamente.";
                    LOGGER.log(Level.INFO, "Recepcionista ID " + id + " eliminado exitosamente.");
                } else {
                    errorRespuesta = "No se pudo eliminar el recepcionista. Puede que tenga registros asociados (ej. citas, ventas).";
                    LOGGER.log(Level.WARNING, "Fallo al eliminar recepcionista ID: " + id + ". Posiblemente por FK.");
                }
            } else {
                errorRespuesta = "Tipo de empleado no válido para eliminar.";
                LOGGER.log(Level.WARNING, "Intento de eliminar con tipo de empleado no válido: " + tipo);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al eliminar empleado (" + tipo + ", ID: " + id + "): " + e.getMessage(), e);
            errorRespuesta = "Error interno del servidor al eliminar empleado: " + e.getMessage();
        } finally {
            // Usar patrón Post-Redirect-Get (PRG)
            HttpSession session = request.getSession();
            if (!mensajeRespuesta.isEmpty()) {
                session.setAttribute("mensajeFlash", mensajeRespuesta);
            }
            if (!errorRespuesta.isEmpty()) {
                session.setAttribute("errorFlash", errorRespuesta);
            }
            response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
        }
    }
}