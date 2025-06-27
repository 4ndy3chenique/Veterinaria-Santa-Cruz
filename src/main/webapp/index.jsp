<%-- 
    Document   : index
    Created on : 4 may 2025, 2:26:23
    Author     : andy9
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>VeterinariaSantaCruz</title>
    <link rel="stylesheet" href="css/index.css">    
</head>
<body>

<!-- navbar -->
<nav class="navbar">
    <div class="logo-container">
        <a href="#">
        <img id="logo" src="Recursos/Logo.png" alt="Logo de Veterinaria Santa Cruz" class="logo">
        </a>
    </div>
    <div class="hamburger" id="hamburger" aria-label="Menú" aria-expanded="false">
        <span></span>
        <span></span>
        <span></span>
    </div>
    <div class="nav-links" id="nav-links">
        <div class="center-links">
            <a href="#">Nosotros</a>
            <a href="#">Servicios</a>
            <a href="#">Productos</a>
            <a href="#">Contacto</a>
        </div>
    <div class="buttons">
        <a href="#" class="btn login">Inicio Sesión</a>
        <a href="#" onclick="abrirRegistroDesdeLogin()" class="btn register">Registrarse</a>
    </div>
    </div>

    <!-- Botón flotante modo noche -->
    <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>
</nav>

<!-- Modal Selección Inicial -->
<div id="modalSeleccionInicial" class="modal">
    <div class="modal-content animado">
        <span class="cerrar" onclick="cerrarModal('modalSeleccionInicial')">&times;</span>
        <img id="logoSeleccionInicial" src="Recursos/Logo.png" alt="Logo" class="icono-patita">
        <h2>¿Cómo deseas ingresar?</h2>
        <button class="btn1 rol-btn1" onclick="mostrarModalEquipo()">Soy del equipo</button>
        <button class="btn1 rol-btn2" onclick="abrirLogin('Cliente')">Soy cliente</button>
    </div>
</div>

<!-- Modal para equipo (Administrador/Recepcionista) -->
<div id="modalEquipo" class="modal">
    <div class="modal-content animado">
        <span class="cerrar" onclick="cerrarModal('modalEquipo')">&times;</span>
        <img id="logoModalEquipo" src="Recursos/Logo.png" alt="Logo" class="icono-patita">
        <h2>Ingreso del equipo</h2>
        <button class="btn1 rol-btn1" onclick="abrirLogin('Administrador')">Administrador</button>
        <button class="btn1 rol-btn2" onclick="abrirLogin('Recepcionista')">Recepcionista</button>
    </div>
</div>
<!-- Modal Login Genérico -->
<div id="modalLogin" class="modal">
    <div class="modal-content animado">
        <span class="cerrar" onclick="cerrarModal('modalLogin')">&times;</span>
        <h2 id="tituloLogin">Iniciar Sesión</h2>
        <img src="Recursos/IconUser.svg" alt="Icono Usuario" class="icono-usuario">

        <form id="formLogin" action="LoginServlet" method="post">
            <input type="hidden" name="rol" id="inputRol">
            <input type="text" name="correo" placeholder="Correo electrónico" required>
            <input type="password" name="contrasena" placeholder="Contraseña" required>
            <button type="submit" class="btn1 iniciar-sesion">Ingresar</button>
        </form>

        <!-- Mensaje de error (inicialmente oculto) -->
        <div id="mensajeErrorLogin" style="display: none; color: red; font-weight: bold; margin-top: 10px;">
            Correo o contraseña incorrectos. Intente nuevamente.
        </div>

        <div id="opcionesRegistro" style="display: none;">
            <p>¿Aún no tienes una cuenta? <a href="#" onclick="abrirRegistroDesdeLogin()">Regístrate</a></p>
        </div>
    </div>
</div>


<!-- Modal Registro (solo para clientes) -->
 <%
    String errorRegistro = (String) request.getAttribute("error");
    String exitoRegistro = (String) request.getAttribute("exito");

    String valNombres = request.getParameter("nombres") != null ? request.getParameter("nombres") : "";
    String valApellidos = request.getParameter("apellidos") != null ? request.getParameter("apellidos") : "";
    String valDni = request.getParameter("dni") != null ? request.getParameter("dni") : "";
    String valTelefono = request.getParameter("telefono") != null ? request.getParameter("telefono") : "";
    String valCorreo = request.getParameter("correo") != null ? request.getParameter("correo") : "";
%>

<div id="modalRegistro" class="modal" style="<%= (errorRegistro != null || exitoRegistro != null) ? "display:flex;" : "" %>">
    <div class="modal-content animado">
        <span class="cerrar" onclick="cerrarModal('modalRegistro')">&times;</span>
        <img id="logoRegistro" src="Recursos/Logo.png" alt="Logo" class="icono-patita">
        <h2>Registrar Cliente</h2>



        <% if ("general".equals(errorRegistro)) { %>
            <div class="alert error">Error general al registrar. Intenta nuevamente.</div>
        <% } %>

        <% if (exitoRegistro != null) { %>
            <div class="alert success">¡Registro exitoso! Ya puedes iniciar sesión.</div>
        <% } %>

        <form class="form-registro" action="ClienteServlet" method="post">
            <div class="input-group">
                <input type="text" name="nombres" placeholder="Nombres" required 
                       pattern="[A-Za-zÁÉÍÓÚáéíóúñÑ ]+" title="Solo letras"
                       value="<%= valNombres %>">

                <input type="text" name="apellidos" placeholder="Apellidos" required 
                       pattern="[A-Za-zÁÉÍÓÚáéíóúñÑ ]+" title="Solo letras"
                       value="<%= valApellidos %>">
            </div>

            <div class="input-group">
                <div style="width: 100%;">
                    <input type="text" name="dni" placeholder="DNI" required 
                           min="10000000" max="99999999" title="8 dígitos exactos"
                           value="<%= valDni %>"
                           class="<%= "dni".equals(errorRegistro) ? "campo-error" : "" %>">
                    <% if ("dni".equals(errorRegistro)) { %>
                        <div style="color: red; font-size: 13px;">DNI ya registrado.</div>
                    <% } %>
                </div>

                <div style="width: 100%;">
                    <input type="tel" name="telefono" placeholder="Número telefónico" required 
                           pattern="9[0-9]{8}" title="Debe empezar con 9 y tener 9 dígitos"
                           value="<%= valTelefono %>"
                           class="<%= "telefono".equals(errorRegistro) ? "campo-error" : "" %>">
                    <% if ("telefono".equals(errorRegistro)) { %>
                        <div style="color: red; font-size: 13px;">Número ya registrado.</div>
                    <% } %>
                </div>
            </div>

            <input type="email" name="correo" placeholder="Correo Electrónico" required maxlength="100"
                   value="<%= valCorreo %>">

            <input type="password" name="contrasena" placeholder="Contraseña" required minlength="8" maxlength="45">

            <button type="submit" class="btn1 btnRegister">Registrar</button>
            <p class="switch-login">¿Ya tienes una cuenta? <a href="#" onclick="abrirLoginDesdeRegistro()">Iniciar Sesión</a></p>
        </form>
    </div>
</div>




<!-- Contenido principal de la página -->    
    
<div class="overlay" id="overlay"></div>

<section class="main-section">
    <div class="main-content">
        <div class="text-container">
            <h1>Veterinaria Santa Cruz en Lima</h1>
            <p class="subtext">Cuidamos a tu mascota con amor, experiencia y compromiso.</p>
        </div>
        <div class="image-container">
            <img src="Recursos/ImgDoctor.png" alt="Veterinaria Santa Cruz">
        </div>
    </div>
</section>

<section class="bloque-principal">
    <div class="div-texto">
        <div class="div-texto-centro">
            <div class="div-titulo1">
                <h2>Priorizando a tu compañero de vida</h2>
            </div>
            <div class="div-contenido1">
                <p>
                    En <span>Santa Cruz</span>, nuestra principal misión es asegurar que cada mascota que atendemos disfrute de una vida feliz y saludable. 
                    Nos dedicamos a ofrecer el más alto nivel de atención veterinaria, con profesionalismo y un profundo compromiso de cariño.<br><br>
                    Nuestro equipo de veterinarios y personal especializado trabaja incansablemente para fomentar el cuidado preventivo, brindar tratamientos integrales y acompañar a tu querida mascota en cada etapa de su vida.
                </p>
            </div>
        </div>
    </div>
    <div class="div-imagen1">
        <img src="Recursos/Gato.svg" alt="Gato acostado" class="imagen">
    </div>
</section>


<!----------------------------------------------------------------------------------->
    <section class="bloque-servicios">
        <div class="div-contenido">
            <div class="titulo-servicios">
                <h2>Nuestros Servicios</h2>
            </div>
            <div class="subtitulo-servicios">
                <p>
                    En Veterinaria Santa Cruz estamos plenamente capacitados para diagnosticar y tratar todas las enfermedades que pueda presentar tu mascota. 
                    Contamos con tecnología de diagnóstico de última generación y un equipo profesional en constante formación, actualizándonos en nuevas técnicas y métodos para brindarte una atención de calidad en medicina veterinaria para animales de compañía.
                </p>
            </div>
        </div>
    
        <div class="div-slider">
            <!-- Botón flecha izquierda -->
            <button class="flecha izquierda">
                <img src="Recursos/FIzquierda.svg" alt="Flecha Izquierda">
            </button>
    
            <div class="slider-contenedor">
                <!-- Tarjetas 1-6 -->
                <div class="tarjeta">
                    <div class="tarjeta-imagen">
                        <img src="Recursos/Consulta.svg" alt="Consulta General">
                    </div>
                    <div class="tarjeta-contenido">
                        <h3>Consulta General</h3>
                        <p>Evaluamos el estado de salud de tu mascota a través de un chequeo físico completo, resolviendo dudas sobre alimentación, comportamiento o síntomas clínicos.</p>
                    </div>
                </div>
    
                <div class="tarjeta">
                    <div class="tarjeta-imagen">
                        <img src="Recursos/Vacunacion.svg" alt="Vacunación">
                    </div>
                    <div class="tarjeta-contenido">
                        <h3>Vacunación</h3>
                        <p>Aplicamos vacunas esenciales para prevenir enfermedades contagiosas, adaptadas a la especie y edad de salud de tu mascota.</p>
                    </div>
                </div>
    
                <div class="tarjeta">
                    <div class="tarjeta-imagen">
                        <img src="Recursos/Desparacitacion.svg" alt="Desparasitación">
                    </div>
                    <div class="tarjeta-contenido">
                        <h3>Desparasitación</h3>
                        <p>Eliminamos parásitos internos y externos para cuidar su bienestar y el de tu familia.</p>
                    </div>
                </div>
    
                <div class="tarjeta">
                    <div class="tarjeta-imagen">
                        <img src="Recursos/ImgAtencionE.svg" alt="Atención de Urgencias">
                    </div>
                    <div class="tarjeta-contenido">
                        <h3>Atención de Urgencias</h3>
                        <p>Contamos con atención inmediata para emergencias médicas que pongan en riesgo la vida o salud de tu mascota.</p>
                    </div>
                </div>
    
                <div class="tarjeta">
                    <div class="tarjeta-imagen">
                        <img src="Recursos/ImgCertificados.svg" alt="Certificados de Salud">
                    </div>
                    <div class="tarjeta-contenido">
                        <h3>Certificados de Salud</h3>
                        <p>Emitimos certificados veterinarios oficiales para viajes, adopciones o concursos, avalando el estado de salud de tu mascota.</p>
                    </div>
                </div>
    
                <div class="tarjeta">
                    <div class="tarjeta-imagen">
                        <img src="Recursos/ImgPeluqueria 1.svg" alt="Baño y Peluquería">
                    </div>
                    <div class="tarjeta-contenido">
                        <h3>Baño y Peluquería</h3>
                        <p>Ofrecemos servicios de estética y cuidados como baños, corte de uñas, limpieza de oídos y peluquería especializada.</p>
                    </div>
                </div>
    
            </div>
    
            <!-- Botón flecha derecha -->
            <button class="flecha derecha">
                <img src="Recursos/FDerecha.svg" alt="Flecha Derecha">
            </button>
        </div>
    </section>

    
    <!----------------------------------------------------------------------------------->
    <section class="bloque-tienda">
        <!-- Contenido del título -->
        <div class="div-contenido-tienda">
            <h2>Tienda Santa Cruz</h2>
        </div>
    
        <!-- Barra de tarjetas en movimiento -->
        <div class="div-slider-tienda">
            <div class="slider-tienda-contenedor">
                <!-- Tarjeta 1 -->
                <div class="tarjeta-tienda">
                    <div class="tarjeta-imagen-tienda">
                        <img src="Recursos/Gatotienda.svg" alt="Alimentos balanceados">
                    </div>
                    <div class="tarjeta-texto-tienda">
                        <div class="titulo-tarjeta">
                            <h3>Alimentos balanceados</h3>
                        </div>
                        <div class="subtitulo-tarjeta">
                            <p>Croquetas y alimentos húmedos para cachorros, adultos y mascotas con necesidades especiales (digestivas, renales, obesidad, etc.).</p>
                        </div>
                    </div>
                </div>
    
                <!-- Tarjeta 2 -->
                <div class="tarjeta-tienda">
                    <div class="tarjeta-imagen-tienda">
                        <img src="Recursos/Suplementos.svg" alt="Suplementos y vitaminas">
                    </div>
                    <div class="tarjeta-texto-tienda">
                        <div class="titulo-tarjeta">
                            <h3>Suplementos y vitaminas</h3>
                        </div>
                        <div class="subtitulo-tarjeta">
                            <p>Apoyo nutricional para fortalecer articulaciones, sistema inmune, piel, pelaje y más.</p>
                        </div>
                    </div>
                </div>
    
                <!-- Tarjeta 3 -->
                <div class="tarjeta-tienda">
                    <div class="tarjeta-imagen-tienda">
                        <img src="Recursos/Desparacitacion.svg" alt="Antipulgas y desparasitantes">
                    </div>
                    <div class="tarjeta-texto-tienda">
                        <div class="titulo-tarjeta">
                            <h3>Antipulgas y desparasitantes</h3>
                        </div>
                        <div class="subtitulo-tarjeta">
                            <p>Pipetas, collares, comprimidos y jarabes para prevenir y tratar pulgas, garrapatas y parásitos internos.</p>
                        </div>
                    </div>
                </div>
    
                <!-- Tarjeta 4 -->
                <div class="tarjeta-tienda">
                    <div class="tarjeta-imagen-tienda">
                        <img src="Recursos/Higiene.svg" alt="Productos de higiene">
                    </div>
                    <div class="tarjeta-texto-tienda">
                        <div class="titulo-tarjeta">
                            <h3>Productos de higiene</h3>
                        </div>
                        <div class="subtitulo-tarjeta">
                            <p>Shampoos medicados, jabones dermatológicos, colonias y talcos para el cuidado diario.</p>
                        </div>
                    </div>
                </div>
    
                <!-- Tarjeta 5 -->
                <div class="tarjeta-tienda">
                    <div class="tarjeta-imagen-tienda">
                        <img src="Recursos/ImgJuguetes 1.svg" alt="Juguetes y enriquecimiento">
                    </div>
                    <div class="tarjeta-texto-tienda">
                        <div class="titulo-tarjeta">
                            <h3>Juguetes y enriquecimiento</h3>
                        </div>
                        <div class="subtitulo-tarjeta">
                            <p>Pelotas, mordedores y juegos interactivos para estimular física y mentalmente a tu mascota.</p>
                        </div>
                    </div>
                </div>
    
                <!-- Tarjeta 6 -->
                <div class="tarjeta-tienda">
                    <div class="tarjeta-imagen-tienda">
                        <img src="Recursos/ImgCollar 1.svg" alt="Collares, correas y arneses">
                    </div>
                    <div class="tarjeta-texto-tienda">
                        <div class="titulo-tarjeta">
                            <h3>Collares, correas y arneses</h3>
                        </div>
                        <div class="subtitulo-tarjeta">
                            <p>Variedad de estilos y tamaños para paseos cómodos y seguros.</p>
                        </div>
                    </div>
                </div>
    
            </div>
        </div>
    </section>


    <!----------------------------------------------------------------------------------->

    <footer class="footer">
        <div class="footer-contenido">
            <h2>Veterinaria Santa Cruz</h2>
            <p>
                Tu mascota merece lo mejor. Escríbenos o visítanos para agendar una consulta, resolver tus dudas o conocer más sobre nuestros servicios y productos.
            </p>
            <p>
                Estamos aquí para cuidar a tu mejor amigo con cariño y profesionalismo.
            </p>
    
            <div class="footer-social">
                <a href="#"><img src="Recursos/Tiktok.svg" alt="TikTok"></a>
                <a href="#"><img src="Recursos/WhatsApp.svg" alt="WhatsApp"></a>
                <a href="#"><img src="Recursos/Facebook.svg" alt="Facebook"></a>
            </div>
    
            <div class="footer-copy">
                <p>©Grupo 3 / SolucionesWeb</p>
            </div>
        </div>
    </footer>
    <script src="Js/Index.js"></script>
    <script src="Js/ModoNocheIndex.js"></script>
</body>
</html>
