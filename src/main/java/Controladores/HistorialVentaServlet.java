package Controladores;

import Modelo.HistorialVenta;
import ModeloDAO.HistorialVentaDAO;
import Modelo.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "HistorialVentaServlet", urlPatterns = {"/HistorialVentaServlet"})
public class HistorialVentaServlet extends HttpServlet {

    private HistorialVentaDAO historialDAO;

    @Override
    public void init() throws ServletException {
        Connection con = Conexion.getConnection();
        if (con == null) {
            throw new ServletException("No se pudo establecer conexión a la base de datos.");
        }
        historialDAO = new HistorialVentaDAO(con);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tipo = request.getParameter("tipo");
        if (tipo == null || tipo.isEmpty()) {
            tipo = "todos";
        }

        List<HistorialVenta> ventas;

        if (tipo.equalsIgnoreCase("Recepcionista") || tipo.equalsIgnoreCase("Cliente")) {
            ventas = historialDAO.listarPorTipo(tipo);
        } else {
            ventas = historialDAO.listarTodas();
        }

        request.setAttribute("ventas", ventas);
        request.setAttribute("tipoSeleccionado", tipo);

        request.getRequestDispatcher("/VistasWeb/VistasRecep/HistorialVentas.jsp")
               .forward(request, response);
    }
}
