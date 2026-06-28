package com.meta.roomcast.data

/**
 * Catálogo estático de muebles para RoomCast Fase 1.
 * En Fase 2 esto se reemplazará con una API REST por tienda (White Label).
 *
 * Los modelos .glb son los mismos que incluye el MrukSample de Meta.
 */
object FurnitureCatalog {

  val items: List<FurnitureItem> = listOf(

    // ── Asientos ──────────────────────────────────────────────────────────
    FurnitureItem(
        id = "couch_01",
        name = "Sofá de 3 plazas",
        brand = "ModernHome",
        price = 12500.0,
        category = FurnitureCategory.SEATING,
        glbAsset = "furniture/Couch.glb",
        widthM = 2.1f,
        depthM = 0.9f,
        heightM = 0.85f,
        description = "Sofá moderno tapizado en tela, perfecto para sala de estar.",
        purchaseUrl = "https://www.example.com/sofa-3-plazas"
    ),
    FurnitureItem(
        id = "chair_01",
        name = "Silla de escritorio",
        brand = "ErgoWork",
        price = 3800.0,
        category = FurnitureCategory.SEATING,
        glbAsset = "furniture/Chair.glb",
        widthM = 0.6f,
        depthM = 0.6f,
        heightM = 1.1f,
        description = "Silla ergonómica con soporte lumbar ajustable.",
        purchaseUrl = "https://www.example.com/silla-escritorio"
    ),
    FurnitureItem(
        id = "stool_01",
        name = "Taburete alto",
        brand = "ModernHome",
        price = 1200.0,
        category = FurnitureCategory.SEATING,
        glbAsset = "furniture/Stool.glb",
        widthM = 0.4f,
        depthM = 0.4f,
        heightM = 0.75f,
        description = "Taburete minimalista ideal para barra de cocina.",
        purchaseUrl = "https://www.example.com/taburete"
    ),

    // ── Mesas ─────────────────────────────────────────────────────────────
    FurnitureItem(
        id = "table_01",
        name = "Mesa de comedor",
        brand = "WoodCraft",
        price = 8900.0,
        category = FurnitureCategory.TABLES,
        glbAsset = "furniture/Table.glb",
        widthM = 1.6f,
        depthM = 0.9f,
        heightM = 0.75f,
        description = "Mesa de madera natural para 6 personas.",
        purchaseUrl = "https://www.example.com/mesa-comedor"
    ),

    // ── Camas ─────────────────────────────────────────────────────────────
    FurnitureItem(
        id = "bed_twin",
        name = "Cama individual",
        brand = "DreamRest",
        price = 6500.0,
        category = FurnitureCategory.BEDS,
        glbAsset = "furniture/TwinBed.glb",
        widthM = 1.0f,
        depthM = 2.0f,
        heightM = 0.5f,
        description = "Cama individual con cabecera acolchada.",
        purchaseUrl = "https://www.example.com/cama-individual"
    ),

    // ── Almacenamiento ────────────────────────────────────────────────────
    FurnitureItem(
        id = "shelf_01",
        name = "Estantería",
        brand = "ModernHome",
        price = 3200.0,
        category = FurnitureCategory.STORAGE,
        glbAsset = "furniture/Shelf.glb",
        widthM = 0.8f,
        depthM = 0.3f,
        heightM = 1.8f,
        description = "Estantería de 5 niveles ideal para libros y decoración.",
        purchaseUrl = "https://www.example.com/estanteria"
    ),
    FurnitureItem(
        id = "cabinet_01",
        name = "Gabinete",
        brand = "ModernHome",
        price = 4500.0,
        category = FurnitureCategory.STORAGE,
        glbAsset = "furniture/Cabinet.glb",
        widthM = 0.9f,
        depthM = 0.45f,
        heightM = 1.2f,
        description = "Gabinete con puertas y cajones de madera.",
        purchaseUrl = "https://www.example.com/gabinete"
    ),
    FurnitureItem(
        id = "storage_01",
        name = "Baúl organizador",
        brand = "OrganizeIt",
        price = 2100.0,
        category = FurnitureCategory.STORAGE,
        glbAsset = "furniture/Storage.glb",
        widthM = 1.1f,
        depthM = 0.5f,
        heightM = 0.55f,
        description = "Baúl multifuncional que también sirve como mesa de centro.",
        purchaseUrl = "https://www.example.com/baul"
    ),

    // ── Iluminación ───────────────────────────────────────────────────────
    FurnitureItem(
        id = "lamp_floor",
        name = "Lámpara de piso",
        brand = "LightUp",
        price = 1800.0,
        category = FurnitureCategory.LIGHTING,
        glbAsset = "furniture/Lamp.glb",
        widthM = 0.35f,
        depthM = 0.35f,
        heightM = 1.65f,
        description = "Lámpara de piso con luz cálida, ideal para rincón de lectura.",
        purchaseUrl = "https://www.example.com/lampara-piso"
    ),
    FurnitureItem(
        id = "lamp_ceiling",
        name = "Lámpara de techo",
        brand = "LightUp",
        price = 2400.0,
        category = FurnitureCategory.LIGHTING,
        glbAsset = "furniture/CeilingLamp.glb",
        widthM = 0.5f,
        depthM = 0.5f,
        heightM = 0.3f,
        description = "Lámpara colgante de diseño escandinavo.",
        purchaseUrl = "https://www.example.com/lampara-techo"
    ),
    FurnitureItem(
        id = "lamp_round",
        name = "Lámpara redonda",
        brand = "LightUp",
        price = 1950.0,
        category = FurnitureCategory.LIGHTING,
        glbAsset = "furniture/RoundLamp.glb",
        widthM = 0.4f,
        depthM = 0.4f,
        heightM = 1.4f,
        description = "Lámpara de piso con pantalla redonda de tela.",
        purchaseUrl = "https://www.example.com/lampara-redonda"
    ),

    // ── Decoración ────────────────────────────────────────────────────────
    FurnitureItem(
        id = "plant_01",
        name = "Planta de interior",
        brand = "GreenSpace",
        price = 650.0,
        category = FurnitureCategory.DECOR,
        glbAsset = "furniture/Plant1.glb",
        widthM = 0.4f,
        depthM = 0.4f,
        heightM = 0.6f,
        description = "Planta decorativa de bajo mantenimiento.",
        purchaseUrl = "https://www.example.com/planta-interior"
    ),
    FurnitureItem(
        id = "plant_02",
        name = "Planta grande",
        brand = "GreenSpace",
        price = 1200.0,
        category = FurnitureCategory.DECOR,
        glbAsset = "furniture/Plant2.glb",
        widthM = 0.6f,
        depthM = 0.6f,
        heightM = 1.4f,
        description = "Palmera de interior, perfecta para espacios amplios.",
        purchaseUrl = "https://www.example.com/planta-grande"
    ),
    FurnitureItem(
        id = "plant_03",
        name = "Planta colgante",
        brand = "GreenSpace",
        price = 480.0,
        category = FurnitureCategory.DECOR,
        glbAsset = "furniture/Plant3.glb",
        widthM = 0.3f,
        depthM = 0.3f,
        heightM = 0.5f,
        description = "Planta colgante con maceta de cerámica blanca.",
        purchaseUrl = "https://www.example.com/planta-colgante"
    ),
    FurnitureItem(
        id = "plant_04",
        name = "Suculenta decorativa",
        brand = "GreenSpace",
        price = 320.0,
        category = FurnitureCategory.DECOR,
        glbAsset = "furniture/Plant4.glb",
        widthM = 0.2f,
        depthM = 0.2f,
        heightM = 0.25f,
        description = "Suculenta en maceta artesanal, perfecta para escritorio.",
        purchaseUrl = "https://www.example.com/suculenta"
    ),
    FurnitureItem(
        id = "wall_art",
        name = "Arte para pared",
        brand = "ArtDeco",
        price = 1500.0,
        category = FurnitureCategory.DECOR,
        glbAsset = "furniture/WallArt.glb",
        widthM = 0.8f,
        depthM = 0.05f,
        heightM = 0.6f,
        description = "Cuadro decorativo de arte abstracto, marcos de madera natural.",
        purchaseUrl = "https://www.example.com/arte-pared"
    ),
    FurnitureItem(
        id = "screen_01",
        name = "Monitor de escritorio",
        brand = "TechDesk",
        price = 5500.0,
        category = FurnitureCategory.DECOR,
        glbAsset = "furniture/ComputerScreen.glb",
        widthM = 0.62f,
        depthM = 0.2f,
        heightM = 0.45f,
        description = "Monitor 27\" para home office.",
        purchaseUrl = "https://www.example.com/monitor"
    ),
    // ── 3 Nuevos Productos Escaneados (Marketplace) ───────────────────────
    FurnitureItem(
        id = "scanned_box_01",
        name = "Caja de Cartón Retro",
        brand = "Escaneo Usuario (Juan P.)",
        price = 45.0,
        category = FurnitureCategory.DECOR,
        glbAsset = "furniture/BoxCardBoard.glb",
        widthM = 0.4f,
        depthM = 0.4f,
        heightM = 0.4f,
        description = "Caja de cartón rústica escaneada en 3D para decoración de estilo industrial.",
        purchaseUrl = "https://www.example.com/caja-retro"
    ),
    FurnitureItem(
        id = "scanned_window_01",
        name = "Ventana Colonial Escaneada",
        brand = "Antigüedades CDMX",
        price = 4200.0,
        category = FurnitureCategory.DECOR,
        glbAsset = "furniture/Window.glb",
        widthM = 1.0f,
        depthM = 0.15f,
        heightM = 1.2f,
        description = "Ventana de madera antigua escaneada directamente de una casona colonial.",
        purchaseUrl = "https://www.example.com/ventana-colonial"
    ),
    FurnitureItem(
        id = "scanned_door_01",
        name = "Puerta Rústica Tallada",
        brand = "Maderas del Bosque",
        price = 8500.0,
        category = FurnitureCategory.DECOR,
        glbAsset = "furniture/Door.glb",
        widthM = 0.9f,
        depthM = 0.1f,
        heightM = 2.0f,
        description = "Puerta rústica de entrada con herrajes de hierro, escaneada en 3D para previsualización.",
        purchaseUrl = "https://www.example.com/puerta-rustica"
    ),
  )

  /** Filtrar por categoría, o devuelve todos si es ALL */
  fun byCategory(category: FurnitureCategory): List<FurnitureItem> {
    return if (category == FurnitureCategory.ALL) items
    else items.filter { it.category == category }
  }

  /** Buscar por nombre */
  fun search(query: String): List<FurnitureItem> {
    val q = query.lowercase().trim()
    return if (q.isEmpty()) items
    else items.filter { it.name.lowercase().contains(q) || it.brand.lowercase().contains(q) }
  }
}
