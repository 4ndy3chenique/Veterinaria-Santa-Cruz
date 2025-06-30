<%@page import="java.util.List"%>
<%@page import="Modelo.UsuarioCitas"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Citas de Usuarios - VeterinariaSantaCruz</title>
    <link href="https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath()%>/css/ModoNoche-Sidebar.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            padding-left: 78px; /* Ancho por defecto del sidebar minimizado */
            transition: all 0.2s ease;
        }
        .sidebar.close ~ body {
             padding-left: 250px; /* Ancho del sidebar expandido */
        }
        .home {
            position: relative;
            left: 78px;
            width: calc(100% - 78px);
            transition: all 0.2s ease;
            padding: 20px; /* Espaciado dentro del contenido principal */
        }
        .sidebar.close ~ .home {
            left: 250px;
            width: calc(100% - 250px);
        }
        .table-container {
            margin-top: 20px;
        }
        /* Estilos adicionales si deseas ocultar o mostrar secciones */
        .hidden {
            display: none;
        }
    </style>
</head>
<body>
    <%-- INICIO DEL CÓDIGO DEL MENÚ LATERAL (SIDEBAR) --%>
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
             <i class='bx bx-chevron-right toggle'></i>
        </header>

        <div class="menu-bar">
            <div class="menu">
                 <ul class="menu-links">
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/VistasWeb/VistasRecep/RecepDash.jsp">
                            <i class='bx bx-home-alt icon'></i><span class="text nav-text">General</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/ClienteRServlet">
                            <i class='bx bx-group icon'></i><span class="text nav-text">Clientes</span></a>
                    </li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/CitaServlet"><i class='bx bxs-calendar icon'></i><span class="text nav-text">Citas</span></a>
                    </li>
                    <li class="nav-link active"> <%-- Resaltado para la página actual --%>
                        <a href="<%= request.getContextPath()%>/UsuarioCitaRecepServlet?accion=listarCitaUsuarios">
                            <i class='bx bx-calendar-alt icon'></i><span class="text nav-text">Citas de Usuarios</span></a>
                    </li>
                     <li class="nav-link">
                        <a href="#">
                            <i class='bx bx-wallet icon'></i>
                            <span class="text nav-text">Pagos</span>
                        </a>
                    </li>
                </ul>
            </div>

            <div class="bottom-content">
                <li class="">
                    <a href="<%= request.getContextPath()%>/LogoutServlet">
                        <i class='bx bx-log-out icon'></i>
                        <span class="text nav-text">Salir</span>
                    </a>
                </li>
                <li class="mode">
                    <div class="sun-moon">
                        <i class='bx bx-moon icon moon'></i>
                        <i class='bx bx-sun icon sun'></i>
                    </div>
                    <span class="mode-text text">Modo Oscuro</span>

                    <div class="toggle-switch">
                        <span class="switch"></span>
                    </div>
                </li>
            </div>
        </div>
    </nav>
    <%-- FIN DEL CÓDIGO DEL MENÚ LATERAL (SIDEBAR) --%>

    <%-- CONTENIDO PRINCIPAL DE LA PÁGINA DE GESTIÓN DE CITAS DE USUARIO --%>
    <section class="home">
        <div class="text">Gestión de Citas de Usuarios</div>

        <%-- Mensaje de notificación --%>
        <% if (request.getAttribute("mensaje") != null) { %>
            <div class="alert alert-info" role="alert">
                <%= request.getAttribute("mensaje") %>
            </div>
        <% } %>
        <% if (request.getAttribute("mensajeBusqueda") != null) { %>
            <div class="alert alert-secondary" role="alert">
                <%= request.getAttribute("mensajeBusqueda") %>
            </div>
        <% } %>

        <% 
            String modo = (String) request.getAttribute("modo");
            if ("listar".equals(modo) || modo == null) { // Mostrar la tabla de listado por defecto
        %>
            <div class="mb-3">
                <%-- Formulario de Búsqueda por ID --%>
                <form class="d-flex mb-3" action="UsuarioCitaRecepServlet" method="GET" style="max-width: 300px;">
                    <input type="hidden" name="accion" value="buscarMedianteID">
                    <input class="form-control me-2" type="text" placeholder="Buscar por ID de Cita" name="id">
                    <button class="btn btn-outline-info" type="submit">Buscar por ID</button>
                </form>

                <%-- Formulario de Búsqueda General --%>
                <form class="d-flex mb-3" action="UsuarioCitaRecepServlet" method="GET">
                    <input type="hidden" name="accion" value="buscar">
                    <input class="form-control me-2" type="search" placeholder="Buscar por cliente, veterinario o estado" aria-label="Search" name="terminoBusqueda">
                    <button class="btn btn-outline-primary" type="submit">Buscar</button>
                </form>
                
                <a href="UsuarioCitaRecepServlet?accion=listarCitaUsuarios" class="btn btn-secondary mb-3">Mostrar Todas las Citas</a>
            </div>

            <div class="table-container">
                <table class="table table-bordered table-hover">
                    <thead class="table-dark">
                        <tr>
                            <th>ID Cita</th>
                            <th>Cliente</th>
                            <th>Fecha</th>
                            <th>Hora</th>
                            <th>Veterinario</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<UsuarioCitas> citas = (List<UsuarioCitas>) request.getAttribute("citas");
                            if (citas != null && !citas.isEmpty()) {
                                for (UsuarioCitas cita : citas) {
                        %>
                        <tr>
                            <td><%= cita.getIdCita() %></td>
                            <td><%= cita.getNombreCliente() %></td>
                            <td><%= cita.getFecha() %></td>
                            <td><%= cita.getHora() %></td>
                            <td><%= cita.getVeterinario() %></td>
                            <td><%= cita.getEstado() %></td>
                            <td>
                                <a href="UsuarioCitaRecepServlet?accion=EditarCitaUsuario&id=<%= cita.getIdCita() %>" class="btn btn-warning btn-sm">Editar</a>
                                <a href="UsuarioCitaRecepServlet?accion=EliminarCitaUsuario&id=<%= cita.getIdCita() %>" class="btn btn-danger btn-sm" onclick="return confirm('¿Estás seguro de que quieres eliminar esta cita?');">Eliminar</a>
                            </td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="7">No hay citas para mostrar.</td>
                        </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
            </div>
        <% 
            } else if ("editar".equals(modo)) {
                UsuarioCitas cita = (UsuarioCitas) request.getAttribute("cita");
                if (cita == null) { 
        %>
                <div class="alert alert-danger" role="alert">
                    No se pudo cargar la información de la cita para editar.
                </div>
                <a href="UsuarioCitaRecepServlet?accion=listarCitaUsuarios" class="btn btn-secondary">Volver a la Lista</a>
        <%      } else { %>
                <div class="edit-form-container">
                    <h3>Editar Detalles de Cita</h3>
                    <form action="UsuarioCitaRecepServlet" method="POST">
                        <input type="hidden" name="accion" value="guardarEdicion">
                        <input type="hidden" name="idCita" value="<%= cita.getIdCita() %>">

                        <div class="mb-3">
                            <label for="idCitaDisplay" class="form-label">ID Cita:</label>
                            <input type="text" class="form-control" id="idCitaDisplay" value="<%= cita.getIdCita() %>" readonly>
                        </div>
                        <div class="mb-3">
                            <label for="nombreCliente" class="form-label">Cliente:</label>
                            <input type="text" class="form-control" id="nombreCliente" value="<%= cita.getNombreCliente() %>" readonly>
                        </div>
                        <div class="mb-3">
                            <label for="fecha" class="form-label">Fecha:</label>
                            <input type="date" class="form-control" id="fecha" name="fecha" value="<%= cita.getFecha() %>" required>
                        </div>
                        <div class="mb-3">
                            <label for="hora" class="form-label">Hora:</label>
                            <input type="time" class="form-control" id="hora" name="hora" value="<%= cita.getHora() %>" required>
                        </div>
                        <div class="mb-3">
                            <label for="veterinario" class="form-label">Veterinario:</label>
                            <input type="text" class="form-control" id="veterinario" value="<%= cita.getVeterinario() %>" readonly>
                            </div>
                        <div class="mb-3">
                            <label for="estado" class="form-label">Estado:</label>
                            <select class="form-select" id="estado" name="estado" required>
                                <option value="Pendiente" <%= "Pendiente".equals(cita.getEstado()) ? "selected" : "" %>>Pendiente</option>
                                <option value="Confirmada" <%= "Confirmada".equals(cita.getEstado()) ? "selected" : "" %>>Confirmada</option>
                                <option value="Completada" <%= "Completada".equals(cita.getEstado()) ? "selected" : "" %>>Completada</option>
                                <option value="Cancelada" <%= "Cancelada".equals(cita.getEstado()) ? "selected" : "" %>>Cancelada</option>
                            </select>
                        </div>

                        <button type="submit" class="btn btn-primary">Guardar Cambios</button>
                        <a href="UsuarioCitaRecepServlet?accion=listarCitaUsuarios" class="btn btn-secondary">Cancelar y Volver</a>
                    </form>
                </div>
        <%      } // Fin de if (cita == null) para editar
            } // Fin de if "editar"
        %>
    </section>

    <%-- Scripts para el modo noche y sidebar --%>
    <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>