package com.meta.roomcast.data

/**
 * Categorías de muebles disponibles en el catálogo.
 */
enum class FurnitureCategory(val displayName: String, val emoji: String) {
  ALL("Todos", "🏠"),
  SEATING("Asientos", "🛋️"),
  TABLES("Mesas", "🪑"),
  BEDS("Camas", "🛏️"),
  STORAGE("Almacenamiento", "🗄️"),
  LIGHTING("Iluminación", "💡"),
  DECOR("Decoración", "🌿"),
}

/**
 * Representa un mueble del catálogo.
 *
 * @param id          Identificador único del mueble
 * @param name        Nombre del mueble en español
 * @param brand       Marca o tienda (para B2B White Label)
 * @param price       Precio en MXN
 * @param category    Categoría para filtrado en el catálogo
 * @param glbAsset    Ruta relativa al .glb dentro de assets/
 * @param widthM      Ancho en metros (para validación de espacio)
 * @param depthM      Profundidad en metros
 * @param heightM     Alto en metros
 * @param description Descripción breve del producto
 * @param purchaseUrl URL de la tienda online para comprar
 */
data class FurnitureItem(
    val id: String,
    val name: String,
    val brand: String = "RoomCast",
    val price: Double,
    val category: FurnitureCategory,
    val glbAsset: String,
    val widthM: Float,
    val depthM: Float,
    val heightM: Float,
    val description: String = "",
    val purchaseUrl: String = "",
)
