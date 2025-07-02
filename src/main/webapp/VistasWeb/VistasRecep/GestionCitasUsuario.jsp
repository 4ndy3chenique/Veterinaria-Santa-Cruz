<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.UsuarioCitas"%>
<%@page import="Modelo.UsuarioCliente" %> <%-- Importar UsuarioCliente para obtener nombre si es necesario --%>


<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gesti�n de Citas de Usuarios - VeterinariaSantaCruz</title>
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
        /* Estilos para el formulario de edici�n */
        .edit-form-container {
            max-width: 600px;
            margin: 20px auto;
            padding: 30px;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            background-color: #fff;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .edit-form-container h3 {
            text-align: center;
            margin-bottom: 25px;
            color: #3aafa9;
            font-weight: 600;
        }
        .form-label {
            font-weight: 500;
        }
    </style>
</head>
<body>
    <%-- INICIO DEL C�DIGO DEL MEN� LATERAL (SIDEBAR) --%>
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
                    <li class="nav-link active"> <%-- Resaltado para la p�gina actual --%>
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
    <%-- FIN DEL C�DIGO DEL MEN� LATERAL (SIDEBAR) --%>

    <%-- CONTENIDO PRINCIPAL DE LA P�GINA DE GESTI�N DE CITAS DE USUARIO --%>
    <section class="home">
        <div class="text">Gesti�n de Citas de Usuarios</div>

        <%-- Mensaje de notificaci�n --%>
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
            // Mostrar la tabla de listado por defecto o si se especific� "listar" o b�squeda
            if ("listar".equals(modo) || modo == null || "buscar".equals(modo) || "buscarMedianteID".equals(modo)) {
        %>
            <div class="mb-3">
                <%-- Formulario de B�squeda por ID --%>
                <form class="d-flex mb-3" action="UsuarioCitaRecepServlet" method="GET" style="max-width: 300px;">
                    <input type="hidden" name="accion" value="buscarMedianteID">
                    <input class="form-control me-2" type="text" placeholder="Buscar por ID de Cita" name="id">
                    <button class="btn btn-outline-info" type="submit">Buscar por ID</button>
                </form>

                <%-- Formulario de B�squeda General --%>
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
                            <th>Motivo</th> <%-- Agregado --%>
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
                            <td><%= cita.getNombreCliente() %></td> <%-- Asume que UsuarioCitas tiene getNombreCliente() --%>
                            <td><%= cita.getFecha() %></td>
                            <td><%= cita.getHora() %></td>
                            <td><%= cita.getVeterinario() %></td>
                            <td><%= cita.getMotivo() %></td> <%-- Aseg�rate de que este dato venga de la DB --%>
                            <td><%= cita.getEstado() %></td>
                            <td>
                                <%-- Botones de Acciones --%>
                                <div class="d-grid gap-2">
                                    <a href="UsuarioCitaRecepServlet?accion=editar&id=<%= cita.getIdCita() %>" class="btn btn-warning btn-sm">Editar</a>
                                    <%-- El bot�n "Ver" puede ser opcional si el editar ya muestra toda la info en readonly --%>
                                    <%-- <a href="UsuarioCitaRecepServlet?accion=ver&id=<%= cita.getIdCita() %>" class="btn btn-info btn-sm">Ver</a> --%>
                                    <a href="UsuarioCitaRecepServlet?accion=eliminar&id=<%= cita.getIdCita() %>" class="btn btn-danger btn-sm" onclick="return confirm('�Est�s seguro de que quieres eliminar esta cita?');">Eliminar</a>
                                </div>
                                <div class="dropdown mt-2">
                                    <button class="btn btn-primary btn-sm dropdown-toggle" type="button" id="dropdownEstado<%= cita.getIdCita() %>" data-bs-toggle="dropdown" aria-expanded="false">
                                        Cambiar Estado
                                    </button>
                                    <ul class="dropdown-menu" aria-labelledby="dropdownEstado<%= cita.getIdCita() %>">
                                        <li><a class="dropdown-item" href="UsuarioCitaRecepServlet?accion=actualizarEstado&id=<%= cita.getIdCita() %>&estado=Pendiente">Pendiente</a></li>
                                        <li><a class="dropdown-item" href="UsuarioCitaRecepServlet?accion=actualizarEstado&id=<%= cita.getIdCita() %>&estado=Confirmada">Confirmada</a></li>
                                        <li><a class="dropdown-item" href="UsuarioCitaRecepServlet?accion=actualizarEstado&id=<%= cita.getIdCita() %>&estado=Completada">Completada</a></li>
                                        <li><a class="dropdown-item" href="UsuarioCitaRecepServlet?accion=actualizarEstado&id=<%= cita.getIdCita() %>&estado=Cancelada">Cancelada</a></li>
                                    </ul>
                                </div>
                            </td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="8">No hay citas para mostrar.</td> <%-- colspan ajustado a 8 --%>
                        </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
            </div>
        <%
            } else if ("editar".equals(modo) || "ver".equals(modo)) { // Modo de edici�n o visualizaci�n de una cita
                UsuarioCitas cita = (UsuarioCitas) request.getAttribute("cita");
                if (cita == null) {
        %>
                <div class="alert alert-danger" role="alert">
                    No se pudo cargar la informaci�n de la cita.
                </div>
                <a href="UsuarioCitaRecepServlet?accion=listarCitaUsuarios" class="btn btn-secondary">Volver a la Lista</a>
        <%     } else { %>
                <div class="edit-form-container">
                    <h3><%= "editar".equals(modo) ? "Editar Detalles de Cita" : "Ver Detalles de Cita" %></h3>
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
                            <input type="date" class="form-control" id="fecha" name="fecha" value="<%= cita.getFecha() %>" <%= "ver".equals(modo) ? "readonly" : "" %> required>
                        </div>
                        <div class="mb-3">
                            <label for="hora" class="form-label">Hora:</label>
                            <input type="time" class="form-control" id="hora" name="hora" value="<%= cita.getHora() %>" <%= "ver".equals(modo) ? "readonly" : "" %> required>
                        </div>
                        <div class="mb-3">
                            <label for="veterinario" class="form-label">Veterinario:</label>
                            <input type="text" class="form-control" id="veterinario" name="veterinario" value="<%= cita.getVeterinario() %>" <%= "ver".equals(modo) ? "readonly" : "" %> required>
                        </div>
                        <div class="mb-3">
                            <label for="motivo" class="form-label">Motivo:</label>
                            <textarea class="form-control" id="motivo" name="motivo" rows="3" <%= "ver".equals(modo) ? "readonly" : "" %> required><%= cita.getMotivo() %></textarea>
                        </div>
                        <div class="mb-3">
                            <label for="estado" class="form-label">Estado:</label>
                            <select class="form-select" id="estado" name="estado" <%= "ver".equals(modo) ? "disabled" : "" %> required>
                                <option value="Pendiente" <%= "Pendiente".equals(cita.getEstado()) ? "selected" : "" %>>Pendiente</option>
                                <option value="Confirmada" <%= "Confirmada".equals(cita.getEstado()) ? "selected" : "" %>>Confirmada</option>
                                <option value="Completada" <%= "Completada".equals(cita.getEstado()) ? "selected" : "" %>>Completada</option>
                                <option value="Cancelada" <%= "Cancelada".equals(cita.getEstado()) ? "selected" : "" %>>Cancelada</option>
                            </select>
                        </div>

                        <% if ("editar".equals(modo)) { %>
                        <button type="submit" class="btn btn-primary">Guardar Cambios</button>
                        <% } %>
                        <a href="UsuarioCitaRecepServlet?accion=listarCitaUsuarios" class="btn btn-secondary">Volver a la Lista</a>
                    </form>
                </div>
        <%     } // Fin de if (cita == null) para editar/ver
            } // Fin de if "editar" o "ver"
        %>
    </section>

    <%-- Scripts para el modo noche y sidebar --%>
    <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>