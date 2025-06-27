package Controladores; // Asegúrate de que este paquete sea correcto

import Modelo.Veterinario;
import Modelo.Recepcionista;
import ModeloDAO.VeterinarioDAO; // CORRECTO: La importación debe ser desde ModeloDAO
import ModeloDAO.RecepcionistaDAO; // CORRECTO: La importación debe ser desde ModeloDAO

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "EmpleadoServlet", urlPatterns = {"/EmpleadoServlet"})
public class EmpleadoServlet extends HttpServlet {

    private VeterinarioDAO veterinarioDAO;
    private RecepcionistaDAO recepcionistaDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        // Las instanciaciones también deben referirse al paquete ModeloDAO
        veterinarioDAO = new ModeloDAO.VeterinarioDAO();
        recepcionistaDAO = new ModeloDAO.RecepcionistaDAO();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String accion = request.getParameter("accion");

        if (accion == null || accion.isEmpty()) {
            accion = "listar"; // Acción por defecto
        }

        switch (accion) {
            case "listar":
                listarEmpleados(request, response);
                break;
            case "guardarVeterinario":
                guardarVeterinario(request, response);
                break;
            case "eliminarveterinario":
                eliminarVeterinario(request, response);
                break;
            case "guardarRecepcionista":
                guardarRecepcionista(request, response);
                break;
            case "eliminarrecepcionista":
                eliminarRecepcionista(request, response);
                break;
            default:
                listarEmpleados(request, response); // Si la acción no se reconoce, listamos
                break;
        }
    }

    private void listarEmpleados(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Veterinario> listaVeterinarios = veterinarioDAO.listarVeterinarios();
        List<Recepcionista> listaRecepcionistas = recepcionistaDAO.listarRecepcionistas();

        request.setAttribute("listaVeterinarios", listaVeterinarios);
        request.setAttribute("listaRecepcionistas", listaRecepcionistas);

        request.getRequestDispatcher("/VistasWeb/VistasAdmin/empleados.jsp").forward(request, response);
    }

    private void guardarVeterinario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("idVeterinario");
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String numero = request.getParameter("numero");
        String dni = request.getParameter("dni");
        String especialidad = request.getParameter("especialidad");

        Veterinario vet = new Veterinario();
        vet.setNombre(nombre);
        vet.setApellido(apellido);
        vet.setNumero(numero);
        vet.setDni(dni);
        vet.setEspecialidad(especialidad);

        boolean exito;
        String mensaje;
        String tipoMensaje;

        if (idParam != null && !idParam.isEmpty()) { // Es una actualización
            int idVeterinario = Integer.parseInt(idParam);
            vet.setIdVeterinario(idVeterinario);
            exito = veterinarioDAO.actualizarVeterinario(vet);

            if (exito) {
                mensaje = "Veterinario actualizado correctamente.";
                tipoMensaje = "success";
            } else {
                mensaje = "Error al actualizar veterinario.";
                tipoMensaje = "danger";
            }
        } else { // Es un nuevo registro
            exito = veterinarioDAO.agregarVeterinario(vet);
            if (exito) {
                mensaje = "Veterinario agregado correctamente.";
                tipoMensaje = "success";
            } else {
                mensaje = "Error al agregar veterinario.";
                tipoMensaje = "danger";
            }
        }

        request.setAttribute("mensaje", mensaje);
        request.setAttribute("tipoMensaje", tipoMensaje);
        listarEmpleados(request, response); // Volver a listar para ver los cambios
    }

    private void eliminarVeterinario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean exito = veterinarioDAO.eliminarVeterinario(id);

        String mensaje;
        String tipoMensaje;
        if (exito) {
            mensaje = "Veterinario eliminado correctamente.";
            tipoMensaje = "success";
        } else {
            mensaje = "Error al eliminar veterinario.";
            tipoMensaje = "danger";
        }

        request.setAttribute("mensaje", mensaje);
        request.setAttribute("tipoMensaje", tipoMensaje);
        listarEmpleados(request, response);
    }

    private void guardarRecepcionista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("idRecepcionista");
        String nombre = request.getParameter("R_Nombre");
        String apellido = request.getParameter("R_Apellido");
        String numero = request.getParameter("R_Numero");
        String dni = request.getParameter("R_Dni");
        String correo = request.getParameter("R_Correo");
        String contrasena = request.getParameter("R_Contrasena");

        Recepcionista rec = new Recepcionista();
        rec.setNombre(nombre);
        rec.setApellido(apellido);
        rec.setNumero(numero);
        rec.setDni(dni);
        rec.setCorreo(correo);

        boolean exito;
        String mensaje;
        String tipoMensaje;

        if (idParam != null && !idParam.isEmpty()) { // Es una actualización
            int idRecepcionista = Integer.parseInt(idParam);
            rec.setIdRecepcionista(idRecepcionista);

            // Actualizar datos básicos (sin contraseña)
            exito = recepcionistaDAO.actualizarRecepcionista(rec);

            // Si se proporcionó una nueva contraseña, actualizarla por separado
            if (contrasena != null && !contrasena.isEmpty()) {
                // AQUÍ ES DONDE DEBERÍAS HASHEAR LA NUEVA CONTRASEÑA
                // String hashedPassword = PasswordHasher.hash(contrasena);
                // recepcionistaDAO.actualizarContrasenaRecepcionista(idRecepcionista, hashedPassword);
                recepcionistaDAO.actualizarContrasenaRecepcionista(idRecepcionista, contrasena); // TEMPORAL: Inseguro
            }

            if (exito) {
                mensaje = "Recepcionista actualizado correctamente.";
                tipoMensaje = "success";
            } else {
                mensaje = "Error al actualizar recepcionista.";
                tipoMensaje = "danger";
            }

        } else { // Es un nuevo registro
            rec.setContrasena(contrasena); // Asignar la contraseña al objeto antes de agregar
            // AQUÍ ES DONDE DEBERÍAS HASHEAR LA CONTRASEÑA ANTES DE ENVIARLA AL DAO
            // String hashedPassword = PasswordHasher.hash(contrasena);
            // rec.setContrasena(hashedPassword);
            exito = recepcionistaDAO.agregarRecepcionista(rec);

            if (exito) {
                mensaje = "Recepcionista agregado correctamente.";
                tipoMensaje = "success";
            } else {
                mensaje = "Error al agregar recepcionista.";
                tipoMensaje = "danger";
            }
        }

        request.setAttribute("mensaje", mensaje);
        request.setAttribute("tipoMensaje", tipoMensaje);
        listarEmpleados(request, response);
    }

    private void eliminarRecepcionista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean exito = recepcionistaDAO.eliminarRecepcionista(id);

        String mensaje;
        String tipoMensaje;
        if (exito) {
            mensaje = "Recepcionista eliminado correctamente.";
            tipoMensaje = "success";
        } else {
            mensaje = "Error al eliminar recepcionista.";
            tipoMensaje = "danger";
        }

        request.setAttribute("mensaje", mensaje);
        request.setAttribute("tipoMensaje", tipoMensaje);
        listarEmpleados(request, response);
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

    @Override
    public String getServletInfo() {
        return "Servlet para la gestión de empleados (Veterinarios y Recepcionistas)";
    }
}