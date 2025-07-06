<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/proteger.jsp" %>
<%@page import="Modelo.Producto"%>
<%@page import="Modelo.CarritoItem"%>
<%@page import="java.util.List"%>

<%    String mensaje = (String) session.getAttribute("mensaje");
    session.removeAttribute("mensaje");

    Boolean abrirModal = (Boolean) session.getAttribute("abrirModal");
    session.removeAttribute("abrirModal");

    List<Producto> listaProductos = (List<Producto>) request.getAttribute("productos");

    int carritoCount = 0;
    List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
    if (carrito != null)
        carritoCount = carrito.size();
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Veterinaria Santa Cruz</title>
        <link href="https://cdn.jsdelivr.net/npm/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ProductosCards.css">
        <style>
            .modal {
                display: none;
                position: fixed;
                z-index: 9999;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0,0,0,0.4);
                justify-content: center;
                align-items: center;
            }
            .modal-content {
                background: white;
                padding: 20px;
                border-radius: 6px;
                max-width: 600px;
                width: 90%;
                position: relative;
            }
            .close-button {
                position: absolute;
                top: 10px;
                right: 15px;
                font-size: 20px;
                cursor: pointer;
            }
        </style>
    </head>

    <body>
        <nav class="sidebar">
            <header>
                <div class="image-text">
                    <span class="image">
                        <img id="logoAdmin" src="<%= request.getContextPath()%>/Recursos/Logo.png" alt="Logo" class="logo">
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
                        <a href="${pageContext.request.contextPath}/HistorialVentaServlet">
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
                <h1>Venta de Productos</h1>
                <form class="barra-busqueda" method="get" action="${pageContext.request.contextPath}/ProductoRecepServlet">
                    <input type="hidden" name="accion" value="listar">
                    <input type="text" name="busqueda" placeholder="Buscar productos..." value="${param.busqueda}">
                    <button type="submit" class="btn">Buscar</button>
                </form>
            </div>

            <% if (mensaje != null) {%>
            <div class="alert"><%= mensaje%></div>
            <% } %>

            <div class="card-grid">
                <% if (listaProductos != null && !listaProductos.isEmpty()) {
                        for (Producto p : listaProductos) {
                            String imagenSrc = (p.getImagen() != null && !p.getImagen().isEmpty())
                                    ? request.getContextPath() + "/" + p.getImagen()
                                    : request.getContextPath() + "/Recursos/sin_imagen.png";
                %>
                <div class="card">
                    <img src="<%= imagenSrc%>" alt="<%= p.getNombreProducto()%>">
                    <div class="nombre"><%= p.getNombreProducto()%></div>
                    <div class="precio">S/. <%= String.format("%.2f", p.getPrecio())%></div>
                    <div class="stock">Stock: <%= p.getStock()%></div>
                    <form method="post" action="${pageContext.request.contextPath}/ProductoRecepServlet">
                        <input type="hidden" name="accion" value="agregarAlCarrito">
                        <input type="hidden" name="idProducto" value="<%= p.getIdProducto()%>">
                        <button type="submit">Vender</button>
                    </form>
                </div>
                <% }
                } else { %>
                <p>No hay productos registrados.</p>
                <% } %>
            </div>
        </main>

        <!-- Icono Carrito -->
        <div id="carritoIcon" class="carrito-icon" onclick="abrirCarritoModal()">
            <i class='bx bx-cart'></i>
            <% if (carritoCount > 0) {%>
            <span class="carrito-count"><%= carritoCount%></span>
            <% } %>
        </div>

        <!-- Modal Carrito -->
        <div id="carritoModal" class="modal">
            <div class="modal-content">
                <span class="close-button" onclick="cerrarCarritoModal()">&times;</span>
                <h2>Carrito de Venta</h2>

                <% if (carrito != null && !carrito.isEmpty()) { %>
                <table class="tabla-carrito">
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Cantidad</th>
                            <th>Subtotal</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (CarritoItem item : carrito) {%>
                        <tr>
                            <td><%= item.getNombreProducto()%></td>
                            <td><%= item.getCantidad()%></td>
                            <td>S/. <%= String.format("%.2f", item.getSubtotal())%></td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/ProductoRecepServlet">
                                    <input type="hidden" name="accion" value="eliminarDelCarrito">
                                    <input type="hidden" name="idProducto" value="<%= item.getIdCarrito()%>">
                                    <button type="submit">Eliminar</button>
                                </form>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <form method="post" action="${pageContext.request.contextPath}/ProductoRecepServlet">
                    <input type="hidden" name="accion" value="procesarVenta">
                    <button type="submit" class="btn procesar-venta">Procesar Venta</button>
                </form>
                <% } else { %>
                <p>Aún no se han añadido productos para vender.</p>
                <% }%>
            </div>
        </div>

        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>

        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
        <script>
                    function abrirCarritoModal() {
                        document.getElementById('carritoModal').style.display = 'flex';
                    }
                    function cerrarCarritoModal() {
                        document.getElementById('carritoModal').style.display = 'none';
                    }
                    window.addEventListener('click', function (e) {
                        if (e.target.id === 'carritoModal')
                            cerrarCarritoModal();
                    });

                    document.addEventListener('DOMContentLoaded', () => {
            <% if (abrirModal != null && abrirModal) { %>
                        abrirCarritoModal();
            <% }%>
                    });
        </script>
    </body>
</html>
