package com.meta.roomcast.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
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
    onRotate: (Float) -> Unit,
    onReScanRoom: () -> Unit
) {
  RoomCastTheme {
    var activeTab by remember { mutableStateOf("Browse") }
    var selectedCategory by remember { mutableStateOf(FurnitureCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var categoryQuery by remember { mutableStateOf("") }

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
              icon = Icons.Default.Refresh,
              label = "Escanear C.",
              isSelected = activeTab == "ScanRoom",
              onClick = { onReScanRoom() }
          )
          SidebarButton(
              icon = Icons.Default.Add,
              label = "Crear 3D",
              isSelected = activeTab == "ScanObject",
              onClick = { activeTab = "ScanObject" }
          )
          SidebarButton(
              icon = Icons.Default.FavoriteBorder,
              label = "Favoritos",
              isSelected = activeTab == "Saved",
              onClick = { activeTab = "Saved" }
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

              IconButton(onClick = { activeTab = "Notifications" }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = if (activeTab == "Notifications") accentBlue else textPrimary
                )
              }
              // Profile pic placeholder
              Box(
                  modifier = Modifier
                      .size(36.dp)
                      .background(if (activeTab == "Profile") accentBlue else Color(0xFFCBD5E1), CircleShape)
                      .clickable { activeTab = "Profile" },
                  contentAlignment = Alignment.Center
              ) {
                Text(
                    text = "👤",
                    fontSize = 18.sp,
                    color = if (activeTab == "Profile") Color.White else textPrimary
                )
              }
            }
          }

          // ── Body Grid & Showcase ────────────────────────────────────────────
          if (activeTab == "ScanObject") {
            ObjectScannerSimulator(onPlaceInRoom, onItemSelected)
          } else if (activeTab == "Profile") {
            ProfileView()
          } else if (activeTab == "Notifications") {
            NotificationsView()
          } else {
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
                      Text("SELECT 3D", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1), letterSpacing = 2.sp)
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
                        // Typographic gradient thumbnail instead of emoji
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8))
                                    ),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                          Text(
                              text = item.category.displayName.uppercase(),
                              fontSize = 20.sp,
                              fontWeight = FontWeight.Bold,
                              color = Color(0xFF475569),
                              letterSpacing = 2.sp
                          )
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

              // Right column: Category chips + Item Grid
              Column(
                  modifier = Modifier.weight(0.55f),
                  verticalArrangement = Arrangement.spacedBy(16.dp)
              ) {
                // Category filter bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                  TextField(
                      value = categoryQuery,
                      onValueChange = { categoryQuery = it },
                      placeholder = { Text("Filtrar categorías...", fontSize = 11.sp) },
                      colors = TextFieldDefaults.colors(
                          focusedContainerColor = Color(0xFFF1F5F9),
                          unfocusedContainerColor = Color(0xFFF1F5F9),
                          focusedIndicatorColor = Color.Transparent,
                          unfocusedIndicatorColor = Color.Transparent
                      ),
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.width(160.dp).height(38.dp)
                  )
                  
                  // Filtered Category Chips (scrollable row)
                  val categoriesToDisplay = listOf(
                      FurnitureCategory.ALL,
                      FurnitureCategory.MARKETPLACE,
                      FurnitureCategory.SEATING,
                      FurnitureCategory.TABLES,
                      FurnitureCategory.BEDS,
                      FurnitureCategory.STORAGE,
                      FurnitureCategory.DECOR,
                      FurnitureCategory.LIGHTING
                  ).filter { 
                      it.displayName.contains(categoryQuery, ignoreCase = true) 
                  }

                  Row(
                      modifier = Modifier.weight(1f),
                      horizontalArrangement = Arrangement.spacedBy(6.dp),
                      verticalAlignment = Alignment.CenterVertically
                  ) {
                    categoriesToDisplay.forEach { category ->
                      CategoryTabChip(
                          category = category,
                          isSelected = category == selectedCategory,
                          onClick = { selectedCategory = category }
                      )
                    }
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
}

// ── 3D OBJECT SCANNER SIMULATOR ───────────────────────────────────────────────

@Composable
fun ObjectScannerSimulator(
    onPlaceInRoom: (FurnitureItem) -> Unit,
    onItemSelected: (FurnitureItem) -> Unit
) {
  var step by remember { mutableStateOf(0) }
  var progress by remember { mutableStateOf(0f) }
  val accentBlue = Color(0xFF0F62FE)
  val textPrimary = Color(0xFF1E293B)
  val textSecondary = Color(0xFF64748B)

  // Auto-advance scanning progress if step == 1
  LaunchedEffect(step) {
    if (step == 1) {
      progress = 0f
      while (progress < 1f) {
        kotlinx.coroutines.delay(100)
        progress += 0.05f
      }
      step = 2 // Move to processing
    }
    if (step == 2) {
      kotlinx.coroutines.delay(3000) // 3 seconds cloud processing delay
      step = 3 // Done!
    }
  }

  Column(
      modifier = Modifier
          .fillMaxSize()
          .background(Color.White, RoundedCornerShape(16.dp))
          .padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    Text(
        text = "Escáner de Objetos 3D (Marketplace Creator)",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = textPrimary
    )
    
    when (step) {
      0 -> { // Setup instructions
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f).widthIn(max = 500.dp)
        ) {
          Box(
              modifier = Modifier
                  .size(80.dp)
                  .background(accentBlue.copy(alpha = 0.1f), CircleShape),
              contentAlignment = Alignment.Center
          ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = accentBlue,
                modifier = Modifier.size(48.dp)
            )
          }
          Text(
              "¿Cómo funciona?",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = textPrimary
          )
          Text(
              "1. Coloca un objeto real (ej. una caja, adorno o artículo) frente a ti en una mesa bien iluminada.\n" +
              "2. Presiona 'Iniciar Escaneo'.\n" +
              "3. Mueve tu cabeza lentamente alrededor del objeto en 360 grados.\n" +
              "4. El visor capturará los ángulos y nuestro pipeline en la nube generará el modelo 3D (.glb) listo para vender y colocar.",
              fontSize = 13.sp,
              color = textSecondary,
              lineHeight = 20.sp
          )
          Spacer(Modifier.height(16.dp))
          Button(
              onClick = { step = 1 },
              colors = ButtonDefaults.buttonColors(containerColor = accentBlue),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth().height(48.dp)
          ) {
            Text("Iniciar Escaneo 3D", color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }
      1 -> { // Scanning simulation
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
          Text("Escaneando...", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textPrimary)
          Spacer(Modifier.height(16.dp))
          Box(
              modifier = Modifier
                  .size(160.dp)
                  .background(Color(0xFFF1F5F9), CircleShape)
                  .border(2.dp, accentBlue, CircleShape),
              contentAlignment = Alignment.Center
          ) {
            Text("3D OBJECT", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = accentBlue)
          }
          Spacer(Modifier.height(24.dp))
          LinearProgressIndicator(
              progress = progress,
              color = accentBlue,
              trackColor = Color(0xFFE2E8F0),
              modifier = Modifier.width(300.dp).height(8.dp).clip(CircleShape)
          )
          Spacer(Modifier.height(12.dp))
          Text("Capturando ángulos... ${(progress * 100).toInt()}%", fontSize = 12.sp, color = textSecondary)
        }
      }
      2 -> { // Cloud Processing simulation
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
          CircularProgressIndicator(color = accentBlue, modifier = Modifier.size(64.dp))
          Spacer(Modifier.height(24.dp))
          Text(
              "Reconstruyendo en la Nube (AI NeRF)",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = textPrimary
          )
          Text(
              "Generando geometría 3D y texturas de alta resolución...",
              fontSize = 13.sp,
              color = textSecondary,
              modifier = Modifier.padding(top = 8.dp)
          )
        }
      }
      3 -> { // Completed Screen
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f).widthIn(max = 450.dp)
        ) {
          Text("¡Escaneo Completado!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
          
          Box(
              modifier = Modifier
                  .size(140.dp)
                  .background(Color(0xFFDCFCE7), CircleShape),
              contentAlignment = Alignment.Center
          ) {
            Text("OK", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
          }

          Text(
              "Se ha creado un modelo 3D optimizado:",
              fontSize = 14.sp,
              color = textSecondary
          )

          Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
              border = BorderStroke(1.dp, Color(0xFFE2E8F0))
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text("Nombre: Caja de Cartón Retro", fontWeight = FontWeight.Bold, color = textPrimary)
              Text("Origen: Escaneo Quest 3 (Juan P.)", fontSize = 12.sp, color = textSecondary)
              Text("Dimensiones: 0.4m × 0.4m × 0.4m", fontSize = 12.sp, color = textSecondary)
              Text("Precio sugerido: $45.00 MXN", fontSize = 12.sp, color = textSecondary)
            }
          }

          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Button(
                onClick = {
                  val boxItem = FurnitureCatalog.items.first { it.id == "scanned_box_01" }
                  onItemSelected(boxItem)
                  onPlaceInRoom(boxItem)
                  step = 4
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
              Text("Place in Room", color = Color.White)
            }
            
            Button(
                onClick = { step = 0 },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
              Text("Volver", color = Color.White)
            }
          }
        }
      }
      4 -> { // Placed success screen
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f).widthIn(max = 450.dp)
        ) {
          Text("¡Objeto Colocado en Realidad Mixta!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = accentBlue)
          Spacer(Modifier.height(16.dp))
          Text(
              "El objeto 'Caja de Cartón Retro' ahora está en tu habitación física. Puedes usar tus manos o controladores para moverlo y rotarlo.",
              fontSize = 13.sp,
              color = textSecondary,
              textAlign = TextAlign.Center
          )
          Spacer(Modifier.height(24.dp))
          Button(
              onClick = { step = 0 },
              colors = ButtonDefaults.buttonColors(containerColor = accentBlue),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.width(200.dp).height(48.dp)
          ) {
            Text("Escanear Otro", color = Color.White)
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
    Text(category.displayName, color = text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))
                ),
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
      Text(
          text = item.category.displayName.uppercase(),
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF64748B),
          letterSpacing = 1.sp
      )
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

  val (indicatorSymbol, text, color, bg) = when (status) {
    FitStatusUi.FITS -> Quadruple("✓", "Cabe perfectamente en tu espacio", Color(0xFF16A34A), Color(0xFFDCFCE7))
    FitStatusUi.DOES_NOT_FIT -> Quadruple("✕", "No cabe en el espacio disponible", Color(0xFFDC2626), Color(0xFFFEE2E2))
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
    Text(indicatorSymbol, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
  }
}

private data class Quadruple<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

enum class FitStatusUi { UNKNOWN, FITS, DOES_NOT_FIT }

// ── NEW MARKETPLACE PROFILE VIEW ─────────────────────────────────────────────

@Composable
fun ProfileView() {
  val textPrimary = Color(0xFF1E293B)
  val textSecondary = Color(0xFF64748B)
  val accentBlue = Color(0xFF0F62FE)

  Column(
      modifier = Modifier
          .fillMaxSize()
          .background(Color.White, RoundedCornerShape(16.dp))
          .padding(32.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp)
  ) {
    Text("Mi Perfil de Creador", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textPrimary)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Box(
          modifier = Modifier
              .size(80.dp)
              .background(accentBlue, CircleShape),
          contentAlignment = Alignment.Center
      ) {
        Text("JP", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
      }
      Column {
        Text("Juan Pérez", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
        Text("Creador del Marketplace · ID: #98342", fontSize = 13.sp, color = textSecondary)
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .background(Color(0xFFE0EFE0), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text("Cuenta Verificada", fontSize = 10.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
        }
      }
    }

    Divider(color = Color(0xFFE2E8F0))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      ProfileStatCard("Ventas Totales", "$4,500 MXN", Modifier.weight(1f))
      ProfileStatCard("Modelos 3D Escaneados", "3 Artículos", Modifier.weight(1f))
      ProfileStatCard("Reputación", "4.9 ★", Modifier.weight(1f))
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Text("Configuración de Marketplace", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
      Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
          border = BorderStroke(1.dp, Color(0xFFE2E8F0))
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("• Facturación: Conectado a Stripe / MercadoPago", fontSize = 12.sp, color = textSecondary)
          Text("• Sensor LiDAR: Quest 3 Object Mesh Enabled", fontSize = 12.sp, color = textSecondary)
          Text("• Formato exportado: .glb compatible con Horizon OS", fontSize = 12.sp, color = textSecondary)
        }
      }
    }
  }
}

@Composable
fun ProfileStatCard(title: String, value: String, modifier: Modifier = Modifier) {
  Card(
      modifier = modifier,
      colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
      border = BorderStroke(1.dp, Color(0xFFE2E8F0))
  ) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(title, fontSize = 11.sp, color = Color(0xFF64748B))
      Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
    }
  }
}

// ── NEW MARKETPLACE NOTIFICATIONS VIEW ───────────────────────────────────────

@Composable
fun NotificationsView() {
  val textPrimary = Color(0xFF1E293B)

  Column(
      modifier = Modifier
          .fillMaxSize()
          .background(Color.White, RoundedCornerShape(16.dp))
          .padding(32.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    Text("Notificaciones", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textPrimary)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
      NotificationItem("¡Nueva venta!", "Un usuario compró tu modelo 'Caja de Cartón Retro' por $45.00 MXN.", "Hace 15 min")
      NotificationItem("Escaneo procesado", "Tu modelo 'Puerta Rústica Tallada' se ha procesado con éxito en la nube AI y está listo para ser publicado.", "Hace 1 hora")
      NotificationItem("Actualización de software", "Meta Horizon OS ha actualizado el kit de sensores LiDAR para mayor resolución de malla.", "Ayer")
      NotificationItem("Verificación de cuenta", "Tu identidad como creador ha sido verificada y ya puedes recibir transferencias directas.", "Hace 3 días")
    }
  }
}

@Composable
fun NotificationItem(title: String, body: String, time: String) {
  Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
      border = BorderStroke(1.dp, Color(0xFFE2E8F0))
  ) {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
          modifier = Modifier
              .size(8.dp)
              .background(Color(0xFF0F62FE), CircleShape)
      )
      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
          Text(time, fontSize = 10.sp, color = Color(0xFF94A3B8))
        }
        Text(body, fontSize = 12.sp, color = Color(0xFF64748B))
      }
    }
  }
}
