<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="Modelo.Cliente" %>
<%@ page import="Modelo.Veterinario" %>

<%
    List<Cliente> listaClientes = (List<Cliente>) request.getAttribute("listaClientes");
    Cliente clienteSel = (Cliente) request.getAttribute("clienteSeleccionado");
    List<Veterinario> listaVeterinarios = (List<Veterinario>) request.getAttribute("listaVeterinarios");

    String mensaje = (String) request.getAttribute("mensaje");
    String tipoMensaje = (String) request.getAttribute("tipoMensaje");

    if (listaClientes == null) {
        listaClientes = new java.util.ArrayList<>();
    }
    if (listaVeterinarios == null) {
        listaVeterinarios = new java.util.ArrayList<>();
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Gestión de Clientes</title>
        <link href="https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/GestorClientes.css">
        <style>
            /* General button styles for consistency */
            .btn {
                padding: 10px 15px; /* Provides the button shape */
                border: none;
                border-radius: 5px;
                cursor: pointer;
                font-size: 1rem;
                font-weight: 500;
                transition: background-color 0.3s ease, transform 0.2s ease, box-shadow 0.2s ease;
                display: inline-flex; /* Makes it behave like a block for styling but keeps it inline */
                align-items: center;
                gap: 5px;
                text-decoration: none; /* Remove underline for links acting as buttons */
            }

            /* Styles for "Eliminar" button (danger/red) */
            .btn-eliminar, .btn.btn-eliminar { /* This selector ensures styling even if only btn-eliminar is present */
                background-color: #f44336; /* Red */
                color: white; /* White text */
            }
            .btn-eliminar:hover, .btn.btn-eliminar:hover {
                background-color: #da190b;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
            }

            /* Styles for "Crear Cita" button (a distinct color, e.g., purple or orange) */
            .btn-cita, .btn.btn-cita {
                background-color: #9c27b0; /* Purple */
                color: white;
            }
            .btn-cita:hover, .btn.btn-cita:hover {
                background-color: #7b1fa2;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
            }

            /* Table specific styles for actions column - crucial for spacing */
            .tabla-clientes .acciones-td {
                display: flex; /* This makes the links arrange horizontally */
                gap: 8px; /* Adds space between the buttons */
                justify-content: center; /* Centers them in the column if there's extra space */
                align-items: center;
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
                    <a href="${pageContext.request.contextPath}/VistasWeb/VistasRecep/GestionProductosR.jsp">
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
                <h1>Gestión de Clientes</h1>
                <div class="acciones">
                    <form method="get" action="${pageContext.request.contextPath}/ClienteRServlet">
                        <input type="hidden" name="accion" value="listar" />
                        <input type="text" name="busqueda" placeholder="Buscar cliente..."
                               value="${param.busqueda != null ? param.busqueda : ''}" />
                        <button type="submit" class="btn btn-editar">Buscar</button>
                    </form>
                    <button class="btn btn-agregar" onclick="abrirModal('modalAgregarCliente')">Agregar Cliente</button>
                </div>
            </div>

            <% if (mensaje != null) {%>
            <div class="mensaje <%= tipoMensaje != null ? tipoMensaje : ""%>">
                <%= mensaje%>
            </div>
            <% } %>

            <table class="tabla-clientes">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Apellido</th>
                        <th>DNI</th>
                        <th>Teléfono</th>
                        <th>Fecha Registro</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (listaClientes != null && !listaClientes.isEmpty()) {
                        for (Cliente c : listaClientes) {%>
                    <tr>
                        <td><%= c.getIdCliente()%></td>
                        <td><%= c.getNombre()%></td>
                        <td><%= c.getApellido()%></td>
                        <td><%= c.getDni()%></td>
                        <td><%= c.getTelefono()%></td>
                        <td><%= c.getFechaRegistro() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(c.getFechaRegistro()) : "N/A"%></td>
                        <td class="acciones-td">
                            <a href="${pageContext.request.contextPath}/ClienteRServlet?accion=ver&id=<%= c.getIdCliente()%>"
                               class="btn btn-editar">Editar</a>
                            <%-- CHANGE THIS LINE: Add 'btn' class --%>
                            <a href="#" class="btn btn-eliminar" onclick="confirmarEliminacion(<%= c.getIdCliente()%>)">Eliminar</a>
                            <a href="#" class="btn btn-cita" onclick="abrirModalCrearCita(<%= c.getIdCliente()%>)">Crear Cita</a>
                        </td>
                    </tr>
                    <% }
                } else { %>
                    <tr><td colspan="7">No hay clientes registrados.</td></tr>
                    <% } %>
                </tbody>
            </table>

            <% if (clienteSel != null) {%>
            <div id="modalVerCliente" class="modal mostrar">
                <div class="modal-content animado">
                    <span class="cerrar" onclick="cerrarModal('modalVerCliente')">&times;</span>
                    <h2>Detalles del Cliente</h2>
                    <form action="${pageContext.request.contextPath}/ClienteRServlet" method="post" class="form-detalles" onsubmit="return validarFormularioCliente(this)">
                        <input type="hidden" name="accion" value="guardar">
                        <input type="hidden" name="id" value="<%= clienteSel.getIdCliente()%>">

                        <div class="form-grid">
                            <div class="form-group">
                                <label>Nombre:</label>
                                <input type="text" name="nombre" value="<%= clienteSel.getNombre()%>" required>
                            </div>
                            <div class="form-group">
                                <label>Apellido:</label>
                                <input type="text" name="apellido" value="<%= clienteSel.getApellido()%>" required>
                            </div>
                        </div>

                        <div class="form-grid">
                            <div class="form-group">
                                <label>DNI:</label>
                                <input type="text" name="dni" value="<%= clienteSel.getDni()%>"
                                       required maxlength="8" pattern="[0-9]{8}" title="8 dígitos">
                            </div>
                            <div class="form-group">
                                <label>Teléfono:</label>
                                <input type="text" name="telefono" value="<%= clienteSel.getTelefono()%>"
                                       required pattern="9[0-9]{8}" title="9 dígitos empezando con 9">
                            </div>
                        </div>

                        <div class="form-group">
                            <button type="submit" class="btn btn-editar">Guardar Cambios</button>
                            <button type="button" class="btn btn-eliminar" onclick="confirmarEliminacion(<%= clienteSel.getIdCliente()%>)">Eliminar Cliente</button>
                        </div>
                    </form>
                </div>
            </div>
            <% } %>

            <div id="modalAgregarCliente" class="modal">
                <div class="modal-content animado">
                    <span class="cerrar" onclick="cerrarModal('modalAgregarCliente')">&times;</span>
                    <h2>Agregar Nuevo Cliente</h2>
                    <form action="${pageContext.request.contextPath}/ClienteRServlet" method="post" onsubmit="return validarFormularioCliente(this)">
                        <input type="hidden" name="accion" value="guardar">

                        <div class="form-grid">
                            <div class="form-group">
                                <label>Nombre:</label>
                                <input type="text" name="nombre" required>
                            </div>
                            <div class="form-group">
                                <label>Apellido:</label>
                                <input type="text" name="apellido" required>
                            </div>
                        </div>

                        <div class="form-grid">
                            <div class="form-group">
                                <label>DNI:</label>
                                <input type="text" name="dni" required maxlength="8"
                                       pattern="[0-9]{8}" title="8 dígitos">
                            </div>
                            <div class="form-group">
                                <label>Teléfono:</label>
                                <input type="text" name="telefono" required
                                       pattern="9[0-9]{8}" title="9 dígitos empezando con 9">
                            </div>
                        </div>

                        <button type="submit" class="btn btn-agregar">Registrar</button>
                    </form>
                </div>
            </div>

            <div id="modalCrearCita" class="modal">
                <div class="modal-content animado">
                    <span class="cerrar" onclick="cerrarModal('modalCrearCita')">&times;</span>
                    <h2>Crear Cita</h2>
                    <form action="${pageContext.request.contextPath}/CitaServlet" method="post">
                        <input type="hidden" name="accion" value="crearCita">
                        <input type="hidden" name="idCliente" id="idClienteCita">

                        <div class="form-group">
                            <label for="idVeterinario">Veterinario:</label>
                            <select name="idVeterinario" id="idVeterinario" required>
                                <option value="">-- Seleccione un veterinario --</option>
                                <% for (Veterinario vet : listaVeterinarios) { %>
                                    <option value="<%= vet.getIdVeterinario() %>">
                                        Dr(a). <%= vet.getNombre() %> <%= vet.getApellido() %> - <%= vet.getEspecialidad() %>
                                    </option>
                                <% } %>
                            </select>

                        </div>

                        <div class="form-group">
                            <label>Fecha:</label>
                            <input type="date" name="fecha" required>
                        </div>
                        <div class="form-group">
                            <label>Hora:</label>
                            <input type="time" name="hora" required>
                        </div>
                        <div class="form-group">
                            <label>Motivo:</label>
                            <textarea name="motivo" rows="3"></textarea>
                        </div>

                        <button type="submit" class="btn btn-cita">Crear Cita</button>
                    </form>
                </div>
            </div>
        </main>
        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>
        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
        <script src="<%= request.getContextPath()%>/Js/JsRecepcionista/GestionClientes.js"></script>

        <script>
                        // Función para abrir modales
                        function abrirModal(id) {
                            document.getElementById(id).classList.add('mostrar');
                            document.body.style.overflow = 'hidden';
                        }

                        // Función para cerrar modales
                        function cerrarModal(id) {
                            document.getElementById(id).classList.remove('mostrar');
                            document.body.style.overflow = 'auto';
                            // If it's the edit modal, remove clienteSeleccionado from URL/state
                            if (id === 'modalVerCliente') {
                                // Remove any query params that might keep the modal open on refresh
                                const url = new URL(window.location.href);
                                url.searchParams.delete('accion');
                                url.searchParams.delete('id');
                                window.history.pushState({}, '', url);
                            }
                        }

                        // Función específica para abrir el modal de crear cita y pasar el ID del cliente
                        function abrirModalCrearCita(idCliente) {
                            document.getElementById('idClienteCita').value = idCliente; // Establece el valor del campo oculto
                            abrirModal('modalCrearCita'); // Abre el modal
                        }

                        // Función para confirmar eliminación
                        function confirmarEliminacion(id) {
                            if (confirm('¿Estás seguro de que deseas eliminar este cliente? Esta acción es irreversible.')) {
                                if (id) {
                                    window.location.href = "${pageContext.request.contextPath}/ClienteRServlet?accion=eliminar&id=" + id;
                                }
                            }
                            return false; // Prevent default action if confirmation fails
                        }

                        // Función para validar formularios de cliente
                        function validarFormularioCliente(form) {
                            const dni = form.querySelector('input[name="dni"]').value;
                            const telefono = form.querySelector('input[name="telefono"]').value;

                            if (!/^\d{8}$/.test(dni)) {
                                alert('El DNI debe tener exactamente 8 dígitos.');
                                return false;
                            }

                            if (!/^9\d{8}$/.test(telefono)) {
                                alert('El teléfono debe tener 9 dígitos y comenzar con 9.');
                                return false;
                            }

                            return true;
                        }

                        // Cerrar modal al hacer clic fuera del contenido
                        window.onclick = function (event) {
                            if (event.target.classList.contains('modal')) {
                                event.target.classList.remove('mostrar');
                                document.body.style.overflow = 'auto';
                                // If it's the edit modal, remove client ID from URL
                                if (event.target.id === 'modalVerCliente') {
                                    const url = new URL(window.location.href);
                                    url.searchParams.delete('accion');
                                    url.searchParams.delete('id');
                                    window.history.pushState({}, '', url);
                                }
                            }
                        }

                        // Mostrar modal de edición si hay un cliente seleccionado
            <% if (clienteSel != null) { %>
                        document.addEventListener('DOMContentLoaded', function () {
                            abrirModal('modalVerCliente');
                        });
            <% }%>
        </script>
    </body>
</html>