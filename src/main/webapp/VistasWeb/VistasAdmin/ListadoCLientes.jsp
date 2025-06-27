<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.Cliente"%>
<%@page import="Modelo.UsuarioCliente"%>
<%
    List<Cliente> listaClientes = (List<Cliente>) request.getAttribute("listaClientes");
    List<UsuarioCliente> listaUsuarios = (List<UsuarioCliente>) request.getAttribute("listaUsuarios");
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
            main {
                margin-left: 280px;
                padding: 20px;
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
            
            .btn-ver {
                color: #2196F3;
                text-decoration: none;
                font-weight: 500;
            }
            
            .btn-ver:hover {
                text-decoration: underline;
            }
            
            .btn-agregar {
                display: inline-block;
                padding: 10px 15px;
                background-color: #4CAF50;
                color: white;
                border: none;
                border-radius: 4px;
                text-decoration: none;
                margin-bottom: 15px;
            }
            
            .btn-agregar:hover {
                background-color: #45a049;
            }
            
            /* Estilos para modo noche */
            body.modo-noche .tab-container {
                background: #2d3748;
                color: #e2e8f0;
            }
            
            body.modo-noche .tab-header {
                border-bottom-color: #4a5568;
            }
            
            body.modo-noche .tab-button {
                color: #e2e8f0;
            }
            
            body.modo-noche .tab-button.active {
                color: #63b3ed;
                border-bottom-color: #63b3ed;
            }
            
            body.modo-noche .tab-button:hover:not(.active) {
                background-color: #4a5568;
            }
            
            body.modo-noche .tabla-clientes th {
                background-color: #1a202c;
                color: #ffffff;
            }
            
            body.modo-noche .tabla-clientes td {
                color: #e2e8f0;
                border-bottom-color: #4a5568;
            }
            
            body.modo-noche .tabla-clientes tr:hover {
                background-color: #4a5568;
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
            <div class="tab-container">
                <div class="tab-header">
                    <button class="tab-button active" onclick="openTab(event, 'clientes')">Clientes Registrados</button>
                    <button class="tab-button" onclick="openTab(event, 'usuarios')">Usuarios Clientes</button>
                </div>
                
                <div id="clientes" class="tab-content active">
                    <a href="<%= request.getContextPath()%>/AdminClienteServlet?accion=mostrarFormulario">
                        <button class="btn-agregar">Agregar Cliente</button>
                    </a>
                    <table class="tabla-clientes">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
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
                                            <a href="<%= request.getContextPath()%>/AdminClienteServlet?accion=verDetalle&idCliente=<%= c.getIdCliente() %>" class="btn-ver">Ver Detalle</a>
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
                    <table class="tabla-clientes">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
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
                                            <a href="<%= request.getContextPath()%>/AdminClienteServlet?accion=verDetalleUsuario&idUsuario=<%= u.getIdUsuario() %>" class="btn-ver">Ver Detalle</a>
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
                // Ocultar todos los contenidos de pestañas
                const tabContents = document.getElementsByClassName("tab-content");
                for (let i = 0; i < tabContents.length; i++) {
                    tabContents[i].classList.remove("active");
                }
                
                // Desactivar todos los botones de pestañas
                const tabButtons = document.getElementsByClassName("tab-button");
                for (let i = 0; i < tabButtons.length; i++) {
                    tabButtons[i].classList.remove("active");
                }
                
                // Mostrar la pestaña actual y activar el botón
                document.getElementById(tabName).classList.add("active");
                evt.currentTarget.classList.add("active");
            }
        </script>
    </body>
</html>