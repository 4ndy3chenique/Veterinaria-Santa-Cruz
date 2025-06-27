package Controladores;

import Modelo.Conexion;
import Modelo.ContactoProveedor;
import ModeloDAO.ContactoProveedorDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet("/ContactoProveedorServlet")
public class ContactoProveedorServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        String idProveedor = request.getParameter("idProveedor");

        try (Connection con = Conexion.getConnection()) {
            ContactoProveedorDAO contactoDAO = new ContactoProveedorDAO(con);

            switch (accion.toLowerCase()) {
                case "agregar":
                    agregarContacto(request, contactoDAO);
                    break;
                case "actualizar":
                    actualizarContacto(request, contactoDAO);
                    break;
                case "eliminar":
                    eliminarContacto(request, contactoDAO);
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Redireccionar siempre a ver el proveedor con el modal abierto
        if (idProveedor != null && idProveedor.matches("\\d+")) {
            response.sendRedirect("ProveedorServlet?accion=ver&id=" + idProveedor);
        } else {
            response.sendRedirect("ProveedorServlet?accion=listar");
        }
    }

    private void agregarContacto(HttpServletRequest request, ContactoProveedorDAO dao) {
        try {
            ContactoProveedor contacto = new ContactoProveedor();
            contacto.setIdProveedor(Integer.parseInt(request.getParameter("idProveedor")));
            contacto.setNombreContacto(request.getParameter("nombreContacto"));
            contacto.setCargo(request.getParameter("cargo"));
            contacto.setTelefono(request.getParameter("telefono"));
            contacto.setCorreoContacto(request.getParameter("correoContacto"));
            dao.agregarContacto(contacto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actualizarContacto(HttpServletRequest request, ContactoProveedorDAO dao) {
        try {
            ContactoProveedor contacto = new ContactoProveedor();
            contacto.setIdContacto(Integer.parseInt(request.getParameter("idContacto")));
            contacto.setIdProveedor(Integer.parseInt(request.getParameter("idProveedor")));
            contacto.setNombreContacto(request.getParameter("nombreContacto"));
            contacto.setCargo(request.getParameter("cargo"));
            contacto.setTelefono(request.getParameter("telefono"));
            contacto.setCorreoContacto(request.getParameter("correoContacto"));
            dao.actualizarContacto(contacto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminarContacto(HttpServletRequest request, ContactoProveedorDAO dao) {
        try {
            int idContacto = Integer.parseInt(request.getParameter("idContacto"));
            dao.eliminarContacto(idContacto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
