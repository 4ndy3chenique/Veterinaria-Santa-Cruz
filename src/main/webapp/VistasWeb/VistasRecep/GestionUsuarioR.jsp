<%@ include file="/proteger.jsp" %>
<%@page import="Modelo.Veterinario"%>
<%@page import="Modelo.Cliente"%>
<%@page import="Modelo.Cita"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Date" %>
<%@ page import="java.sql.Time" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Citas</title>
    <link href="https://cdn.jsdelivr.net/npm/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/GestorCitas.css">
</head>
<%
    List<Cita> listaCitas = (List<Cita>) request.getAttribute("listaCitas");
    List<Cliente> listaClientes = (List<Cliente>) request.getAttribute("listaClientes");
    List<Veterinario> listaVeterinarios = (List<Veterinario>) request.getAttribute("listaVeterinarios");
    Cita citaSel = (Cita) request.getAttribute("citaSeleccionada");
    String mensaje = (String) request.getAttribute("mensaje");
    String tipoMensaje = (String) request.getAttribute("tipoMensaje");

    if (listaCitas == null) listaCitas = new ArrayList<>();
    if (listaClientes == null) listaClientes = new ArrayList<>();
    if (listaVeterinarios == null) listaVeterinarios = new ArrayList<>();

    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
    SimpleDateFormat dateTimeDisplayFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>

<body>
    <nav class="sidebar">
        <header>
            <div class="image-text">
                <span class="image">
                    <img id="logoAdmin" src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo de Veterinaria Santa Cruz" class="logo">
                </span>
                <div class="header-text">
                    <span class="name">Recepcionista</span>
                    <span class="profession">Veterinaria Santa Cruz</span>
                </div>
            </div>
        </header>

        <div class="menu-bar">
            <ul class="menu-links">
                <li class="nav-link"><a href="${pageContext.request.contextPath}/VistasWeb/VistasRecep/RecepDash.jsp">
                        <i class='bx bx-home-alt icon'></i><span class="text">General</span></a></li>
                <li class="nav-link"><a href="${pageContext.request.contextPath}/ClienteRServlet">
                        <i class='bx bx-group icon'></i><span class="text">Clientes</span></a></li>
                <li class="nav-link"><a href="${pageContext.request.contextPath}/CitaServlet">
                        <i class='bx bxs-calendar icon'></i><span class="text">Citas</span></a></li>
                <li class="nav-link"><a href="${pageContext.request.contextPath}/UsuarioCitaRecepServlet">
                        <i class='bx bx-calendar-alt icon'></i><span class="text">Citas de Usuarios</span></a></li>
                                    <li class="nav-link"><a href="${pageContext.request.contextPath}/LogoutServlet"><i class='bx bx-log-out icon'>
                    
                                </i><span class="text">Salir</span></a></li>
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
                        <th>Fecha y Hora</th>
                        <th>Motivo</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (!listaCitas.isEmpty()) {
                        for (Cita c : listaCitas) { %>
                        <tr>
                            <td><%= c.getIdCita() %></td>
                            <td><%= c.getNombreCliente() %> <%= c.getApellidoCliente() %></td>
                            <td>Dr(a). <%= c.getNombreVeterinario() %> <%= c.getApellidoVeterinario() %> (<%= c.getEspecialidadVeterinario() %>)</td>
                            <td><%= dateTimeDisplayFormatter.format(c.getFecha()) %> <%= timeFormatter.format(c.getHora()) %></td>
                            <td><%= c.getMotivo() %></td>
                            <td><%= c.getEstado() %></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/CitaServlet?accion=ver&id=<%= c.getIdCita() %>" 
                                   class="btn btn-editar">Editar</a>
                            </td>
                        </tr>
                    <% }} else { %>
                        <tr>
                            <td colspan="7" class="no-data">No hay citas registradas.</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>

        <%-- Modal Agregar Cita --%>
        <div id="modalAgregarCita" class="modal">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/CitaServlet" method="post">
                    <input type="hidden" name="accion" value="guardar">
                    <input type="hidden" name="idCita" value="<%= citaSel != null ? citaSel.getIdCita() : "" %>">
                    
                    <label for="idCliente">Cliente:</label>
                    <select name="idCliente" id="idCliente" required>
                        <% for (Cliente cli : listaClientes) { %>
                            <option value="<%= cli.getIdCliente() %>"><%= cli.getNombre() %> <%= cli.getApellido() %></option>
                        <% } %>
                    </select>

                    <label for="idVeterinario">Veterinario:</label>
                    <select name="idVeterinario" id="idVeterinario" required>
                        <% for (Veterinario vet : listaVeterinarios) { %>
                            <option value="<%= vet.getIdVeterinario() %>">
                                Dr(a). <%= vet.getNombre() %> <%= vet.getApellido() %> - <%= vet.getEspecialidad() %>
                            </option>
                        <% } %>
                    </select>

                    <label for="fecha">Fecha:</label>
                    <input type="date" name="fecha" id="fecha" value="<%= citaSel != null ? dateFormatter.format(citaSel.getFecha()) : "" %>" required>
                    
                    <label for="hora">Hora:</label>
                    <input type="time" name="hora" id="hora" value="<%= citaSel != null ? timeFormatter.format(citaSel.getHora()) : "" %>" required>
                    
                    <label for="motivo">Motivo:</label>
                    <textarea name="motivo" id="motivo" required><%= citaSel != null ? citaSel.getMotivo() : "" %></textarea>
                    
                    <label for="estado">Estado:</label>
                    <select name="estado" id="estado" required>
                        <option value="Pendiente" <%= citaSel != null && "Pendiente".equals(citaSel.getEstado()) ? "selected" : "" %>>Pendiente</option>
                        <option value="Confirmada" <%= citaSel != null && "Confirmada".equals(citaSel.getEstado()) ? "selected" : "" %>>Confirmada</option>
                        <option value="Cancelada" <%= citaSel != null && "Cancelada".equals(citaSel.getEstado()) ? "selected" : "" %>>Cancelada</option>
                        <option value="Completada" <%= citaSel != null && "Completada".equals(citaSel.getEstado()) ? "selected" : "" %>>Completada</option>
                    </select>
                    
                    <div class="modal-actions">
                        <button type="button" class="btn-cerrar" onclick="cerrarModal('modalAgregarCita')">Cerrar</button>
                        <button type="submit" class="btn-guardar">Guardar Cita</button>
                    </div>
                </form>
            </div>
        </div>

        <%-- Modal Editar/Eliminar Cita --%>
        <% if (citaSel != null) { %>
        <div id="modalVerCita" class="modal mostrar">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/CitaServlet" method="post">
                    <input type="hidden" name="accion" value="guardar">
                    <input type="hidden" name="idCita" value="<%= citaSel.getIdCita() %>">

                    <label for="editIdCliente">Cliente:</label>
                    <select name="idCliente" id="editIdCliente" required>
                        <% for (Cliente cli : listaClientes) { %>
                            <option value="<%= cli.getIdCliente() %>"
                                <%= (cli.getIdCliente() == citaSel.getIdCliente()) ? "selected" : "" %>>
                                <%= cli.getNombre() %> <%= cli.getApellido() %>
                            </option>
                        <% } %>
                    </select>

                    <label for="editIdVeterinario">Veterinario:</label>
                    <select name="idVeterinario" id="editIdVeterinario" required>
                        <% for (Veterinario vet : listaVeterinarios) { %>
                            <option value="<%= vet.getIdVeterinario() %>"
                                <%= (vet.getIdVeterinario() == citaSel.getIdVeterinario()) ? "selected" : "" %>>
                                Dr(a). <%= vet.getNombre() %> <%= vet.getApellido() %> - <%= vet.getEspecialidad() %>
                            </option>
                        <% } %>
                    </select>

                    <label for="editFecha">Fecha:</label>
                    <input type="date" name="fecha" id="editFecha" value="<%= dateFormatter.format(citaSel.getFecha()) %>" required>
                    
                    <label for="editHora">Hora:</label>
                    <input type="time" name="hora" id="editHora" value="<%= timeFormatter.format(citaSel.getHora()) %>" required>
                    
                    <label for="editMotivo">Motivo:</label>
                    <textarea name="motivo" id="editMotivo" required><%= citaSel.getMotivo() %></textarea>
                    
                    <label for="editEstado">Estado:</label>
                    <select name="estado" id="editEstado" required>
                        <option <%= "Pendiente".equals(citaSel.getEstado()) ? "selected" : "" %>>Pendiente</option>
                        <option <%= "Confirmada".equals(citaSel.getEstado()) ? "selected" : "" %>>Confirmada</option>
                        <option <%= "Cancelada".equals(citaSel.getEstado()) ? "selected" : "" %>>Cancelada</option>
                        <option <%= "Completada".equals(citaSel.getEstado()) ? "selected" : "" %>>Completada</option>
                    </select>
                    
                    <div class="modal-actions">
                        <button type="button" class="btn-eliminar" onclick="confirmarEliminar(<%= citaSel.getIdCita() %>)">Eliminar</button>
                        <div>
                            <button type="button" class="btn-cerrar" onclick="cerrarModal('modalVerCita')">Cerrar</button>
                            <button type="submit" class="btn-guardar">Guardar Cambios</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        <% } %>
    </main>

    <button id="modoNocheBtn" class="modo-noche-flotante">🌙</button>

    <script src="${pageContext.request.contextPath}/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
    <script>
        function confirmarEliminar(id) {
            if (confirm('¿Está seguro de eliminar esta cita?')) {
                window.location.href = '${pageContext.request.contextPath}/UsuarioCitaRecepServlet?accion=eliminar&id=' + id;
            }
        }
        window.onclick = e => {
            document.querySelectorAll(".modal").forEach(m => {
                if (e.target === m) m.style.display = 'none';
            });
        };
    </script>
</body>
</html>