package com.meta.roomcast.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.roomcast.data.FurnitureCatalog
import com.meta.roomcast.data.FurnitureCategory
import com.meta.roomcast.data.FurnitureItem
import com.meta.roomcast.ui.theme.*

/**
 * CatalogPanel — Unified Dashboard UI matching the user's premium screenshot.
 *
 * Layout:
 *  - Left Sidebar: Navigation tabs (Browse, Saved, Categories)
 *  - Center/Left: Large selected item showcase with space validation and "Place in Room" button.
 *  - Center/Right: Category filter + 2-column scrollable grid of items.
 */
@Composable
fun CatalogPanel(
    selectedItem: FurnitureItem?,
    fitStatus: FitStatusUi,
    isPlaced: Boolean,
    onItemSelected: (FurnitureItem) -> Unit,
    onPlaceInRoom: (FurnitureItem) -> Unit,
    onRemoveFromRoom: () -> Unit,
    onRotate: (Float) -> Unit
) {
  RoomCastTheme {
    var activeTab by remember { mutableStateOf("Browse") }
    var selectedCategory by remember { mutableStateOf(FurnitureCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    val displayItems by remember(selectedCategory, searchQuery) {
      derivedStateOf {
        val catItems = FurnitureCatalog.byCategory(selectedCategory)
        if (searchQuery.isEmpty()) catItems
        else catItems.filter { it.name.contains(searchQuery, ignoreCase = true) }
      }
    }

    // Modern light glassmorphism palette for a clean store feel
    val dashboardBg = Color(0xF2F4F6F9) // translucent off-white
    val sidebarBg = Color(0xFFE9EDF0)
    val cardBg = Color.White
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF64748B)
    val accentBlue = Color(0xFF0F62FE)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dashboardBg, RoundedCornerShape(24.dp))
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(24.dp))
            .shadow(16.dp, RoundedCornerShape(24.dp))
    ) {
      Row(modifier = Modifier.fillMaxSize()) {
        
        // ── 1. LEFT SIDEBAR (Navigation) ──────────────────────────────────────
        Column(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight()
                .background(sidebarBg)
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
          // App Logo
          Box(
              modifier = Modifier
                  .size(48.dp)
                  .background(accentBlue, CircleShape),
              contentAlignment = Alignment.Center
          ) {
            Text("RC", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Navigation buttons
          SidebarButton(
              icon = Icons.Default.Home,
              label = "Explorar",
              isSelected = activeTab == "Browse",
              onClick = { activeTab = "Browse" }
          )
          SidebarButton(
              icon = Icons.Default.FavoriteBorder,
              label = "Favoritos",
              isSelected = activeTab == "Saved",
              onClick = { activeTab = "Saved" }
          )
          SidebarButton(
              icon = Icons.Default.List,
              label = "Categorías",
              isSelected = activeTab == "Categories",
              onClick = { activeTab = "Categories" }
          )
        }

        // Divider
        Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color(0xFFE2E8F0)))

        // ── MAIN CONTENT AREA ─────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
          
          // ── Header ──────────────────────────────────────────────────────────
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                  text = "Horizon Object Market",
                  fontSize = 26.sp,
                  fontWeight = FontWeight.Bold,
                  color = textPrimary
              )
              Text(
                  text = "Encuentra y visualiza muebles en tu espacio real",
                  fontSize = 13.sp,
                  color = textSecondary
              )
            }

            // Top-right controls: Search, Notification, Profile
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
              // Search input
              TextField(
                  value = searchQuery,
                  onValueChange = { searchQuery = it },
                  placeholder = { Text("Buscar...", fontSize = 12.sp) },
                  leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                  colors = TextFieldDefaults.colors(
                      focusedContainerColor = Color.White,
                      unfocusedContainerColor = Color.White,
                      disabledContainerColor = Color.White,
                      focusedIndicatorColor = Color.Transparent,
                      unfocusedIndicatorColor = Color.Transparent
                  ),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                      .width(200.dp)
                      .height(44.dp)
              )

              IconButton(onClick = {}) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = textPrimary)
              }
              // Profile pic placeholder
              Box(
                  modifier = Modifier
                      .size(36.dp)
                      .background(Color(0xFFCBD5E1), CircleShape),
                  contentAlignment = Alignment.Center
              ) {
                Text("👤", fontSize = 18.sp)
              }
            }
          }

          // ── Body Grid & Showcase ────────────────────────────────────────────
          Row(
              modifier = Modifier
                  .fillMaxWidth()
                  .weight(1f),
              horizontalArrangement = Arrangement.spacedBy(24.dp)
          ) {
            
            // Left column: Showcase Detail Card
            Box(
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxHeight()
                    .background(cardBg, RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
              AnimatedContent(
                  targetState = selectedItem,
                  transitionSpec = { fadeIn() togetherWith fadeOut() },
                  label = "showcaseAnim"
              ) { item ->
                if (item == null) {
                  Column(
                      modifier = Modifier.fillMaxSize(),
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement = Arrangement.Center
                  ) {
                    Text("🛋️", fontSize = 64.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Selecciona un mueble",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        "Verás las medidas y podrás colocarlo a escala real",
                        fontSize = 12.sp,
                        color = textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                  }
                } else {
                  Column(
                      modifier = Modifier.fillMaxSize(),
                      verticalArrangement = Arrangement.SpaceBetween
                  ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                      // Large Emoji / Visual simulation card
                      Box(
                          modifier = Modifier
                              .fillMaxWidth()
                              .height(180.dp)
                              .background(
                                  Brush.radialGradient(
                                      colors = listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))
                                  ),
                                  RoundedCornerShape(12.dp)
                              ),
                          contentAlignment = Alignment.Center
                      ) {
                        Text(item.category.emoji, fontSize = 90.sp)
                      }

                      // Title & Brand
                      Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                      ) {
                        Column(modifier = Modifier.weight(1f)) {
                          Text(item.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                          Text(item.brand, fontSize = 12.sp, color = textSecondary)
                        }
                        Text(
                            "$${"%,.0f".format(item.price)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentBlue
                        )
                      }

                      // Description
                      Text(
                          item.description,
                          fontSize = 12.sp,
                          color = textSecondary,
                          maxLines = 3,
                          overflow = TextOverflow.Ellipsis
                      )

                      // Dimensions Row
                      Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        DimensionLabel("Ancho", "${item.widthM}m")
                        DimensionLabel("Profundidad", "${item.depthM}m")
                        DimensionLabel("Alto", "${item.heightM}m")
                      }

                      // Fit status check
                      FitStatusIndicator(fitStatus)

                      // Rotation Controls (Only visible if the item is currently placed in the room)
                      if (isPlaced) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                          Text(
                              text = "Girar Mueble (360°)",
                              fontSize = 12.sp,
                              fontWeight = FontWeight.Bold,
                              color = textPrimary
                          )
                          Row(
                              modifier = Modifier.fillMaxWidth(),
                              horizontalArrangement = Arrangement.spacedBy(10.dp),
                              verticalAlignment = Alignment.CenterVertically
                          ) {
                            Button(
                                onClick = { onRotate(-15f) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                              Text("↩ Izquierda -15°", color = textPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { onRotate(15f) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                              Text("Derecha +15° ↪", color = textPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                          }
                        }
                      }
                    }

                    // Showcase action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                      Button(
                          onClick = { onPlaceInRoom(item) },
                          colors = ButtonDefaults.buttonColors(containerColor = accentBlue),
                          shape = RoundedCornerShape(12.dp),
                          modifier = Modifier
                              .weight(1f)
                              .height(48.dp)
                      ) {
                        Text("Place in Room", fontWeight = FontWeight.SemiBold, color = Color.White)
                      }

                      Button(
                          onClick = onRemoveFromRoom,
                          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                          shape = RoundedCornerShape(12.dp),
                          modifier = Modifier.height(48.dp)
                      ) {
                        Text("Quitar", color = Color.White)
                      }

                      IconButton(
                          onClick = {},
                          modifier = Modifier
                              .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                              .size(48.dp)
                      ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = textPrimary)
                      }
                    }
                  }
                }
              }
            }

            // Right column: Category tabs + Item Grid
            Column(
                modifier = Modifier.weight(0.55f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              // Scrollable category chips
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                listOf(
                    FurnitureCategory.ALL,
                    FurnitureCategory.SEATING,
                    FurnitureCategory.TABLES,
                    FurnitureCategory.BEDS,
                    FurnitureCategory.STORAGE,
                    FurnitureCategory.DECOR
                ).forEach { category ->
                  CategoryTabChip(
                      category = category,
                      isSelected = category == selectedCategory,
                      onClick = { selectedCategory = category }
                  )
                }
              }

              // 2-column Vertical grid of products
              LazyVerticalGrid(
                  columns = GridCells.Fixed(2),
                  horizontalArrangement = Arrangement.spacedBy(12.dp),
                  verticalArrangement = Arrangement.spacedBy(12.dp),
                  modifier = Modifier.fillMaxSize()
              ) {
                items(displayItems, key = { it.id }) { item ->
                  CatalogGridCard(
                      item = item,
                      isSelected = item == selectedItem,
                      onClick = { onItemSelected(item) }
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

// ── SUB-COMPONENTS ────────────────────────────────────────────────────────────

@Composable
private fun SidebarButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
  val tintColor = if (isSelected) Color(0xFF0F62FE) else Color(0xFF64748B)
  val bg = if (isSelected) Color(0x1F0F62FE) else Color.Transparent

  Column(
      modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(bg)
          .clickable(onClick = onClick)
          .padding(vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
  ) {
    Icon(icon, contentDescription = null, tint = tintColor, modifier = Modifier.size(24.dp))
    Spacer(Modifier.height(4.dp))
    Text(label, fontSize = 11.sp, color = tintColor, fontWeight = FontWeight.SemiBold)
  }
}

@Composable
private fun CategoryTabChip(
    category: FurnitureCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
  val bg = if (isSelected) Color(0xFF0F62FE) else Color.White
  val text = if (isSelected) Color.White else Color(0xFF475569)

  Box(
      modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(bg)
          .border(1.dp, if (isSelected) Color.Transparent else Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
          .clickable(onClick = onClick)
          .padding(horizontal = 14.dp, vertical = 8.dp),
      contentAlignment = Alignment.Center
  ) {
    Text("${category.emoji} ${category.displayName}", color = text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
  }
}

@Composable
private fun CatalogGridCard(
    item: FurnitureItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
  val border = if (isSelected) Color(0xFF0F62FE) else Color(0xFFE2E8F0)
  val bg = if (isSelected) Color(0x0D0F62FE) else Color.White

  Column(
      modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(bg)
          .border(1.dp, border, RoundedCornerShape(12.dp))
          .clickable(onClick = onClick)
          .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // Thumbnail preview
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
      Text(item.category.emoji, fontSize = 48.sp)
    }

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(
          item.name,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF1E293B),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
      )
      Text(item.brand, fontSize = 10.sp, color = Color(0xFF64748B))
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
          "$${"%,.0f".format(item.price)}",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF0F62FE)
      )
      Text(
          "${item.widthM}m × ${item.depthM}m",
          fontSize = 9.sp,
          color = Color(0xFF94A3B8)
      )
    }
  }
}

@Composable
private fun DimensionLabel(label: String, value: String) {
  Column(
      modifier = Modifier
          .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
          .padding(horizontal = 12.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
    Text(label, fontSize = 9.sp, color = Color(0xFF64748B))
  }
}

@Composable
private fun FitStatusIndicator(status: FitStatusUi) {
  if (status == FitStatusUi.UNKNOWN) return

  val (emoji, text, color, bg) = when (status) {
    FitStatusUi.FITS -> Quadruple("✓", "Cabe perfectamente en tu espacio", Color(0xFF16A34A), Color(0xFFDCFCE7))
    FitStatusUi.DOES_NOT_FIT -> Quadruple("⚠️", "No cabe en el espacio disponible", Color(0xFFDC2626), Color(0xFFFEE2E2))
    else -> Quadruple("", "", Color.Transparent, Color.Transparent)
  }

  Row(
      modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(bg)
          .padding(8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
  ) {
    Text(emoji, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
  }
}

private data class Quadruple<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

enum class FitStatusUi { UNKNOWN, FITS, DOES_NOT_FIT }
