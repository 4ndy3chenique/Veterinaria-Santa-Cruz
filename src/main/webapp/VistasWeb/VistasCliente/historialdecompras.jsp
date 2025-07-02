<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <title>Historial de Compras - Veterinaria</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/accesorios.css">
  <link href="https://fonts.googleapis.com/css?family=Poppins:400,600&display=swap" rel="stylesheet" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <style>
    /* Puedes mantener los mismos estilos que en accesorios.jsp */
    .historial-container {
      max-width: 700px;
      margin: 40px auto 0 auto;
      background: #fff;
      border-radius: 18px;
      box-shadow: 0 4px 24px rgba(0,0,0,0.10);
      padding: 2.5rem 2.2rem 2rem 2.2rem;
    }
    .historial-container h2 {
      text-align: center;
      margin-bottom: 1.8rem;
      font-size: 2rem;
      font-weight: 700;
      color: #2bb673;
    }
    .historial-lista {
      list-style: none;
      padding: 0;
      margin: 0;
    }
    .historial-lista li {
      background: #f6f6f6;
      margin-bottom: 15px;
      border-radius: 9px;
      padding: 1.1rem 1.4rem;
      font-size: 1.13rem;
      color: #333;
      font-weight: 500;
      box-shadow: 0 1px 4px rgba(0,0,0,0.05);
      display: flex;
      align-items: center;
      gap: 12px;
    }
    .historial-lista li .icon {
      color: #2bb673;
      font-size: 1.3em;
    }
    .btn-regresar {
      background: #777;
      color: #fff;
      border: none;
      padding: .7rem 1.3rem;
      border-radius: 7px;
      cursor: pointer;
      font-weight: 600;
      font-size: 1rem;
      margin-top: 1.2rem;
      transition: background 0.2s;
      display: block;
      margin-left: auto;
      margin-right: auto;
    }
    .btn-regresar:hover {
      background: #444;
    }
    /* Sidebar y navbar igual que accesorios.jsp */
    .sidebar-perfil {
      display: none;
      position: fixed;
      top: 0; right: 0;
      width: 280px;
      height: 100%;
      background-color: #fff;
      box-shadow: -2px 0 5px rgba(0,0,0,0.3);
      z-index: 2000;
      padding-top: 50px;
      overflow-y: auto;
    }
    .sidebar-perfil.active {
      display: block;
    }
    .sidebar-perfil h2 {
      margin: 0 0 20px 20px;
      font-weight: 600;
      font-size: 22px;
    }
    .sidebar-perfil a {
      display: block;
      padding: 15px 25px;
      color: #333;
      text-decoration: none;
      border-bottom: 1px solid #eee;
      font-size: 16px;
      transition: background-color 0.2s;
    }
    .sidebar-perfil a:hover {
      background-color: #f0f0f0;
    }
    #sidebarOverlay {
      display: none;
      position: fixed;
      top: 0; left: 0;
      width: 100vw; height: 100vh;
      background-color: rgba(0,0,0,0.4);
      z-index: 1500;
    }
    #sidebarOverlay.active {
      display: block;
    }
    .center-links a.active-link {
      border-bottom: 2px solid #000;
      font-weight: 600;
    }
  </style>
</head>
<body>

  <!-- NAVBAR -->
  <nav class="navbar">
    <div class="logo-container">
      <a href="${pageContext.request.contextPath}/index.jsp">
        <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo" class="logo" />
      </a>
    </div>
    <div class="nav-links">
      <div class="center-links">
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Nosotros.jsp">Nosotros</a>
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp">Servicios</a>
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Productos.jsp">Productos</a>
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Contacto.jsp">Contacto</a>
      </div>
      <div class="buttons">
        <a href="javascript:void(0)" class="btn perfil" id="verPerfilBtn">Ver perfil</a>
      </div>
    </div>
    <div class="hamburger" id="hamburger-menu">
      <span></span><span></span><span></span>
    </div>
  </nav>

  <!-- CONTENIDO PRINCIPAL: HISTORIAL -->
  <div class="historial-container">
    <h2>Historial de Compras</h2>
    <ul class="historial-lista">
      <%
        List<String> historial = (List<String>) session.getAttribute("historialCompras");
        if (historial != null && !historial.isEmpty()) {
          for (String producto : historial) {
      %>
            <li><span class="icon">&#128722;</span> <%= producto %></li>
      <%
          }
        } else {
      %>
        <li style="text-align:center;color:#888;">No hay compras registradas.</li>
      <%
        }
      %>
    </ul>
    <button class="btn-regresar" onclick="window.location.href='${pageContext.request.contextPath}/VistasWeb/VistasCliente/Productos.jsp'">Regresar a productos</button>
  </div>

  <!-- Sidebar perfil -->
  <div id="sidebarPerfil" class="sidebar-perfil" role="dialog" aria-modal="true" aria-labelledby="perfilTitle">
    <h2 id="perfilTitle">Mi Perfil</h2>
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/MiPerfil.jsp">Mi perfil</a>
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/historialdecompras.jsp">Historial de compras/servicios</a>
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/historialdecitas.jsp">Citas agendadas</a>
    <a href="${pageContext.request.contextPath}/LogoutServlet">Cerrar sesión</a>
  </div>

  <!-- Overlay para cerrar sidebar -->
  <div id="sidebarOverlay"></div>

  <script>
    // Sidebar perfil
    document.getElementById('verPerfilBtn').addEventListener('click', function() {
      document.getElementById('sidebarPerfil').classList.add('active');
      document.getElementById('sidebarOverlay').classList.add('active');
    });
    document.getElementById('sidebarOverlay').addEventListener('click', function() {
      document.getElementById('sidebarPerfil').classList.remove('active');
      this.classList.remove('active');
    });

    // Menú hamburguesa
    document.getElementById('hamburger-menu').onclick = function () {
      document.querySelector('.nav-links').classList.toggle('active');
    };

    // Subrayado en barra de navegación para la página activa
    window.addEventListener('DOMContentLoaded', () => {
      const navLinks = document.querySelectorAll('.center-links a');
      const currentPath = window.location.pathname;
      navLinks.forEach(link => {
        if (link.getAttribute('href') === currentPath) {
          link.classList.add('active-link');
        }
      });
    });
  </script>
</body>
</html>
