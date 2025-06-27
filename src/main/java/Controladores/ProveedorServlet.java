package Controladores;

import Modelo.Proveedor;
import ModeloDAO.ProveedorDAO;
import Modelo.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet("/ProveedorServlet")
public class ProveedorServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        try (Connection con = Conexion.getConnection()) {
            ProveedorDAO proveedorDAO = new ProveedorDAO(con);

            switch (accion) {
                case "listar":
                    listar(request, response, proveedorDAO);
                    break;
                case "listarInactivos":
                    listarInactivos(request, response, proveedorDAO);
                    break;
                case "ver":
                    ver(request, response, proveedorDAO);
                    break;
                case "guardar":
                    guardar(request, response, proveedorDAO);
                    break;
                case "eliminar":
                    eliminar(request, response, proveedorDAO);
                    break;
                case "cambiarEstado":
                    cambiarEstado(request, response, proveedorDAO);
                    break;
                case "reactivar":
                    reactivarProveedor(request, response, proveedorDAO);
                    break;
                default:
                    response.sendRedirect("error.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response, ProveedorDAO dao)
            throws ServletException, IOException {
        String filtro = request.getParameter("busqueda");
        List<Proveedor> proveedores;

        if (filtro != null && !filtro.trim().isEmpty()) {
            proveedores = dao.buscarPorNombreODireccion(filtro.trim());
            request.setAttribute("valorBusqueda", filtro);
        } else {
            proveedores = dao.listar();
        }

        request.setAttribute("listaProveedores", proveedores);
        request.setAttribute("vista", "activos"); // << Aquí está el atributo clave
        request.getRequestDispatcher("/VistasWeb/VistasAdmin/GestorProveedores.jsp").forward(request, response);
    }

    private void listarInactivos(HttpServletRequest request, HttpServletResponse response, ProveedorDAO dao)
            throws ServletException, IOException {
        String filtro = request.getParameter("busqueda");
        List<Proveedor> proveedores;

        if (filtro != null && !filtro.trim().isEmpty()) {
            proveedores = dao.buscarPorNombreODireccionInactivos(filtro.trim());
            request.setAttribute("valorBusqueda", filtro);
        } else {
            proveedores = dao.listarInactivos();
        }

        request.setAttribute("listaProveedores", proveedores);
        request.setAttribute("vista", "inactivos"); // << Aquí también
        request.getRequestDispatcher("/VistasWeb/VistasAdmin/GestorProveedores.jsp").forward(request, response);
    }

    private void ver(HttpServletRequest request, HttpServletResponse response, ProveedorDAO dao)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Proveedor proveedor = dao.obtenerPorId(id);
            request.setAttribute("proveedorSeleccionado", proveedor);

            if (proveedor.getEstado() == 1) {
                listar(request, response, dao);
            } else {
                listarInactivos(request, response, dao);
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("ProveedorServlet?accion=listar");
        }
    }

    private void guardar(HttpServletRequest request, HttpServletResponse response, ProveedorDAO dao)
            throws ServletException, IOException {
        int id = 0;
        try {
            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.isEmpty()) {
                id = Integer.parseInt(idStr);
            }
        } catch (NumberFormatException ignored) {
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setRazonSocial(request.getParameter("razonSocial"));
        proveedor.setRuc(request.getParameter("ruc"));
        proveedor.setDireccion(request.getParameter("direccion"));
        proveedor.setCorreo(request.getParameter("correo"));
        proveedor.setEstado(1);

        if (id == 0) {
            dao.agregar(proveedor);
            Proveedor nuevo = dao.obtenerPorRuc(proveedor.getRuc());
            request.setAttribute("proveedorSeleccionado", nuevo);
        } else {
            proveedor.setIdProveedor(id);
            dao.actualizar(proveedor);
            request.setAttribute("proveedorSeleccionado", dao.obtenerPorId(id));
        }

        listar(request, response, dao);
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response, ProveedorDAO dao)
            throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            dao.eliminar(id);
        } catch (NumberFormatException ignored) {
        }
        response.sendRedirect("ProveedorServlet?accion=listar");
    }

    private void cambiarEstado(HttpServletRequest request, HttpServletResponse response, ProveedorDAO dao)
            throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            int estado = Integer.parseInt(request.getParameter("estado"));

            if (estado == 1) {
                dao.eliminar(id); // cambia a inactivo
                response.sendRedirect("ProveedorServlet?accion=listar");
            } else {
                dao.reactivar(id); // cambia a activo
                response.sendRedirect("ProveedorServlet?accion=listarInactivos");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("error.jsp");
        }
    }

    private void reactivarProveedor(HttpServletRequest request, HttpServletResponse response, ProveedorDAO dao)
            throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            dao.reactivar(id);
        } catch (NumberFormatException ignored) {
        }
        response.sendRedirect("ProveedorServlet?accion=listarInactivos");
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
}
