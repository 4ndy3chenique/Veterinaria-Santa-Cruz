<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.Cliente"%>
<%@page import="Modelo.UsuarioCliente"%>
<%-- Retrieve lists from request scope --%>
<%
    List<Cliente> listaClientes = (List<Cliente>) request.getAttribute("listaClientes");
    List<UsuarioCliente> listaUsuarios = (List<UsuarioCliente>) request.getAttribute("listaUsuarios");
    // Retrieve the active tab from the servlet
    String activeTab = (String) request.getAttribute("activeTab");
    if (activeTab == null || activeTab.isEmpty()) {
        activeTab = "clientesRegistrados"; // Default tab if not set by servlet
    }
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Listado de Clientes</title>
        <link href="https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
        <style>
            /* Your existing CSS styles */
            main {
                margin-left: 280px;
                padding: 20px;
            }
            
            h1 {
                color: #333;
                margin-bottom: 20px;
            }
            body.modo-noche h1 {
                color: #e2e8f0;
            }

            .tab-container {
                background: white;
                border-radius: 8px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                overflow: hidden;
            }
            
            .tab-header {
                display: flex;
                border-bottom: 1px solid #eee;
            }
            
            .tab-button {
                padding: 12px 20px;
                background: none;
                border: none;
                cursor: pointer;
                font-weight: 500;
                color: #555;
                transition: all 0.3s;
            }
            
            .tab-button.active {
                color: #2196F3;
                border-bottom: 2px solid #2196F3;
            }
            
            .tab-button:hover:not(.active) {
                background-color: #f5f5f5;
            }
            
            .tab-content {
                display: none;
                padding: 20px;
            }
            
            .tab-content.active {
                display: block;
            }
            
            .tabla-clientes {
                width: 100%;
                border-collapse: collapse;
                margin-top: 15px;
            }
            
            .tabla-clientes th, .tabla-clientes td {
                padding: 12px 15px;
                text-align: left;
                border-bottom: 1px solid #eee;
            }
            
            .tabla-clientes th {
                background-color: #f8f9fa;
                color: #555;
                font-weight: 600;
            }
            
            .tabla-clientes tr:hover {
                background-color: #f5f5f5;
            }
            
            .btn-accion {
                padding: 6px 10px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 0.9em;
                margin-right: 5px;
            }
            .btn-editar { background-color: #2196F3; color: white; }
            .btn-editar:hover { background-color: #1976D2; }
            .btn-eliminar { background-color: #f44336; color: white; }
            .btn-eliminar:hover { background-color: #d32f2f; }
            
            .btn-agregar { /* Keep btn-agregar style for consistency if other buttons might use it later, just remove the button itself */
                display: inline-block;
                padding: 10px 15px;
                background-color: #4CAF50;
                color: white;
                border: none;
                border-radius: 4px;
                text-decoration: none;
                margin-bottom: 15px;
                cursor: pointer;
            }
            
            .btn-agregar:hover {
                background-color: #45a049;
            }
            
            .search-form {
                margin-bottom: 20px;
                display: flex;
                gap: 10px;
            }
            .search-form input[type="text"] {
                flex-grow: 1;
                padding: 8px;
                border: 1px solid #ccc;
                border-radius: 4px;
            }
            .search-form button {
                padding: 8px 15px;
                background-color: #007bff;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
            }
            .search-form button:hover {
                background-color: #0056b3;
            }

            .message-success {
                background-color: #d4edda;
                color: #155724;
                padding: 10px;
                border-radius: 5px;
                margin-bottom: 15px;
                border: 1px solid #c3e6cb;
            }
            .message-error {
                background-color: #f8d7da;
                color: #721c24;
                padding: 10px;
                border-radius: 5px;
                margin-bottom: 15px;
                border: 1px solid #f5c6cb;
            }
            
            /* --- Nuevos estilos para el botón de PDF --- */
            .btn-pdf {
                display: inline-flex; /* Usar flexbox para centrar icono y texto */
                align-items: center; /* Alinear verticalmente al centro */
                gap: 5px; /* Espacio entre icono y texto */
                padding: 10px 15px;
                background-color: #dc3545; /* Color rojo de Bootstrap para danger */
                color: white;
                border: none;
                border-radius: 4px;
                text-decoration: none;
                margin-left: 10px; /* Margen a la izquierda para separarlo de otros elementos */
                cursor: pointer;
                font-size: 0.9em;
                transition: background-color 0.3s ease;
            }

            .btn-pdf:hover {
                background-color: #c82333; /* Un rojo más oscuro al pasar el mouse */
            }

            .btn-pdf i {
                font-size: 1.2em; /* Tamaño del icono */
            }

            /* Estilos para modo noche */
            body.modo-noche .tab-container { background: #2d3748; color: #e2e8f0; }
            body.modo-noche .tab-header { border-bottom-color: #4a5568; }
            body.modo-noche .tab-button { color: #e2e8f0; }
            body.modo-noche .tab-button.active { color: #63b3ed; border-bottom-color: #63b3ed; }
            body.modo-noche .tab-button:hover:not(.active) { background-color: #4a5568; }
            body.modo-noche .tabla-clientes th { background-color: #1a202c; color: #ffffff; }
            body.modo-noche .tabla-clientes td { color: #e2e8f0; border-bottom-color: #4a5568; }
            body.modo-noche .tabla-clientes tr:hover { background-color: #4a5568; }
            body.modo-noche .search-form input[type="text"] { background-color: #4a5568; border-color: #636b6f; color: #e2e8f0; }
            body.modo-noche .search-form button { background-color: #63b3ed; }
            body.modo-noche .search-form button:hover { background-color: #4299e1; }
            body.modo-noche .btn-editar { background-color: #63b3ed; }
            body.modo-noche .btn-editar:hover { background-color: #4299e1; }
            body.modo-noche .btn-eliminar { background-color: #fc8181; }
            body.modo-noche .btn-eliminar:hover { background-color: #e53e3e; }
            /* Modo noche para el botón PDF */
            body.modo-noche .btn-pdf {
                background-color: #ef4444; /* Rojo más brillante para modo noche */
            }
            body.modo-noche .btn-pdf:hover {
                background-color: #dc2626; /* Rojo más oscuro al pasar el mouse en modo noche */
            }


            /* --- Estilos para los Modales --- */
            .modal {
                display: none; /* Hidden by default */
                position: fixed; /* Stay in place */
                z-index: 1000; /* Sit on top */
                left: 0;
                top: 0;
                width: 100%; /* Full width */
                height: 100%; /* Full height */
                overflow: auto; /* Enable scroll if needed */
                background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
                justify-content: center;
                align-items: center;
            }

            .modal-content {
                background-color: #fefefe;
                margin: auto; /* Centered */
                padding: 25px;
                border: 1px solid #888;
                width: 80%; /* Could be responsive */
                max-width: 500px;
                border-radius: 8px;
                box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2), 0 6px 20px 0 rgba(0,0,0,0.19);
                position: relative;
            }
            body.modo-noche .modal-content {
                background-color: #2d3748;
                color: #e2e8f0;
                border-color: #4a5568;
            }

            .modal-close-button {
                color: #aaa;
                float: right;
                font-size: 28px;
                font-weight: bold;
                position: absolute;
                top: 10px;
                right: 15px;
            }

            .modal-close-button:hover,
            .modal-close-button:focus {
                color: black;
                text-decoration: none;
                cursor: pointer;
            }
            body.modo-noche .modal-close-button {
                color: #e2e8f0;
            }
            body.modo-noche .modal-close-button:hover,
            body.modo-noche .modal-close-button:focus {
                color: #aaa;
            }

            .modal-content h2 {
                margin-top: 0;
                color: #333;
                margin-bottom: 20px;
            }
            body.modo-noche .modal-content h2 {
                color: #e2e8f0;
            }

            .modal-content label {
                display: block;
                margin-bottom: 8px;
                font-weight: 500;
            }

            .modal-content input[type="text"],
            .modal-content input[type="email"],
            .modal-content input[type="password"] {
                width: calc(100% - 22px); /* Adjust for padding and border */
                padding: 10px;
                margin-bottom: 15px;
                border: 1px solid #ddd;
                border-radius: 4px;
                box-sizing: border-box; /* Include padding in width */
            }
            body.modo-noche .modal-content input[type="text"],
            body.modo-noche .modal-content input[type="email"],
            body.modo-noche .modal-content input[type="password"] {
                background-color: #4a5568;
                border-color: #636b6f;
                color: #e2e8f0;
            }

            .modal-content button[type="submit"] {
                background-color: #4CAF50;
                color: white;
                padding: 10px 15px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 1em;
            }
            .modal-content button[type="submit"]:hover {
                background-color: #45a049;
            }
            body.modo-noche .modal-content button[type="submit"] {
                background-color: #63b3ed;
            }
            body.modo-noche .modal-content button[type="submit"]:hover {
                background-color: #4299e1;
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
                        <a href="<%= request.getContextPath()%>/AdminClienteServlet"><i class='bx bxs-calendar icon'></i><span class="text">Clientes</span></a>
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
            <h1>Administración de Clientes y Usuarios</h1>

            <%-- Mensajes de éxito y error --%>
            <% String mensaje = (String) request.getAttribute("mensaje"); %>
            <% String error = (String) request.getAttribute("error"); %>
            
            <% if (mensaje != null && !mensaje.isEmpty()) { %>
                <div class="message-success"><%= mensaje %></div>
            <% } %>
            <% if (error != null && !error.isEmpty()) { %>
                <div class="message-error"><%= error %></div>
            <% } %>

            <div class="tab-container">
                <div class="tab-header">
                    <button class="tab-button" id="tabButtonClientes" onclick="openTab(event, 'clientes')">Clientes Registrados</button>
                    <button class="tab-button" id="tabButtonUsuarios" onclick="openTab(event, 'usuarios')">Usuarios Clientes</button>
                </div>
                
                <div id="clientes" class="tab-content">
                    <%-- Controles para Clientes --%>
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                        <%-- Formulario de búsqueda para Clientes --%>
                        <form action="${pageContext.request.contextPath}/AdminClienteServlet" method="post" class="search-form" style="margin-bottom: 0;">
                            <input type="hidden" name="accion" value="buscarCliente">
                            <input type="hidden" name="activeTab" value="clientesRegistrados">
                            <input type="text" name="textoBusqueda" placeholder="Buscar por Nombre, DNI, Teléfono...">
                            <button type="submit">Buscar Cliente</button>
                        </form>
                        
                        <%-- Botón para Generar PDF de Clientes --%>
                        <a href="${pageContext.request.contextPath}/ReporteClientePDFServlet?tipo=clientes" target="_blank" class="btn-pdf">
                            <i class='bx bxs-file-pdf'></i> Generar PDF Clientes
                        </a>
                    </div>
                    
                    <table class="tabla-clientes">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nombre Completo</th>
                                <th>DNI</th>
                                <th>Teléfono</th>
                                <th>Fecha de Registro</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (listaClientes != null && !listaClientes.isEmpty()) { %>
                                <% for (Cliente c : listaClientes) { %>
                                    <tr>
                                        <td><%= c.getIdCliente() %></td>
                                        <td><%= c.getNombre() %> <%= c.getApellido() %></td>
                                        <td><%= c.getDni() %></td>
                                        <td><%= c.getTelefono() %></td>
                                        <td><%= c.getFechaRegistro() %></td>
                                        <td>
                                            <%-- Botón para Editar Cliente --%>
                                            <button class="btn-accion btn-editar" 
                                                    onclick="openEditClienteModal(<%= c.getIdCliente() %>, '<%= c.getNombre() %>', '<%= c.getApellido() %>', '<%= c.getDni() %>', '<%= c.getTelefono() %>')">
                                                Editar
                                            </button>
                                            
                                            <%-- Formulario para Eliminar Cliente (con confirmación JS) --%>
                                            <form action="${pageContext.request.contextPath}/AdminClienteServlet" method="post" style="display:inline;" onsubmit="return confirm('¿Estás seguro de que quieres eliminar a <%= c.getNombre() %> <%= c.getApellido() %>?');">
                                                <input type="hidden" name="accion" value="eliminarCliente">
                                                <input type="hidden" name="activeTab" value="clientesRegistrados">
                                                <input type="hidden" name="idCliente" value="<%= c.getIdCliente() %>">
                                                <button type="submit" class="btn-accion btn-eliminar">Eliminar</button>
                                            </form>
                                        </td>
                                    </tr>
                                <% } %>
                            <% } else { %>
                                <tr>
                                    <td colspan="6" style="text-align: center;">No hay clientes registrados</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                
                <div id="usuarios" class="tab-content">
                    <%-- Controles para Usuarios Clientes --%>
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                        <%-- Formulario de búsqueda para Usuarios Clientes --%>
                        <form action="${pageContext.request.contextPath}/AdminClienteServlet" method="post" class="search-form" style="margin-bottom: 0;">
                            <input type="hidden" name="accion" value="buscarUsuario">
                            <input type="hidden" name="activeTab" value="usuariosClientes">
                            <input type="text" name="textoBusqueda" placeholder="Buscar por Nombre, DNI, Correo...">
                            <button type="submit">Buscar Usuario</button>
                        </form>

                        <%-- Botón para Generar PDF de Usuarios Clientes --%>
                        <a href="${pageContext.request.contextPath}/ReporteClientePDFServlet?tipo=usuarios" target="_blank" class="btn-pdf">
                            <i class='bx bxs-file-pdf'></i> Generar PDF Usuarios
                        </a>
                    </div>

                    <table class="tabla-clientes">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nombre Completo</th>
                                <th>DNI</th>
                                <th>Correo</th>
                                <th>Fecha de Registro</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (listaUsuarios != null && !listaUsuarios.isEmpty()) { %>
                                <% for (UsuarioCliente u : listaUsuarios) { %>
                                    <tr>
                                        <td><%= u.getIdUsuario() %></td>
                                        <td><%= u.getNombre() %> <%= u.getApellido() %></td>
                                        <td><%= u.getDni() %></td>
                                        <td><%= u.getCorreo() %></td>
                                        <td><%= u.getFechaRegistro() %></td>
                                        <td>
                                            <%-- Botón para Editar Usuario --%>
                                            <button class="btn-accion btn-editar" 
                                                    onclick="openEditUsuarioModal(<%= u.getIdUsuario() %>, '<%= u.getNombre() %>', '<%= u.getApellido() %>', '<%= u.getDni() %>', '<%= u.getTelefono() %>', '<%= u.getCorreo() %>')">
                                                Editar
                                            </button>
                                            
                                            <%-- Formulario para Eliminar Usuario (con confirmación JS) --%>
                                            <form action="${pageContext.request.contextPath}/AdminClienteServlet" method="post" style="display:inline;" onsubmit="return confirm('¿Estás seguro de que quieres eliminar a <%= u.getNombre() %> <%= u.getApellido() %>?');">
                                                <input type="hidden" name="accion" value="eliminarUsuario">
                                                <input type="hidden" name="activeTab" value="usuariosClientes">
                                                <input type="hidden" name="idUsuario" value="<%= u.getIdUsuario() %>">
                                                <button type="submit" class="btn-accion btn-eliminar">Eliminar</button>
                                            </form>
                                        </td>
                                    </tr>
                                <% } %>
                            <% } else { %>
                                <tr>
                                    <td colspan="6" style="text-align: center;">No hay usuarios registrados</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </main>
        
        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>
        <script src="${pageContext.request.contextPath}/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
        
        <script>
            // Función para cambiar entre pestañas
            function openTab(evt, tabName) {
                const tabContents = document.getElementsByClassName("tab-content");
                for (let i = 0; i < tabContents.length; i++) {
                    tabContents[i].classList.remove("active");
                }
                
                const tabButtons = document.getElementsByClassName("tab-button");
                for (let i = 0; i < tabButtons.length; i++) {
                    tabButtons[i].classList.remove("active");
                }
                
                document.getElementById(tabName).classList.add("active");
                evt.currentTarget.classList.add("active");
            }

            // Script para activar la pestaña correcta al cargar la página
            document.addEventListener('DOMContentLoaded', function() {
                const activeTabFromServlet = "<%= activeTab %>"; // Get value from servlet
                if (activeTabFromServlet === "clientesRegistrados") {
                    document.getElementById("clientes").classList.add("active");
                    document.getElementById("tabButtonClientes").classList.add("active");
                } else if (activeTabFromServlet === "usuariosClientes") {
                    document.getElementById("usuarios").classList.add("active");
                    document.getElementById("tabButtonUsuarios").classList.add("active");
                } else {
                    // Fallback to default if somehow not set (e.g., initial load without param)
                    document.getElementById("clientes").classList.add("active");
                    document.getElementById("tabButtonClientes").classList.add("active");
                }
            });

            // --- Funciones para abrir/cerrar Modales ---
            function closeModal(modalId) {
                document.getElementById(modalId).style.display = 'none';
            }

            // Se ha quitado la función openAddClienteModal()

            // --- Modal EDITAR Cliente ---
            function openEditClienteModal(id, nombre, apellido, dni, telefono) {
                document.getElementById('editClienteId').value = id;
                document.getElementById('editClienteNombre').value = nombre;
                document.getElementById('editClienteApellido').value = apellido;
                document.getElementById('editClienteDni').value = dni;
                document.getElementById('editClienteTelefono').value = telefono;
                document.getElementById('editClienteModal').style.display = 'flex'; // Use flex for centering
            }

            // --- Modal EDITAR UsuarioCliente ---
            function openEditUsuarioModal(id, nombre, apellido, dni, telefono, correo) {
                document.getElementById('editUsuarioId').value = id;
                document.getElementById('editUsuarioNombre').value = nombre;
                document.getElementById('editUsuarioApellido').value = apellido;
                document.getElementById('editUsuarioDni').value = dni;
                document.getElementById('editUsuarioTelefono').value = telefono;
                document.getElementById('editUsuarioCorreo').value = correo;
                // NOTA: No pases la contraseña directamente por JS por seguridad.
                // Si la edición de contraseña es necesaria, el campo se dejaría vacío
                // y el servlet solo la actualizaría si el usuario ingresa una nueva.
                document.getElementById('editUsuarioContrasena').value = ''; // Siempre vacía por seguridad
                document.getElementById('editUsuarioModal').style.display = 'flex'; // Use flex for centering
            }
        </script>

        <div id="editClienteModal" class="modal">
            <div class="modal-content">
                <span class="modal-close-button" onclick="closeModal('editClienteModal')">&times;</span>
                <h2>Editar Cliente General</h2>
                <form action="${pageContext.request.contextPath}/AdminClienteServlet" method="post">
                    <input type="hidden" name="accion" value="editarCliente">
                    <input type="hidden" name="activeTab" value="clientesRegistrados">
                    <input type="hidden" name="idCliente" id="editClienteId">
                    
                    <label for="editClienteNombre">Nombre:</label>
                    <input type="text" id="editClienteNombre" name="nombre" required>
                    
                    <label for="editClienteApellido">Apellido:</label>
                    <input type="text" id="editClienteApellido" name="apellido" required>
                    
                    <label for="editClienteDni">DNI:</label>
                    <input type="text" id="editClienteDni" name="dni" required>
                    
                    <label for="editClienteTelefono">Teléfono:</label>
                    <input type="text" id="editClienteTelefono" name="telefono">
                    
                    <button type="submit">Guardar Cambios</button>
                </form>
            </div>
        </div>

        <div id="editUsuarioModal" class="modal">
            <div class="modal-content">
                <span class="modal-close-button" onclick="closeModal('editUsuarioModal')">&times;</span>
                <h2>Editar Usuario Cliente</h2>
                <form action="${pageContext.request.contextPath}/AdminClienteServlet" method="post">
                    <input type="hidden" name="accion" value="editarUsuario">
                    <input type="hidden" name="activeTab" value="usuariosClientes">
                    <input type="hidden" name="idUsuario" id="editUsuarioId">
                    
                    <label for="editUsuarioNombre">Nombre:</label>
                    <input type="text" id="editUsuarioNombre" name="nombre" required>
                    
                    <label for="editUsuarioApellido">Apellido:</label>
                    <input type="text" id="editUsuarioApellido" name="apellido" required>
                    
                    <label for="editUsuarioDni">DNI:</label>
                    <input type="text" id="editUsuarioDni" name="dni" required>
                    
                    <label for="editUsuarioTelefono">Teléfono:</label>
                    <input type="text" id="editUsuarioTelefono" name="telefono">
                    
                    <label for="editUsuarioCorreo">Correo Electrónico:</label>
                    <input type="email" id="editUsuarioCorreo" name="correo" required>
                    
                    <label for="editUsuarioContrasena">Nueva Contraseña (dejar vacío para no cambiar):</label>
                    <input type="password" id="editUsuarioContrasena" name="contrasena">
                    <small style="color: #666;">**Recomendación de seguridad:** Las contraseñas deben ser hasheadas en el servidor y nunca se deben mostrar aquí. Este campo es solo para *establecer* una nueva contraseña si es necesario.</small>
                    
                    <button type="submit">Guardar Cambios</button>
                </form>
            </div>
        </div>
    </body>
</html>