# Guía Completa de Ejemplos — Meta Spatial SDK

Esta guía técnica describe al detalle cada uno de los proyectos de ejemplo y casos de estudio (*Showcases*) incluidos en este repositorio de muestras de **Meta Spatial SDK** para Meta Horizon OS.

Su objetivo es servir de referencia para comprender la arquitectura ECS (Entity Component System) de Meta y encontrar rápidamente las plantillas de código fuente adecuadas para cada funcionalidad.

---

## 🛠️ Conceptos Clave de Arquitectura

Antes de explorar los ejemplos, es fundamental entender los tres pilares de Meta Spatial SDK:

1. **ECS (Entity Component System)**:
   - **Entity**: Un identificador único (no contiene datos ni lógica).
   - **Component**: Clases de datos puras que describen propiedades (ej. `Transform`, `Mesh`, `Grabbable`, `Physics`). Se definen en Kotlin o a través de archivos XML de esquema (`.xml`).
   - **System / Activity**: La lógica de negocio que consulta las entidades con ciertos componentes y las actualiza en cada frame (`onSceneTick`).

2. **Paneles Espaciales**:
   - Elementos de UI 2D renderizados en el espacio 3D. Se construyen utilizando **Jetpack Compose** o layouts XML tradicionales de Android y se registran usando `ComposeViewPanelRegistration` en la actividad.

3. **Mixed Reality Utility Kit (MRUK)**:
   - Librería para interactuar con la habitación real escaneada. Permite buscar el piso (`MRUKLabel.FLOOR`), paredes, techos, y obtener sus dimensiones físicas y colisionadores.

---

## 📁 Ejemplos Base (Directorio Raíz)

### 1. `RoomCast` (Nuestro Proyecto)
* **Descripción**: Aplicación B2B *White Label* orientada al retail y previsualización de muebles en Realidad Mixta.
* **Características Clave**:
  - Escaneo de habitación en tiempo real con MRUK (con fallback JSON local si el dispositivo no tiene escena).
  - Interfaz de usuario unificada en Compose (*Dashboard de gran formato*) con estilo Glassmorphism off-white premium.
  - Colisión física exacta mediante el componente `Box` adaptado a las dimensiones reales de cada mueble.
  - Spawneo inteligente a 1.0m del usuario y manipulación estable con `GrabbableType.PIVOT_Y` (bloqueo en el eje vertical).
  - Rotación asistida interactiva por botones de UI en 360 grados.
* **Archivos Clave**:
  - [RoomCastActivity.kt](file:///Users/gerryvela/Documents/Meta-Apps/Meta-Spatial-SDK-Samples/RoomCast/app/src/main/java/com/meta/roomcast/RoomCastActivity.kt): Ciclo de vida de la escena, lógica de spawneo, validación física del piso y callbacks de rotación.
  - [CatalogPanel.kt](file:///Users/gerryvela/Documents/Meta-Apps/Meta-Spatial-SDK-Samples/RoomCast/app/src/main/java/com/meta/roomcast/ui/CatalogPanel.kt): Interfaz completa en Compose (Sidebar de navegación, rejilla de productos, showcase y botones de giro).
  - [PlacedFurniture.xml](file:///Users/gerryvela/Documents/Meta-Apps/Meta-Spatial-SDK-Samples/RoomCast/app/src/main/components/PlacedFurniture.xml): Esquema del componente ECS personalizado.

### 2. `DevSpatialWorkstation` (Nuestro Proyecto)
* **Descripción**: Entorno de desarrollo inmersivo que simula una estación de trabajo espacial multitarea con paneles flotantes curvos interactivos.
* **Características Clave**:
  - Panel editor de código fuente en Compose.
  - Terminal virtual y visor web embebido.
  - Selector de Skybox dinámico para alternar entornos inmersivos (ej. Neon Office, Cyber Cafe).
* **Archivos Clave**:
  - [WorkstationActivity.kt](file:///Users/gerryvela/Documents/Meta-Apps/Meta-Spatial-SDK-Samples/DevSpatialWorkstation/app/src/main/java/com/meta/spatial/samples/devworkstation/WorkstationActivity.kt): Carga de escena GLXF, inicialización del Dock y cambio de Skybox 3D.

### 3. `MrukSample`
* **Descripción**: El ejemplo primario para integrar escaneo del mundo real.
* **Características Clave**:
  - Inicialización y escucha de eventos de MRUK (`MRUKFeature`).
  - Generación de mallas procedimentales de colisión física (`AnchorProceduralMesh`) para que los objetos virtuales interactúen con el suelo, las mesas o las paredes físicas.
  - Carga automática de habitaciones de prueba locales en JSON si el visor no cuenta con un escaneo guardado.
* **Archivos Clave**:
  - `MrukAnchorMeshSampleActivity.kt`: Demuestra la configuración de colisionadores físicos automáticos en las etiquetas `FLOOR`, `CEILING` y `WALL_FACE`.

### 4. `Object3DSampleIsdk`
* **Descripción**: Demostración avanzada de colocación de objetos 3D y manipulación utilizando el **Interaction SDK (ISDK)** de Meta.
* **Características Clave**:
  - Hover affordance: El objeto aumenta ligeramente de tamaño (escala) al ser apuntado por el rayo o la mano del usuario.
  - Gestión avanzada de agarres directos e indirectos.
  - Integración de físicas de colisión con gravedad y masa.
* **Archivos Clave**:
  - `Object3DSampleIsdkActivity.kt`: Configura el `IsdkInputListenerSystem` para detectar cuándo se agarra, apunta o suelta un objeto en el espacio.
  - `PanelLayout.kt`: Spawneo y escala dinámica de las entidades usando animadores de valor (`ValueAnimator`).

### 5. `Object3DSample`
* **Descripción**: Introducción básica a la carga de modelos 3D y edición de propiedades de escena a través de herramientas visuales.
* **Características Clave**:
  - Importación y posicionamiento de mallas en formato `.glb`.
  - Integración básica del componente `Grabbable` por defecto.
* **Archivos Clave**:
  - `Object3DSampleActivity.kt`: Ejemplo mínimo de inicialización de la escena 3D y carga de entornos de iluminación (`IBLEnvironment`).

### 6. `PhysicsSample`
* **Descripción**: Muestra el funcionamiento del motor de físicas físicas en tiempo real (Bullet Physics) integrado en el SDK.
* **Características Clave**:
  - Gravedad, rebotes, masa, densidad y fricción.
  - Interacciones físicas entre múltiples objetos (choques y apilamiento).
  - Soporte para visualización de líneas de debug físicas en el espacio 3D.
* **Archivos Clave**:
  - `PhysicsSampleActivity.kt`: Asignación de componentes `Physics` a entidades y manipulación dinámica de estados (`DYNAMIC` vs `KINEMATIC`).

### 7. `UISetSample`
* **Descripción**: Demostración del catálogo visual y las directrices estéticas de Meta para interfaces de usuario en Horizon OS.
* **Características Clave**:
  - Aplicación de `SpatialTheme` y `LocalColorScheme` para que Compose herede la tipografía y bordes correctos del sistema.
  - Estructuración de menús espaciales flotantes, botones, sliders y listas de selección consistentes.
* **Archivos Clave**:
  - `UiSetSampleActivity.kt` y `UiSetLayout.kt`: Creación de paneles con temas oscuros/claros de Horizon OS.

### 8. `AnimationsSample`
* **Descripción**: Control y reproducción de animaciones esqueléticas y de transformación.
* **Características Clave**:
  - Carga de clips de animación embebidos en archivos GLB.
  - Control de reproducción (Play, Pause, Loop, Stop) mediante el componente `Animated`.
  - Animación procedural cuadro a cuadro en `onSceneTick`.
* **Archivos Clave**:
  - `AnimationsSampleActivity.kt` y `DroneSystem.kt`: Simulación de un dron flotante que sigue al usuario y reproduce clips de vuelo.

### 9. `HybridSample`
* **Descripción**: Transición fluida entre vistas tradicionales de Android 2D y experiencias de realidad mixta espaciales.
* **Características Clave**:
  - Cómo iniciar una aplicación en modo plano clásico de pantalla (2D) y "romper" los bordes para transformarla en una experiencia inmersiva 3D a petición del usuario.
* **Archivos Clave**:
  - `HybridSampleActivity.kt`: Ciclo de vida y cambio de modo de renderizado de la actividad.

### 10. `CustomComponentsSample`
* **Descripción**: Demostración técnica de cómo extender el motor ECS del Spatial SDK con componentes personalizados de datos.
* **Características Clave**:
  - Creación de archivos de definición XML de componentes en la carpeta `main/components/`.
  - Generación de código automática por el plugin de Gradle para los nuevos tipos de componentes.
  - Lógica de sistemas (`System`) que operan sobre estos componentes personalizados en cada frame.
* **Archivos Clave**:
  - `LookAtComponent.xml` y `LookAtSystem.kt`: Un sistema que obliga a una entidad (como un ojo o cara) a girar y apuntar siempre hacia la cámara del visor del usuario.

### 11. `MediaPlayerSample` y `PremiumMediaSample`
* **Descripción**: Reproductores multimedia espaciales avanzados.
* **Características Clave**:
  - Streaming de video inmersivo de 180° y 360°.
  - Soporte de protección de derechos digitales (DRM) mediante Widevine.
  - Proyección de reflejos de luz dinámicos emitidos por el panel de video sobre las paredes y piso físicos detectados por MRUK.
* **Archivos Clave**:
  - `MediaPlayerSampleActivity.kt` y `PremiumMediaSampleActivity.kt`: Integración de Exoplayer con texturas de renderizado en malla 3D.

### 12. `SpatialVideoSample`
* **Descripción**: Muestra la reproducción de archivos de video multicanal con audio espacializado (3D Audio).
* **Características Clave**:
  - Modulación del sonido basado en la distancia y orientación de la cabeza del usuario con respecto al televisor virtual.
* **Archivos Clave**:
  - `SpatialVideoSampleActivity.kt`: Configuración de fuentes de audio posicional y atenuación sonora.

### 13. `BodyTrackingSample`
* **Descripción**: Acceso a los datos del esqueleto y articulaciones del cuerpo del usuario.
* **Características Clave**:
  - Lectura de la posición de hombros, codos, muñecas y dedos en tiempo real.
  - Sincronización de un avatar virtual o herramientas inmersivas con los movimientos físicos del cuerpo.

---

## 🚀 Showcases (Aplicaciones Completas en `/Showcases`)

Estas son aplicaciones reales publicadas en la tienda oficial de Meta que sirven como ejemplos de producción:

### 1. `Showcases/focus`
* **Propósito**: Aplicación de productividad multitarea.
* **Por qué estudiarla**: Excelente para comprender cómo gestionar múltiples ventanas flotantes alineadas entre sí (Docking), base de datos de tareas persistentes en SQLite y sincronización de estados complejos.

### 2. `Showcases/media_view`
* **Propósito**: Galería de fotos y videos espaciales.
* **Por qué estudiarla**: Muestra animaciones fluidas de apertura de paneles de imágenes y el uso de gestos inmersivos avanzados para escalar y rotar contenido fotográfico 2D flotando en tu habitación.

### 3. `Showcases/geo_voyage`
* **Propósito**: Globo terráqueo virtual e interactivo en 3D.
* **Por qué estudiarla**: Implementación premium de interacción de escala global. Enseña a crear una esfera interactiva que gira con las manos físicas, carga mapas dinámicos y reacciona de forma táctil.

### 4. `Showcases/meta_spatial_scanner`
* **Propósito**: Escáner 3D del entorno del usuario.
* **Por qué estudiarla**: Muestra la integración más avanzada posible de reconstrucción de mallas espaciales y manipulación de geometría compleja escaneada en tiempo real.
