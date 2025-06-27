<%@page import="Modelo.Veterinario"%>
<%@page import="Modelo.Cliente"%>
<%@page import="Modelo.Cita"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Date" %>
<%@ page import="java.sql.Time" %>

<%
    List<Cita> listaCitas = (List<Cita>) request.getAttribute("listaCitas");
    Cita citaSel = (Cita) request.getAttribute("citaSeleccionada");
    String mensaje = (String) request.getAttribute("mensaje");
    String tipoMensaje = (String) request.getAttribute("tipoMensaje");

    List<Cliente> listaClientes = (List<Cliente>) request.getAttribute("listaClientes");
    List<Veterinario> listaVeterinarios = (List<Veterinario>) request.getAttribute("listaVeterinarios");

    if (listaCitas == null) listaCitas = new ArrayList<>();
    if (listaClientes == null) listaClientes = new ArrayList<>();
    if (listaVeterinarios == null) listaVeterinarios = new ArrayList<>();

    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
    SimpleDateFormat dateTimeDisplayFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Citas</title>
    <link href="https://cdn.jsdelivr.net/npm/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/GestorCitas.css">
    <style>
        .modal {
            display: none; /* Oculto por defecto */
            position: fixed; /* Fijo en la pantalla */
            z-index: 1; /* Por encima de otros elementos */
            left: 0;
            top: 0;
            width: 100%; /* Ancho completo */
            height: 100%; /* Alto completo */
            overflow: auto; /* Habilita el desplazamiento si es necesario */
            background-color: rgb(0,0,0); /* Color de fondo */
            background-color: rgba(0,0,0,0.4); /* Fondo negro con opacidad */
        }

        .modal-content {
            background-color: white;
            margin: 15% auto;
            padding: 20px;
            border: 1px solid #888;
            width: 80%;
            max-width: 600px;
            border-radius: 8px;
        }

        .modal-actions {
            display: flex;
            justify-content: space-between;
            margin-top: 20px;
        }

        .btn-eliminar {
            background-color: #f44336;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
        }

        .btn-eliminar:hover {
            background-color: #d32f2f;
        }

        .btn-cerrar {
            background-color: #ccc;
            color: black;
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
        }

        .btn-guardar {
            background-color: #4CAF50;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
        }

        .btn-guardar:hover {
            background-color: #388E3C;
        }
        
        /* Estilos para el estado de las citas */
        .estado-pendiente {
            background-color: #fff3cd;
            color: #856404;
            padding: 3px 8px;
            border-radius: 4px;
            font-weight: bold;
        }
        
        .estado-confirmada {
            background-color: #d4edda;
            color: #155724;
            padding: 3px 8px;
            border-radius: 4px;
            font-weight: bold;
        }
        
        .estado-cancelada {
            background-color: #f8d7da;
            color: #721c24;
            padding: 3px 8px;
            border-radius: 4px;
            font-weight: bold;
        }
        
        .estado-completada {
            background-color: #d1ecf1;
            color: #0c5460;
            padding: 3px 8px;
            border-radius: 4px;
            font-weight: bold;
        }
        
        /* Mejoras para la tabla */
        .tabla-citas th {
            background-color: #3aafa9;
            color: white;
        }
        
        .tabla-citas tr:nth-child(even) {
            background-color: #f2f2f2;
        }
        
        .tabla-citas tr:hover {
            background-color: #e9e9e9;
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
                <li class="nav-link"><a href="${pageContext.request.contextPath}/UsuarioCitaRecepServlet">
                        <i class='bx bx-calendar-alt icon'></i><span class="text">Citas de Usuarios</span></a></li>
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
            <h1>Gestión de Citas</h1>
            <div class="acciones">
                <form method="get" action="${pageContext.request.contextPath}/CitaServlet">
                    <input type="hidden" name="accion" value="buscar" />
                    <input type="text" name="busqueda" placeholder="Buscar citas..." 
                           value="${param.busqueda != null ? param.busqueda : ''}" />
                    <button type="submit" class="btn btn-editar">Buscar</button>
                    <a href="${pageContext.request.contextPath}/CitaServlet" class="btn btn-limpiar">Limpiar</a>
                </form>
                <button class="btn btn-agregar" onclick="abrirModal('modalAgregarCita')">Agregar Cita</button>
            </div>
        </div>
        
        <%-- Mensaje --%>
        <% if (mensaje != null) { %>
            <div class="alert <%= tipoMensaje != null ? tipoMensaje : "" %>"><%= mensaje %></div>
        <% } %>

        <div class="tabla-citas">
            <table class="tabla-citas th">
                <thead>
                    <tr>
                        <th>ID Cita</th>
                        <th>Cliente</th>
                        <th>Veterinario</th>
                        <th>Fecha</th>
                        <th>Hora</th>
                        <th>Motivo</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (!listaCitas.isEmpty()) {
                        for (Cita c : listaCitas) { 
                            String claseEstado = "";
                            switch(c.getEstado()) {
                                case "Pendiente": claseEstado = "estado-pendiente"; break;
                                case "Confirmada": claseEstado = "estado-confirmada"; break;
                                case "Cancelada": claseEstado = "estado-cancelada"; break;
                                case "Completada": claseEstado = "estado-completada"; break;
                            }
                    %>
                        <tr>
                            <td><%= c.getIdCita() %></td>
                            <td><%= c.getNombreCliente() %> <%= c.getApellidoCliente() %></td>
                            <td>Dr(a). <%= c.getNombreVeterinario() %> <%= c.getApellidoVeterinario() %></td>
                            <td><%= dateFormatter.format(c.getFecha()) %></td>
                            <td><%= timeFormatter.format(c.getHora()) %></td>
                            <td><%= c.getMotivo() %></td>
                            <td><span class="<%= claseEstado %>"><%= c.getEstado() %></span></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/CitaServlet?accion=editar&id=<%= c.getIdCita() %>" 
                                   class="btn btn-editar">Editar</a>
                                <a href="${pageContext.request.contextPath}/CitaServlet?accion=eliminar&id=<%= c.getIdCita() %>" 
                                   class="btn btn-eliminar" onclick="return confirm('¿Está seguro de eliminar esta cita?')">Eliminar</a>
                            </td>
                        </tr>
                    <% }} else { %>
                        <tr>
                            <td colspan="8" class="no-data">No hay citas registradas.</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>

        <%-- Modal Agregar Cita --%>
        <div id="modalAgregarCita" class="modal">
            <div class="modal-content">
                <span class="close" onclick="cerrarModal('modalAgregarCita')">&times;</span>
                <h2><%= citaSel == null ? "Agregar Nueva Cita" : "Editar Cita" %></h2>
                
                <form action="${pageContext.request.contextPath}/CitaServlet" method="post">
                    <input type="hidden" name="accion" value="<%= citaSel == null ? "guardar" : "actualizar" %>">
                    <% if (citaSel != null) { %>
                        <input type="hidden" name="idCita" value="<%= citaSel.getIdCita() %>">
                    <% } %>
                    
                    <div class="form-group">
                        <label for="idCliente">Cliente:</label>
                        <select name="idCliente" id="idCliente" required class="form-control">
                            <option value="">Seleccione un cliente</option>
                            <% for (Cliente cli : listaClientes) { %>
                                <option value="<%= cli.getIdCliente() %>" 
                                    <%= (citaSel != null && cli.getIdCliente() == citaSel.getIdCliente()) ? "selected" : "" %>>
                                    <%= cli.getNombre() %> <%= cli.getApellido() %> - <%= cli.getDni() %>
                                </option>
                            <% } %>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="idVeterinario">Veterinario:</label>
                        <select name="idVeterinario" id="idVeterinario" required class="form-control">
                            <option value="">Seleccione un veterinario</option>
                            <% for (Veterinario vet : listaVeterinarios) { %>
                                <option value="<%= vet.getIdVeterinario() %>"
                                    <%= (citaSel != null && vet.getIdVeterinario() == citaSel.getIdVeterinario()) ? "selected" : "" %>>
                                    Dr(a). <%= vet.getNombre() %> <%= vet.getApellido() %> - <%= vet.getEspecialidad() %>
                                </option>
                            <% } %>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="fecha">Fecha:</label>
                        <input type="date" name="fecha" id="fecha" 
                               value="<%= citaSel != null ? dateFormatter.format(citaSel.getFecha()) : "" %>" 
                               required class="form-control">
                    </div>
                    
                    <div class="form-group">
                        <label for="hora">Hora:</label>
                        <input type="time" name="hora" id="hora" 
                               value="<%= citaSel != null ? timeFormatter.format(citaSel.getHora()) : "" %>" 
                               required class="form-control">
                    </div>
                    
                    <div class="form-group">
                        <label for="motivo">Motivo:</label>
                        <select name="motivo" id="motivo" required class="form-control">
                            <option value="">Seleccione un motivo</option>
                            <option value="Consulta general" <%= citaSel != null && "Consulta general".equals(citaSel.getMotivo()) ? "selected" : "" %>>Consulta general</option>
                            <option value="Vacunación" <%= citaSel != null && "Vacunación".equals(citaSel.getMotivo()) ? "selected" : "" %>>Vacunación</option>
                            <option value="Cirugía" <%= citaSel != null && "Cirugía".equals(citaSel.getMotivo()) ? "selected" : "" %>>Cirugía</option>
                            <option value="Desparasitación" <%= citaSel != null && "Desparasitación".equals(citaSel.getMotivo()) ? "selected" : "" %>>Desparasitación</option>
                            <option value="Peluquería" <%= citaSel != null && "Peluquería".equals(citaSel.getMotivo()) ? "selected" : "" %>>Peluquería</option>
                            <option value="Chequeo" <%= citaSel != null && "Chequeo".equals(citaSel.getMotivo()) ? "selected" : "" %>>Chequeo</option>
                            <option value="Otro" <%= citaSel != null && "Otro".equals(citaSel.getMotivo()) ? "selected" : "" %>>Otro</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="estado">Estado:</label>
                        <select name="estado" id="estado" required class="form-control">
                            <option value="Pendiente" <%= citaSel != null && "Pendiente".equals(citaSel.getEstado()) ? "selected" : "" %>>Pendiente</option>
                            <option value="Confirmada" <%= citaSel != null && "Confirmada".equals(citaSel.getEstado()) ? "selected" : "" %>>Confirmada</option>
                            <option value="Cancelada" <%= citaSel != null && "Cancelada".equals(citaSel.getEstado()) ? "selected" : "" %>>Cancelada</option>
                            <option value="Completada" <%= citaSel != null && "Completada".equals(citaSel.getEstado()) ? "selected" : "" %>>Completada</option>
                        </select>
                    </div>
                    
                    <div class="modal-actions">
                        <% if (citaSel != null) { %>
                            <button type="button" class="btn-eliminar" onclick="confirmarEliminar(<%= citaSel.getIdCita() %>)">Eliminar</button>
                        <% } %>
                        <div>
                            <button type="button" class="btn-cerrar" onclick="cerrarModal('modalAgregarCita')">Cancelar</button>
                            <button type="submit" class="btn-guardar"><%= citaSel == null ? "Guardar" : "Actualizar" %></button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </main>
    <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>
    <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
    <script>
        // Mostrar modal si hay una cita seleccionada (para edición)
        <% if (citaSel != null) { %>
            document.addEventListener('DOMContentLoaded', function() {
                abrirModal('modalAgregarCita');
            });
        <% } %>
        
        function abrirModal(modalId) {
            const modal = document.getElementById(modalId);
            if (modal) {
                modal.style.display = 'block';
            }
        }

        function cerrarModal(modalId) {
            const modal = document.getElementById(modalId);
            if (modal) {
                modal.style.display = 'none';
                // Redirigir para limpiar parámetros si es necesario
                if (modalId === 'modalAgregarCita' && <%= citaSel != null %>) {
                    window.location.href = '${pageContext.request.contextPath}/CitaServlet';
                }
            }
        }

        function confirmarEliminar(idCita) {
            if (confirm('¿Está seguro que desea eliminar esta cita?')) {
                window.location.href = '${pageContext.request.contextPath}/CitaServlet?accion=eliminar&id=' + idCita;
            }
        }

        // Cierra el modal si se hace clic fuera de él
        window.onclick = function(event) {
            const modals = document.getElementsByClassName("modal");
            for (let modal of modals) {
                if (event.target == modal) {
                    modal.style.display = "none";
                    // Redirigir para limpiar parámetros si es necesario
                    if (modal.id === 'modalAgregarCita' && <%= citaSel != null %>) {
                        window.location.href = '${pageContext.request.contextPath}/CitaServlet';
                    }
                }
            }
        }
        
        // Validación de fecha (no permitir fechas pasadas)
        document.getElementById('fecha')?.addEventListener('change', function() {
            const fechaInput = this.value;
            const hoy = new Date().toISOString().split('T')[0];
            
            if (fechaInput < hoy) {
                alert('No se pueden agendar citas en fechas pasadas');
                this.value = hoy;
            }
        });
    </script>
</body>
</html>