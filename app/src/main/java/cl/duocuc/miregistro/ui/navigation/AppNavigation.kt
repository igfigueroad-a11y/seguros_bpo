package cl.duocuc.miregistro.ui.navigation
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.model.Siniestro
import cl.duocuc.miregistro.ui.screens.AdjuntarEvidenciaScreen
import cl.duocuc.miregistro.ui.screens.ConsultaSiniestroScreen
import cl.duocuc.miregistro.ui.screens.DetalleSiniestroScreen
import cl.duocuc.miregistro.ui.screens.LoginScreen
import cl.duocuc.miregistro.ui.screens.NuevaContrasenaScreen
import cl.duocuc.miregistro.ui.screens.PanelLiquidadorScreen
import cl.duocuc.miregistro.ui.screens.RecuperarClaveScreen
import cl.duocuc.miregistro.ui.screens.RegistroScreen
import cl.duocuc.miregistro.ui.screens.SiniestroCerradoScreen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import  androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.padding

import cl.duocuc.miregistro.ui.theme.MiRegistroTheme
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("seguros_BPO")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White


                )
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            composable("registro") {
                RegistroScreen(
                    onRegistroExitoso = {
                        navController.navigate("login") {
                            popUpTo("registro") { inclusive = true }
                        }
                    },
                    onNavegarLogin = { navController.navigate("login") }
                )
            }

            composable("login") {

                // creamos las variables
                var mostrarAlerta by remember { mutableStateOf(false) }
                var destinoPendiente by remember { mutableStateOf("") }


                LoginScreen(
                    onLoginExitoso = { usuario ->

                        destinoPendiente = when (usuario.rolNombre) {
                            "LIQUIDADOR" -> "panel/${usuario.id}"
                            "ASEGURADO" -> "consulta"
                            else -> "home_temporal"
                        }
                        // mostramos la alerta antes de navegar
                        mostrarAlerta = true
                    },

                    onNavegarRecuperar = { navController.navigate("recuperar_clave") },
                    onNavegarRegistro = { navController.navigate("registro") }
                )
                if (mostrarAlerta) {
                    AlertDialog(
                        onDismissRequest = {
                            mostrarAlerta = false
                        },
                        title = {
                            Text("Bienvenida")
                        },
                        text = {
                            Text("presiona para continuar")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    mostrarAlerta = false
                                    navController.navigate(destinoPendiente) {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            ) { Text("Continuar") }
                        }
                    )
                }
            }


            composable("recuperar_clave") {
                RecuperarClaveScreen(
                    onVolver = { navController.popBackStack() },
                    onRecuperarExitoso = { correoValidado ->
                        navController.navigate("nueva_contrasena/$correoValidado") {
                            popUpTo("recuperar_clave") { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = "nueva_contrasena/{correo}",
                arguments = listOf(navArgument("correo") { type = NavType.StringType })
            ) { entry ->
                val correo = entry.arguments?.getString("correo") ?: ""
                NuevaContrasenaScreen(
                    correo = correo,
                    onContrasenaActualizada = {
                        navController.navigate("login") {
                            popUpTo("nueva_contrasena/$correo") { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = "panel/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("id") ?: 0L
                PanelLiquidadorScreen(
                    idLiquidador = id,
                    onCerrarSesion = {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("consulta") {
                ConsultaSiniestroScreen(
                    onSiniestroEncontrado = { numero ->
                        navController.navigate("estado/$numero")
                    }
                )
            }

            composable("estado/{numero}") { entry ->
                val numeroIngresado = entry.arguments?.getString("numero") ?: ""
                val contexto = LocalContext.current
                val db = UsuarioDbHelper(contexto)

                val siniestroReal = db.buscarSiniestroPorNumero(numeroIngresado)

                val siniestroAMostrar = siniestroReal ?: Siniestro(
                    numero = numeroIngresado.ifBlank { "SIN-26-008942" },
                    aseguradoNombre = "María González",
                    companiaNombre = "Seguros Cordillera",
                    tipoSiniestro = "Reporte de daños por colisión",
                    fechaOcurrencia = "23-10-2026",
                    idEtapa = 2L,
                    etapaNombre = "En evaluación",
                    fechaUltimaActualizacion = "Hoy, 10:42",
                    comentario = ""
                )

                if (siniestroAMostrar.etapaNombre.equals("Cerrado", ignoreCase = true)) {
                    SiniestroCerradoScreen(
                        siniestro = siniestroAMostrar,
                        onVolver = { navController.popBackStack() }
                    )
                } else {
                    DetalleSiniestroScreen(
                        siniestro = siniestroAMostrar,
                        onVolver = { navController.popBackStack() },
                        onAdjuntarEvidencia = {
                            navController.navigate("evidencia/${siniestroAMostrar.numero}")
                        }
                    )
                }
            }

            composable("evidencia/{numero}") { entry ->
                val numero = entry.arguments?.getString("numero") ?: ""
                AdjuntarEvidenciaScreen(
                    numeroSiniestro = numero,
                    onVolver = { navController.popBackStack() }
                )
            }

            composable("home_temporal") {
                PlaceholderPantalla("Home (Analista)") {}
            }
        }
    }
}

    @Composable
    fun PlaceholderPantalla(titulo: String, onClick: () -> Unit) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            TextButton(onClick = onClick) { Text(text = titulo) }
        }
    }





@Preview (showBackground = true)
@Composable
fun AppNavigationPreview(){
    MiRegistroTheme {
        AppNavigation()
    }
}