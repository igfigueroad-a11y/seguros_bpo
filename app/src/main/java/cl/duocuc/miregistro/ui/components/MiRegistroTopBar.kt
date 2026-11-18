package cl.duocuc.miregistro.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duocuc.miregistro.model.Usuario
import cl.duocuc.miregistro.ui.theme.Primario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiRegistroTopBar(
    titulo: String = "MiRegistro",
    usuario: Usuario?,
    mostrarVolver: Boolean = false,
    onVolver: () -> Unit = {},
    onVerMisSiniestros: () -> Unit = {},
    onEditarPerfil: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    var menuAbierto by remember { mutableStateOf(false) }

    val iniciales = remember(usuario?.nombre) {
        val partes = usuario?.nombre?.trim()?.split(" ")?.filter { it.isNotBlank() }.orEmpty()
        when {
            partes.size >= 2 -> "${partes[0].first()}${partes[1].first()}".uppercase()
            partes.size == 1 -> partes[0].take(2).uppercase()
            else -> "US"
        }
    }

    val primerNombre = remember(usuario?.nombre) {
        usuario?.nombre?.trim()?.split(" ")?.firstOrNull() ?: "Perfil"
    }

    TopAppBar(
        title = {
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        },
        navigationIcon = {
            if (mostrarVolver) {
                IconButton(onClick = onVolver) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            Box(modifier = Modifier.padding(end = 8.dp)) {
                // Botón píldora con iniciales y nombre
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.16f))
                        .clickable { menuAbierto = true }
                        .padding(start = 6.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = iniciales,
                            color = Primario,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = primerNombre,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Opciones de perfil",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Menú desplegable de usuario
                DropdownMenu(
                    expanded = menuAbierto,
                    onDismissRequest = { menuAbierto = false },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = Color.White
                ) {
                    // Cabecera con datos del usuario
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = usuario?.nombre ?: "Usuario",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF1C1B22)
                        )
                        Text(
                            text = usuario?.correo ?: "",
                            fontSize = 12.sp,
                            color = Color(0xFF5F6368)
                        )
                    }

                    HorizontalDivider()

                    DropdownMenuItem(
                        text = { Text("Mis siniestros", fontWeight = FontWeight.Medium) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = null,
                                tint = Primario
                            )
                        },
                        onClick = {
                            menuAbierto = false
                            onVerMisSiniestros()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Editar perfil", fontWeight = FontWeight.Medium) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Primario
                            )
                        },
                        onClick = {
                            menuAbierto = false
                            onEditarPerfil()
                        }
                    )

                    HorizontalDivider()

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Cerrar sesión",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            menuAbierto = false
                            onCerrarSesion()
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Primario,
            titleContentColor = Color.White
        )
    )
}