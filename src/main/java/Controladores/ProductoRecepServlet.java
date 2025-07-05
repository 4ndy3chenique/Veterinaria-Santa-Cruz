package Controladores;

import Modelo.Producto;
import ModeloDAO.ProductoRecepDAO;
import Modelo.Conexion;
import Modelo.Proveedor;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

@WebServlet(name = "ProductoRecepServlet", urlPatterns = {"/ProductoRecepServlet"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 15    // 15 MB
)
public class ProductoRecepServlet extends HttpServlet {

    private ProductoRecepDAO productoDAO;

    @Override
    public void init() throws ServletException {
        Connection con = Conexion.getConnection();
        if (con == null) {
            System.err.println("ERROR SERVLET: No se pudo establecer conexión a la base de datos en init(). Revisa Modelo.Conexion.");
            throw new ServletException("No se pudo establecer conexión a la base de datos.");
        }
        productoDAO = new ProductoRecepDAO(con);
        System.out.println("DEBUG SERVLET: ProductoRecepServlet inicializado. Conexión DAO establecida.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }
        System.out.println("DEBUG SERVLET: Recibida petición GET con acción: " + accion);

        switch (accion) {
            case "listar":
                listarProductos(request, response);
                break;
            case "ver": // Este caso ahora solo cargará el producto en sesión y redirigirá a listar
                mostrarDetallesProductoParaEditar(request, response);
                break;
            case "form": // Este método ya no es necesario, solo redirige al listado principal
                System.out.println("DEBUG SERVLET: Acción 'form' recibida. Redirigiendo a listar (modal se abre con JS).");
                response.sendRedirect("ProductoRecepServlet?accion=listar");
                break;
            default:
                System.out.println("DEBUG SERVLET: Acción GET no reconocida: " + accion + ". Redirigiendo a listar.");
                response.sendRedirect("ProductoRecepServlet?accion=listar");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        System.out.println("DEBUG SERVLET: Recibida petición POST con acción: " + accion);

        switch (accion) {
            case "guardar":
                guardarProducto(request, response);
                break;
            case "cambiarEstado":
                cambiarEstadoProducto(request, response);
                break;
            case "eliminarDefinitivo":
                eliminarProductoDefinitivo(request, response);
                break;
            default:
                System.out.println("DEBUG SERVLET: Acción POST no reconocida: " + accion + ". Redirigiendo a listar.");
                response.sendRedirect("ProductoRecepServlet?accion=listar");
        }
    }

    private void listarProductos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String busqueda = request.getParameter("busqueda");
            String mostrarInactivosParamStr = request.getParameter("mostrarInactivos");

            System.out.println("DEBUG SERVLET: [listarProductos] Iniciando. Búsqueda: '" + busqueda + "', Mostrar Inactivos Param (String): " + mostrarInactivosParamStr);

            List<Producto> productosBase;
            List<Producto> productosFinal = new ArrayList<>();

            if (busqueda != null && !busqueda.trim().isEmpty()) {
                productosBase = productoDAO.buscarPorNombre(busqueda.trim());
                System.out.println("DEBUG SERVLET: [listarProductos] Productos encontrados por búsqueda: " + productosBase.size());
            } else {
                productosBase = productoDAO.listarTodos();
                System.out.println("DEBUG SERVLET: [listarProductos] Todos los productos cargados del DAO: " + productosBase.size());
            }

            boolean showingAll = false;

            if ("true".equals(mostrarInactivosParamStr)) {
                for (Producto p : productosBase) {
                    if (p.getEstado() == 0) {
                        productosFinal.add(p);
                    }
                }
                System.out.println("DEBUG SERVLET: [listarProductos] Filtrando para mostrar SOLO INACTIVOS. Cantidad final: " + productosFinal.size());
            } else if ("false".equals(mostrarInactivosParamStr)) {
                for (Producto p : productosBase) {
                    if (p.getEstado() == 1) {
                        productosFinal.add(p);
                    }
                }
                System.out.println("DEBUG SERVLET: [listarProductos] Filtrando para mostrar SOLO ACTIVOS. Cantidad final: " + productosFinal.size());
            } else {
                productosFinal.addAll(productosBase);
                showingAll = true;
                System.out.println("DEBUG SERVLET: [listarProductos] No hay parámetro de filtro de estado o es 'mostrar todos', mostrando TODOS los productos. Cantidad final: " + productosFinal.size());
            }

            List<Proveedor> proveedores = productoDAO.listarProveedores();
            Map<Integer, String> mapaProveedores = proveedores.stream()
                                                             .collect(Collectors.toMap(Proveedor::getIdProveedor, Proveedor::getRazonSocial));
            System.out.println("DEBUG SERVLET: [listarProductos] Total de proveedores cargados en mapa: " + mapaProveedores.size());


            request.setAttribute("productos", productosFinal);
            request.setAttribute("mostrarInactivosState", mostrarInactivosParamStr);
            request.setAttribute("showingAll", showingAll);
            request.setAttribute("mapaProveedores", mapaProveedores);

            System.out.println("DEBUG SERVLET: [listarProductos] Productos finales enviados al JSP: " + productosFinal.size() + " productos.");
            if (productosFinal.isEmpty()) {
                System.out.println("DEBUG SERVLET: [listarProductos] ATENCIÓN: La lista de productos ENVIADA al JSP está vacía.");
            }

            request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionProductosR.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("ERROR SERVLET: Excepción en listarProductos: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("mensaje", "Error al listar productos: " + e.getMessage());
            request.setAttribute("tipoMensaje", "alert-error");
            request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionProductosR.jsp").forward(request, response);
        }
    }

    private void mostrarDetallesProductoParaEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("DEBUG SERVLET: Mostrando detalles de producto para editar.");
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Producto producto = productoDAO.obtenerPorId(id);

            if (producto != null) {
                request.getSession().setAttribute("productToEdit", producto);
                System.out.println("DEBUG SERVLET: Producto ID " + id + " cargado en sesión para edición.");
            } else {
                request.getSession().setAttribute("mensaje", "Producto no encontrado.");
                request.getSession().setAttribute("tipoMensaje", "alert-error");
                System.err.println("ERROR SERVLET: Producto con ID " + id + " no encontrado para edición.");
            }
            response.sendRedirect("ProductoRecepServlet?accion=listar");
        } catch (NumberFormatException e) {
            System.err.println("ERROR SERVLET: ID de producto inválido en mostrarDetallesProductoParaEditar. " + e.getMessage());
            request.getSession().setAttribute("mensaje", "ID de producto inválido.");
            request.getSession().setAttribute("tipoMensaje", "alert-error");
            response.sendRedirect("ProductoRecepServlet?accion=listar");
        } catch (Exception e) {
            System.err.println("ERROR SERVLET: Excepción en mostrarDetallesProductoParaEditar: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("mensaje", "Ocurrió un error al obtener detalles del producto: " + e.getMessage());
            request.getSession().setAttribute("tipoMensaje", "alert-error");
            response.sendRedirect("ProductoRecepServlet?accion=listar");
        }
    }

    private void guardarProducto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("DEBUG SERVLET: Iniciando guardarProducto()...");
        Producto producto = new Producto(); // Crear producto aquí para poder pasarlo a la sesión en caso de error
        try {
            int idProducto = 0;
            String idStr = request.getParameter("idProducto");
            if (idStr != null && !idStr.isEmpty()) {
                idProducto = Integer.parseInt(idStr);
            }
            System.out.println("DEBUG SERVLET: idProducto (desde form): '" + idStr + "' -> parseado a " + idProducto);

            // --- Lógica para idProveedor ---
            int idProveedor;
            String idProveedorParam;
            if (idProducto != 0) { // Si es una edición
                // Leer el ID del proveedor del campo oculto si el select está deshabilitado
                idProveedorParam = request.getParameter("originalIdProveedor");
                System.out.println("DEBUG SERVLET: originalIdProveedor (para edición): '" + idProveedorParam + "'");
            } else { // Si es un nuevo producto (el select no está deshabilitado)
                idProveedorParam = request.getParameter("idProveedor");
                System.out.println("DEBUG SERVLET: idProveedor (para nuevo producto): '" + idProveedorParam + "'");
            }
            // Intenta parsear idProveedorParam, si falla, lanzará NumberFormatException
            idProveedor = Integer.parseInt(idProveedorParam);
            // --- Fin Lógica para idProveedor ---


            String nombre = request.getParameter("nombre");
            String descripcion = request.getParameter("descripcion");
            String unidadMedida = request.getParameter("unidadMedida");
            String precioStr = request.getParameter("precio");
            String stockStr = request.getParameter("stock");
            String estadoStr = request.getParameter("estado");

            System.out.println("DEBUG SERVLET: Parámetros recibidos:");
            System.out.println("  nombre: '" + nombre + "'");
            System.out.println("  descripcion: '" + descripcion + "'");
            System.out.println("  unidadMedida: '" + unidadMedida + "'");
            System.out.println("  precio: '" + precioStr + "'");
            System.out.println("  stock: '" + stockStr + "'");
            System.out.println("  estado: '" + estadoStr + "'");

            // Conversión de tipos con manejo de errores más específico
            BigDecimal precio = null;
            int stock = 0;
            int estado = 0;

            try {
                precio = new BigDecimal(precioStr);
            } catch (NumberFormatException e) {
                System.err.println("ERROR SERVLET: NumberFormatException para 'precio': '" + precioStr + "'");
                throw new NumberFormatException("Precio inválido: " + precioStr);
            }
            try {
                stock = Integer.parseInt(stockStr);
            } catch (NumberFormatException e) {
                System.err.println("ERROR SERVLET: NumberFormatException para 'stock': '" + stockStr + "'");
                throw new NumberFormatException("Stock inválido: " + stockStr);
            }
            try {
                estado = Integer.parseInt(estadoStr);
            } catch (NumberFormatException e) {
                System.err.println("ERROR SERVLET: NumberFormatException para 'estado': '" + estadoStr + "'");
                throw new NumberFormatException("Estado inválido: " + estadoStr);
            }


            String nombreArchivo = null;
            Part imagenPart = request.getPart("imagen");

            if (imagenPart != null && imagenPart.getSize() > 0 && imagenPart.getSubmittedFileName() != null && !imagenPart.getSubmittedFileName().isEmpty()) {
                String nombreOriginal = imagenPart.getSubmittedFileName();
                nombreArchivo = System.currentTimeMillis() + "_" + nombreOriginal.replaceAll("[^a-zA-Z0-9_.]", "_");
                String rutaCarpeta = getServletContext().getRealPath("/Recursos/ProductosVenta");
                File carpeta = new File(rutaCarpeta);
                if (!carpeta.exists()) {
                    carpeta.mkdirs();
                }
                imagenPart.write(rutaCarpeta + File.separator + nombreArchivo);
                nombreArchivo = "Recursos/ProductosVenta/" + nombreArchivo;
                System.out.println("DEBUG SERVLET: Imagen subida exitosamente: " + nombreArchivo);
            } else {
                String currentImagen = request.getParameter("currentImagen");
                if (currentImagen != null && !currentImagen.isEmpty()) {
                    nombreArchivo = currentImagen;
                    System.out.println("DEBUG SERVLET: No se subió nueva imagen. Conservando imagen existente: " + nombreArchivo);
                } else {
                    nombreArchivo = null;
                    System.out.println("DEBUG SERVLET: No hay nueva imagen ni imagen existente para conservar.");
                }
            }


            producto.setIdProducto(idProducto);
            producto.setNombreProducto(nombre);
            producto.setDescripcion(descripcion);
            producto.setUnidadMedida(unidadMedida);
            producto.setPrecio(precio);
            producto.setStock(stock);
            producto.setEstado(estado);
            producto.setIdProveedor(idProveedor);
            producto.setImagen(nombreArchivo);

            boolean exito;
            String mensajeExito = "";
            String mensajeError = "";

            if (idProducto == 0) {
                exito = productoDAO.agregar(producto);
                if (exito) {
                    mensajeExito = "Producto agregado correctamente.";
                } else {
                    mensajeError = "Error al agregar el producto.";
                }
            } else {
                // Para la actualización, solo actualizamos los campos permitidos
                // Recuperar el producto existente para mantener los campos no editables
                Producto productoExistente = productoDAO.obtenerPorId(idProducto);
                if (productoExistente != null) {
                    productoExistente.setPrecio(precio);
                    productoExistente.setStock(stock);
                    productoExistente.setEstado(estado);
                    // Mantener nombre, descripcion, unidad, proveedor, imagen originales
                    // Si se permitiera cambiar imagen, la lógica de nombreArchivo ya la manejaría
                    exito = productoDAO.actualizar(productoExistente); // Usar el productoExistente actualizado
                    if (exito) {
                        mensajeExito = "Producto actualizado correctamente.";
                    } else {
                        mensajeError = "Error al actualizar el producto.";
                    }
                } else {
                    mensajeError = "Producto a actualizar no encontrado.";
                    exito = false;
                }
            }

            if (exito) {
                request.getSession().setAttribute("mensaje", mensajeExito);
                request.getSession().setAttribute("tipoMensaje", "alert-success");
                System.out.println("DEBUG SERVLET: Operación de guardar exitosa: " + mensajeExito);
            } else {
                request.getSession().setAttribute("mensaje", mensajeError);
                request.getSession().setAttribute("tipoMensaje", "alert-error");
                System.err.println("ERROR SERVLET: Operación de guardar fallida: " + mensajeError);
                request.getSession().setAttribute("productToEdit", producto);
            }
            response.sendRedirect("ProductoRecepServlet?accion=listar");

        } catch (NumberFormatException e) {
            System.err.println("ERROR SERVLET: Error de formato de número al guardar producto: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("mensaje", "Error: Datos numéricos inválidos (" + e.getMessage() + ").");
            request.getSession().setAttribute("tipoMensaje", "alert-error");
            request.getSession().setAttribute("productToEdit", producto);
            response.sendRedirect("ProductoRecepServlet?accion=listar");
        } catch (Exception e) {
            System.err.println("ERROR SERVLET: Excepción inesperada en guardarProducto: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("mensaje", "Ocurrió un error al guardar el producto: " + e.getMessage());
            request.getSession().setAttribute("tipoMensaje", "alert-error");
            request.getSession().setAttribute("productToEdit", producto);
            response.sendRedirect("ProductoRecepServlet?accion=listar");
        }
    }

    private void cambiarEstadoProducto(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println("DEBUG SERVLET: Iniciando cambiarEstadoProducto()...");
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            int estadoActual = Integer.parseInt(request.getParameter("estado"));
            int nuevoEstado = (estadoActual == 1) ? 0 : 1;

            boolean exito = productoDAO.cambiarEstado(id, nuevoEstado);

            if (exito) {
                String mensajeExito = (nuevoEstado == 0) ? "Producto desactivado correctamente." : "Producto activado correctamente.";
                request.getSession().setAttribute("mensaje", mensajeExito);
                request.getSession().setAttribute("tipoMensaje", "alert-success");
                System.out.println("DEBUG SERVLET: Estado de producto ID " + id + " cambiado a " + nuevoEstado + " exitosamente.");
            } else {
                String mensajeError = (nuevoEstado == 0) ? "Error al desactivar el producto." : "Error al activar el producto.";
                request.getSession().setAttribute("mensaje", mensajeError);
                request.getSession().setAttribute("tipoMensaje", "alert-error");
                System.err.println("ERROR SERVLET: Fallo al cambiar estado de producto ID " + id + " a " + nuevoEstado + ".");
            }

            String redirectUrl = "ProductoRecepServlet?accion=listar";
            String busquedaParam = request.getParameter("busqueda");
            String mostrarInactivosParam = request.getParameter("mostrarInactivos");

            if (busquedaParam != null && !busquedaParam.isEmpty()) {
                redirectUrl += "&busqueda=" + busquedaParam;
            }
            if (mostrarInactivosParam != null && !mostrarInactivosParam.isEmpty()) {
                redirectUrl += "&mostrarInactivos=" + mostrarInactivosParam;
            }
            response.sendRedirect(redirectUrl);

        } catch (NumberFormatException e) {
            System.err.println("ERROR SERVLET: ID o estado de producto inválido en cambiarEstadoProducto: " + e.getMessage());
            request.getSession().setAttribute("mensaje", "ID o estado de producto inválido para cambiar estado.");
            request.getSession().setAttribute("tipoMensaje", "alert-error");
            response.sendRedirect("ProductoRecepServlet?accion=listar");
        } catch (Exception e) {
            System.err.println("ERROR SERVLET: Excepción inesperada en cambiarEstadoProducto: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("mensaje", "Ocurrió un error al cambiar el estado del producto: " + e.getMessage());
            request.getSession().setAttribute("tipoMensaje", "alert-error");
            response.sendRedirect("ProductoRecepServlet?accion=listar");
        }
    }

    private void eliminarProductoDefinitivo(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println("DEBUG SERVLET: Iniciando eliminarProductoDefinitivo()...");
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean exito = productoDAO.eliminarDefinitivo(id);

            if (exito) {
                request.getSession().setAttribute("mensaje", "Producto eliminado definitivamente.");
                request.getSession().setAttribute("tipoMensaje", "alert-success");
                System.out.println("DEBUG SERVLET: Producto ID " + id + " eliminado definitivamente.");
            } else {
                request.getSession().setAttribute("mensaje", "Error al eliminar definitivamente el producto.");
                request.getSession().setAttribute("tipoMensaje", "alert-error");
                System.err.println("ERROR SERVLET: Fallo al eliminar definitivamente producto ID " + id + ".");
            }

            String redirectUrl = "ProductoRecepServlet?accion=listar";
            String busquedaParam = request.getParameter("busqueda");
            String mostrarInactivosParam = request.getParameter("mostrarInactivos");

            if (busquedaParam != null && !busquedaParam.isEmpty()) {
                redirectUrl += "&busqueda=" + busquedaParam;
            }
            if (mostrarInactivosParam != null && !mostrarInactivosParam.isEmpty()) {
                redirectUrl += "&mostrarInactivos=" + mostrarInactivosParam;
            }
            response.sendRedirect(redirectUrl);

        } catch (NumberFormatException e) {
            System.err.println("ERROR SERVLET: ID de producto inválido en eliminarProductoDefinitivo: " + e.getMessage());
            request.getSession().setAttribute("mensaje", "ID de producto inválido para eliminación definitiva.");
            request.getSession().setAttribute("tipoMensaje", "alert-error");
            response.sendRedirect("ProductoRecepServlet?accion=listar");
        } catch (Exception e) {
            System.err.println("ERROR SERVLET: Excepción inesperada en eliminarProductoDefinitivo: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("mensaje", "Ocurrió un error al eliminar definitivamente el producto: " + e.getMessage());
            request.getSession().setAttribute("tipoMensaje", "alert-error");
            response.sendRedirect("ProductoRecepServlet?accion=listar");
        }
    }
}
