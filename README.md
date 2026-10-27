# Mis Gastos – App móvil de control de gastos personales

Proyecto de la **Evaluación Parcial 2 – DSY1105 Desarrollo de Aplicaciones Móviles (DuocUC)**.

**Integrantes:** _(escribir nombres de los dos estudiantes)_
**Equipo N°:** _(completar)_

## Descripción
Aplicación Android para registrar y revisar gastos personales. Cada usuario crea una cuenta, inicia sesión y administra sus propios gastos, que quedan guardados en el teléfono.

## Funcionalidades
- Registro e inicio de sesión con validación por campo (ícono de alerta, borde rojo y mensaje animado).
- CRUD de gastos (crear, listar, editar, eliminar) con total acumulado.
- Persistencia local con **Room** (usuarios y gastos).
- Arquitectura **MVVM**: `data/` (Room), `viewmodel/` (estado y lógica), `ui/` (pantallas Compose), `util/` (validaciones y recursos nativos).
- Validaciones centralizadas en `util/Validador.kt`, desacopladas de la interfaz.
- Animaciones: transiciones entre pantallas, mensajes de error, botón con escala al presionar, total animado, tarjetas que se expanden.
- Recursos nativos: **cámara** (foto del comprobante) y **ubicación GPS** (dónde se hizo el gasto), con solicitud de permisos en tiempo de ejecución.

## Cómo ejecutar
1. Instalar Android Studio (Koala o superior) con JDK 17.
2. `File > Open` y seleccionar la carpeta del proyecto; esperar el Gradle Sync.
3. Ejecutar en un emulador o dispositivo con API 24+. En el emulador, configurar la cámara y una ubicación en *Extended controls*.
4. Registrar un usuario y comenzar a agregar gastos.

## Tecnologías
Kotlin · Jetpack Compose · Material 3 · Navigation Compose · Room · Coil · Google Play Services Location
