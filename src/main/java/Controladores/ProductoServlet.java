package Controladores;

import Modelo.Producto;
import ModeloDAO.ProductoDAO;
import Modelo.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "ProductoServlet", urlPatterns = {"/ProductoServlet"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 15
)
public class ProductoServlet extends HttpServlet {

    private ProductoDAO productoDAO;

    @Override
    public void init() throws ServletException {
        Connection con = Conexion.getConnection();
        productoDAO = new ProductoDAO(con);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
                listarProductos(request, response);
                break;
            case "listarInactivos":
                listarProductosInactivos(request, response);
                break;
            case "ver":
                mostrarDetallesProducto(request, response);
                break;
            case "eliminar":
                eliminarProducto(request, response);
                break;
            default:
                response.sendRedirect("error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        switch (accion) {
            case "guardar":
                guardarProducto(request, response);
                break;
            case "cambiarEstado":
                cambiarEstadoProducto(request, response);
                break;
            case "eliminarDefinitivo":
                eliminarProductoDefinitivo(request, response);
                break;
            default:
                response.sendRedirect("error.jsp");
        }
    }

    private void listarProductos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String busqueda = request.getParameter("busqueda");
            boolean inactivos = "true".equals(request.getParameter("inactivos"));
            List<Producto> productos;

            if (busqueda != null && !busqueda.trim().isEmpty()) {
                productos = productoDAO.buscarPorNombre(busqueda.trim(), inactivos);
            } else {
                productos = inactivos ? productoDAO.listarInactivos() : productoDAO.listarTodos();
            }

            request.setAttribute("productos", productos);
            request.setAttribute("mostrarInactivos", inactivos);
            request.getRequestDispatcher("/VistasWeb/VistasAdmin/GestorProductos.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    private void listarProductosInactivos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Producto> productos = productoDAO.listarInactivos();
            request.setAttribute("productos", productos);
            request.setAttribute("mostrarInactivos", true);
            request.getRequestDispatcher("/VistasWeb/VistasAdmin/GestorProductos.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    private void mostrarDetallesProducto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Producto producto = productoDAO.obtenerPorId(id);
            if (producto != null) {
                request.setAttribute("productoSeleccionado", producto);
                listarProductos(request, response);
            } else {
                response.sendRedirect("ProductoServlet?accion=listar");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    private void guardarProducto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idProducto = 0;
            String idStr = request.getParameter("idProducto");
            if (idStr != null && !idStr.isEmpty()) {
                idProducto = Integer.parseInt(idStr);
            }

            int idProveedor = Integer.parseInt(request.getParameter("idProveedor"));
            String nombre = request.getParameter("nombre");
            String descripcion = request.getParameter("descripcion");
            String unidadMedida = request.getParameter("unidadMedida");
            BigDecimal precio = new BigDecimal(request.getParameter("precio"));
            int stock = Integer.parseInt(request.getParameter("stock"));
            int estado = Integer.parseInt(request.getParameter("estado"));

            Part imagenPart = request.getPart("imagen");
            String nombreArchivo = null;

            if (imagenPart != null && imagenPart.getSize() > 0) {
                String nombreOriginal = imagenPart.getSubmittedFileName();
                nombreArchivo = System.currentTimeMillis() + "_" + nombreOriginal;
                String rutaCarpeta = request.getServletContext().getRealPath("/Recursos/ProductosVenta");
                File carpeta = new File(rutaCarpeta);
                if (!carpeta.exists()) {
                    carpeta.mkdirs();
                }
                imagenPart.write(rutaCarpeta + File.separator + nombreArchivo);
            }

            Producto producto = new Producto();
            producto.setNombreProducto(nombre);
            producto.setDescripcion(descripcion);
            producto.setUnidadMedida(unidadMedida);
            producto.setPrecio(precio);
            producto.setStock(stock);
            producto.setEstado(estado);
            producto.setIdProveedor(idProveedor);
            producto.setIdProducto(idProducto);

            if (nombreArchivo != null) {
                producto.setImagen("Recursos/ProductosVenta/" + nombreArchivo);
            } else {
                Producto actual = productoDAO.obtenerPorId(idProducto);
                if (actual != null) {
                    producto.setImagen(actual.getImagen());
                }
            }

            boolean exito;
            if (idProducto == 0) {
                exito = productoDAO.agregar(producto);
            } else {
                exito = productoDAO.actualizar(producto);
            }

            response.sendRedirect("ProductoServlet?accion=listar");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    private void eliminarProducto(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Producto producto = productoDAO.obtenerPorId(id);
            if (producto != null) {
                productoDAO.eliminar(id);
                // Redirige a la lista correspondiente según el estado del producto
                String redireccion = producto.getEstado() == 1 ? "listar" : "listarInactivos";
                response.sendRedirect("ProductoServlet?accion=" + redireccion);
            } else {
                response.sendRedirect("error.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    private void eliminarProductoDefinitivo(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean exito = productoDAO.eliminarDefinitivo(id);
            
            // Determinar si estamos viendo activos o inactivos
            boolean mostrarInactivos = request.getParameter("mostrarInactivos") != null 
                                    && request.getParameter("mostrarInactivos").equals("true");
            
            // Redirigir manteniendo el contexto de la vista actual
            if(mostrarInactivos) {
                response.sendRedirect("ProductoServlet?accion=listarInactivos");
            } else {
                response.sendRedirect("ProductoServlet?accion=listar");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    private void cambiarEstadoProducto(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            int estadoActual = Integer.parseInt(request.getParameter("estado"));
            int nuevoEstado = (estadoActual == 1) ? 0 : 1;

            Producto producto = productoDAO.obtenerPorId(id);
            if (producto != null) {
                producto.setEstado(nuevoEstado);
                productoDAO.actualizar(producto);
            }

            // Redirige a la lista correspondiente según el nuevo estado
            String redireccion = nuevoEstado == 1 ? "listar" : "listarInactivos";
            response.sendRedirect("ProductoServlet?accion=" + redireccion);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}