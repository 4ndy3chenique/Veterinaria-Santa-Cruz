package Controladores;

import Modelo.Producto;
import Modelo.CarritoItem;
import Modelo.Recepcionista;
import ModeloDAO.ProductoRecepDAO;
import Modelo.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.Connection;
import java.util.*;

@WebServlet(name = "ProductoRecepServlet", urlPatterns = {"/ProductoRecepServlet"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 10 * 1024 * 1024,
        maxRequestSize = 15 * 1024 * 1024
)
public class ProductoRecepServlet extends HttpServlet {

    private ProductoRecepDAO productoDAO;

    @Override
    public void init() throws ServletException {
        Connection con = Conexion.getConnection();
        if (con == null) {
            throw new ServletException("No se pudo establecer conexión a la base de datos.");
        }
        productoDAO = new ProductoRecepDAO(con);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listarProductos(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            response.sendRedirect("ProductoRecepServlet");
            return;
        }

        switch (accion) {
            case "agregarAlCarrito":
                agregarAlCarrito(request, response);
                break;
            case "procesarVenta":
                procesarVenta(request, response);
                break;
            case "eliminarDelCarrito":
                eliminarDelCarrito(request, response);
                break;
            default:
                response.sendRedirect("ProductoRecepServlet");
        }
    }

    private void listarProductos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Producto> productos = productoDAO.listarTodos();

        HttpSession session = request.getSession();
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }

        request.setAttribute("productos", productos);
        request.setAttribute("carrito", carrito);

        request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionProductosR.jsp").forward(request, response);
    }

    private void agregarAlCarrito(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        int idProducto;
        try {
            idProducto = Integer.parseInt(request.getParameter("idProducto"));
        } catch (NumberFormatException e) {
            session.setAttribute("mensaje", "ID de producto inválido.");
            response.sendRedirect("ProductoRecepServlet");
            return;
        }

        int cantidad = 1;

        boolean encontrado = false;
        for (CarritoItem item : carrito) {
            if (item.getIdCarrito() == idProducto) {
                item.setCantidad(item.getCantidad() + cantidad);
                item.setSubtotal(item.getPrecio().multiply(new java.math.BigDecimal(item.getCantidad())));
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            Producto producto = productoDAO.obtenerPorId(idProducto);
            if (producto != null && producto.getStock() >= cantidad) {
                CarritoItem item = new CarritoItem();
                item.setIdCarrito(idProducto);
                item.setNombreProducto(producto.getNombreProducto());
                item.setPrecio(producto.getPrecio());
                item.setCantidad(cantidad);
                item.setSubtotal(producto.getPrecio().multiply(new java.math.BigDecimal(cantidad)));
                carrito.add(item);
            } else {
                session.setAttribute("mensaje", "Producto no encontrado o sin stock.");
            }
        }

        session.setAttribute("carrito", carrito);
        session.setAttribute("abrirModal", true);
        response.sendRedirect("ProductoRecepServlet");
    }

    private void eliminarDelCarrito(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();

        int idProducto;
        try {
            idProducto = Integer.parseInt(request.getParameter("idProducto"));
        } catch (NumberFormatException e) {
            session.setAttribute("mensaje", "ID de producto inválido.");
            response.sendRedirect("ProductoRecepServlet");
            return;
        }

        carrito.removeIf(item -> item.getIdCarrito() == idProducto);

        session.setAttribute("carrito", carrito);
        session.setAttribute("abrirModal", true);
        response.sendRedirect("ProductoRecepServlet");
    }

    private void procesarVenta(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();

        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null || carrito.isEmpty()) {
            session.setAttribute("mensaje", "El carrito está vacío.");
            response.sendRedirect("ProductoRecepServlet");
            return;
        }

        
        Integer idRecepcionista = (Integer) session.getAttribute("idRecepcionista");
        if (idRecepcionista == null || idRecepcionista <= 0) {
            Recepcionista r = (Recepcionista) session.getAttribute("recepcionista");
            if (r != null) {
                idRecepcionista = r.getIdRecepcionista();
                session.setAttribute("idRecepcionista", idRecepcionista);
            }
        }

        if (idRecepcionista == null || idRecepcionista <= 0) {
            session.setAttribute("mensaje", "Error interno: no se encontró sesión activa de recepcionista.");
            response.sendRedirect("ProductoRecepServlet");
            return;
        }

        boolean ventaExitosa = productoDAO.venderCarrito(idRecepcionista, carrito);

        if (ventaExitosa) {
            session.removeAttribute("carrito");
            session.setAttribute("mensaje", "Venta registrada correctamente.");
            response.sendRedirect("HistorialVentaServlet");
        } else {
            session.setAttribute("mensaje", "Error al registrar la venta. Verifique stock.");
            response.sendRedirect("ProductoRecepServlet");
        }
    }
}
