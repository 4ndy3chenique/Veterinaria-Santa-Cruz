package Controladores;

import Modelo.Veterinario;
import Modelo.Recepcionista;
import ModeloDAO.RecepcionistaDAO;
import ModeloDAO.VeterinarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Importaciones de iTextPDF
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.Element;

@WebServlet(name = "ReporteEmpleadosServlet", urlPatterns = {"/ReporteEmpleadosServlet"})
public class ReporteEmpleadosServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ReporteEmpleadosServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configurar la respuesta HTTP para que el navegador sepa que es un PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"Reporte_Empleados.pdf\"");

        try (OutputStream os = response.getOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, os);
            document.open();

            // Fuentes personalizadas
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLUE);
            Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
            Font fontContenido = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);

            // Título del reporte
            Paragraph titulo = new Paragraph("Reporte de Empleados - Veterinaria Santa Cruz", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            // --- Sección de Veterinarios ---
            document.add(new Paragraph(" ")); // Espacio en blanco
            Paragraph subTituloVeterinarios = new Paragraph("Listado de Veterinarios", fontSubtitulo);
            subTituloVeterinarios.setAlignment(Element.ALIGN_LEFT);
            subTituloVeterinarios.setSpacingAfter(10);
            document.add(subTituloVeterinarios);

            VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
            List<Veterinario> listaVeterinarios = veterinarioDAO.listarVeterinarios();

            if (listaVeterinarios != null && !listaVeterinarios.isEmpty()) {
                PdfPTable tablaVeterinarios = new PdfPTable(6); // 6 columnas: ID, Nombre, Apellido, DNI, Teléfono, Especialidad
                tablaVeterinarios.setWidthPercentage(100);
                tablaVeterinarios.setSpacingBefore(10f);
                tablaVeterinarios.setSpacingAfter(10f);
                tablaVeterinarios.setWidths(new float[]{0.5f, 1.5f, 1.5f, 1f, 1f, 1.5f}); // Anchos relativos de columnas

                // Encabezados de la tabla de Veterinarios
                addTableHeader(tablaVeterinarios, fontHeader, new String[]{"ID", "Nombre", "Apellido", "DNI", "Teléfono", "Especialidad"});

                // Filas de datos de Veterinarios
                for (Veterinario vet : listaVeterinarios) {
                    addCell(tablaVeterinarios, String.valueOf(vet.getIdVeterinario()), fontContenido);
                    addCell(tablaVeterinarios, vet.getNombre(), fontContenido);
                    addCell(tablaVeterinarios, vet.getApellido(), fontContenido);
                    addCell(tablaVeterinarios, vet.getDni(), fontContenido);
                    addCell(tablaVeterinarios, vet.getNumero(), fontContenido);
                    addCell(tablaVeterinarios, vet.getEspecialidad(), fontContenido);
                }
                document.add(tablaVeterinarios);
            } else {
                document.add(new Paragraph("No hay veterinarios registrados.", fontContenido));
            }

            // --- Sección de Recepcionistas ---
            document.add(new Paragraph(" ")); // Espacio en blanco
            Paragraph subTituloRecepcionistas = new Paragraph("Listado de Recepcionistas", fontSubtitulo);
            subTituloRecepcionistas.setAlignment(Element.ALIGN_LEFT);
            subTituloRecepcionistas.setSpacingAfter(10);
            document.add(subTituloRecepcionistas);

            RecepcionistaDAO recepcionistaDAO = new RecepcionistaDAO();
            List<Recepcionista> listaRecepcionistas = recepcionistaDAO.listarRecepcionistas();

            if (listaRecepcionistas != null && !listaRecepcionistas.isEmpty()) {
                PdfPTable tablaRecepcionistas = new PdfPTable(6); // 6 columnas: ID, Nombre, Apellido, DNI, Teléfono, Correo
                tablaRecepcionistas.setWidthPercentage(100);
                tablaRecepcionistas.setSpacingBefore(10f);
                tablaRecepcionistas.setSpacingAfter(10f);
                tablaRecepcionistas.setWidths(new float[]{0.5f, 1.5f, 1.5f, 1f, 1f, 2f}); // Anchos relativos de columnas

                // Encabezados de la tabla de Recepcionistas
                addTableHeader(tablaRecepcionistas, fontHeader, new String[]{"ID", "Nombre", "Apellido", "DNI", "Teléfono", "Correo"});

                // Filas de datos de Recepcionistas
                for (Recepcionista rec : listaRecepcionistas) {
                    addCell(tablaRecepcionistas, String.valueOf(rec.getIdRecepcionista()), fontContenido);
                    addCell(tablaRecepcionistas, rec.getNombre(), fontContenido);
                    addCell(tablaRecepcionistas, rec.getApellido(), fontContenido);
                    addCell(tablaRecepcionistas, rec.getDni(), fontContenido);
                    addCell(tablaRecepcionistas, rec.getNumero(), fontContenido);
                    addCell(tablaRecepcionistas, rec.getCorreo(), fontContenido);
                }
                document.add(tablaRecepcionistas);
            } else {
                document.add(new Paragraph("No hay recepcionistas registrados.", fontContenido));
            }

            document.close();
            LOGGER.log(Level.INFO, "Reporte PDF de empleados generado exitosamente.");

        } catch (DocumentException e) {
            LOGGER.log(Level.SEVERE, "Error al generar el documento PDF: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el reporte PDF.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error de E/S al generar el reporte PDF: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de E/S al generar el reporte PDF.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al generar el reporte PDF: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error inesperado al generar el reporte PDF.");
        }
    }

    // Método auxiliar para añadir encabezados de tabla
    private void addTableHeader(PdfPTable table, Font font, String[] headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setBackgroundColor(new BaseColor(65, 105, 225)); // Azul más oscuro
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    // Método auxiliar para añadir celdas de datos
    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        table.addCell(cell);
    }
}
