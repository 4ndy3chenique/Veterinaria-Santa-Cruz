<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/proteger.jsp" %>
<%@page import="Modelo.HistorialVenta"%>
<%@page import="java.util.List"%>

<%    List<HistorialVenta> ventas = (List<HistorialVenta>) request.getAttribute("ventas");
    String tipoSeleccionado = (String) request.getAttribute("tipoSeleccionado");
    if (tipoSeleccionado == null)
        tipoSeleccionado = "todos";
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Historial de Ventas - Veterinaria Santa Cruz</title>
        <link href="https://cdn.jsdelivr.net/npm/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/HistorialVentas.css">
    </head>

    <body>
        <nav class="sidebar">
            <header>
                <div class="image-text">
                    <span class="image">
                        <img src="<%= request.getContextPath()%>/Recursos/Logo.png" alt="Logo" class="logo">
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
                        <a href="${pageContext.request.contextPath}/ProductoRecepServlet">
                            <i class='bx bx-package icon'></i><span class="text">Productos</span></a>
                    </li>

                    <li class="nav-link">
                        <a href="${pageContext.request.contextPath}/HistorialVentaServlet" class="active">
                            <i class='bx bx-receipt icon'></i><span class="text">Historial de Ventas</span></a>
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
            <div class="header">
                <h1>Historial de Ventas</h1>
                <form method="get" action="${pageContext.request.contextPath}/HistorialVentaServlet">
                    <select name="tipo">
                        <option value="todos" <%= "todos".equalsIgnoreCase(tipoSeleccionado) ? "selected" : ""%>>Todos</option>
                        <option value="Recepcionista" <%= "Recepcionista".equalsIgnoreCase(tipoSeleccionado) ? "selected" : ""%>>Recepcionista</option>
                        <option value="Cliente" <%= "Cliente".equalsIgnoreCase(tipoSeleccionado) ? "selected" : ""%>>Cliente</option>
                    </select>
                    <button type="submit">Filtrar</button>
                </form>
            </div>

            <table class="tabla-ventas">
                <thead>
                    <tr>
                        <th>ID Venta</th>
                        <th>Usuario</th>
                        <th>Producto</th>
                        <th>Cantidad</th>
                        <th>Total</th>
                        <th>Estado</th>
                        <th>Fecha Venta</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if (ventas != null && !ventas.isEmpty()) {
                            for (HistorialVenta v : ventas) {
                    %>
                    <tr>
                        <td><%= v.getIdVenta()%></td>
                        <td><%= v.getNombreUsuario() != null ? v.getNombreUsuario() : "-"%></td>
                        <td><%= v.getNombreProducto() != null ? v.getNombreProducto() : "-"%></td>
                        <td><%= v.getCantidad()%></td>
                        <td>S/. <%= String.format("%.2f", v.getTotal())%></td>
                        <td><%= v.getEstado()%></td>
                        <td><%= v.getFechaVenta()%></td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr>
                        <td colspan="7">No hay ventas registradas para este criterio.</td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </main>

        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>

        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
    </body>
</html>
