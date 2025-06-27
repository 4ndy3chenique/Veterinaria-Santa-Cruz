package Controladores;

import Modelo.Cliente;
import Modelo.Conexion;
import ModeloDAO.ClienteDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ListadoClientesServlet", urlPatterns = {"/ListadoClientesServlet"})
public class ListadoClientesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Connection conexion = null;
        try {
            // 1. Establecer conexión a la base de datos
            conexion = Conexion.getConnection();
            ClienteDAO clienteDAO = new ClienteDAO(conexion);
            
            // 2. Obtener parámetros de búsqueda/filtro
            String filtro = request.getParameter("filtro");
            String busqueda = request.getParameter("busqueda");
            
            // 3. Obtener lista de clientes según parámetros
            List<Cliente> listaClientes;
            if (busqueda != null && !busqueda.isEmpty()) {
                // Búsqueda específica
                listaClientes = clienteDAO.buscarClientes(busqueda);
            } else {
                // Listado completo o últimos registros
                String tipoListado = request.getParameter("tipoListado");
                if ("recientes".equals(tipoListado)) {
                    listaClientes = clienteDAO.listarClientesRecientes(5); // Últimos 5 clientes
                } else {
                    listaClientes = clienteDAO.listarClientes(); // Todos los clientes
                }
            }
            
            // 4. Enviar datos a la vista
            request.setAttribute("listaClientes", listaClientes);
            request.setAttribute("filtroActual", filtro);
            request.setAttribute("busquedaActual", busqueda);
            
            // 5. Redireccionar a la vista adecuada
            String destino = request.getParameter("destino");
            if (destino == null) destino = "/VistasWeb/VistasAdmin/listadoClientes.jsp";
            request.getRequestDispatcher(destino).forward(request, response);
            
        } catch (SQLException e) {
            // Manejo de errores
            e.printStackTrace();
            request.setAttribute("error", "Error de base de datos: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } finally {
            // Cerrar conexión
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
        // Redirigir las peticiones POST a GET para mantener consistencia
        doGet(request, response);
    }
}