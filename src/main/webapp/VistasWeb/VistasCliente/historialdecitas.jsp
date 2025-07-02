<%@ include file="/proteger.jsp" %>
<%@ page import="java.util.List" %>
<%@ page import="Modelo.UsuarioCitas" %>
<%@ page import="ModeloDAO.UsuarioCitasDAO" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="Modelo.UsuarioCliente" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.LocalTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.sql.Time" %>
<%
    // Obtener el objeto usuario de la sesión
    UsuarioCliente usuarioObj = (UsuarioCliente) session.getAttribute("usuario");
    String usuarioNombre = ""; // Inicializar para evitar NullPointerException

    List<UsuarioCitas> citas = null; // Inicializar lista de citas
    String mensaje = request.getParameter("mensaje");

    // Obtener fecha y hora actual
    LocalDate fechaActual = LocalDate.now();
    LocalTime horaActual = LocalTime.now();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter timeFormatter24h = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter timeFormatter12h = DateTimeFormatter.ofPattern("hh:mm a");

    if (usuarioObj != null) {
        usuarioNombre = usuarioObj.getNombre();
        UsuarioCitasDAO dao = new UsuarioCitasDAO();
        citas = dao.listarCitasPorUsuario(usuarioObj.getIdUsuario());
    } else {
        response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Historial de Citas</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet" />
    <style>
        body { font-family: 'Poppins', Arial, sans-serif; background: #f8f8f8; margin: 0; }
        .navbar {
            display: flex; align-items: center; justify-content: space-between;
            background: #3aafa9; padding: 0 30px; height: 70px;
        }
        .logo-container { display: flex; align-items: center; }
        .logo { height: 56px; }
        .nav-links { display: flex; align-items: center; }
        .center-links a {
            color: #fff; text-decoration: none; margin: 0 16px; font-weight: 500; font-size: 1.1rem;
            transition: color 0.2s;
        }
        .center-links a:hover, .center-links a.active-link { color: #17252a; border-bottom: 2px solid #fff; }
        .buttons .btn { background: #def2f1; color: #3aafa9; padding: 8px 18px; border-radius: 20px; text-decoration: none; font-weight: 600; margin-left: 10px; }
        .sidebar-perfil {
            display: none; position: fixed; top: 0; right: 0; width: 280px; height: 100%;
            background: #fff; box-shadow: -2px 0 5px rgba(0,0,0,0.3); z-index: 2000; padding-top: 50px; overflow-y: auto;
        }
        .sidebar-perfil.active { display: block; }
        .sidebar-perfil h2 { margin: 0 0 20px 20px; font-weight: 600; font-size: 22px; }
        .sidebar-perfil a { display: block; padding: 15px 25px; color: #333; text-decoration: none; border-bottom: 1px solid #eee; font-size: 16px; transition: background 0.2s; }
        .sidebar-perfil a:hover { background: #f0f0f0; }
        #sidebarOverlay { display: none; position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0,0,0,0.4); z-index: 1500; }
        #sidebarOverlay.active { display: block; }
        .historial-container {
            max-width: 900px; margin: 40px auto 60px auto; padding: 20px;
            border: 1px solid #ccc; border-radius: 8px; background: #fff;
        }
        .historial-container h1 { text-align: center; margin-bottom: 30px; color: #3aafa9; font-size: 2rem; font-weight: 600; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px 10px; border-bottom: 1px solid #eee; text-align: center; font-size: 1rem; }
        th { background: #3aafa9; color: #fff; font-weight: 600; }
        tr:hover { background: #f1f1f1; }
        .alert-success, .alert-error {
            max-width: 600px; margin: 20px auto; padding: 15px; border-radius: 5px; font-size: 1rem; text-align: center;
        }
        .alert-success { background: #d4edda; color: #155724; }
        .alert-error { background: #f8d7da; color: #721c24; }
        .hora-am-pm { text-transform: lowercase; }
        .hora-pasada { color: red; }
    </style>
</head>
<body>
<nav class="navbar">
    <div class="logo-container">
        <a href="${pageContext.request.contextPath}/index.jsp">
            <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo de Veterinaria Santa Cruz" class="logo" />
        </a>
    </div>
    <div class="nav-links">
        <div class="center-links">
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Nosotros.jsp" id="link-nosotros">Nosotros</a>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp" id="link-servicios">Servicios</a>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Productos.jsp" id="link-productos">Productos</a>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Contacto.jsp" id="link-contacto">Contacto</a>
        </div>
        <div class="buttons">
            <a href="javascript:void(0)" class="btn perfil" id="verPerfilBtn">Ver Perfil</a>
        </div>
    </div>
</nav>

<% if ("registrado".equals(mensaje)) { %>
    <div class="alert-success">¡Cita registrada correctamente!</div>
<% } else if ("error".equals(mensaje)) { %>
    <div class="alert-error">Error al registrar la cita. Por favor, inténtalo de nuevo.</div>
<% } %>

<div class="historial-container">
    <h1>Historial de Citas de <%= usuarioNombre %></h1>
    <table>
        <thead>
            <tr>
                <th>ID Cita</th>
                <th>Fecha</th>
                <th>Hora</th>
                <th>Veterinario</th>
                <th>Motivo</th>
                <th>Estado</th>
            </tr>
        </thead>
        <tbody>
        <%
            if (citas != null && !citas.isEmpty()) {
                for (UsuarioCitas c : citas) {
                    // Convertir hora a formato AM/PM
                    String horaFormateada = "";
                    boolean esHoraPasada = false;
                    try {
                        Time horaTime = c.getHora();
                        String horaStr = horaTime.toString().substring(0, 5); // Formato HH:mm
                        LocalTime hora = LocalTime.parse(horaStr);
                        horaFormateada = hora.format(timeFormatter12h).toLowerCase();
                        
                        // Verificar si la hora ya pasó
                        if (c.getFecha().equals(fechaActual.format(dateFormatter))) {
                            esHoraPasada = hora.isBefore(horaActual);
                        }
                    } catch (Exception e) {
                        horaFormateada = c.getHora().toString();
                    }
        %>
            <tr>
                <td><%= c.getIdCita() %></td>
                <td><%= c.getFecha() %></td>
                <td class="hora-am-pm <%= esHoraPasada ? "hora-pasada" : "" %>" title="<%= esHoraPasada ? "Esta hora ya ha pasado" : "" %>">
                    <%= horaFormateada %>
                </td>
                <td><%= c.getVeterinario() %></td>
                <td><%= c.getMotivo() %></td>
                <td><%= c.getEstado() %></td>
            </tr>
        <%
                }
            } else {
        %>
            <tr>
                <td colspan="6" style="text-align:center;">No tienes citas registradas.</td>
            </tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>

<div id="sidebarPerfil" class="sidebar-perfil" role="dialog" aria-modal="true" aria-labelledby="perfilTitle">
    <h2 id="perfilTitle">Mi Perfil</h2>
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/MiPerfil.jsp">Mi perfil</a>
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/historialdecompras.jsp">Historial de compras/servicios</a>
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/historialdecitas.jsp">Citas agendadas</a>
    <a href="${pageContext.request.contextPath}/LogoutServlet">Cerrar sesión</a>
</div>
<div id="sidebarOverlay"></div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    // Abrir sidebar perfil
    var verPerfilBtn = document.getElementById('verPerfilBtn');
    if (verPerfilBtn) {
        verPerfilBtn.addEventListener('click', function() {
            document.getElementById('sidebarPerfil').classList.add('active');
            document.getElementById('sidebarOverlay').classList.add('active');
        });
    }

    // Cerrar sidebar perfil al hacer click en overlay
    var sidebarOverlay = document.getElementById('sidebarOverlay');
    if (sidebarOverlay) {
        sidebarOverlay.addEventListener('click', function() {
            document.getElementById('sidebarPerfil').classList.remove('active');
            this.classList.remove('active');
        });
    }

    // Subrayado en barra de navegación para la página activa
    const navLinks = document.querySelectorAll('.center-links a');
    const path = window.location.pathname.toLowerCase();
    navLinks.forEach(link => {
        const href = link.getAttribute('href').toLowerCase();
        if (path.includes('nosotros') && href.includes('nosotros')) {
            link.classList.add('active-link');
        } else if (path.includes('servicios') && href.includes('servicios')) {
            link.classList.add('active-link');
        } else if (path.includes('productos') && href.includes('productos')) {
            link.classList.add('active-link');
        } else if (path.includes('contacto') && href.includes('contacto')) {
            link.classList.add('active-link');
        } else {
            link.classList.remove('active-link');
        }
    });
});
</script>

</body>
</html>