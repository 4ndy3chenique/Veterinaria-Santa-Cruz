<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/proteger.jsp" %>
<%@page import="Modelo.Producto"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Map"%>
<%@page import="Modelo.Proveedor"%>

<%
    // Recuperar mensajes de sesión
    String mensaje = (String) session.getAttribute("mensaje");
    String tipoMensaje = (String) session.getAttribute("tipoMensaje");
    session.removeAttribute("mensaje");
    session.removeAttribute("tipoMensaje");

    // Recuperar atributos del request
    List<Producto> listaProductos = (List<Producto>) request.getAttribute("productos");
    // El estado del filtro de inactivos se pasa como String desde el servlet
    String mostrarInactivosState = (String) request.getAttribute("mostrarInactivosState");

    // Bandera para el JSP: true si se están mostrando TODOS los productos (activos e inactivos)
    boolean showingAll = false;
    Object showingAllAttr = request.getAttribute("showingAll");
    if (showingAllAttr instanceof Boolean) {
        showingAll = (Boolean) showingAllAttr;
    }

    Map<Integer, String> mapaProveedores = (Map<Integer, String>) request.getAttribute("mapaProveedores");

    // Recuperar producto para editar si viene de una redirección (ej. error de validación)
    Producto productToEdit = (Producto) session.getAttribute("productToEdit");
    session.removeAttribute("productToEdit"); // Limpiar después de usar

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    // Lógica para el texto y enlace del botón de filtro
    String toggleButtonText;
    String toggleButtonLink;
    String toggleButtonClass;

    if (showingAll) { // Si actualmente se muestran TODOS los productos
        toggleButtonText = "Mostrar Inactivos";
        toggleButtonLink = request.getContextPath() + "/ProductoRecepServlet?accion=listar&busqueda=" + (request.getParameter("busqueda") != null ? request.getParameter("busqueda") : "") + "&mostrarInactivos=true";
        toggleButtonClass = "btn-limpiar"; // Usar un color que indique "cambiar a inactivos"
    } else if ("true".equals(mostrarInactivosState)) { // Si actualmente se muestran SOLO INACTIVOS
        toggleButtonText = "Mostrar Activos";
        toggleButtonLink = request.getContextPath() + "/ProductoRecepServlet?accion=listar&busqueda=" + (request.getParameter("busqueda") != null ? request.getParameter("busqueda") : "") + "&mostrarInactivos=false";
        toggleButtonClass = "btn-desactivar"; // Usar un color que indique "cambiar a activos"
    } else { // Si actualmente se muestran SOLO ACTIVOS ("false".equals(mostrarInactivosState))
        toggleButtonText = "Mostrar Todos";
        toggleButtonLink = request.getContextPath().concat("/ProductoRecepServlet?accion=listar"); // Sin parámetro para mostrar todos
        if (request.getParameter("busqueda") != null && !request.getParameter("busqueda").isEmpty()) {
            toggleButtonLink += "&busqueda=" + request.getParameter("busqueda");
        }
        toggleButtonClass = "btn-activar"; // Usar un color que indique "cambiar a todos"
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Productos - Veterinaria Santa Cruz</title>
    <link href="https://cdn.jsdelivr.net/npm/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
    <style>
        /* Estilos generales */
        body {
            font-family: 'Arial', sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
        }

        main {
            margin-left: 250px;
            padding: 20px;
            transition: all 0.3s;
        }

        .header-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            flex-wrap: wrap;
            gap: 15px; /* Espaciado entre elementos */
        }

        h1 {
            color: #2b7a78;
            margin: 0;
        }

        /* Estilos para la tabla */
        .tabla-productos {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            background-color: white;
            border-radius: 8px;
            overflow: hidden;
        }

        .tabla-productos th, .tabla-productos td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #e0e0e0;
        }

        .tabla-productos th {
            background-color: #3aafa9;
            color: white;
            font-weight: 600;
        }

        .tabla-productos tr:nth-child(even) {
            background-color: #f9f9f9;
        }

        .tabla-productos tr:hover {
            background-color: #f1f1f1;
        }

        /* Estilos para los botones */
        .btn {
            padding: 8px 16px;
            border-radius: 4px;
            border: none;
            cursor: pointer;
            font-weight: 500;
            transition: all 0.3s;
            margin-left: 5px;
            text-decoration: none;
            display: inline-block;
        }

        .btn-agregar {
            background-color: #2b7a78;
            color: white;
        }

        .btn-agregar:hover {
            background-color: #1f5e5b;
        }

        .btn-editar {
            background-color: #ffc107;
            color: #212529;
        }

        .btn-editar:hover {
            background-color: #e0a800;
        }

        .btn-desactivar { /* Nuevo nombre para btn-eliminar de antes */
            background-color: #dc3545;
            color: white;
        }

        .btn-desactivar:hover {
            background-color: #c82333;
        }

        .btn-activar {
            background-color: #28a745;
            color: white;
        }

        .btn-activar:hover {
            background-color: #218838;
        }

        .btn-buscar {
            background-color: #17a2b8;
            color: white;
        }

        .btn-buscar:hover {
            background-color: #138496;
        }

        .btn-limpiar {
            background-color: #6c757d;
            color: white;
        }

        .btn-limpiar:hover {
            background-color: #5a6268;
        }

        .btn-eliminar-definitivo { /* Nuevo estilo para eliminar definitivamente */
            background-color: #6a0000;
            color: white;
        }
        .btn-eliminar-definitivo:hover {
            background-color: #4a0000;
        }

        /* Estilos para los estados */
        .estado-activo {
            background-color: #d4edda;
            color: #155724;
            padding: 5px 10px;
            border-radius: 4px;
            font-weight: bold;
            display: inline-block;
        }

        .estado-inactivo {
            background-color: #f8d7da;
            color: #721c24;
            padding: 5px 10px;
            border-radius: 4px;
            font-weight: bold;
            display: inline-block;
        }

        .estado-disponible {
            background-color: #e2f0fb;
            color: #0c5460;
            padding: 5px 10px;
            border-radius: 4px;
            font-weight: bold;
            display: inline-block;
        }

        .estado-agotado {
            background-color: #fff3cd;
            color: #856404;
            padding: 5px 10px;
            border-radius: 4px;
            font-weight: bold;
            display: inline-block;
        }

        /* Estilos para la imagen del producto */
        .img-producto {
            width: 50px;
            height: 50px;
            object-fit: cover;
            border-radius: 4px;
            border: 1px solid #ddd;
        }

        /* Formulario de búsqueda */
        .form-busqueda {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 15px;
            flex-grow: 1; /* Permite que el formulario ocupe espacio disponible */
            justify-content: flex-end; /* Alinea a la derecha dentro de su contenedor */
        }

        .form-busqueda input[type="text"] {
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            width: 250px;
        }

        /* Mensajes de alerta */
        .alert {
            padding: 10px 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }

        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        /* No hay datos */
        .no-data {
            text-align: center;
            padding: 20px;
            color: #6c757d;
            font-style: italic;
        }

        /* Fecha de registro */
        .fecha-registro {
            font-size: 0.85em;
            color: #6c757d;
        }

        /* Contenedor de filtros */
        .filtros-container {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            flex-wrap: wrap;
            gap: 15px;
        }

        .switch-inactivos {
            display: flex;
            align-items: center;
            gap: 5px;
        }

        /* Estilos para los modales (general) */
        .modal {
            display: none; /* Oculto por defecto */
            position: fixed; /* Posición fija */
            z-index: 1000; /* Z-index alto para que esté por encima de todo */
            left: 0;
            top: 0;
            width: 100%; /* Ancho completo */
            height: 100%; /* Alto completo */
            overflow: auto; /* Habilitar scroll si el contenido es muy grande */
            background-color: rgba(0,0,0,0.4); /* Fondo semi-transparente */
            justify-content: center; /* Centrar horizontalmente */
            align-items: center; /* Centrar verticalmente */
            backdrop-filter: blur(5px); /* Efecto de desenfoque en el fondo */
        }

        .modal-content {
            background-color: #fefefe;
            margin: auto; /* Centrar el contenido del modal */
            padding: 30px;
            border: 1px solid #888;
            width: 80%; /* Ancho del contenido del modal */
            max-width: 600px; /* Ancho máximo */
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
            position: relative;
            animation: fadeIn 0.3s ease-out; /* Animación de entrada */
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .close-button {
            color: #aaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
            position: absolute;
            top: 10px;
            right: 20px;
            cursor: pointer;
        }

        .close-button:hover,
        .close-button:focus {
            color: black;
            text-decoration: none;
            cursor: pointer;
        }

        .modal-content h2 {
            color: #2b7a78;
            margin-top: 0;
            margin-bottom: 20px;
            text-align: center;
        }

        .modal-content form label {
            display: block;
            margin-bottom: 5px;
            color: #333;
            font-weight: bold;
        }

        .modal-content form input[type="text"],
        .modal-content form input[type="number"],
        .modal-content form input[type="date"], /* Nuevo para fecha */
        .modal-content form textarea,
        .modal-content form select {
            width: calc(100% - 22px); /* Ajustar padding y borde */
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            box-sizing: border-box; /* Incluir padding y borde en el ancho */
        }

        /* Estilos específicos para campos de solo lectura/deshabilitados en el modal de edición */
        .modal-content form input[readonly],
        .modal-content form textarea[readonly],
        .modal-content form select[disabled],
        .modal-content form input[type="file"][disabled] {
            background-color: #e9e9e9; /* Un color de fondo para indicar que es de solo lectura */
            cursor: not-allowed; /* Un cursor para indicar que no es editable */
        }


        .modal-content form textarea {
            resize: vertical;
            min-height: 80px;
        }

        .modal-content form button {
            margin-top: 10px;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            font-weight: bold;
        }

        #imagePreview {
            display: block;
            margin-top: 10px;
            margin-bottom: 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            object-fit: cover;
        }
    </style>
</head>
<body>
    <nav class="sidebar">
        <header>
            <div class="image-text">
                <span class="image">
                    <img id="logoAdmin" src="<%= request.getContextPath()%>/Recursos/Logo.png" alt="Logo de Veterinaria Santa Cruz" class="logo">
                </span>
                <div class="header-text">
                    <span class="name">Recepcionista</span>
                    <span class="profession">Veterinaria Santa Cruz</span>
                </div>
            </div>
        </header>

        <div class="menu-bar">
            <ul class="menu-links">
                <li class="nav-link">
                    <a href="<%= request.getContextPath()%>/VistasWeb/VistasRecep/RecepDash.jsp">
                        <i class='bx bx-home-alt icon'></i><span class="text">General</span>
                    </a>
                </li>
                <li class="nav-link">
                    <a href="<%= request.getContextPath()%>/ClienteRServlet">
                        <i class='bx bx-group icon'></i><span class="text">Clientes</span></a>
                </li>
                <li class="nav-link">
                    <a href="<%= request.getContextPath()%>/CitaServlet">
                        <i class='bx bxs-calendar icon'></i><span class="text">Citas</span></a>
                </li>
                <li class="nav-link">
                    <a href="${pageContext.request.contextPath}/UsuarioCitaRecepServlet">
                        <i class='bx bx-calendar-alt icon'></i><span class="text">Citas de Usuarios</span></a>
                </li>
                <li class="nav-link">
                    <a href="${pageContext.request.contextPath}/ProductoRecepServlet" class="active">
                        <i class='bx bx-package icon'></i><span class="text">Productos</span>
                    </a>
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
                   <a href="#" class="btn btn-agregar" onclick="openSaleModal(); return false;">Vender Producto</a>
            </div>
        </div>

        <div class="filtros-container">
            <div class="form-busqueda">
                <form method="get" action="${pageContext.request.contextPath}/ProductoRecepServlet">
                    <input type="hidden" name="accion" value="listar">
                    <input type="text" name="busqueda" placeholder="Buscar productos..."
                            value="${param.busqueda != null ? param.busqueda : ''}">
                    <%-- Usar mostrarInactivosState para preservar el filtro al buscar --%>
                    <input type="hidden" name="mostrarInactivos" value="<%= mostrarInactivosState != null ? mostrarInactivosState : "" %>">
                    <button type="submit" class="btn btn-buscar">Buscar</button>
                    <a href="${pageContext.request.contextPath}/ProductoRecepServlet?accion=listar" class="btn btn-limpiar">Limpiar</a>
                </form>
            </div>
            <div class="switch-inactivos">
                <span>Filtrar por estado:</span>
                <a href="<%= toggleButtonLink %>" class="btn <%= toggleButtonClass %>">
                    <%= toggleButtonText %>
                </a>
            </div>
        </div>

        <% if (mensaje != null) { %>
            <div class="alert <%= tipoMensaje != null ? tipoMensaje : "" %>"><%= mensaje %></div>
        <% } %>

        <div class="tabla-productos-container">
            <table class="tabla-productos">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Imagen</th>
                        <th>Nombre</th>
                        <th>Descripción</th>
                        <th>Precio (S/.)</th>
                        <th>Stock</th>
                        <th>Unidad</th>
                        <th>Proveedor</th>
                        <th>Estado</th>
                        <th>Registro</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (listaProductos != null && !listaProductos.isEmpty()) {
                        for (Producto p : listaProductos) {
                            String claseEstado = p.getEstado() == 1 ? "estado-activo" : "estado-inactivo";
                            String estadoTexto = p.getEstado() == 1 ? "Activo" : "Inactivo";
                            String claseStock = p.getStock() > 0 ? "estado-disponible" : "estado-agotado";
                            String stockTexto = p.getStock() > 0 ? "Disponible" : "Agotado";

                            // Obtener el nombre del proveedor usando el mapa
                            String nombreProveedor = "N/A"; // Valor por defecto si no se encuentra
                            if (mapaProveedores != null && mapaProveedores.containsKey(p.getIdProveedor())) {
                                nombreProveedor = mapaProveedores.get(p.getIdProveedor());
                            } else {
                                // Para depuración: si no encuentra el ID del proveedor en el mapa
                                System.out.println("DEBUG: Proveedor ID " + p.getIdProveedor() + " no encontrado en el mapa para producto ID " + p.getIdProducto());
                            }
                    %>
                        <tr>
                            <td><%= p.getIdProducto() %></td>
                            <td>
                                <% // Asegúrate de que la ruta de la imagen sea correcta
                                    String imagenSrc = (p.getImagen() != null && !p.getImagen().isEmpty()) ?
                                            request.getContextPath() + "/" + p.getImagen() :
                                            request.getContextPath() + "/Recursos/sin_imagen.png"; // Imagen por defecto
                                %>
                                <img src="<%= imagenSrc %>"
                                            alt="<%= p.getNombreProducto() %>" class="img-producto">
                            </td>
                            <td><%= p.getNombreProducto() %></td>
                            <td><%= p.getDescripcion() != null ? p.getDescripcion() : "" %></td>
                            <td>S/. <%= String.format("%.2f", p.getPrecio()) %></td>
                            <td><%= p.getStock() %></td>
                            <td><%= p.getUnidadMedida() %></td>
                            <td><%= nombreProveedor %></td>
                            <td>
                                <span class="<%= claseEstado %>"><%= estadoTexto %></span>
                                <span class="<%= claseStock %>"><%= stockTexto %></span>
                            </td>
                            <td class="fecha-registro">
                                <%
                                if (p.getFechaRegistro() != null) {
                                    out.print(dateFormat.format(p.getFechaRegistro()));
                                } else {
                                    out.print("N/A");
                                }
                                %>
                            </td>
                            <td>
                                <%-- Cambiado para llamar a la función JavaScript editProduct --%>
                                <a href="#" onclick="editProduct(<%= p.getIdProducto() %>); return false;"
                                   class="btn btn-editar">Editar</a>
                                <%-- ELIMINADO: Botón Desactivar/Activar --%>
                                <% if (p.getEstado() == 0) { %> <%-- Solo mostrar eliminar definitivo si está inactivo --%>
                                    <a href="${pageContext.request.contextPath}/ProductoRecepServlet?accion=eliminarDefinitivo&id=<%= p.getIdProducto() %>&busqueda=${param.busqueda != null ? param.busqueda : ''}&mostrarInactivos=<%= mostrarInactivosState != null ? mostrarInactivosState : "" %>"
                                       class="btn btn-eliminar-definitivo"
                                       onclick="return confirm('¿Está seguro de eliminar permanentemente este producto? Esta acción no se puede deshacer.')">Eliminar Def.</a>
                                <% } %>
                            </td>
                        </tr>
                    <% }
                    } else { %>
                        <tr>
                            <td colspan="11" class="no-data">
                                <%
                                String noDataMessage;
                                if (request.getParameter("busqueda") != null && !request.getParameter("busqueda").isEmpty()) {
                                    noDataMessage = "No se encontraron productos con la búsqueda: '" + request.getParameter("busqueda") + "'";
                                } else if ("true".equals(mostrarInactivosState)) {
                                    noDataMessage = "No hay productos inactivos para mostrar.";
                                } else if ("false".equals(mostrarInactivosState)) {
                                    noDataMessage = "No hay productos activos para mostrar.";
                                } else { // showingAll
                                    noDataMessage = "No hay productos registrados.";
                                }
                                out.print(noDataMessage);
                                %>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </main>

    <!-- Modal para Agregar/Editar Producto (EXISTENTE) -->
    <div id="productModal" class="modal">
        <div class="modal-content">
            <span class="close-button" onclick="closeProductModal()">&times;</span>
            <h2><span id="modalTitle"></span> Producto</h2>
            <form id="productForm" action="${pageContext.request.contextPath}/ProductoRecepServlet" method="post" enctype="multipart/form-data">
                <input type="hidden" name="accion" id="formAccion" value="guardar">
                <input type="hidden" name="idProducto" id="idProducto">

                <label for="nombre">Nombre:</label>
                <input type="text" id="nombre" name="nombre" required readonly><br>

                <label for="descripcion">Descripción:</label>
                <textarea id="descripcion" name="descripcion" readonly></textarea><br>

                <label for="precio">Precio (S/.):</label>
                <input type="number" id="precio" name="precio" step="0.01" required><br>

                <label for="stock">Stock:</label>
                <input type="number" id="stock" name="stock" required><br>

                <label for="unidadMedida">Unidad de Medida:</label>
                <input type="text" id="unidadMedida" name="unidadMedida" readonly><br>

                <label for="idProveedor">Proveedor:</label>
                <select id="idProveedor" name="idProveedor" required disabled>
                    <option value="">Seleccione un proveedor</option>
                    <%-- Aquí se usa el mapa de proveedores pasado desde el servlet --%>
                    <% if (mapaProveedores != null) {
                        for (Map.Entry<Integer, String> entry : mapaProveedores.entrySet()) { %>
                            <option value="<%= entry.getKey() %>"><%= entry.getValue() %></option>
                        <% }
                    } %>
                </select><br>
                <%-- Campo oculto para enviar el ID del proveedor, ya que el select está deshabilitado --%>
                <input type="hidden" name="originalIdProveedor" id="originalIdProveedor">

                <label for="estado">Estado:</label>
                <select id="estado" name="estado" required>
                    <option value="1">Activo</option>
                    <option value="0">Inactivo</option>
                </select><br>

                <label for="imagen">Imagen:</label>
                <input type="file" id="imagen" name="imagen" accept="image/*" onchange="previewImage(event)" disabled><br>
                <img id="imagePreview" src="" alt="Previsualización de Imagen" style="max-width: 100px; max-height: 100px; display: none;">
                <input type="hidden" name="currentImagen" id="currentImagen"> <!-- Para mantener la imagen existente en edición si no se sube una nueva -->

                <button type="submit" class="btn btn-agregar">Guardar Cambios</button>
                <button type="button" class="btn btn-limpiar" onclick="closeProductModal()">Cancelar</button>
            </form>
        </div>
    </div>

    <!-- NUEVO Modal para Vender Producto -->
    <div id="saleModal" class="modal">
        <div class="modal-content">
            <span class="close-button" onclick="closeSaleModal()">&times;</span>
            <h2>Registrar Venta</h2>
            <form id="saleForm" action="${pageContext.request.contextPath}/VentaServlet" method="post">
                <input type="hidden" name="accion" value="registrarVenta">

                <label for="nombreCliente">Nombre del Cliente:</label>
                <input type="text" id="nombreCliente" name="nombreCliente" required><br>

                <label for="idProductoVenta">Producto:</label>
                <select id="idProductoVenta" name="idProductoVenta" required onchange="calculateTotalPrice()">
                    <option value="">Seleccione un producto</option>
                    <!-- Las opciones se llenarán con JavaScript desde allProducts -->
                </select><br>

                <label for="cantidad">Cantidad:</label>
                <input type="number" id="cantidad" name="cantidad" min="1" value="1" required oninput="calculateTotalPrice()"><br>

                <label for="fechaVenta">Fecha de Venta:</label>
                <input type="date" id="fechaVenta" name="fechaVenta" required><br>

                <label for="precioTotal">Precio Total (S/.):</label>
                <input type="text" id="precioTotal" name="precioTotal" readonly value="0.00"><br>

                <button type="submit" class="btn btn-agregar">Registrar Venta</button>
                <button type="button" class="btn btn-limpiar" onclick="closeSaleModal()">Cancelar</button>
            </form>
        </div>
    </div>

    <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>
    <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
    <script>
        // JavaScript para controlar el modal de PRODUCTO (EXISTENTE)
        const productModal = document.getElementById('productModal');
        const modalTitle = document.getElementById('modalTitle');
        const productForm = document.getElementById('productForm');
        const idProductoInput = document.getElementById('idProducto');
        const formAccionInput = document.getElementById('formAccion');
        const nombreInput = document.getElementById('nombre');
        const descripcionInput = document.getElementById('descripcion');
        const precioInput = document.getElementById('precio');
        const stockInput = document.getElementById('stock');
        const unidadMedidaInput = document.getElementById('unidadMedida');
        const idProveedorSelect = document.getElementById('idProveedor');
        const estadoSelect = document.getElementById('estado');
        const imagenInput = document.getElementById('imagen');
        const imagePreview = document.getElementById('imagePreview');
        const currentImagenInput = document.getElementById('currentImagen');
        // Nuevo para el ID de proveedor original
        const originalIdProveedorInput = document.getElementById('originalIdProveedor');


        // JavaScript para controlar el NUEVO modal de VENTA
        const saleModal = document.getElementById('saleModal');
        const saleForm = document.getElementById('saleForm');
        const idProductoVentaSelect = document.getElementById('idProductoVenta');
        const cantidadInput = document.getElementById('cantidad');
        const fechaVentaInput = document.getElementById('fechaVenta');
        const precioTotalInput = document.getElementById('precioTotal');

        // Lista de productos disponible en el JSP (para edición y para el modal de venta)
        const allProducts = [
            <%
            if (listaProductos != null) {
                for (int i = 0; i < listaProductos.size(); i++) {
                    Producto p = listaProductos.get(i);
                    // Escapar comillas simples en strings para JavaScript
                    String nombreProd = p.getNombreProducto() != null ? p.getNombreProducto().replace("'", "\\'") : "";
                    String descProd = p.getDescripcion() != null ? p.getDescripcion().replace("'", "\\'") : "";
                    String unidadMed = p.getUnidadMedida() != null ? p.getUnidadMedida().replace("'", "\\'") : "";
                    String imagenPath = p.getImagen() != null ? p.getImagen().replace("'", "\\'") : "";

                    out.print("{");
                    out.print("idProducto: " + p.getIdProducto() + ",");
                    out.print("nombreProducto: '" + nombreProd + "',");
                    out.print("descripcion: '" + descProd + "',");
                    out.print("precio: " + p.getPrecio() + ",");
                    out.print("stock: " + p.getStock() + ",");
                    out.print("unidadMedida: '" + unidadMed + "',");
                    out.print("estado: " + p.getEstado() + ",");
                    out.print("idProveedor: " + p.getIdProveedor() + ",");
                    out.print("imagen: '" + imagenPath + "'");
                    out.print("}" + (i < listaProductos.size() - 1 ? "," : ""));
                }
            }
            %>
        ];

        // Funciones para el modal de PRODUCTO (EXISTENTE)
        function openProductModal(productData) {
            productModal.style.display = 'flex'; // Usar flex para centrar
            productForm.reset(); // Limpiar el formulario
            imagePreview.style.display = 'none';
            imagePreview.src = '';
            currentImagenInput.value = ''; // Limpiar la imagen actual oculta

            if (productData) {
                // Modo edición
                modalTitle.textContent = 'Editar';
                idProductoInput.value = productData.idProducto;
                formAccionInput.value = 'guardar'; // La acción es 'guardar' para ambos (insertar/actualizar)
                
                // Campos de solo lectura
                nombreInput.value = productData.nombreProducto;
                nombreInput.readOnly = true; // Hacer de solo lectura
                descripcionInput.value = productData.descripcion;
                descripcionInput.readOnly = true; // Hacer de solo lectura
                unidadMedidaInput.value = productData.unidadMedida;
                unidadMedidaInput.readOnly = true; // Hacer de solo lectura

                // Campos editables
                precioInput.value = productData.precio;
                stockInput.value = productData.stock;
                estadoSelect.value = productData.estado;

                // Para el select de proveedor, seleccionarlo y deshabilitarlo
                idProveedorSelect.value = productData.idProveedor;
                idProveedorSelect.disabled = true; // Deshabilitar para que no se pueda cambiar
                originalIdProveedorInput.value = productData.idProveedor; // Guardar el ID original para enviar

                // Deshabilitar input de imagen
                imagenInput.disabled = true;

                if (productData.imagen && productData.imagen !== '') {
                    imagePreview.src = '<%= request.getContextPath() %>/' + productData.imagen;
                    imagePreview.style.display = 'block';
                    currentImagenInput.value = productData.imagen; // Guardar la ruta de la imagen existente
                }
            } else {
                // Modo agregar (este caso ya no se usa con el botón "Vender Producto")
                // Pero se mantiene por si se añade un botón de "Agregar Producto" en otro lado
                modalTitle.textContent = 'Agregar';
                idProductoInput.value = ''; // ID vacío para nuevo producto
                formAccionInput.value = 'guardar';
                estadoSelect.value = '1'; // Por defecto activo
                idProveedorSelect.value = ''; // Sin proveedor seleccionado
                
                // Habilitar campos para agregar
                nombreInput.readOnly = false;
                descripcionInput.readOnly = false;
                unidadMedidaInput.readOnly = false;
                idProveedorSelect.disabled = false;
                imagenInput.disabled = false;
            }
        }

        function closeProductModal() {
            productModal.style.display = 'none';
            productForm.reset(); // Limpiar el formulario al cerrar
            imagePreview.style.display = 'none';
            imagePreview.src = '';
            currentImagenInput.value = '';
            // Asegurarse de re-habilitar los campos deshabilitados para el siguiente uso (si se abre en modo agregar)
            nombreInput.readOnly = false;
            descripcionInput.readOnly = false;
            unidadMedidaInput.readOnly = false;
            idProveedorSelect.disabled = false;
            imagenInput.disabled = false;
        }

        function editProduct(productId) {
            const product = allProducts.find(p => p.idProducto === productId);
            if (product) {
                openProductModal(product);
            } else {
                console.error('Producto con ID ' + productId + ' no encontrado en la lista del JSP.');
                alert('Error: Producto no encontrado para edición.');
            }
        }

        function previewImage(event) {
            const reader = new FileReader();
            reader.onload = function(){
                imagePreview.src = reader.result;
                imagePreview.style.display = 'block';
            };
            if (event.target.files[0]) {
                reader.readAsDataURL(event.target.files[0]);
            } else {
                imagePreview.style.display = 'none';
                imagePreview.src = '';
            }
        }

        // Funciones para el NUEVO modal de VENTA
        function openSaleModal() {
            saleModal.style.display = 'flex';
            saleForm.reset(); // Limpiar el formulario

            // Llenar el select de productos
            idProductoVentaSelect.innerHTML = '<option value="">Seleccione un producto</option>'; // Resetear opciones
            allProducts.forEach(product => {
                // Solo añadir productos activos y con stock > 0
                if (product.estado === 1 && product.stock > 0) {
                    const option = document.createElement('option');
                    option.value = product.idProducto;
                    option.textContent = product.nombreProducto + ' (Stock: ' + product.stock + ')';
                    idProductoVentaSelect.appendChild(option);
                }
            });

            // Establecer la fecha actual
            const today = new Date();
            const yyyy = today.getFullYear();
            const mm = String(today.getMonth() + 1).padStart(2, '0'); // Months start at 0!
            const dd = String(today.getDate()).padStart(2, '0');
            fechaVentaInput.value = `${yyyy}-${mm}-${dd}`;

            calculateTotalPrice(); // Calcular el total inicial
        }

        function closeSaleModal() {
            saleModal.style.display = 'none';
            saleForm.reset(); // Limpiar el formulario al cerrar
        }

        function calculateTotalPrice() {
            const selectedProductId = idProductoVentaSelect.value;
            const quantity = parseInt(cantidadInput.value);
            let totalPrice = 0;

            if (selectedProductId && !isNaN(quantity) && quantity > 0) {
                const selectedProduct = allProducts.find(p => p.idProducto == selectedProductId);
                if (selectedProduct) {
                    totalPrice = selectedProduct.precio * quantity;
                    // Validar stock
                    if (quantity > selectedProduct.stock) {
                        alert('Error: La cantidad solicitada (' + quantity + ') excede el stock disponible (' + selectedProduct.stock + ') para ' + selectedProduct.nombreProducto + '.');
                        cantidadInput.value = selectedProduct.stock; // Ajustar a stock máximo
                        totalPrice = selectedProduct.precio * selectedProduct.stock; // Recalcular con stock ajustado
                    }
                }
            }
            precioTotalInput.value = totalPrice.toFixed(2); // Formatear a 2 decimales
        }


        // Cerrar cualquier modal si se hace clic fuera de él
        window.onclick = function(event) {
            if (event.target == productModal) {
                closeProductModal();
            }
            if (event.target == saleModal) {
                closeSaleModal();
            }
        }

        // Manejar la apertura del modal de PRODUCTO si hay un producto para editar desde la sesión (ej. después de un error de validación)
        window.onload = function() {
            // Se construye el objeto JavaScript directamente si productToEdit no es nulo
            const productToEditFromSession = <%
                if (productToEdit != null) {
                    // Escapar comillas simples en strings para JavaScript
                    String editProdNombre = productToEdit.getNombreProducto() != null ? productToEdit.getNombreProducto().replace("'", "\\'") : "";
                    String editProdDesc = productToEdit.getDescripcion() != null ? productToEdit.getDescripcion().replace("'", "\\'") : "";
                    String editProdUnidad = productToEdit.getUnidadMedida() != null ? productToEdit.getUnidadMedida().replace("'", "\\'") : "";
                    String editProdImagen = productToEdit.getImagen() != null ? productToEdit.getImagen().replace("'", "\\'") : "";

                    out.print("{" +
                        "idProducto: " + productToEdit.getIdProducto() + "," +
                        "nombreProducto: '" + editProdNombre + "'," +
                        "descripcion: '" + editProdDesc + "'," +
                        "precio: " + productToEdit.getPrecio() + "," +
                        "stock: " + productToEdit.getStock() + "," +
                        "unidadMedida: '" + editProdUnidad + "'," +
                        "estado: " + productToEdit.getEstado() + "," +
                        "idProveedor: " + productToEdit.getIdProveedor() + "," +
                        "imagen: '" + editProdImagen + "'" +
                    "}");
                } else {
                    out.print("null");
                }
            %>;

            if (productToEditFromSession) {
                openProductModal(productToEditFromSession);
            }
        };

    </script>
    <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
</body>
</html>
