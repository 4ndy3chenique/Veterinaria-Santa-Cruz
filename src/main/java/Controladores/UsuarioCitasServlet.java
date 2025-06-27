package Controladores;

import Modelo.UsuarioCitas;
import ModeloDAO.UsuarioCitasDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;

@WebServlet("/UsuarioCitasServlet")
public class UsuarioCitasServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {
            String nombreCliente = request.getParameter("nombreCliente");
            String fechaStr = request.getParameter("fecha");
            String horaStr = request.getParameter("hora");
            String veterinario = request.getParameter("veterinario");
            String estado = request.getParameter("estado");

            // Convertir los parámetros String a los tipos adecuados
            Date fecha = Date.valueOf(fechaStr);
            Time hora = Time.valueOf(horaStr + ":00"); // Asegurar formato HH:MM:SS

            UsuarioCitas cita = new UsuarioCitas();
            cita.setNombreCliente(nombreCliente);
            cita.setFecha(fecha);
            cita.setHora(hora);
            cita.setVeterinario(veterinario);
            cita.setEstado(estado);

            UsuarioCitasDAO dao = new UsuarioCitasDAO();
            boolean exito = dao.registrarCita(cita);

            if (exito) {
                response.sendRedirect(request.getContextPath() + 
                    "/VistasWeb/VistasCliente/historialdecitas.jsp?mensaje=registrado");
            } else {
                response.sendRedirect(request.getContextPath() + 
                    "/VistasWeb/VistasCliente/historialdecitas.jsp?mensaje=error");
            }
        } catch (IllegalArgumentException e) {
            // Manejo de errores en el formato de fecha/hora
            response.sendRedirect(request.getContextPath() + 
                "/VistasWeb/VistasCliente/historialdecitas.jsp?mensaje=formato_invalido");
        } catch (Exception e) {
            // Manejo de otros errores
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + 
                "/VistasWeb/VistasCliente/historialdecitas.jsp?mensaje=error");
        }
    }
}