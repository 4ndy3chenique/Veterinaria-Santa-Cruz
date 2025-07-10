package Controladores;

import ModeloDAO.ClienteDAO;
import ModeloDAO.UsuarioClienteDAO;
import Modelo.Cliente;
import Modelo.UsuarioCliente; 
import Modelo.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "AdminClienteServlet", urlPatterns = {"/AdminClienteServlet"})
public class AdminClienteServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminClienteServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection conexion = null;
        try {
            conexion = Conexion.getConnection(); // Obtener una conexión para esta solicitud

            // --- Cargar lista de Clientes Normales ---
            ClienteDAO clienteDAO = new ClienteDAO(conexion); // Inyecta la conexión
            List<Cliente> listaClientes = clienteDAO.listarClientes();
            request.setAttribute("listaClientes", listaClientes);

            // --- Cargar lista de Usuarios Clientes ---
            UsuarioClienteDAO usuarioDAO = new UsuarioClienteDAO(conexion); // Inyecta la conexión
            // Puedes ajustar la cantidad si quieres un límite o usar 0/Integer.MAX_VALUE para todos
            List<UsuarioCliente> listaUsuarios = usuarioDAO.listarUsuariosRecientes(1000); 
            request.setAttribute("listaUsuarios", listaUsuarios);

            // Determinar qué tab debe estar activa (si hay un parámetro en la URL o guardado de un POST)
            String activeTab = request.getParameter("activeTab");
            if (activeTab == null || activeTab.isEmpty()) {
                activeTab = "clientesRegistrados"; // Pestaña por defecto
            }
            request.setAttribute("activeTab", activeTab);

            request.getRequestDispatcher("/VistasWeb/VistasAdmin/ListadoCLientes.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de base de datos en doGet: " + e.getMessage(), e);
            request.setAttribute("error", "Error de base de datos: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } finally {
            if (conexion != null) {
                try {
                    conexion.close(); // Asegúrate de cerrar la conexión
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error al cerrar la conexión en doGet: " + e.getMessage(), e);
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener la acción y la pestaña activa para mantener el estado
        String accion = request.getParameter("accion"); 
        String activeTab = request.getParameter("activeTab");
        if (activeTab == null || activeTab.isEmpty()) {
            activeTab = "clientesRegistrados"; // Por defecto
        }
        request.setAttribute("activeTab", activeTab); // Mantener el estado de la pestaña

        Connection conexion = null;

        try {
            conexion = Conexion.getConnection(); // Obtener una conexión para esta solicitud
            ClienteDAO clienteDAO = new ClienteDAO(conexion);
            UsuarioClienteDAO usuarioDAO = new UsuarioClienteDAO(conexion); // Inyecta la conexión

            if (accion != null) {
                switch (accion) {
                    // --- Acciones para Cliente Normal (pestaña "Clientes Registrados") ---
                    case "editarCliente":
                        editarCliente(request, response, clienteDAO);
                        break;
                    case "eliminarCliente":
                        eliminarCliente(request, response, clienteDAO);
                        break;
                    case "buscarCliente":
                        buscarCliente(request, response, clienteDAO);
                        break;
                    
                    // --- Nuevas Acciones para UsuarioCliente (pestaña "Usuarios Clientes") ---
                    case "editarUsuario":
                        editarUsuario(request, response, usuarioDAO);
                        break;
                    case "eliminarUsuario":
                        eliminarUsuario(request, response, usuarioDAO);
                        break;
                    case "buscarUsuario":
                        buscarUsuario(request, response, usuarioDAO);
                        break;

                    default:
                        // Si no hay una acción específica, recarga la lista predeterminada
                        doGet(request, response);
                        break;
                }
            } else {
                // Si no se proporciona el parámetro de acción, recarga la lista predeterminada
                doGet(request, response);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de base de datos en doPost para acción " + accion + ": " + e.getMessage(), e);
            request.setAttribute("error", "Error de base de datos: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } finally {
            if (conexion != null) {
                try {
                    conexion.close(); // Asegúrate de cerrar la conexión
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error al cerrar la conexión en doPost: " + e.getMessage(), e);
                }
            }
        }
    }

    // --- Métodos Privados para la gestión de Cliente Normal ---

    private void editarCliente(HttpServletRequest request, HttpServletResponse response, ClienteDAO clienteDAO)
            throws ServletException, IOException, SQLException {
        // Obtener parámetros del formulario
        int idCliente = Integer.parseInt(request.getParameter("idCliente"));
        String nuevoNombre = request.getParameter("nombre");
        String nuevoApellido = request.getParameter("apellido");
        String nuevoDni = request.getParameter("dni");
        String nuevoTelefono = request.getParameter("telefono");

        // Crear un objeto Cliente con los datos actualizados
        Cliente cliente = new Cliente();
        cliente.setIdCliente(idCliente);
        cliente.setNombre(nuevoNombre); 
        cliente.setApellido(nuevoApellido);
        cliente.setDni(nuevoDni);
        cliente.setTelefono(nuevoTelefono);

        // Llamar al método del DAO para actualizar el cliente
        boolean actualizado = clienteDAO.actualizarCliente(cliente);

        if (actualizado) {
            request.setAttribute("mensaje", "Cliente general actualizado exitosamente.");
        } else {
            request.setAttribute("error", "No se pudo actualizar el cliente general. Verifique el ID o los datos.");
        }
        // Redirigir de nuevo a la lista de clientes, asegurando que la pestaña correcta esté activa
        request.setAttribute("activeTab", "clientesRegistrados"); 
        doGet(request, response);
    }

    private void eliminarCliente(HttpServletRequest request, HttpServletResponse response, ClienteDAO clienteDAO)
            throws ServletException, IOException, SQLException {
        int idCliente = Integer.parseInt(request.getParameter("idCliente"));

        boolean eliminado = clienteDAO.eliminarCliente(idCliente);

        if (eliminado) {
            request.setAttribute("mensaje", "Cliente general eliminado exitosamente.");
        } else {
            request.setAttribute("error", "No se pudo eliminar el cliente general. Es posible que el cliente no exista.");
        }
        // Redirigir de nuevo a la lista de clientes, asegurando que la pestaña correcta esté activa
        request.setAttribute("activeTab", "clientesRegistrados"); 
        doGet(request, response);
    }

    private void buscarCliente(HttpServletRequest request, HttpServletResponse response, ClienteDAO clienteDAO)
            throws ServletException, IOException, SQLException {
        String textoBusqueda = request.getParameter("textoBusqueda");
        List<Cliente> resultadosBusqueda = null;

        if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
            resultadosBusqueda = clienteDAO.buscarClientes(textoBusqueda);
        } else {
            // Si el texto de búsqueda está vacío, mostrar todos los clientes
            resultadosBusqueda = clienteDAO.listarClientes();
        }

        request.setAttribute("listaClientes", resultadosBusqueda);
        // Asegurar que la pestaña de clientes normales esté activa después de la búsqueda
        request.setAttribute("activeTab", "clientesRegistrados"); 
        
        // Es importante cargar también la lista de UsuarioCliente para que la otra pestaña 
        // no esté vacía si el usuario cambia de pestaña.
        Connection tempCon = null;
        try {
            tempCon = Conexion.getConnection();
            UsuarioClienteDAO usuarioDAO = new UsuarioClienteDAO(tempCon); 
            request.setAttribute("listaUsuarios", usuarioDAO.listarUsuariosRecientes(1000));
        } finally {
            if (tempCon != null) {
                try { tempCon.close(); } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Error al cerrar conexión temp en buscarCliente:", e); }
            }
            tempCon = null; // Set to null to avoid using a closed connection reference
        }
        
        request.getRequestDispatcher("/VistasWeb/VistasAdmin/ListadoCLientes.jsp")
                .forward(request, response);
    }

    // --- Nuevos Métodos Privados para la gestión de UsuarioCliente ---

    private void editarUsuario(HttpServletRequest request, HttpServletResponse response, UsuarioClienteDAO usuarioDAO)
            throws ServletException, IOException, SQLException {
        // Obtener parámetros del formulario
        int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
        String nuevoNombre = request.getParameter("nombre");
        String nuevoApellido = request.getParameter("apellido");
        String nuevoDni = request.getParameter("dni");
        String nuevoTelefono = request.getParameter("telefono");
        String nuevoCorreo = request.getParameter("correo");
        String nuevaContrasena = request.getParameter("contrasena"); 

        // Crear un objeto UsuarioCliente con los datos actualizados
        UsuarioCliente usuario = new UsuarioCliente();
        usuario.setIdUsuario(idUsuario);
        usuario.setNombre(nuevoNombre);
        usuario.setApellido(nuevoApellido);
        usuario.setDni(nuevoDni);
        usuario.setTelefono(nuevoTelefono);
        usuario.setCorreo(nuevoCorreo);
        usuario.setContrasena(nuevaContrasena); // ¡RECORDATORIO: Considera usar hashing para contraseñas!

        // Llamar al método del DAO para actualizar el usuario cliente
        boolean actualizado = usuarioDAO.actualizarUsuarioCliente(usuario);

        if (actualizado) {
            request.setAttribute("mensaje", "Usuario cliente actualizado exitosamente.");
        } else {
            request.setAttribute("error", "No se pudo actualizar el usuario cliente. Verifique el ID o los datos.");
        }
        // Redirigir de nuevo a la lista de usuarios, asegurando que la pestaña correcta esté activa
        request.setAttribute("activeTab", "usuariosClientes"); 
        doGet(request, response);
    }

    private void eliminarUsuario(HttpServletRequest request, HttpServletResponse response, UsuarioClienteDAO usuarioDAO)
            throws ServletException, IOException, SQLException {
        int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));

        boolean eliminado = usuarioDAO.eliminarUsuarioCliente(idUsuario);

        if (eliminado) {
            request.setAttribute("mensaje", "Usuario cliente eliminado exitosamente.");
        } else {
            request.setAttribute("error", "No se pudo eliminar el usuario cliente. Es posible que no exista.");
        }
        // Redirigir de nuevo a la lista de usuarios, asegurando que la pestaña correcta esté activa
        request.setAttribute("activeTab", "usuariosClientes"); 
        doGet(request, response);
    }

    private void buscarUsuario(HttpServletRequest request, HttpServletResponse response, UsuarioClienteDAO usuarioDAO)
            throws ServletException, IOException, SQLException {
        String textoBusqueda = request.getParameter("textoBusqueda");
        List<UsuarioCliente> resultadosBusqueda = null;

        if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
            resultadosBusqueda = usuarioDAO.buscarUsuariosClientes(textoBusqueda);
        } else {
            // Si el texto de búsqueda está vacío, mostrar todos los usuarios
            resultadosBusqueda = usuarioDAO.listarUsuariosRecientes(1000); 
        }

        request.setAttribute("listaUsuarios", resultadosBusqueda);
        // Asegurar que la pestaña de usuarios clientes esté activa después de la búsqueda
        request.setAttribute("activeTab", "usuariosClientes"); 
        
        // Es importante cargar también la lista de Cliente para que la otra pestaña 
        // no esté vacía si el usuario cambia de pestaña.
        Connection tempCon = null;
        try {
            tempCon = Conexion.getConnection();
            ClienteDAO clienteDAO = new ClienteDAO(tempCon);
            request.setAttribute("listaClientes", clienteDAO.listarClientes());
        } finally {
            if (tempCon != null) {
                try { tempCon.close(); } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Error al cerrar conexión temp en buscarUsuario:", e); }
            }
            tempCon = null; // Set to null to avoid using a closed connection reference
        }

        request.getRequestDispatcher("/VistasWeb/VistasAdmin/ListadoCLientes.jsp")
                .forward(request, response);
    }
}