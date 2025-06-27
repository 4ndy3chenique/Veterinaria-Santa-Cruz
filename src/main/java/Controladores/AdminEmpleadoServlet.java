package Controladores;

import Modelo.Veterinario;
import Modelo.Recepcionista;
import Modelo.Conexion;
import ModeloDAO.RecepcionistaDAO;
import ModeloDAO.VeterinarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level; // Añadido para logging
import java.util.logging.Logger; // Añadido para logging

@WebServlet(name = "AdminEmpleadoServlet", urlPatterns = {"/AdminEmpleadoServlet"})
public class AdminEmpleadoServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminEmpleadoServlet.class.getName()); // Añadido para logging

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        String tipoEmpleado = request.getParameter("tipo"); // Aunque no se usa directamente aquí, se mantiene.

        if (accion == null || accion.isEmpty()) {
            listarEmpleados(request, response);
        } else {
            switch (accion) {
                case "ver":
                    mostrarEmpleado(request, response);
                    break;
                case "editar":
                    mostrarFormularioEdicion(request, response);
                    break;
                case "eliminar":
                    eliminarEmpleado(request, response);
                    break;
                case "nuevo": // Añadir caso para mostrar formulario de nuevo empleado
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

        String accion = request.getParameter("accion");
        String tipoEmpleado = request.getParameter("tipoEmpleado");

        if (accion != null) {
            switch (accion) {
                case "agregar":
                    agregarEmpleado(request, response, tipoEmpleado);
                    break;
                case "editar":
                    actualizarEmpleado(request, response, tipoEmpleado);
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

        Connection conexion = null;
        conexion = Conexion.getConnection(); // Si los DAOs manejan su propia conexión, no necesitas cerrar aquí.
        // Si los DAOs reciben la conexión, debes cerrarla aquí.
        // Para simplificar y evitar problemas de cierre prematuro, los DAOs deben obtener y cerrar su propia conexión.
        // Por lo tanto, quitar la llamada a cerrarConexion(conexion) si los DAOs se instancian sin pasar la conexión.
        // cerrarConexion(conexion); // Comentado si los DAOs manejan su propia conexión
        if (conexion == null) {
            LOGGER.log(Level.SEVERE, "Conexión a la BD nula en listarEmpleados.");
            manejarError(request, response, "Error interno del servidor: No se pudo conectar a la base de datos.");
            return;
        }
        // Es mejor instanciar los DAOs sin pasar la conexión, y que cada DAO la obtenga y cierre.
        // Si el DAO no tiene un constructor que acepta Connection, esto causará un error.
        // Asumo que ya has corregido los DAOs para que NO tomen Connection en el constructor.
        // Si tus DAOs *sí* tienen constructor con Connection, déjalos como estaban, pero
        // asegúrate de que el cierre de la conexión se haga solo UNA VEZ al final del servlet.
        
        // --- Opción 1: DAOs manejan su propia conexión (recomendado si no usas Pool de conexiones) ---
        VeterinarioDAO veterinarioDAO = new VeterinarioDAO(); // Sin pasar conexión
        List<Veterinario> listaVeterinarios = veterinarioDAO.listarVeterinarios();
        request.setAttribute("listaVeterinarios", listaVeterinarios);
        RecepcionistaDAO recepcionistaDAO = new RecepcionistaDAO(); // Sin pasar conexión
        List<Recepcionista> listaRecepcionistas = recepcionistaDAO.listarRecepcionistas();
        request.setAttribute("listaRecepcionistas", listaRecepcionistas);
        
        // --- Si tus DAOs AÚN toman una Connection en el constructor, entonces: ---
        // VeterinarioDAO veterinarioDAO = new VeterinarioDAO(conexion);
        // List<Veterinario> listaVeterinarios = veterinarioDAO.listarVeterinarios();
        // request.setAttribute("listaVeterinarios", listaVeterinarios);
        
        // RecepcionistaDAO recepcionistaDAO = new RecepcionistaDAO(conexion);
        // List<Recepcionista> listaRecepcionistas = recepcionistaDAO.listarRecepcionistas();
        // request.setAttribute("listaRecepcionistas", listaRecepcionistas);
        // --- Fin de Opción 2 ---
        request.getRequestDispatcher("/VistasWeb/VistasAdmin/ListadoEmpleados.jsp")
                .forward(request, response);
    }

    private void mostrarEmpleado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tipo = request.getParameter("tipo");
        int id = Integer.parseInt(request.getParameter("id"));
        Connection conexion = null; // No es necesario aquí si los DAOs manejan su propia conexión

        try {
            // conexion = Conexion.getConnection(); // Comentado si los DAOs manejan su propia conexión

            if ("veterinario".equals(tipo)) {
                VeterinarioDAO dao = new VeterinarioDAO(); // Sin pasar conexión
                Veterinario vet = dao.obtenerVeterinarioPorId(id); // Asumiendo que el método es obtenerVeterinarioPorId
                if (vet != null) {
                    request.setAttribute("empleado", vet);
                    request.setAttribute("tipoEmpleado", "veterinario");
                } else {
                    manejarError(request, response, "Veterinario no encontrado con ID: " + id);
                    return;
                }
            } else { // Asume que el otro tipo es recepcionista
                RecepcionistaDAO dao = new RecepcionistaDAO(); // Sin pasar conexión
                Recepcionista rec = dao.obtenerRecepcionistaPorId(id); // Asumiendo que el método es obtenerRecepcionistaPorId
                 if (rec != null) {
                    request.setAttribute("empleado", rec);
                    request.setAttribute("tipoEmpleado", "recepcionista");
                } else {
                    manejarError(request, response, "Recepcionista no encontrado con ID: " + id);
                    return;
                }
            }

            request.getRequestDispatcher("/VistasWeb/VistasAdmin/DetalleEmpleado.jsp")
                   .forward(request, response);

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID de empleado inválido: " + request.getParameter("id"), e);
            manejarError(request, response, "ID de empleado inválido.");
        } catch (Exception e) { // Capturar Exception general si SQLException no es suficiente
            LOGGER.log(Level.SEVERE, "Error al mostrar empleado: " + e.getMessage(), e);
            manejarError(request, response, "Error al mostrar empleado: " + e.getMessage());
        } finally {
            // cerrarConexion(conexion); // Comentado si los DAOs manejan su propia conexión
        }
    }

    private void mostrarFormularioEdicion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tipo = request.getParameter("tipo");
        int id = 0;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido para edición: " + request.getParameter("id"), e);
            manejarError(request, response, "ID de empleado inválido para edición.");
            return;
        }

        Connection conexion = null; // No es necesario aquí si los DAOs manejan su propia conexión

        try {
            // conexion = Conexion.getConnection(); // Comentado si los DAOs manejan su propia conexión

            if ("veterinario".equals(tipo)) {
                VeterinarioDAO dao = new VeterinarioDAO(); // Sin pasar conexión
                Veterinario vet = dao.obtenerVeterinarioPorId(id); // Usar obtenerVeterinarioPorId
                if (vet != null) {
                    request.setAttribute("empleado", vet);
                    request.setAttribute("tipoEmpleado", "veterinario");
                } else {
                    manejarError(request, response, "Veterinario no encontrado para edición con ID: " + id);
                    return;
                }
            } else { // Asume que el otro tipo es recepcionista
                RecepcionistaDAO dao = new RecepcionistaDAO(); // Sin pasar conexión
                Recepcionista rec = dao.obtenerRecepcionistaPorId(id); // Usar obtenerRecepcionistaPorId
                if (rec != null) {
                    request.setAttribute("empleado", rec);
                    request.setAttribute("tipoEmpleado", "recepcionista");
                } else {
                    manejarError(request, response, "Recepcionista no encontrado para edición con ID: " + id);
                    return;
                }
            }

            request.setAttribute("accion", "editar");
            request.getRequestDispatcher("/VistasWeb/VistasAdmin/FormularioEmpleado.jsp")
                   .forward(request, response);

        } catch (Exception e) { // Capturar Exception general si SQLException no es suficiente
            LOGGER.log(Level.SEVERE, "Error al cargar formulario de edición: " + e.getMessage(), e);
            manejarError(request, response, "Error al cargar formulario: " + e.getMessage());
        } finally {
            // cerrarConexion(conexion); // Comentado si los DAOs manejan su propia conexión
        }
    }
    
    // Nuevo método para mostrar el formulario de agregar
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("accion", "agregar"); // Para que el formulario sepa que es una operación de agregar
        request.setAttribute("tipoEmpleado", request.getParameter("tipo")); // Para preseleccionar el tipo
        request.getRequestDispatcher("/VistasWeb/VistasAdmin/FormularioEmpleado.jsp").forward(request, response);
    }

    private void agregarEmpleado(HttpServletRequest request, HttpServletResponse response, String tipoEmpleado)
            throws ServletException, IOException {

        // Connection conexion = null; // No es necesario si los DAOs manejan su propia conexión
        try {
            // conexion = Conexion.getConnection(); // Comentado si los DAOs manejan su propia conexión
            // if (conexion == null) { ... } // Ya no es necesario aquí

            boolean exito = false;
            if ("veterinario".equals(tipoEmpleado)) {
                Veterinario vet = new Veterinario();
                vet.setNombre(request.getParameter("nombre"));
                vet.setApellido(request.getParameter("apellido"));
                vet.setDni(request.getParameter("dni"));
                vet.setNumero(request.getParameter("numero"));
                vet.setEspecialidad(request.getParameter("especialidad"));

                VeterinarioDAO dao = new VeterinarioDAO(); // Sin pasar conexión
                exito = dao.agregarVeterinario(vet);

                if (exito) {
                    request.setAttribute("mensaje", "Veterinario agregado exitosamente");
                } else {
                    request.setAttribute("error", "No se pudo agregar el veterinario. Verifique los datos o si el DNI ya existe.");
                }
            } else if ("recepcionista".equals(tipoEmpleado)) { // Añadir else if para recepcionista
                Recepcionista rec = new Recepcionista();
                rec.setNombre(request.getParameter("nombre"));
                rec.setApellido(request.getParameter("apellido"));
                rec.setDni(request.getParameter("dni"));
                rec.setNumero(request.getParameter("numero"));
                rec.setCorreo(request.getParameter("correo"));
                rec.setContrasena(request.getParameter("contrasena")); // Asegúrate de hashear la contraseña en un entorno real

                RecepcionistaDAO dao = new RecepcionistaDAO(); // Sin pasar conexión
                exito = dao.agregarRecepcionista(rec);

                if (exito) {
                    request.setAttribute("mensaje", "Recepcionista agregado exitosamente");
                } else {
                    request.setAttribute("error", "No se pudo agregar el recepcionista. Verifique los datos o si el DNI/Correo ya existe.");
                }
            } else {
                request.setAttribute("error", "Tipo de empleado no válido para agregar.");
            }

            listarEmpleados(request, response); // Redirige a la lista de empleados después de agregar

        } catch (Exception e) { // Capturar Exception general si SQLException no es suficiente
            LOGGER.log(Level.SEVERE, "Error al agregar empleado: " + e.getMessage(), e);
            manejarError(request, response, "Error al agregar empleado: " + e.getMessage());
        } finally {
            // cerrarConexion(conexion); // Comentado si los DAOs manejan su propia conexión
        }
    }

    private void actualizarEmpleado(HttpServletRequest request, HttpServletResponse response, String tipoEmpleado)
            throws ServletException, IOException {

        // Connection conexion = null; // No es necesario si los DAOs manejan su propia conexión
        try {
            // conexion = Conexion.getConnection(); // Comentado si los DAOs manejan su propia conexión
            // if (conexion == null) { ... } // Ya no es necesario aquí

            int id = 0;
            try {
                id = Integer.parseInt(request.getParameter("idEmpleado")); // Asegúrate de que el nombre del parámetro sea correcto
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "ID inválido para actualización: " + request.getParameter("idEmpleado"), e);
                manejarError(request, response, "ID de empleado inválido para actualización.");
                return;
            }

            boolean exito = false;
            if ("veterinario".equals(tipoEmpleado)) {
                Veterinario vet = new Veterinario();
                vet.setIdVeterinario(id);
                vet.setNombre(request.getParameter("nombre"));
                vet.setApellido(request.getParameter("apellido"));
                vet.setDni(request.getParameter("dni"));
                vet.setNumero(request.getParameter("numero"));
                vet.setEspecialidad(request.getParameter("especialidad"));

                VeterinarioDAO dao = new VeterinarioDAO(); // Sin pasar conexión
                exito = dao.actualizarVeterinario(vet);

                if (exito) {
                    request.setAttribute("mensaje", "Veterinario actualizado exitosamente");
                } else {
                    request.setAttribute("error", "No se pudo actualizar el veterinario.");
                }
            } else if ("recepcionista".equals(tipoEmpleado)) { // Añadir else if para recepcionista
                Recepcionista rec = new Recepcionista();
                rec.setIdRecepcionista(id);
                rec.setNombre(request.getParameter("nombre"));
                rec.setApellido(request.getParameter("apellido"));
                rec.setDni(request.getParameter("dni"));
                rec.setNumero(request.getParameter("numero"));
                rec.setCorreo(request.getParameter("correo"));

                // Solo actualizar contraseña si se proporcionó una nueva
                String nuevaContrasena = request.getParameter("contrasena");
                if (nuevaContrasena != null && !nuevaContrasena.isEmpty()) {
                    rec.setContrasena(nuevaContrasena); // Asegúrate de hashear la contraseña
                }
                
                RecepcionistaDAO dao = new RecepcionistaDAO(); // Sin pasar conexión
                exito = dao.actualizarRecepcionista(rec);

                if (exito) {
                    request.setAttribute("mensaje", "Recepcionista actualizado exitosamente");
                } else {
                    request.setAttribute("error", "No se pudo actualizar el recepcionista.");
                }
            } else {
                request.setAttribute("error", "Tipo de empleado no válido para actualizar.");
            }

            listarEmpleados(request, response); // Redirige a la lista después de actualizar

        } catch (Exception e) { // Capturar Exception general si SQLException no es suficiente
            LOGGER.log(Level.SEVERE, "Error al actualizar empleado: " + e.getMessage(), e);
            manejarError(request, response, "Error al actualizar empleado: " + e.getMessage());
        } finally {
            // cerrarConexion(conexion); // Comentado si los DAOs manejan su propia conexión
        }
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
            response.getWriter().write("ID de empleado inválido.");
            return;
        }

        // Connection conexion = null; // No es necesario si los DAOs manejan su propia conexión
        try {
            // conexion = Conexion.getConnection(); // Comentado si los DAOs manejan su propia conexión
            // if (conexion == null) { ... } // Ya no es necesario aquí

            boolean exito = false;

            if ("veterinario".equals(tipo)) {
                VeterinarioDAO dao = new VeterinarioDAO(); // Sin pasar conexión
                exito = dao.eliminarVeterinario(id);
            } else if ("recepcionista".equals(tipo)) { // Añadir else if para recepcionista
                RecepcionistaDAO dao = new RecepcionistaDAO(); // Sin pasar conexión
                exito = dao.eliminarRecepcionista(id);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Tipo de empleado no válido para eliminar.");
                return;
            }

            if (exito) {
                response.setContentType("text/plain");
                response.getWriter().write(tipo + " eliminado exitosamente");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("No se pudo eliminar el " + tipo + ". Puede que tenga registros asociados.");
            }

        } catch (Exception e) { // Capturar Exception general si SQLException no es suficiente
            LOGGER.log(Level.SEVERE, "Error al eliminar empleado: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al eliminar empleado: " + e.getMessage());
        } finally {
            // cerrarConexion(conexion); // Comentado si los DAOs manejan su propia conexión
        }
    }

    private void manejarError(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws ServletException, IOException {
        request.setAttribute("error", mensaje);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }

    // Este método ya NO es necesario si los DAOs manejan su propia conexión
    // Quítalo si tus DAOs obtienen y cierran la conexión internamente.
    // Lo comento aquí para que lo decidas.
    /*
    private void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error al cerrar la conexión: " + e.getMessage(), e);
            }
        }
    }
    */
}