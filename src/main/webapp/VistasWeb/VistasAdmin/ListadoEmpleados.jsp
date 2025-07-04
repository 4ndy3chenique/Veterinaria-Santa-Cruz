<%@ include file="/proteger.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="java.util.List"%>
<%@page import="Modelo.Veterinario"%>
<%@page import="Modelo.Recepcionista"%>
<%
    List<Veterinario> listaVeterinarios = (List<Veterinario>) request.getAttribute("listaVeterinarios");
    List<Recepcionista> listaRecepcionistas = (List<Recepcionista>) request.getAttribute("listaRecepcionistas");
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
            
            /* Modal */
            .modal {
                display: none;
                position: fixed;
                z-index: 1000;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0,0,0,0.5);
            }
            
            .modal-content {
                background-color: #fefefe;
                margin: 5% auto;
                padding: 20px;
                border-radius: 8px;
                width: 50%;
                max-width: 600px;
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
                            <input type="password" id="contrasena" name="contrasena">
                        </div>
                    </div>
                    
                    <div class="form-actions">
                        <button type="button" class="btn btn-secondary" onclick="cerrarModal()">Cancelar</button>
                        <button type="submit" class="btn btn-primary">Guardar</button>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Modal para ver detalles -->
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
            
            // Funciones para el modal
            function mostrarModalAgregar(tipo) {
                const modal = document.getElementById("modalEmpleado");
                const titulo = document.getElementById("modalTitulo");
                const form = document.getElementById("formEmpleado");
                const tipoInput = document.getElementById("tipoEmpleado");
                const accionInput = document.getElementById("accion");
                
                // Resetear formulario
                form.reset();
                
                // Configurar según el tipo de empleado
                tipoInput.value = tipo;
                accionInput.value = "agregar";
                
                if (tipo === "veterinario") {
                    titulo.textContent = "Agregar Veterinario";
                    document.getElementById("especialidadGroup").style.display = "block";
                    document.getElementById("recepcionistaFields").style.display = "none";
                } else {
                    titulo.textContent = "Agregar Recepcionista";
                    document.getElementById("especialidadGroup").style.display = "none";
                    document.getElementById("recepcionistaFields").style.display = "block";
                }
                
                modal.style.display = "block";
            }
            
            function mostrarModalEditar(tipo, id) {
                // Aquí deberías hacer una petición AJAX para obtener los datos del empleado
                // y luego llenar el formulario con esos datos
                console.log("Editar " + tipo + " con ID: " + id);
                
                const modal = document.getElementById("modalEmpleado");
                const titulo = document.getElementById("modalTitulo");
                const form = document.getElementById("formEmpleado");
                const tipoInput = document.getElementById("tipoEmpleado");
                const accionInput = document.getElementById("accion");
                const idInput = document.getElementById("idEmpleado");
                
                // Configurar formulario para edición
                tipoInput.value = tipo;
                accionInput.value = "editar";
                idInput.value = id;
                
                if (tipo === "veterinario") {
                    titulo.textContent = "Editar Veterinario";
                    document.getElementById("especialidadGroup").style.display = "block";
                    document.getElementById("recepcionistaFields").style.display = "none";
                    
                    // Simulamos datos (en producción harías una petición AJAX)
                    // Ejemplo con datos hardcodeados - reemplazar con llamada real
                    document.getElementById("nombre");
                    document.getElementById("apellido") ;
                    document.getElementById("dni");
                    document.getElementById("numero");
                    document.getElementById("especialidad");
                } else {
                    titulo.textContent = "Editar Recepcionista";
                    document.getElementById("especialidadGroup").style.display = "none";
                    document.getElementById("recepcionistaFields").style.display = "block";
                    
                    // Simulamos datos (en producción harías una petición AJAX)
                    document.getElementById("nombre").value = "";
                    document.getElementById("apellido").value = "";
                    document.getElementById("dni").value = "";
                    document.getElementById("numero").value = "";
                    document.getElementById("correo").value = "";
                    document.getElementById("contrasena").value = "";
                }
                
                modal.style.display = "block";
            }
            
         
            function cerrarModal() {
                document.getElementById("modalEmpleado").style.display = "none";
            }
            
           
            window.onclick = function(event) {
                const modal = document.getElementById("modalEmpleado");
                const modalVer = document.getElementById("modalVer");
                
                if (event.target === modal) {
                    modal.style.display = "none";
                }
                
                if (event.target === modalVer) {
                    modalVer.style.display = "none";
                }
            }
        </script>
    </body>
</html>