<%@ page import="java.util.List" %>
<%@ page import="Modelo.Producto" %>
<%@ page import="ModeloDAO.ProveedorDAO" %>
<%@ page import="Modelo.Conexion" %>
<%@ page import="Modelo.Proveedor" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%
    java.sql.Connection conexion = Conexion.getConnection();
    ProveedorDAO proveedorDAO = new ProveedorDAO(conexion);
    List<Proveedor> proveedores = proveedorDAO.listar();
    List<Producto> productos = (List<Producto>) request.getAttribute("productos");
    boolean mostrarInactivos = request.getAttribute("mostrarInactivos") != null && (Boolean) request.getAttribute("mostrarInactivos");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Veterinaria Santa Cruz - Gestión de Productos</title>
        <link href="https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/ModoNoche-Sidebar.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/GestorProductos.css">
    </head>
    <body>
        <!-- Sidebar Navigation -->
        <nav class="sidebar">
            <header>
                <div class="image-text">
                    <span class="image">
                        <img id="logoAdmin" src="<%= request.getContextPath()%>/Recursos/Logo.png" alt="Logo de Veterinaria Santa Cruz" class="logo">
                    </span>
                    <div class="header-text">
                        <span class="name">Administrador</span>
                        <span class="profession">Veterinaria Santa Cruz</span>
                    </div>
                </div>
            </header>

            <div class="menu-bar">
                <ul class="menu-links">
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/VistasWeb/VistasAdmin/AdminDash.jsp">
                            <i class='bx bx-home-alt icon'></i><span class="text">General</span>
                        </a>
                    </li>
                    <li class="nav-link">
                                <a href="<%= request.getContextPath()%>/AdminClienteServlet"><i class='bx bxs-calendar icon'>                           
                            </i><span class="text">Clientes</span></a>
                    </li>   
     <li class="nav-link">
    <a href="<%= request.getContextPath()%>/AdminEmpleadoServlet"><i class='bx bx-group icon'></i><span class="text">Empleados</span></a>
</li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/ProductoServlet?accion=listar&idProveedor=1">
                            <i class='bx bx-package icon'></i><span class="text">Productos</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/ProveedorServlet?accion=listar">
                            <i class='bx bx-store icon'></i><span class="text">Proveedores</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="#"><i class='bx bx-cog icon'></i><span class="text">Ajustes</span></a>
                    </li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/LogoutServlet">
                            <i class='bx bx-log-out icon'></i><span class="text">Salir</span>
                        </a>
                    </li>
                </ul>
            </div>
        </nav>
        <main>
            <div class="header-actions">
                <h1>Gestión de Productos</h1>
                <div class="acciones">
                    <form method="get" action="ProductoServlet">
                        <input type="hidden" name="accion" value="listar" />
                        <% if (mostrarInactivos) { %>
                        <input type="hidden" name="inactivos" value="true" />
                        <% }%>
                        <input type="text" name="busqueda" placeholder="Buscar producto..." value="<%= request.getParameter("busqueda") != null ? request.getParameter("busqueda") : ""%>" />
                        <button type="submit">Buscar</button>
                    </form>

                    <% if (!mostrarInactivos) { %>
                    <a href="ProductoServlet?accion=listarInactivos" class="btn btn-secundario">Ver Inactivos</a>
                    <% } else { %>
                    <a href="ProductoServlet?accion=listar" class="btn btn-secundario">Ver Activos</a>
                    <% } %>
                    <button class="btn btn-agregar" onclick="abrirModal('modalAgregarProducto')">Agregar Producto</button>
                </div>
            </div>
            <table>
                <thead>
                    <tr><th>ID</th><th>Nombre</th><th>Descripción</th><th>Precio</th><th>Stock</th><th>Estado</th><th>Acciones</th></tr>
                </thead>
                <tbody>
                    <% if (productos != null && !productos.isEmpty()) {
                            for (Producto prod : productos) {%>
                    <tr>
                        <td><%= prod.getIdProducto()%></td>
                        <td><%= prod.getNombreProducto()%></td>
                        <td><%= prod.getDescripcion()%></td>
                        <td>S/ <%= prod.getPrecio()%></td>
                        <td><%= prod.getStock()%></td>
                        <td><%= (prod.getEstado() == 1 ? "Activo" : "Inactivo")%></td>
                        <td>
                            <button type="button" class="btn-ver-detalles" onclick="abrirModal('modalProducto_<%= prod.getIdProducto()%>')">Ver Detalles</button>
                        </td>
                    </tr>

                    <!-- MODAL DETALLES -->
                <div id="modalProducto_<%= prod.getIdProducto()%>" class="modal">
                    <div class="modal-content animado">
                        <span class="cerrar" onclick="cerrarModal('modalProducto_<%= prod.getIdProducto()%>')">&times;</span>
                        <h2>Detalles del Producto</h2>
                        <% if (prod.getImagen() != null) {%>
                        <img src="<%= request.getContextPath() + "/" + prod.getImagen()%>" style="max-width: 200px; display: block; margin: 0 auto;">
                        <% }%>
                        <p><strong>Nombre:</strong> <%= prod.getNombreProducto()%></p>
                        <p><strong>Descripción:</strong> <%= prod.getDescripcion()%></p>
                        <p><strong>Precio:</strong> S/ <%= prod.getPrecio()%></p>
                        <p><strong>Stock:</strong> <%= prod.getStock()%></p>
                        <p><strong>Proveedor:</strong> <%= proveedorDAO.obtenerPorId(prod.getIdProveedor()).getRazonSocial()%></p>
                        <p><strong>Estado:</strong> <%= prod.getEstado() == 1 ? "Activo" : "Inactivo"%></p>
                        <div class="acciones-producto">
                            <button type="button" class="btn btn-editar" onclick="abrirModal('modalEditarProducto_<%= prod.getIdProducto()%>')">Editar</button>
                            <form action="ProductoServlet" method="post" onsubmit="return confirm('¿Seguro que deseas eliminar este producto de forma permanente?')">
                                <input type="hidden" name="accion" value="eliminarDefinitivo">
                                <input type="hidden" name="id" value="<%= prod.getIdProducto()%>">
                                <button type="submit" class="btn btn-eliminar">Eliminar Producto</button>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- MODAL EDITAR -->
                <div id="modalEditarProducto_<%= prod.getIdProducto()%>" class="modal">
                    <div class="modal-content animado">
                        <span class="cerrar" onclick="cerrarModal('modalEditarProducto_<%= prod.getIdProducto()%>')">&times;</span>
                        <h2>Editar Producto</h2>
                        <form action="ProductoServlet" method="post" enctype="multipart/form-data">
                            <input type="hidden" name="accion" value="guardar">
                            <input type="hidden" name="idProducto" value="<%= prod.getIdProducto()%>">
                            <label>Nombre</label>
                            <input type="text" name="nombre" value="<%= prod.getNombreProducto()%>" required>
                            <label>Descripción</label>
                            <textarea name="descripcion" required><%= prod.getDescripcion()%></textarea>
                            <label>Precio</label>
                            <input type="number" step="0.01" name="precio" value="<%= prod.getPrecio()%>" required>
                            <label>Stock</label>
                            <input type="number" name="stock" value="<%= prod.getStock()%>" required>
                            <label>Unidad de Medida</label>
                            <input type="text" name="unidadMedida" value="<%= prod.getUnidadMedida() != null ? prod.getUnidadMedida() : ""%>">
                            <label>Proveedor</label>
                            <select name="idProveedor" required>
                                <% for (Proveedor proveedor : proveedores) {%>
                                <option value="<%= proveedor.getIdProveedor()%>" <%= proveedor.getIdProveedor() == prod.getIdProveedor() ? "selected" : ""%>><%= proveedor.getRazonSocial()%></option>
                                <% }%>
                            </select>
                            <label>Imagen</label>
                            <input type="file" name="imagen" accept="image/*">
                            <label>Estado</label>
                            <select name="estado">
                                <option value="1" <%= prod.getEstado() == 1 ? "selected" : ""%>>Activo</option>
                                <option value="0" <%= prod.getEstado() == 0 ? "selected" : ""%>>Inactivo</option>
                            </select>
                            <button type="submit" class="btn btn-agregar">Guardar Cambios</button>
                        </form>
                    </div>
                </div>
                <% }
                } else { %>
                <tr><td colspan="7">No hay productos registrados.</td></tr>
                <% } %>
                </tbody>
            </table>

            <!-- MODAL AGREGAR -->
            <div id="modalAgregarProducto" class="modal">
                <div class="modal-content animado">
                    <span class="cerrar" onclick="cerrarModal('modalAgregarProducto')">&times;</span>
                    <h2>Agregar Nuevo Producto</h2>
                    <form action="ProductoServlet" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="accion" value="guardar">
                        <label>Proveedor</label>
                        <select name="idProveedor" required>
                            <option value="">-- Seleccione un proveedor --</option>
                            <% for (Proveedor proveedor : proveedores) {%>
                            <option value="<%= proveedor.getIdProveedor()%>"><%= proveedor.getRazonSocial()%></option>
                            <% }%>
                        </select>
                        <label>Nombre</label>
                        <input type="text" name="nombre" required>
                        <label>Descripción</label>
                        <textarea name="descripcion" required></textarea>
                        <label>Precio</label>
                        <input type="number" step="0.01" name="precio" required>
                        <label>Stock</label>
                        <input type="number" name="stock" required>
                        <label>Unidad de Medida</label>
                        <input type="text" name="unidadMedida">
                        <label>Imagen</label>
                        <input type="file" name="imagen" accept="image/*">
                        <input type="hidden" name="estado" value="1">
                        <button type="submit" class="btn btn-agregar">Registrar</button>
                    </form>
                </div>
            </div>
        </main>

        <button id="modoNocheBtn" class="modo-noche-flotante">🌙</button>
        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
        <script src="<%= request.getContextPath()%>/Js/JsAdmin/GestorProductos.js"></script>
        
    </body>
</html>
