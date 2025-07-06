package Controladores;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import Modelo.Conexion;
import Modelo.Producto;
import ModeloDAO.ProductoDAO;
import Modelo.Proveedor;
import ModeloDAO.ProveedorDAO;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.List;

// Cambios de importación de javax.servlet a jakarta.servlet
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ProductoPdfServlet")
public class ProductoPdfServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"listado_productos.pdf\"");

        try (Connection conexion = Conexion.getConnection()) {
            ProductoDAO productoDAO = new ProductoDAO(conexion);
            ProveedorDAO proveedorDAO = new ProveedorDAO(conexion);
            List<Producto> productos = productoDAO.listarTodos();

            Document document = new Document();
            OutputStream out = response.getOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph titulo = new Paragraph("Listado de Productos - Veterinaria Santa Cruz", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            // Tabla de Productos
            PdfPTable table = new PdfPTable(7); // 7 columnas: ID, Nombre, Descripción, Precio, Stock, Proveedor, Estado
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            float[] columnWidths = {0.8f, 2f, 3f, 1.2f, 1f, 2f, 1f};
            table.setWidths(columnWidths);

            // Encabezados de la tabla
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            String[] headers = {"ID", "Nombre", "Descripción", "Precio", "Stock", "Proveedor", "Estado"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
            }

            // Datos de los productos
            if (productos != null && !productos.isEmpty()) {
                for (Producto prod : productos) {
                    table.addCell(String.valueOf(prod.getIdProducto()));
                    table.addCell(prod.getNombreProducto());
                    table.addCell(prod.getDescripcion());
                    table.addCell("S/ " + prod.getPrecio());
                    table.addCell(String.valueOf(prod.getStock()));
                    
                    // Obtener nombre del proveedor
                    Proveedor proveedor = proveedorDAO.obtenerPorId(prod.getIdProveedor());
                    table.addCell(proveedor != null ? proveedor.getRazonSocial() : "N/A");
                    
                    table.addCell(prod.getEstado() == 1 ? "Activo" : "Inactivo");
                }
            } else {
                PdfPCell noDataCell = new PdfPCell(new Phrase("No hay productos registrados."));
                noDataCell.setColspan(7);
                noDataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(noDataCell);
            }

            document.add(table);

            document.close();
            out.close();

        } catch (DocumentException e) {
            e.printStackTrace();
            throw new ServletException("Error al generar el PDF: " + e.getMessage());
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error de base de datos al obtener productos: " + e.getMessage());
        }
    }
}