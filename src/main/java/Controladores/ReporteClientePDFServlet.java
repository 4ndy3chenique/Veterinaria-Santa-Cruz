package Controladores;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import Modelo.Cliente;
import Modelo.UsuarioCliente;
import Modelo.Conexion; // Importa tu clase de conexión
import ModeloDAO.ClienteDAO;
import ModeloDAO.UsuarioClienteDAO;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection; // Importa java.sql.Connection
import java.sql.SQLException; // Importa java.sql.SQLException
import java.util.List;

// Importaciones de Jakarta Servlet API
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ReporteClientePDFServlet", urlPatterns = {"/ReporteClientePDFServlet"})
public class ReporteClientePDFServlet extends HttpServlet {

    private static final long serialVersionUID = 1L; // Buena práctica añadir serialVersionUID

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"reporte_clientes.pdf\""); // Nombre de archivo más específico

        String tipoReporte = request.getParameter("tipo");

        Document document = new Document();
        OutputStream out = response.getOutputStream();
        
        // Usa try-with-resources para la conexión, como en ProductoPdfServlet
        try (Connection con = Conexion.getConnection()) { // La conexión se cerrará automáticamente
            // Instancia los DAOs inyectando la conexión
            ClienteDAO clienteDAO = new ClienteDAO(con); 
            UsuarioClienteDAO usuarioClienteDAO = new UsuarioClienteDAO(con); 

            PdfWriter.getInstance(document, out);
            document.open();

            // Definición de fuentes
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLUE);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

            Paragraph title;
            if ("clientes".equals(tipoReporte)) {
                title = new Paragraph("Reporte de Clientes Registrados", titleFont);
            } else if ("usuarios".equals(tipoReporte)) {
                title = new Paragraph("Reporte de Usuarios Clientes", titleFont);
            } else {
                // Mensaje por defecto si el tipo de reporte no es válido
                title = new Paragraph("Tipo de Reporte No Válido", titleFont);
            }
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            if ("clientes".equals(tipoReporte)) {
                List<Cliente> listaClientes = clienteDAO.listarClientes(); // Llama al método listarClientes()
                PdfPTable table = new PdfPTable(5); // Ajusta el número de columnas si es necesario
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(10f);
                // Define los anchos de columna si quieres controlarlos mejor
                // float[] columnWidths = {0.8f, 2f, 2f, 1.5f, 1.5f}; // Ejemplo
                // table.setWidths(columnWidths);

                String[] headers = {"ID", "Nombre", "Apellido", "DNI", "Teléfono"}; // Encabezados Cliente
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                    cell.setBackgroundColor(BaseColor.DARK_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    table.addCell(cell);
                }

                if (listaClientes != null && !listaClientes.isEmpty()) {
                    for (Cliente cliente : listaClientes) {
                        table.addCell(new Phrase(String.valueOf(cliente.getIdCliente()), cellFont));
                        table.addCell(new Phrase(cliente.getNombre(), cellFont));
                        table.addCell(new Phrase(cliente.getApellido(), cellFont));
                        table.addCell(new Phrase(cliente.getDni(), cellFont));
                        table.addCell(new Phrase(cliente.getTelefono(), cellFont));
                    }
                } else {
                    PdfPCell noDataCell = new PdfPCell(new Phrase("No hay clientes registrados.", cellFont));
                    noDataCell.setColspan(5); // Columna span para la tabla de clientes
                    noDataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(noDataCell);
                }
                document.add(table);

            } else if ("usuarios".equals(tipoReporte)) {
                List<UsuarioCliente> listaUsuarios = usuarioClienteDAO.listarUsuarioClientes(); // Llama al método listarUsuarioClientes()
                PdfPTable table = new PdfPTable(6); // 6 columnas: ID, Nombre, Apellido, DNI, Correo, Fecha de Registro
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(10f);
                 // Define los anchos de columna si quieres controlarlos mejor
                // float[] columnWidths = {0.8f, 1.5f, 1.5f, 1.2f, 2f, 1.8f}; // Ejemplo
                // table.setWidths(columnWidths);

                String[] headers = {"ID", "Nombre", "Apellido", "DNI", "Correo", "Fecha Registro"}; // Encabezados UsuarioCliente
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                    cell.setBackgroundColor(BaseColor.DARK_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    table.addCell(cell);
                }

                if (listaUsuarios != null && !listaUsuarios.isEmpty()) {
                    for (UsuarioCliente usuario : listaUsuarios) {
                        table.addCell(new Phrase(String.valueOf(usuario.getIdUsuario()), cellFont));
                        table.addCell(new Phrase(usuario.getNombre(), cellFont));
                        table.addCell(new Phrase(usuario.getApellido(), cellFont));
                        table.addCell(new Phrase(usuario.getDni(), cellFont));
                        table.addCell(new Phrase(usuario.getCorreo(), cellFont));
                        // Formatear la fecha de registro si es necesario
                        table.addCell(new Phrase(usuario.getFechaRegistro() != null ? usuario.getFechaRegistro().toString() : "N/A", cellFont));
                    }
                } else {
                    PdfPCell noDataCell = new PdfPCell(new Phrase("No hay usuarios clientes registrados.", cellFont));
                    noDataCell.setColspan(6); // Columna span para la tabla de usuarios
                    noDataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(noDataCell);
                }
                document.add(table);
            } else {
                // Si el tipo de reporte es desconocido, se agrega un párrafo informativo.
                document.add(new Paragraph("No se pudo generar el reporte. Tipo de reporte especificado no es 'clientes' ni 'usuarios'.", cellFont));
            }

            document.close();
            // out.close() se maneja automáticamente por el try-with-resources del OutputStream

        } catch (DocumentException e) {
            System.err.println("Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Error al generar el PDF: " + e.getMessage(), e);
        } catch (SQLException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Error de base de datos al obtener datos para el PDF: " + e.getMessage(), e);
        } finally {
            // No es necesario cerrar 'out' explícitamente aquí si se usa try-with-resources.
            // Si 'out' no se declara en el try-with-resources, sí sería necesario cerrarlo.
            // if (out != null) { try { out.close(); } catch (IOException e) { e.printStackTrace(); } }
        }
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