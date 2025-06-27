package Controladores;

import ModeloDAO.ClienteDAO;
import ModeloDAO.CitaDAO; // ✅ Importar CitaDAO
import Modelo.Cliente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ClienteRServlet", urlPatterns = {"/ClienteRServlet"})
public class ClienteRServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection conexion = null;
        try {
            conexion = Modelo.Conexion.getConnection();
            ClienteDAO clienteDAO = new ClienteDAO(conexion);
            CitaDAO citaDAO = new CitaDAO(); // ✅ Instanciamos CitaDAO sin conexión

            String accion = request.getParameter("accion");

            if (accion == null || accion.equals("listar")) {
                String busqueda = request.getParameter("busqueda");
                List<Cliente> clientes = (busqueda != null && !busqueda.trim().isEmpty())
                        ? clienteDAO.buscarClientes(busqueda)
                        : clienteDAO.listarClientes();

                request.setAttribute("listaClientes", clientes);
                request.setAttribute("listaVeterinarios", citaDAO.listarVeterinariosParaDropdown()); // ✅ combo lleno
                request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionClientes.jsp").forward(request, response);

            } else if (accion.equals("ver")) {
                int id = Integer.parseInt(request.getParameter("id"));
                Cliente cliente = clienteDAO.obtenerClientePorId(id);

                if (cliente != null) {
                    request.setAttribute("clienteSeleccionado", cliente);
                    request.setAttribute("mensaje", "Cliente cargado para edición");
                    request.setAttribute("tipoMensaje", "exito");
                } else {
                    request.setAttribute("mensaje", "No se encontró el cliente solicitado");
                    request.setAttribute("tipoMensaje", "error");
                }

                request.setAttribute("listaClientes", clienteDAO.listarClientes());
                request.setAttribute("listaVeterinarios", citaDAO.listarVeterinariosParaDropdown()); // ✅ combo lleno
                request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionClientes.jsp").forward(request, response);

            } else if (accion.equals("eliminar")) {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean eliminado = clienteDAO.eliminarCliente(id);

                request.setAttribute("mensaje", eliminado ? "Cliente eliminado correctamente" : "No se pudo eliminar el cliente");
                request.setAttribute("tipoMensaje", eliminado ? "exito" : "error");

                request.setAttribute("listaClientes", clienteDAO.listarClientes());
                request.setAttribute("listaVeterinarios", citaDAO.listarVeterinariosParaDropdown()); // ✅ combo lleno
                request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionClientes.jsp").forward(request, response);
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("mensaje", "Error: " + e.getMessage());
            request.setAttribute("tipoMensaje", "error");
            request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionClientes.jsp").forward(request, response);
        } finally {
            if (conexion != null) {
                try {
                    conexion.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection conexion = null;
        try {
            conexion = Modelo.Conexion.getConnection();
            ClienteDAO clienteDAO = new ClienteDAO(conexion);
            CitaDAO citaDAO = new CitaDAO(); // ✅ también en POST

            String accion = request.getParameter("accion");

            if (accion.equals("guardar")) {
                Cliente cliente = new Cliente();
                String mensaje = "", tipoMensaje = "";

                if (request.getParameter("nombre") == null || request.getParameter("nombre").trim().isEmpty() ||
                    request.getParameter("apellido") == null || request.getParameter("apellido").trim().isEmpty() ||
                    request.getParameter("dni") == null || request.getParameter("dni").trim().isEmpty() ||
                    request.getParameter("telefono") == null || request.getParameter("telefono").trim().isEmpty()) {

                    mensaje = "Todos los campos son obligatorios";
                    tipoMensaje = "error";
                } else {
                    if (request.getParameter("id") != null && !request.getParameter("id").isEmpty()) {
                        cliente.setIdCliente(Integer.parseInt(request.getParameter("id")));
                    }

                    cliente.setNombre(request.getParameter("nombre").trim());
                    cliente.setApellido(request.getParameter("apellido").trim());
                    cliente.setDni(request.getParameter("dni").trim());
                    cliente.setTelefono(request.getParameter("telefono").trim());

                    if (!cliente.getDni().matches("\\d{8}")) {
                        mensaje = "El DNI debe tener exactamente 8 dígitos";
                        tipoMensaje = "error";
                    } else if (!cliente.getTelefono().matches("9\\d{8}")) {
                        mensaje = "El teléfono debe tener 9 dígitos y comenzar con 9";
                        tipoMensaje = "error";
                    } else {
                        boolean exito = (cliente.getIdCliente() > 0)
                                ? clienteDAO.actualizarCliente(cliente)
                                : clienteDAO.insertarCliente(cliente);

                        if (exito) {
                            mensaje = (cliente.getIdCliente() > 0) ? "Cliente actualizado correctamente" : "Cliente registrado correctamente";
                            tipoMensaje = "exito";
                        } else {
                            mensaje = "No se pudo guardar el cliente";
                            tipoMensaje = "error";
                        }
                    }
                }

                request.setAttribute("mensaje", mensaje);
                request.setAttribute("tipoMensaje", tipoMensaje);
                if (tipoMensaje.equals("error") && cliente.getIdCliente() > 0) {
                    request.setAttribute("clienteSeleccionado", cliente);
                }

                request.setAttribute("listaClientes", clienteDAO.listarClientes());
                request.setAttribute("listaVeterinarios", citaDAO.listarVeterinariosParaDropdown()); // ✅ combo lleno
                request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionClientes.jsp").forward(request, response);
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("mensaje", "Error: " + e.getMessage());
            request.setAttribute("tipoMensaje", "error");
            request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionClientes.jsp").forward(request, response);
        } finally {
            if (conexion != null) {
                try {
                    conexion.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}