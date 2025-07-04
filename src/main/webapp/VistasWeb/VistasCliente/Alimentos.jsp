<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <title>Alimentos para Animales - Veterinaria</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/alimentos.css">
  <link href="https://fonts.googleapis.com/css?family=Poppins:400,600&display=swap" rel="stylesheet" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <style>
    /* Modal estilos */
    .modal-carrito {
      display: none;
      position: fixed;
      z-index: 10000;
      left: 0; top: 0; width: 100vw; height: 100vh;
      background: rgba(0,0,0,0.4);
      justify-content: center;
      align-items: center;
    }
    .modal-carrito.active {
      display: flex;
    }
    .modal-contenido {
      background: #fff;
      padding: 2.5rem 2rem;
      border-radius: 18px;
      max-width: 500px;
      width: 95%;
      box-shadow: 0 4px 24px rgba(0,0,0,0.22);
      position: relative;
      text-align: center;
      animation: modalFadeIn 0.22s;
    }
    @keyframes modalFadeIn {
      from { transform: scale(0.93); opacity: 0; }
      to { transform: scale(1); opacity: 1; }
    }
    .cerrar-modal {
      position: absolute;
      top: 1.3rem; right: 1.3rem;
      font-size: 2.1rem;
      color: #888;
      cursor: pointer;
      font-weight: bold;
    }
    #listaCarrito {
      list-style: none;
      padding: 0;
      margin-bottom: 1.5rem;
      max-height: 320px;
      overflow-y: auto;
    }
    #listaCarrito li {
      border: 1px solid #ccc;
      border-radius: 8px;
      padding: 10px;
      margin-bottom: 10px;
      font-size: 1.12rem;
      font-weight: 600;
      color: #333;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .btn-finalizar, .btn-regresar {
      background: #2bb673;
      color: #fff;
      border: none;
      padding: .9rem 1.5rem;
      border-radius: 7px;
      cursor: pointer;
      font-weight: bold;
      font-size: 1.08rem;
      margin-right: 1rem;
      transition: background 0.2s;
    }
    .btn-finalizar:hover {
      background: #228e59;
    }
    .btn-regresar {
      background: #777;
    }
    .btn-regresar:hover {
      background: #444;
    }
    .btn-eliminar {
      background: #ff5555;
      border: none;
      color: #fff;
      padding: 0.35rem 0.8rem;
      border-radius: 5px;
      cursor: pointer;
      font-size: 1.1rem;
      transition: background 0.2s;
    }
    .btn-eliminar:hover {
      background: #d22;
    }
    .botones-carrito {
      display: flex;
      justify-content: flex-end;
      gap: 1rem;
    }
    .btn-agregar {
      margin-top: 8px;
      background: #2bb673;
      color: #fff;
      border: none;
      padding: .5rem 1.1rem;
      border-radius: 6px;
      cursor: pointer;
      font-weight: 600;
      font-size: 1rem;
      transition: background 0.2s;
    }
    .btn-agregar:hover {
      background: #228e59;
    }
    #btnRegresar {
      margin: 1rem 0;
      background: #777;
      color: #fff;
      border: none;
      padding: .7rem 1.3rem;
      border-radius: 7px;
      cursor: pointer;
      font-weight: 600;
      font-size: 1rem;
      transition: background 0.2s;
    }
    #btnRegresar:hover {
      background: #444;
    }
    
    /* Estilos para sidebar perfil y overlay */
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
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Nosotros.jsp" id="link-nosotros">Nosotros</a>
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp" id="link-servicios">Servicios</a>
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Productos.jsp" id="link-productos">Productos</a>
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Contacto.jsp" id="link-contacto">Contacto</a>
      </div>
      <div class="buttons">
        <a href="javascript:void(0)" class="btn perfil" id="verPerfilBtn">Ver perfil</a>
      </div>
    </div>
    <div class="hamburger" id="hamburger-menu">
      <span></span><span></span><span></span>
    </div>
  </nav>

  <!-- SECCIÓN ALIMENTOS -->
  <section class="alimentos-container">
    <h2>Alimentos para Animales</h2>
    <!-- Botón regresar a página anterior -->
    <button id="btnRegresar">Regresar</button>

    <!-- Perros -->
    <h3 class="animal-category">Perros</h3>
    <div class="alimentos-grid">
      <article class="alimento-card" data-nombre="Proplan">
        <img src="${pageContext.request.contextPath}/Recursos/Proplan.png" alt="Proplan" />
        <h4>Proplan</h4>
        <p>Saco premium, contiene carne y nutrición completa.</p>
        <button class="btn-agregar">Agregar al carrito</button>
      </article>
      <article class="alimento-card" data-nombre="Pedigree">
        <img src="${pageContext.request.contextPath}/Recursos/Pedigree.png" alt="Pedigree" />
        <h4>Pedigree</h4>
        <p>Comida  para perros con paladar exigente.</p>
        <button class="btn-agregar">Agregar al carrito</button>
      </article>
      <article class="alimento-card" data-nombre="Royal canin (perro)">
        <img src="${pageContext.request.contextPath}/Recursos/Royalcanin.png" alt="Royal canin" />
        <h4>Royal canin</h4>
        <p>Comida para perros con problemas digestivos, alimentación natural y saludable.</p>
        <button class="btn-agregar">Agregar al carrito</button>
      </article>
      <article class="alimento-card" data-nombre="Canbo">
        <img src="${pageContext.request.contextPath}/Recursos/canboperro.png" alt="Canbo" />
        <h4>Canbo</h4>
        <p>Alimentos premiun para perros,contiene carne de alpaca y naturales.</p>
        <button class="btn-agregar">Agregar al carrito</button>
      </article>
    </div>

    <!-- Gatos -->
    <h3 class="animal-category">Gatos</h3>
    <div class="alimentos-grid">
      <article class="alimento-card" data-nombre="North paw">
        <img src="${pageContext.request.contextPath}/Recursos/Northpaw.png" alt="North paw" />
        <h4>North paw</h4>
        <p>Alimento seco con ingredientes naturales y adaptado a gatos pequeños.</p>
        <button class="btn-agregar">Agregar al carrito</button>
      </article>
      <article class="alimento-card" data-nombre="Perfect fit">
        <img src="${pageContext.request.contextPath}/Recursos/Perfectfit.png" alt="Perfect fit" />
        <h4>Perfect fit</h4>
        <p>Comida húmeda con alto contenido de agua para hidratación.</p>
        <button class="btn-agregar">Agregar al carrito</button>
      </article>
      <article class="alimento-card" data-nombre="Royal canin (gato)">
        <img src="${pageContext.request.contextPath}/Recursos/Royalcat.png" alt="Royal canin" />
        <h4>Royal canin</h4>
        <p>Opciones naturales y balanceadas para gatitos y adultos.</p>
        <button class="btn-agregar">Agregar al carrito</button>
      </article>
      <article class="alimento-card" data-nombre="Brif premiun">
        <img src="${pageContext.request.contextPath}/Recursos/brif.png" alt="Brif premiun" />
        <h4>Brif premiun</h4>
        <p>Alimentos premiun para gatos, con ingredientes naturales.</p>
        <button class="btn-agregar">Agregar al carrito</button>
      </article>
    </div>

    <!-- Hámsters -->
    <h3 class="animal-category">Hámsters</h3>
    <div class="alimentos-grid">
      <article class="alimento-card" data-nombre="Mezcla de Semillas (hámster)">
        <img src="${pageContext.request.contextPath}/Recursos/comidahamster.png" alt="Mezcla de Semillas" />
        <h4>Mezcla de Semillas</h4>
        <p>Semillas seleccionadas para una dieta variada y nutritiva.</p>
        <button class="btn-agregar">Agregar al carrito</button>
      </article>
      <article class="alimento-card" data-nombre="Comida Balanceada (hámster)">
        <img src="${pageContext.request.contextPath}/Recursos/comidahamster2.png" alt="Comida Balanceada" />
        <h4>Comida Balanceada</h4>
        <p>Alimento completo para mantener a tu hámster saludable.</p>
        <button class="btn-agregar">Agregar al carrito</button>
      </article>
    </div>

    <!-- Conejos -->
    <h3 class="animal-category">Conejos</h3>
    <div class="alimentos-grid">
      <article class="alimento-card" data-nombre="Heno Natural">
        <img src="${pageContext.request.contextPath}/Recursos/conejo1.png" alt="Heno Natural" />
        <h4>Heno Natural</h4>
        <p>Heno fresco para favorecer la digestión y salud dental.</p>
        <button class="btn-agregar">Agregar al carrito</button>
      </article>
      <article class="alimento-card" data-nombre="Pellets para Conejos">
        <img src="${pageContext.request.contextPath}/Recursos/conejo2.png" alt="Pellets para Conejos" />
        <h4>Pellets</h4>
        <p>Pellets balanceados para conejos de todas las edades.</p>
        <button class="btn-agregar">Agregar al carrito</button>
      </article>
      <article class="alimento-card" data-nombre="Verduras Frescas">
        <img src="${pageContext.request.contextPath}/Recursos/conejo3.png" alt="Verduras Frescas" />
        <h4>Verduras Frescas</h4>
        <p>Selección de verduras frescas para una dieta natural.</p>
        <button class="btn-agregar">Agregar al carrito</button>
      </article>
    </div>

    <!-- Loros -->
    <h3 class="animal-category">Loros</h3>
    <div class="alimentos-grid">
      <article class="alimento-card" data-nombre="Mezcla de Semillas (loro)">
        <img src="${pageContext.request.contextPath}/Recursos/loro1.png" alt="Mezcla de Semillas" />
        <h4>Mezcla de Semillas</h4>
        <p>Semillas variadas para loros y aves exóticas.</p>
        <button class="btn-agregar">Agregar al carrito</button>
      </article>
      <article class="alimento-card" data-nombre="Pellets para Loros">
        <img src="${pageContext.request.contextPath}/Recursos/loro2.png" alt="Pellets para Loros" />
        <h4>Pellets</h4>
        <p>Pellets balanceados para una dieta completa y saludable.</p>
        <button class="btn-agregar">Agregar al carrito</button>
      </article>
      <article class="alimento-card" data-nombre="Frutas y Verduras">
        <img src="${pageContext.request.contextPath}/Recursos/loro3.png" alt="Frutas y Verduras" />
        <h4>Frutas y Verduras</h4>
        <p>Opciones frescas para complementar la alimentación.</p>
        <button class="btn-agregar">Agregar al carrito</button>
      </article>
    </div>
  </section>
  
  <!-- MODAL DEL CARRITO -->
  <div id="modalCarrito" class="modal-carrito" aria-modal="true" role="dialog">
    <div class="modal-contenido">
      <span class="cerrar-modal" id="cerrarModalCarrito">&times;</span>
      <h2 style="margin-top:0;">Productos agregados al carrito</h2>
      <ul id="listaCarrito"></ul>
      <div class="botones-carrito">
        <button id="finalizarCompra" class="btn-finalizar">Finalizar compra</button>
        <button id="regresarCarrito" class="btn-regresar">Regresar</button>
      </div>
    </div>
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
   <!-- FORMULARIO OCULTO para enviar productos al servlet -->
  <form id="formFinalizarCompra" action="${pageContext.request.contextPath}/FinalizarCompraServlet" method="post" style="display:none;">
    <!-- Los inputs se agregan dinámicamente -->
  </form>

   <script>
    const urlAnterior = '${pageContext.request.contextPath}/VistasWeb/VistasCliente/Productos.jsp';

    document.getElementById('btnRegresar').addEventListener('click', () => {
      window.location.href = urlAnterior;
    });
    
    document.getElementById('hamburger-menu').onclick = function () {
      document.querySelector('.nav-links').classList.toggle('active');
    };

    const carrito = [];

    document.querySelectorAll('.btn-agregar').forEach(btn => {
      btn.addEventListener('click', () => {
        const card = btn.closest('.alimento-card');
        const nombre = card.getAttribute('data-nombre');
        carrito.push(nombre);
        actualizarModalCarrito();
        document.getElementById('modalCarrito').classList.add('active');
      });
    });

    function actualizarModalCarrito() {
      const lista = document.getElementById('listaCarrito');
      lista.innerHTML = '';
      if (carrito.length === 0) {
        lista.innerHTML = '<li style="text-align:center;color:#888;">El carrito está vacío.</li>';
        return;
      }
      carrito.forEach((nombre, idx) => {
        const li = document.createElement('li');
        li.textContent = nombre;
        const btnEliminar = document.createElement('button');
        btnEliminar.textContent = '×';
        btnEliminar.className = 'btn-eliminar';
        btnEliminar.title = 'Quitar del carrito';
        btnEliminar.onclick = () => {
          carrito.splice(idx, 1);
          actualizarModalCarrito();
        };
        li.appendChild(btnEliminar);
        lista.appendChild(li);
      });
    }

    document.getElementById('cerrarModalCarrito').onclick = () => {
      document.getElementById('modalCarrito').classList.remove('active');
    };
    document.getElementById('regresarCarrito').onclick = () => {
      document.getElementById('modalCarrito').classList.remove('active');
    };

    // FINALIZAR COMPRA: envía productos al servlet usando formulario oculto
    document.getElementById('finalizarCompra').onclick = () => {
      if (carrito.length === 0) {
        alert('¡El carrito está vacío!');
        return;
      }
      // Limpiamos el formulario oculto
      const form = document.getElementById('formFinalizarCompra');
      form.innerHTML = '';
      // Creamos un input por cada producto
      carrito.forEach(producto => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'producto'; // El servlet recibirá varios parámetros 'producto'
        input.value = producto;
        form.appendChild(input);
      });
      form.submit();
    };

    document.getElementById('modalCarrito').addEventListener('click', e => {
      if (e.target === document.getElementById('modalCarrito')) {
        document.getElementById('modalCarrito').classList.remove('active');
      }
    });
    
    document.getElementById('verPerfilBtn').addEventListener('click', function() {
      document.getElementById('sidebarPerfil').classList.add('active');
      document.getElementById('sidebarOverlay').classList.add('active');
    });

    document.getElementById('sidebarOverlay').addEventListener('click', function() {
      document.getElementById('sidebarPerfil').classList.remove('active');
      this.classList.remove('active');
    });

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