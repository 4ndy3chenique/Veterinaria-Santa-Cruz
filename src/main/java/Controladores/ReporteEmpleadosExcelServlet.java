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

// Importaciones de JExcelApi
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.format.Colour;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;

@WebServlet(name = "ReporteEmpleadosExcelServlet", urlPatterns = {"/ReporteEmpleadosExcelServlet"})
public class ReporteEmpleadosExcelServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ReporteEmpleadosExcelServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configurar la respuesta HTTP para que el navegador sepa que es un archivo Excel .xls
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"Reporte_Empleados.xls\"");

        try (OutputStream os = response.getOutputStream()) {
            // Crear un nuevo libro de trabajo de JExcelApi
            WritableWorkbook workbook = Workbook.createWorkbook(os);

            // Estilos para encabezados
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, jxl.format.UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
            headerFormat.setBackground(Colour.BLUE_GREY); // Color de fondo para encabezados
            headerFormat.setAlignment(Alignment.CENTRE);
            headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            headerFormat.setWrap(false); // No envolver texto

            // Estilos para contenido
            WritableFont contentFont = new WritableFont(WritableFont.ARIAL, 9, WritableFont.NO_BOLD, false, jxl.format.UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat contentFormat = new WritableCellFormat(contentFont);
            contentFormat.setAlignment(Alignment.LEFT);
            contentFormat.setVerticalAlignment(VerticalAlignment.TOP);
            contentFormat.setWrap(false); // No envolver texto

            // --- Hoja de Veterinarios ---
            WritableSheet sheetVeterinarios = workbook.createSheet("Veterinarios", 0); // Nombre de la hoja y índice

            // Encabezados de Veterinarios
            String[] headersVet = {"ID", "Nombre", "Apellido", "DNI", "Teléfono", "Especialidad"};
            for (int i = 0; i < headersVet.length; i++) {
                sheetVeterinarios.addCell(new Label(i, 0, headersVet[i], headerFormat));
            }

            // Obtener datos de Veterinarios
            VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
            List<Veterinario> listaVeterinarios = veterinarioDAO.listarVeterinarios();

            int rowNumVet = 1; // Empezar desde la segunda fila para los datos
            if (listaVeterinarios != null) {
                for (Veterinario vet : listaVeterinarios) {
                    sheetVeterinarios.addCell(new Label(0, rowNumVet, String.valueOf(vet.getIdVeterinario()), contentFormat));
                    sheetVeterinarios.addCell(new Label(1, rowNumVet, vet.getNombre(), contentFormat));
                    sheetVeterinarios.addCell(new Label(2, rowNumVet, vet.getApellido(), contentFormat));
                    sheetVeterinarios.addCell(new Label(3, rowNumVet, vet.getDni(), contentFormat));
                    sheetVeterinarios.addCell(new Label(4, rowNumVet, vet.getNumero(), contentFormat));
                    sheetVeterinarios.addCell(new Label(5, rowNumVet, vet.getEspecialidad(), contentFormat));
                    rowNumVet++;
                }
            }

            // No hay un autoajuste de columna directo y simple como en POI para JExcelApi,
            // pero puedes establecer anchos fijos si lo deseas:
            // sheetVeterinarios.setColumnView(0, 5); // ID
            // sheetVeterinarios.setColumnView(1, 15); // Nombre
            // ...

            // --- Hoja de Recepcionistas ---
            WritableSheet sheetRecepcionistas = workbook.createSheet("Recepcionistas", 1); // Nombre de la hoja y índice

            // Encabezados de Recepcionistas
            String[] headersRec = {"ID", "Nombre", "Apellido", "DNI", "Teléfono", "Correo"};
            for (int i = 0; i < headersRec.length; i++) {
                sheetRecepcionistas.addCell(new Label(i, 0, headersRec[i], headerFormat));
            }

            // Obtener datos de Recepcionistas
            RecepcionistaDAO recepcionistaDAO = new RecepcionistaDAO();
            List<Recepcionista> listaRecepcionistas = recepcionistaDAO.listarRecepcionistas();

            // *** Nuevo log para depuración ***
            if (listaRecepcionistas != null) {
                LOGGER.log(Level.INFO, "Número de recepcionistas obtenidos para el reporte Excel: " + listaRecepcionistas.size());
            } else {
                LOGGER.log(Level.WARNING, "La lista de recepcionistas es nula.");
            }
            // *******************************

            int rowNumRec = 1; // Empezar desde la segunda fila para los datos
            if (listaRecepcionistas != null) {
                for (Recepcionista rec : listaRecepcionistas) {
                    sheetRecepcionistas.addCell(new Label(0, rowNumRec, String.valueOf(rec.getIdRecepcionista()), contentFormat));
                    sheetRecepcionistas.addCell(new Label(1, rowNumRec, rec.getNombre(), contentFormat));
                    sheetRecepcionistas.addCell(new Label(2, rowNumRec, rec.getApellido(), contentFormat));
                    sheetRecepcionistas.addCell(new Label(3, rowNumRec, rec.getDni(), contentFormat));
                    sheetRecepcionistas.addCell(new Label(4, rowNumRec, rec.getNumero(), contentFormat));
                    sheetRecepcionistas.addCell(new Label(5, rowNumRec, rec.getCorreo(), contentFormat));
                    rowNumRec++;
                }
            }
            // No hay autoajuste de columna directo para JExcelApi

            // Escribir el libro de trabajo en el OutputStream de la respuesta
            workbook.write();
            workbook.close(); // Cerrar el workbook

            LOGGER.log(Level.INFO, "Reporte Excel de empleados generado exitosamente con JExcelApi.");

        } catch (WriteException e) {
            LOGGER.log(Level.SEVERE, "Error al escribir en el documento Excel: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el reporte Excel.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error de E/S al generar el reporte Excel: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de E/S al generar el reporte Excel.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al generar el reporte Excel: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error inesperado al generar el reporte Excel.");
        }
    }
}
