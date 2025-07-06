<%@ include file="/proteger.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="java.util.List"%>
<%@page import="Modelo.Veterinario"%>
<%@page import="Modelo.Recepcionista"%>
<%
    List<Veterinario> listaVeterinarios = (List<Veterinario>) request.getAttribute("listaVeterinarios");
    List<Recepcionista> listaRecepcionistas = (List<Recepcionista>) request.getAttribute("listaRecepcionistas");

    // Obtener mensajes de éxito o error del servlet
    String mensajeExito = (String) request.getAttribute("mensaje");
    String mensajeError = (String) request.getAttribute("error");
    String searchQuery = (String) request.getAttribute("searchQuery"); // Obtener el término de búsqueda
    String activeTab = (String) request.getAttribute("activeTab"); // Obtener la pestaña activa

    // Función auxiliar para escapar cadenas de texto para JavaScript
    // Más robusta: escapa backslashes, comillas simples, comillas dobles, saltos de línea y caracteres no ASCII, y </script>.
    java.util.function.Function<String, String> escapeJsString = (text) -> {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\') {
                sb.append("\\\\");
            } else if (c == '\'') {
                sb.append("\\'");
            } else if (c == '"') { // ¡NUEVO! Escapar comillas dobles
                sb.append("\\\"");
            } else if (c == '\n') {
                sb.append("\\n");
            } else if (c == '\r') {
                sb.append("\\r");
            } else if (c == '<' && i + 7 <= text.length() && text.substring(i, i + 7).equalsIgnoreCase("</script>")) {
                sb.append("<\\/script>"); // Escapar </script> para evitar cierre prematuro del script tag
                i += 6; // Saltar los caracteres de "script>"
            } else if (c < 32 || c > 126) { // Escapar caracteres de control y no ASCII como Unicode
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    };

    // Función auxiliar para escapar cadenas de texto para atributos HTML
    java.util.function.Function<String, String> escapeHtmlAttribute = (text) -> {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;") // HTML entity for single quote
                   .replace("<", "&lt;")
                   .replace(">", "&gt;");
    };
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Gestión de Empleados</title>
        <link href="https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
        <style>
            /* Estilos CSS existentes */
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
            
            .tabla-empleados {
                width: 100%;
                border-collapse: collapse;
            }
            
            .tabla-empleados th, .tabla-empleados td {
                padding: 12px 15px;
                text-align: left;
                border-bottom: 1px solid #eee;
            }
            
            .tabla-empleados th {
                background-color: #f8f9fa;
                color: #555;
                font-weight: 600;
            }
            
            .tabla-empleados tr:hover {
                background-color: #f5f5f5;
            }
            
            .acciones {
                display: flex;
                gap: 10px;
            }
            
            .btn-accion {
                padding: 6px 12px;
                border-radius: 4px;
                text-decoration: none;
                font-size: 14px;
                transition: all 0.3s;
                border: none;
                cursor: pointer;
            }
            
            .btn-ver {
                background-color: #2196F3;
                color: white;
            }
            
            .btn-editar {
                background-color: #FFC107;
                color: #212529;
            }
            
            .btn-eliminar {
                background-color: #F44336;
                color: white;
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
                font-weight: 500;
            }
            
            .btn-agregar:hover {
                background-color: #45a049;
            }
            
            /* Estilos para el nuevo botón PDF */
            .btn-pdf {
                display: inline-flex; /* Usar flexbox para alinear icono y texto */
                align-items: center; /* Centrar verticalmente */
                gap: 5px; /* Espacio entre icono y texto */
                padding: 10px 15px;
                background-color: #dc3545; /* Un rojo para PDF */
                color: white;
                border: none;
                border-radius: 4px;
                text-decoration: none;
                margin-left: 10px; /* Espacio a la izquierda del botón de búsqueda */
                font-weight: 500;
                transition: background-color 0.3s ease;
            }

            .btn-pdf:hover {
                background-color: #c82333; /* Un rojo más oscuro al pasar el ratón */
            }

            /* Estilos para el nuevo botón Excel */
            .btn-excel {
                display: inline-flex; /* Usar flexbox para alinear icono y texto */
                align-items: center; /* Centrar verticalmente */
                gap: 5px; /* Espacio entre icono y texto */
                padding: 10px 15px;
                background-color: #28a745; /* Un verde para Excel */
                color: white;
                border: none;
                border-radius: 4px;
                text-decoration: none;
                margin-left: 10px; /* Espacio a la izquierda del botón PDF */
                font-weight: 500;
                transition: background-color 0.3s ease;
            }

            .btn-excel:hover {
                background-color: #218838; /* Un verde más oscuro al pasar el ratón */
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
            
            body.modo-noche .tabla-empleados th {
                background-color: #1a202c;
                color: #ffffff;
            }
            
            body.modo-noche .tabla-empleados td {
                color: #e2e8f0;
                border-bottom-color: #4a5568;
            }
            
            body.modo-noche .tabla-empleados tr:hover {
                background-color: #4a5568;
            }
            
            /* Modal base */
            .modal {
                display: none;
                position: fixed;
                z-index: 1000;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0,0,0,0.5);
                justify-content: center;
                align-items: center;
            }
            
            .modal-content {
                background-color: #fefefe;
                margin: auto;
                padding: 20px;
                border-radius: 8px;
                width: 50%;
                max-width: 600px;
                box-shadow: 0 4px 8px rgba(0,0,0,0.2);
                position: relative;
            }
            
            body.modo-noche .modal-content {
                background-color: #2d3748;
                color: #e2e8f0;
            }
            
            .close {
                color: #aaa;
                float: right;
                font-size: 28px;
                font-weight: bold;
                cursor: pointer;
                position: absolute;
                top: 10px;
                right: 20px;
            }
            
            .close:hover {
                color: black;
            }
            
            body.modo-noche .close:hover {
                color: white;
            }
            
            /* Formulario */
            .form-group {
                margin-bottom: 15px;
            }
            
            .form-group label {
                display: block;
                margin-bottom: 5px;
                font-weight: 500;
            }
            
            .form-group input, .form-group select {
                width: 100%;
                padding: 8px;
                border: 1px solid #ddd;
                border-radius: 4px;
                box-sizing: border-box;
            }
            
            body.modo-noche .form-group input, 
            body.modo-noche .form-group select {
                background-color: #4a5568;
                border-color: #4a5568;
                color: #e2e8f0;
            }
            
            .form-actions {
                text-align: right;
                margin-top: 20px;
            }
            
            .btn {
                padding: 8px 16px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-weight: 500;
            }
            
            .btn-primary {
                background-color: #2196F3;
                color: white;
            }
            
            .btn-secondary {
                background-color: #6c757d;
                color: white;
                margin-right: 10px;
            }

            /* Estilos específicos para el modal de confirmación/mensaje */
            .modal-message-content {
                text-align: center;
            }

            .modal-message-content h2 {
                margin-bottom: 15px;
            }

            .modal-message-content p {
                margin-bottom: 20px;
                font-size: 1.1em;
            }

            .modal-message-content .btn-group {
                display: flex;
                justify-content: center;
                gap: 15px;
            }

            /* Estilos para la barra de búsqueda */
            .search-bar {
                margin-bottom: 20px;
                display: flex;
                gap: 10px;
                align-items: center;
            }

            .search-bar form {
                display: flex;
                gap: 10px;
                width: 100%;
            }

            .search-bar input[type="text"] {
                flex-grow: 1;
                padding: 8px;
                border: 1px solid #ddd;
                border-radius: 4px;
            }

            body.modo-noche .search-bar input[type="text"] {
                background-color: #4a5568;
                border-color: #4a5568;
                color: #e2e8f0;
            }
        </style>
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
        
        <!-- Contenido principal -->
        <main>
            <div class="tab-container">
                <!-- Pestañas -->
                <div class="tab-header">
                    <button class="tab-button active" onclick="openTab(event, 'veterinarios')">Veterinarios</button>
                    <button class="tab-button" onclick="openTab(event, 'recepcionistas')">Recepcionistas</button>
                </div>
                
                <!-- Barra de búsqueda única para ambas pestañas -->
                <div class="search-bar">
                    <form id="searchForm" action="${pageContext.request.contextPath}/AdminEmpleadoServlet" method="GET">
                        <input type="hidden" name="accion" value="listar">
                        <input type="hidden" name="currentTab" id="currentTabHidden" value="<%= activeTab != null ? escapeHtmlAttribute.apply(activeTab) : "veterinarios" %>">
                        <input type="text" name="query" id="searchInput" placeholder="Buscar..." 
                               value="<%= searchQuery != null ? escapeHtmlAttribute.apply(searchQuery) : "" %>">
                        <button type="submit" class="btn btn-primary">Buscar</button>
                    </form>
                    <%-- Botón para generar reporte PDF --%>
                    <a href="${pageContext.request.contextPath}/ReporteEmpleadosServlet" class="btn-pdf">
                        <i class='bx bxs-file-pdf'></i> Generar Reporte PDF
                    </a>
                    <%-- Nuevo Botón para generar reporte Excel --%>
                    <a href="${pageContext.request.contextPath}/ReporteEmpleadosExcelServlet" class="btn-excel">
                        <i class='bx bxs-file-excel'></i> Generar Reporte Excel
                    </a>
                </div>

                <!-- Contenido de pestaña Veterinarios -->
                <div id="veterinarios" class="tab-content active">
                    <a href="#" class="btn-agregar" onclick="mostrarModalAgregar('veterinario')">
                        <i class='bx bx-plus'></i> Agregar Veterinario
                    </a>
                    <table class="tabla-empleados">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>DNI</th>
                                <th>Teléfono</th>
                                <th>Especialidad</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (listaVeterinarios != null && !listaVeterinarios.isEmpty()) {
                                for (Veterinario v : listaVeterinarios) { %>
                            <tr>
                                <td><%= v.getIdVeterinario() %></td>
                                <td><%= v.getNombre() %> <%= v.getApellido() %></td>
                                <td><%= v.getDni() %></td>
                                <td><%= v.getNumero() %></td>
                                <td><%= v.getEspecialidad() %></td>
                                <td class="acciones">
                                    <a href="#" class="btn-accion btn-editar" 
                                       onclick="mostrarModalEditar('veterinario', <%= v.getIdVeterinario() %>)">Editar</a>
                                    <button class="btn-accion btn-eliminar" 
                                            onclick="confirmarEliminar('veterinario', <%= v.getIdVeterinario() %>, '<%= escapeJsString.apply(v.getNombre() + " " + v.getApellido()) %>')">Eliminar</button>
                                </td>
                            </tr>
                            <% }
                            } else { %>
                            <tr>
                                <td colspan="6">No hay veterinarios registrados</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                
                <!-- Contenido de pestaña Recepcionistas -->
                <div id="recepcionistas" class="tab-content">
                    <a href="#" class="btn-agregar" onclick="mostrarModalAgregar('recepcionista')">
                        <i class='bx bx-plus'></i> Agregar Recepcionista
                    </a>
                    <table class="tabla-empleados">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nombre y Apellido</th>
                                <th>DNI</th>
                                <th>Teléfono</th>
                                <th>Correo</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (listaRecepcionistas != null && !listaRecepcionistas.isEmpty()) {
                                for (Recepcionista r : listaRecepcionistas) { %>
                            <tr>
                                <td><%= r.getIdRecepcionista() %></td>
                                <td><%= r.getNombre() %> <%= r.getApellido() %></td>
                                <td><%= r.getDni() %></td>
                                <td><%= r.getNumero() %></td>
                                <td><%= r.getCorreo() %></td>
                                <td class="acciones">
                                    <a href="#" class="btn-accion btn-editar" 
                                       onclick="mostrarModalEditar('recepcionista', <%= r.getIdRecepcionista() %>)">Editar</a>
                                    <button class="btn-accion btn-eliminar" 
                                            onclick="confirmarEliminar('recepcionista', <%= r.getIdRecepcionista() %>, '<%= escapeJsString.apply(r.getNombre() + " " + r.getApellido()) %>')">Eliminar</button>
                                </td>
                            </tr>
                            <% }
                            } else { %>
                            <tr>
                                <td colspan="6">No hay recepcionistas registrados</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </main>
        
        <!-- Modal para agregar/editar -->
        <div id="modalEmpleado" class="modal">
            <div class="modal-content">
                <span class="close" onclick="cerrarModal()">&times;</span>
                <h2 id="modalTitulo">Agregar Empleado</h2>
                <form id="formEmpleado" action="${pageContext.request.contextPath}/AdminEmpleadoServlet" method="POST">
                    <input type="hidden" id="tipoEmpleado" name="tipoEmpleado">
                    <input type="hidden" id="accion" name="accion" value="agregar">
                    <input type="hidden" id="idEmpleado" name="idEmpleado">
                    
                    <div class="form-group">
                        <label for="nombre">Nombre:</label>
                        <input type="text" id="nombre" name="nombre" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="apellido">Apellido:</label>
                        <input type="text" id="apellido" name="apellido" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="dni">DNI:</label>
                        <input type="text" id="dni" name="dni" required 
                               maxlength="10" 
                               pattern="[0-9]{8,10}" 
                               title="El DNI debe tener entre 8 y 10 dígitos numéricos">
                    </div>

                    <div class="form-group">
                        <label for="numero">Teléfono:</label>
                        <input type="text" id="numero" name="numero" required 
                               maxlength="9" 
                               pattern="[0-9]{9}" 
                               title="El teléfono debe tener 9 dígitos numéricos">
                    </div>
                    
                    <!-- Campos específicos para veterinario -->
                    <div id="especialidadGroup" class="form-group" style="display:none;">
                        <label for="especialidad">Especialidad:</label>
                        <input type="text" id="especialidad" name="especialidad">
                    </div>
                    
                    <!-- Campos específicos para recepcionista -->
                    <div id="recepcionistaFields" style="display:none;">
                        <div class="form-group">
                            <label for="correo">Correo:</label>
                            <input type="email" id="correo" name="correo">
                        </div>
                        <div class="form-group">
                            <label for="contrasena">Contraseña:</label>
                            <input type="password" id="contrasena" name="contrasena" placeholder="Dejar vacío para no cambiar">
                        </div>
                    </div>
                    
                    <div class="form-actions">
                        <button type="button" class="btn btn-secondary" onclick="cerrarModal()">Cancelar</button>
                        <button type="submit" class="btn btn-primary">Guardar</button>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Modal para ver detalles (se mantiene sin cambios significativos) -->
        <div id="modalVer" class="modal">
            <div class="modal-content">
                <span class="close" onclick="cerrarModalVer()">&times;</span>
                <h2 id="modalVerTitulo">Detalles del Empleado</h2>
                <div id="detallesEmpleado">
                    <!-- Los detalles se cargarán aquí dinámicamente -->
                </div>
                <div class="form-actions">
                    <button type="button" class="btn btn-secondary" onclick="cerrarModalVer()">Cerrar</button>
                </div>
            </div>
        </div>

        <!-- Nuevo Modal de Confirmación -->
        <div id="modalConfirmacion" class="modal">
            <div class="modal-content modal-message-content">
                <span class="close" onclick="cerrarModalConfirmacion()">&times;</span>
                <h2>Confirmar Eliminación</h2>
                <p id="confirmacionMensaje"></p>
                <div class="btn-group">
                    <button type="button" class="btn btn-secondary" onclick="cerrarModalConfirmacion()">Cancelar</button>
                    <button type="button" class="btn btn-eliminar" id="btnConfirmarEliminar">Eliminar</button>
                </div>
            </div>
        </div>

        <!-- Nuevo Modal de Mensajes (Éxito/Error) -->
        <div id="modalMensaje" class="modal">
            <div class="modal-content modal-message-content">
                <span class="close" onclick="cerrarModalMensaje()">&times;</span>
                <h2 id="mensajeTitulo"></h2>
                <p id="mensajeContenido"></p>
                <div class="btn-group">
                    <button type="button" class="btn btn-primary" onclick="cerrarModalMensaje()">Cerrar</button>
                </div>
            </div>
        </div>
        
        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>
        <script src="${pageContext.request.contextPath}/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
        
        <script>
            // Variables globales para la eliminación
            let empleadoAEliminarTipo = '';
            let empleadoAEliminarId = 0;

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

                // Actualizar el valor del hidden input y el placeholder de la barra de búsqueda
                const currentTabHidden = document.getElementById("currentTabHidden");
                const searchInput = document.getElementById("searchInput");
                currentTabHidden.value = tabName;
                if (tabName === 'veterinarios') {
                    searchInput.placeholder = "Buscar veterinario por nombre, DNI, o especialidad...";
                } else {
                    searchInput.placeholder = "Buscar recepcionista por nombre, DNI, o correo...";
                }
            }
            
            // Funciones para el modal de agregar/editar
            function mostrarModalAgregar(tipo) {
                const modal = document.getElementById("modalEmpleado");
                const titulo = document.getElementById("modalTitulo");
                const form = document.getElementById("formEmpleado");
                const tipoInput = document.getElementById("tipoEmpleado");
                const accionInput = document.getElementById("accion");
                const idInput = document.getElementById("idEmpleado");
                
                // Resetear formulario y campos específicos
                form.reset();
                idInput.value = ''; // Asegurarse de que el ID esté vacío para agregar
                document.getElementById("contrasena").placeholder = "Dejar vacío para no cambiar"; // Resetear placeholder
                
                // Configurar según el tipo de empleado
                tipoInput.value = tipo;
                accionInput.value = "agregar";
                
                if (tipo === "veterinario") {
                    titulo.textContent = "Agregar Veterinario";
                    document.getElementById("especialidadGroup").style.display = "block";
                    document.getElementById("recepcionistaFields").style.display = "none";
                    document.getElementById("especialidad").required = true;
                    document.getElementById("correo").required = false;
                    document.getElementById("contrasena").required = true;
                } else { // recepcionista
                    titulo.textContent = "Agregar Recepcionista";
                    document.getElementById("especialidadGroup").style.display = "none";
                    document.getElementById("recepcionistaFields").style.display = "block";
                    document.getElementById("especialidad").required = false;
                    document.getElementById("correo").required = true;
                    document.getElementById("contrasena").required = true;
                }
                
                modal.style.display = "flex";
            }
            
            function mostrarModalEditar(tipo, id) {
                const modal = document.getElementById("modalEmpleado");
                const titulo = document.getElementById("modalTitulo");
                const form = document.getElementById("formEmpleado");
                const tipoInput = document.getElementById("tipoEmpleado");
                const accionInput = document.getElementById("accion");
                const idInput = document.getElementById("idEmpleado");
                
                // Resetear formulario y campos específicos
                form.reset();
                document.getElementById("contrasena").placeholder = "Dejar vacío para no cambiar";
                document.getElementById("contrasena").required = false;
                
                // Configurar formulario para edición
                tipoInput.value = tipo;
                accionInput.value = "actualizar";
                idInput.value = id;
                
                // Hacer una petición AJAX para obtener los datos del empleado
                fetch('${pageContext.request.contextPath}/AdminEmpleadoServlet?accion=ver&tipo=' + tipo + '&id=' + id)
                    .then(response => {
                        if (!response.ok) {
                            // Si la respuesta no es OK (ej. 404, 500), intentar leer el mensaje de error
                            return response.json().then(errorData => { 
                                throw new Error(errorData.error || 'Error desconocido al obtener datos del empleado.'); 
                            });
                        }
                        return response.json();
                    })
                    .then(data => {
                        if (data) {
                            document.getElementById("nombre").value = data.nombre || '';
                            document.getElementById("apellido").value = data.apellido || '';
                            document.getElementById("dni").value = data.dni || '';
                            document.getElementById("numero").value = data.numero || '';

                            if (tipo === "veterinario") {
                                titulo.textContent = "Editar Veterinario";
                                document.getElementById("especialidadGroup").style.display = "block";
                                document.getElementById("recepcionistaFields").style.display = "none";
                                document.getElementById("especialidad").value = data.especialidad || '';
                                document.getElementById("especialidad").required = true;
                                document.getElementById("correo").required = false;
                            } else { // recepcionista
                                titulo.textContent = "Editar Recepcionista";
                                document.getElementById("especialidadGroup").style.display = "none";
                                document.getElementById("recepcionistaFields").style.display = "block";
                                document.getElementById("correo").value = data.correo || '';
                                document.getElementById("especialidad").required = false;
                                document.getElementById("correo").required = true;
                            }
                            modal.style.display = "flex"; // Mostrar el modal después de cargar los datos
                        } else {
                            mostrarMensaje("Error", "No se encontraron datos para el empleado con ID: " + id, "error");
                        }
                    })
                    .catch(error => {
                        console.error('Error al cargar datos del empleado:', error);
                        mostrarMensaje("Error", "Error al cargar datos del empleado: " + error.message, "error");
                    });
            }
            
            function cerrarModal() {
                document.getElementById("modalEmpleado").style.display = "none";
            }

            // Funciones para el modal de confirmación de eliminación
            function confirmarEliminar(tipo, id, nombreCompleto) {
                empleadoAEliminarTipo = tipo;
                empleadoAEliminarId = id;
                document.getElementById("confirmacionMensaje").textContent = "¿Estás seguro de que quieres eliminar a " + nombreCompleto + "? Esta acción no se puede deshacer.";
                document.getElementById("modalConfirmacion").style.display = "flex";
            }

            document.getElementById("btnConfirmarEliminar").onclick = function() {
                // Redirigir al servlet para eliminar
                window.location.href = '${pageContext.request.contextPath}/AdminEmpleadoServlet?accion=eliminar&tipo=' + empleadoAEliminarTipo + '&id=' + empleadoAEliminarId;
                cerrarModalConfirmacion();
            };

            function cerrarModalConfirmacion() {
                document.getElementById("modalConfirmacion").style.display = "none";
            }

            // Funciones para el modal de mensajes (éxito/error)
            function mostrarMensaje(titulo, contenido, tipo) {
                const modalMensaje = document.getElementById("modalMensaje");
                document.getElementById("mensajeTitulo").textContent = titulo;
                document.getElementById("mensajeContenido").textContent = contenido;
                // Puedes añadir clases para estilos diferentes según el tipo (éxito/error)
                // if (tipo === "error") { ... }
                modalMensaje.style.display = "flex";
            }

            function cerrarModalMensaje() {
                document.getElementById("modalMensaje").style.display = "none";
                // Recargar la página después de un mensaje de éxito/error para reflejar los cambios
                window.location.reload(); 
            }

            // Mostrar mensajes de éxito/error al cargar la página (desde el servlet)
            window.onload = function() {
                const activeTabFromServlet = "<%= activeTab != null ? escapeJsString.apply(activeTab) : "" %>";
                if (activeTabFromServlet) {
                    // Simular clic en la pestaña correcta para activarla
                    const tabButton = document.querySelector(`.tab-button[onclick*="'${activeTabFromServlet}'"]`);
                    if (tabButton) {
                        openTab({ currentTarget: tabButton }, activeTabFromServlet);
                    }
                } else {
                    // Si no hay pestaña activa, activar la primera por defecto
                    openTab({ currentTarget: document.querySelector('.tab-button.active') }, 'veterinarios');
                }

                const mensajeExito = "<%= mensajeExito != null ? escapeJsString.apply(mensajeExito) : "" %>";
                const mensajeError = "<%= mensajeError != null ? escapeJsString.apply(mensajeError) : "" %>";

                if (mensajeExito) {
                    mostrarMensaje("Éxito", mensajeExito, "success");
                } else if (mensajeError) {
                    mostrarMensaje("Error", mensajeError, "error");
                }
            };
        </script>
    </body>
</html>
