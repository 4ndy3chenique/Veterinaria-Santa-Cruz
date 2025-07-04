<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/proteger.jsp" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>VeterinariaSantaCruz</title>
        <link href="https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="../../css/ModoNoche-Sidebar.css">  
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
                    <a href="<%= request.getContextPath()%>/CitaServlet"><i class='bx bxs-calendar icon'></i><span class="text">Citas</span></a>
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
        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>
        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
    </body>
</html>
