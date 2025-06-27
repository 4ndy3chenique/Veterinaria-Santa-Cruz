/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author PROPIETARIO
 */
@WebServlet("/FinalizarCompraServlet")
public class FinalizarCompraServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Recibe el producto desde el formulario
        String producto = request.getParameter("producto");

        // Guarda en sesión el historial de compras
        HttpSession session = request.getSession();
        List<String> historial = (List<String>) session.getAttribute("historialCompras");
        if (historial == null) {
            historial = new ArrayList<>();
        }
        historial.add(producto);
        session.setAttribute("historialCompras", historial);

        // Redirige al historial de compras
        response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/historialdecompras.jsp");
    }
    }