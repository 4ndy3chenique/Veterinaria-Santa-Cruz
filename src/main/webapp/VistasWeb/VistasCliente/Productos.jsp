<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Productos Veterinaria PetCare</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/productos.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet" />
    <style>
        /* --- Estilos generales del modal --- */
        .modal {
            display: none;
            position: fixed;
            z-index: 10000;
            left: 0; top: 0; width: 100vw; height: 100vh;
            background: rgba(0,0,0,0.4);
            justify-content: center;
            align-items: center;
        }
        .modal.active {
            display: flex;
        }
        .modal-content {
            background: #fff;
            padding: 2.5rem 2rem;
            border-radius: 18px;
            max-width: 500px;
            width: 95%;
            box-shadow: 0 4px 24px rgba(0,0,0,0.22);
            text-align: center;
            position: relative;
            animation: modalFadeIn 0.22s;
        }
        @keyframes modalFadeIn {
            from { transform: scale(0.93); opacity: 0; }
            to { transform: scale(1); opacity: 1; }
        }
        .modal-title {
            margin-bottom: 1.5rem;
            font-size: 1.6rem;
            color: #333;
        }
        .modal-buttons {
            display: flex;
            justify-content: center;
            gap: 1rem;
            margin-top: 2rem;
        }
        .modal-btn {
            background: #5cb85c; /* Verde */
            color: #fff;
            border: none;
            padding: .9rem 1.5rem;
            border-radius: 7px;
            cursor: pointer;
            font-weight: bold;
            font-size: 1.08rem;
            transition: background 0.2s;
        }
        .modal-btn:hover {
            background: #449d44;
        }
        .btn-cancelar {
            background: #d9534f; /* Rojo */
        }
        .btn-cancelar:hover {
            background: #c9302c;
        }
        /* --- Estilos del modal del carrito (existentes) --- */
        .modal-carrito {
            display: none;
            position: fixed;
            z-index: 9999;
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
            max-width: 600px;
            width: 95%;
            box-shadow: 0 4px 24px rgba(0,0,0,0.22);
            position: relative;
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
            margin-bottom: .9rem;
            border-bottom: 1px solid #eee;
            padding-bottom: .7rem;
            display: flex;
            align-items: center;
            gap: 14px;
            font-size: 1.12rem;
        }
        #listaCarrito li {
            margin-bottom: .9rem;
            border-bottom: 1px solid #eee;
            padding-bottom: .7rem;
            display: flex;
            align-items: center;
            gap: 14px;
            font-size: 1.12rem;
            border: 1px solid #ccc;
            border-radius: 8px;
            padding: 10px;
            margin-bottom: 10px;
        }

        #listaCarrito img {
            width: 54px;
            height: 54px;
            object-fit: cover;
            border-radius: 6px;
            margin-right: 8px;
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
            margin-left: auto;
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
        
    </style>
</head>
<body>

<!-- Barra de Navegación -->
<nav class="navbar">
    <div class="logo-container">
        <a href="${pageContext.request.contextPath}/index.jsp">
            <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo"/>
        </a>
    </div>
    <div class="hamburger" id="hamburger" aria-label="Menú">
        <span></span><span></span><span></span>
    </div>
    <div class="nav-links" id="nav-links">
        <div class="center-links">
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Nosotros.jsp" id="link-nosotros">Nosotros</a>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp" id="link-servicios">Servicios</a>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Productos.jsp" id="link-productos" class="active-link">Productos</a>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Contacto.jsp" id="link-contacto">Contacto</a>
        </div>
        <div class="buttons">
            <a href="javascript:void(0)" class="btn perfil" id="verPerfilBtn">Ver Perfil</a>
        </div>
    </div>
</nav>

<!-- Sidebar perfil -->
<div id="sidebarPerfil" class="sidebar-perfil" role="dialog" aria-modal="true" aria-labelledby="perfilTitle">
    <h2 id="perfilTitle">Mi Perfil</h2>
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/MiPerfil.jsp">Mi perfil</a>
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/historialdecompras.jsp">Historial de compras/servicios</a>
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/historialdecitas.jsp">Citas agendadas</a>
    <a href="${pageContext.request.contextPath}/LogoutServlet">Cerrar sesión</a>
</div>
<div id="sidebarOverlay"></div>

<!-- Hero Section -->
<section class="hero container">
    <div class="hero-img">
        <img src="${pageContext.request.contextPath}/Recursos/perroproducto.png" alt="Mascotas felices" />
    </div>
    <div class="hero-text">
        <h2>Productos para el cuidado integral de tus mascotas</h2>
        <p>Alimentos, medicamentos, accesorios y mucho más para perros, gatos y otras mascotas.</p>
    </div>
</section>

<!-- Categorías de Productos -->
<section class="categorias container">
    <h3>Categorías</h3>
    <div class="categorias-grid">
        <div class="categoria-card">
            <img src="${pageContext.request.contextPath}/Recursos/Accesorios.png" alt="Accesorios" />
            <h4>Accesorios</h4>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Accesorios.jsp" class="btn-categoria">Ver Accesorios</a>
        </div>
        <div class="categoria-card">
            <img src="${pageContext.request.contextPath}/Recursos/comida.png" alt="Alimentos" />
            <h4>Alimentos</h4>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Alimentos.jsp" class="btn-categoria">Ver Alimentos</a>
        </div>
        <div class="categoria-card">
            <img src="${pageContext.request.contextPath}/Recursos/medicamentos.png" alt="Medicamentos" />
            <h4>Medicamentos</h4>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Medicamentos.jsp" class="btn-categoria">Ver Medicamentos</a>
        </div>
        <div class="categoria-card">
            <img src="${pageContext.request.contextPath}/Recursos/juguetes.png" alt="Juguetes" />
            <h4>Juguetes</h4>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Juguetes.jsp" class="btn-categoria">Ver Juguetes</a>
        </div>
    </div>
</section>

<!-- Productos dentro de cuadros -->
<section class="productos container">
    <article class="producto-card">
        <div class="producto-img">
            <img src="${pageContext.request.contextPath}/Recursos/comidadeperro.png" alt="Alimento para perro" />
        </div>
        <h3>Alimento Premium para Perros</h3>
        <p>Nutrición completa para perros adultos de todas las razas.</p>
        <p class="precio">$25.000</p>
        <button data-nombre="Alimento Premium para Perros" data-precio="$25.000" data-img="${pageContext.request.contextPath}/Recursos/comidadeperro.png">Agregar al carrito</button>
    </article>

    <article class="producto-card">
        <div class="producto-img">
            <img src="${pageContext.request.contextPath}/Recursos/juguetegato.png" alt="Juguete para gato" />
        </div>
        <h3>Juguete Interactivo para Gatos</h3>
        <p>Estimula la actividad y el juego de tu gato con este divertido juguete.</p>
        <p class="precio">$12.000</p>
        <button data-nombre="Juguete Interactivo para Gatos" data-precio="$12.000" data-img="${pageContext.request.contextPath}/Recursos/juguetegato.png">Agregar al carrito</button>
    </article>
        
        <article class="producto-card">
        <div class="producto-img">
            <img src="${pageContext.request.contextPath}/Recursos/Medicamento.png" alt="Medicamento antipulgas" />
        </div>
        <h3>Medicamento Antipulgas</h3>
        <p>Protección efectiva contra pulgas y garrapatas para perros y gatos.</p>
        <p class="precio">$18.000</p>
        <button data-nombre="Medicamento Antipulgas" data-precio="$18.000" data-img="${pageContext.request.contextPath}/Recursos/Medicamento.png">Agregar al carrito</button>
    </article>

    <article class="producto-card">
        <div class="producto-img">
            <img src="${pageContext.request.contextPath}/Recursos/cama ortopedica.png" alt="Cama para mascota" />
        </div>
        <h3>Cama Ortopédica para Mascotas</h3>
        <p>Comodidad y soporte ideal para mascotas mayores o con problemas articulares.</p>
        <p class="precio">$45.000</p>
        <button data-nombre="Cama Ortopédica para Mascotas" data-precio="$45.000" data-img="${pageContext.request.contextPath}/Recursos/cama ortopedica.png">Agregar al carrito</button>
    </article>
</section>

<!-- MODAL PRINCIPAL (CONFIRMACIÓN) -->
<div id="modalConfirmar" class="modal">
    <div class="modal-content">
        <h2 class="modal-title">¿Agregar <span id="nombreProducto"></span> al carrito?</h2>
        <div class="modal-buttons">
            <button class="modal-btn" id="confirmarAgregar">Confirmar</button>
            <button class="modal-btn btn-cancelar" id="cancelarAgregar">Cancelar</button>
        </div>
    </div>
</div>

<!-- MODAL DEL CARRITO (EXISTENTE) -->
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

<!-- Consejos para el cuidado -->
<section class="consejos container">
    <h3>Consejos para el cuidado de tu mascota</h3>
    <ul>
        <li> Realiza chequeos veterinarios periódicos para prevenir enfermedades.</li>
        <li> Mantén una alimentación balanceada según la edad y raza de tu mascota.</li>
        <li> Proporciona espacios de juego y ejercicio para mantenerlos activos y felices.</li>
        <li> Vacuna y desparasita regularmente para evitar parásitos y enfermedades.</li>
        <li> Ofrece cariño y atención para fortalecer el vínculo con tu mascota.</li>
    </ul>
</section>

<!-- Testimonios -->
<section class="testimonios container">
    <h3>Lo que dicen nuestros clientes</h3>
    <div class="testimonio-card">
        <p>"Excelente atención y productos de calidad. Mi perro está feliz con su alimento nuevo."</p>
        <span>- María G.</span>
    </div>
    <div class="testimonio-card">
        <p>"Muy buena variedad de juguetes y accesorios. El personal siempre muy amable."</p>
        <span>- Juan P.</span>
    </div>
    <div class="testimonio-card">
        <p>"Los medicamentos antipulgas funcionan perfecto y a buen precio. Recomendado."</p>
        <span>- Laura R.</span>
    </div>
</section>

<!-- Footer -->
<footer>
    <div class="container footer-container">
        <p>© 2025 Veterinaria PetCare - Todos los derechos reservados</p>
        <p>Contacto: info@veterinariapetcare.com | Tel: +56 9 1234 5678</p>
        <div class="redes-sociales">
            <span>Facebook</span>
            <span>Instagram</span>
            <span>Twitter</span>
        </div>
    </div>
</footer>

<script>
    // Menú hamburguesa
    document.getElementById('hamburger').addEventListener('click', function() {
        this.classList.toggle('active');
        document.getElementById('nav-links').classList.toggle('active');
    });

    // Abrir sidebar perfil
    document.getElementById('verPerfilBtn').addEventListener('click', function() {
        document.getElementById('sidebarPerfil').classList.add('active');
        document.getElementById('sidebarOverlay').classList.add('active');
    });

    // Cerrar sidebar perfil al hacer click en overlay
    document.getElementById('sidebarOverlay').addEventListener('click', function() {
        document.getElementById('sidebarPerfil').classList.remove('active');
        this.classList.remove('active');
    });

    // --- Modales y Carrito ---
    const carrito = [];
    let productoSeleccionado = null;

    // Elementos de los modales
    const modalConfirmar = document.getElementById('modalConfirmar');
    const modalCarrito = document.getElementById('modalCarrito');

    // Funciones para abrir y cerrar modales
    function abrirModal(modal) {
        modal.classList.add('active');
    }
    function cerrarModal(modal) {
        modal.classList.remove('active');
    }

    // Eventos de los botones "Agregar al carrito"
    document.querySelectorAll('.producto-card button').forEach((btn) => {
        btn.addEventListener('click', function() {
            productoSeleccionado = {
                nombre: this.dataset.nombre,
                precio: this.dataset.precio,
                img: this.dataset.img
            };
            document.getElementById('nombreProducto').textContent = productoSeleccionado.nombre; // Actualiza el nombre en el modal

            abrirModal(modalConfirmar); // Muestra el modal de confirmación
        });
    });

    // Eventos del modal de confirmación
    document.getElementById('confirmarAgregar').addEventListener('click', function() {
        carrito.push(productoSeleccionado); // Agrega al carrito
        actualizarModalCarrito(); // Actualiza el modal del carrito
        cerrarModal(modalConfirmar); // Cierra el modal de confirmación
        abrirModal(modalCarrito); // Abre directamente el modal del carrito
    });
    document.getElementById('cancelarAgregar').addEventListener('click', function() {
        cerrarModal(modalConfirmar); // Cierra el modal de confirmación
    });

    // Funciones del modal del carrito (existentes)
    function actualizarModalCarrito() {
        const lista = document.getElementById('listaCarrito');
        lista.innerHTML = '';
        if (carrito.length === 0) {
            lista.innerHTML = '<li style="text-align:center;color:#888;">El carrito está vacío.</li>';
            return;
        }
        carrito.forEach((prod, idx) => {
            const li = document.createElement('li');
            li.innerHTML = `
                
                <strong>${prod.nombre}</strong> - <span>${prod.precio}</span>
                <button class="btn-eliminar" title="Quitar del carrito">&times;</button>
            `;
            li.querySelector('.btn-eliminar').onclick = function() {
                carrito.splice(idx, 1);
                actualizarModalCarrito();
            };
            lista.appendChild(li);
        });
    }

    document.getElementById('cerrarModalCarrito').onclick = function() {
        cerrarModal(modalCarrito);
    };
    document.getElementById('regresarCarrito').onclick = function() {
        cerrarModal(modalCarrito);
    };

    document.getElementById('finalizarCompra').onclick = function() {
        if(carrito.length === 0){
            alert('¡El carrito está vacío!');
            return;
        }
        alert('¡Gracias por tu compra! ');
        cerrarModal(modalCarrito);
        carrito.length = 0; // Vacía el carrito
        actualizarModalCarrito();
    };

    // Cerrar modales al hacer clic fuera del contenido
    window.addEventListener('click', function(e) {
        if (e.target == modalConfirmar) {
            cerrarModal(modalConfirmar);
        }
        if (e.target == modalCarrito) {
            cerrarModal(modalCarrito);
        }
    });
</script>

</body>
</html>
