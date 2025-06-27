package Controladores;

import Modelo.UsuarioCitaRecep;
import ModeloDAO.UsuarioCitaRecepDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@WebServlet("/UsuarioCitaRecepServlet")
public class UsuarioCitaRecepServlet extends HttpServlet {
    
    private UsuarioCitaRecepDAO dao = new UsuarioCitaRecepDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        try {
            switch (accion) {
                case "nuevo":
                    mostrarFormulario(request, response);
                    break;
                case "ver":
                    verCita(request, response);
                    break;
                case "editar":
                    mostrarFormularioEdicion(request, response);
                    break;
                case "eliminar":
                    eliminarCita(request, response);
                    break;
                default:
                    listarCitas(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("UsuarioCitaRecepServlet?error=true");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        if (accion == null) accion = "guardar";

        try {
            switch (accion) {
                case "guardar":
                    guardarCita(request, response);
                    break;
                case "actualizar":
                    actualizarCita(request, response);
                    break;
                default:
                    listarCitas(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("UsuarioCitaRecepServlet?error=true");
        }
    }

    private void listarCitas(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<UsuarioCitaRecep> lista = dao.listar();
        request.setAttribute("listaCitas", lista);
        request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionCitas.jsp").forward(request, response);
    }

    private void verCita(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        UsuarioCitaRecep cita = dao.obtenerPorId(id);
        request.setAttribute("cita", cita);
        request.getRequestDispatcher("/VistasWeb/VistasRecep/VerCita.jsp").forward(request, response);
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/VistasWeb/VistasRecep/NuevaCita.jsp").forward(request, response);
    }

    private void mostrarFormularioEdicion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        UsuarioCitaRecep cita = dao.obtenerPorId(id);
        request.setAttribute("cita", cita);
        request.getRequestDispatcher("/VistasWeb/VistasRecep/EditarCita.jsp").forward(request, response);
    }

    private void guardarCita(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        UsuarioCitaRecep cita = new UsuarioCitaRecep();
        cita.setNombreCliente(request.getParameter("nombreCliente"));
        cita.setFecha(Date.valueOf(request.getParameter("fecha")));
        cita.setHora(Time.valueOf(request.getParameter("hora") + ":00"));
        cita.setVeterinario(request.getParameter("veterinario"));
        cita.setEstado(request.getParameter("estado"));

        dao.insertar(cita);
        response.sendRedirect("UsuarioCitaRecepServlet?exito=guardar");
    }

    private void actualizarCita(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        UsuarioCitaRecep cita = new UsuarioCitaRecep();
        cita.setIdCita(Integer.parseInt(request.getParameter("idCita")));
        cita.setNombreCliente(request.getParameter("nombreCliente"));
        cita.setFecha(Date.valueOf(request.getParameter("fecha")));
        cita.setHora(Time.valueOf(request.getParameter("hora") + ":00"));
        cita.setVeterinario(request.getParameter("veterinario"));
        cita.setEstado(request.getParameter("estado"));

        dao.actualizar(cita);
        response.sendRedirect("UsuarioCitaRecepServlet?exito=actualizar");
    }

    private void eliminarCita(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        dao.eliminar(id);
        response.sendRedirect("UsuarioCitaRecepServlet?exito=eliminar");
    }
}