package cl.duocuc.miregistro.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.ui.screens.AdjuntarEvidenciaScreen
import cl.duocuc.miregistro.ui.screens.ConsultaSiniestroScreen
import cl.duocuc.miregistro.ui.screens.DetalleSiniestroScreen
import cl.duocuc.miregistro.ui.screens.LoginScreen
import cl.duocuc.miregistro.ui.screens.NuevaContrasenaScreen
import cl.duocuc.miregistro.ui.screens.PanelLiquidadorScreen
import cl.duocuc.miregistro.ui.screens.RecuperarClaveScreen
import cl.duocuc.miregistro.ui.screens.RegistroScreen
import cl.duocuc.miregistro.ui.screens.SiniestroCerradoScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
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
            LoginScreen(
                onLoginExitoso = { usuario ->
                    val destino = when (usuario.rolNombre) {
                        "LIQUIDADOR" -> "panel/${usuario.id}"
                        "ASEGURADO" -> "consulta/${usuario.id}"
                        else -> "home_temporal"
                    }
                    navController.navigate(destino) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavegarRecuperar = { navController.navigate("recuperar_clave") },
                onNavegarRegistro = { navController.navigate("registro") }
            )
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

        composable(
            route = "consulta/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            val idUsuario = entry.arguments?.getLong("id") ?: 0L
            ConsultaSiniestroScreen(
                onSiniestroEncontrado = { numero ->
                    navController.navigate("estado/$numero/$idUsuario")
                },

                onCerrarSesion = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "estado/{numero}/{id}",
            arguments = listOf(
                navArgument("numero") { type = NavType.StringType },
                navArgument("id") { type = NavType.LongType }
            )
        ) { entry ->
            val numeroIngresado = entry.arguments?.getString("numero") ?: ""
            val idUsuario = entry.arguments?.getLong("id") ?: 0L
            val contexto = LocalContext.current
            val db = UsuarioDbHelper(contexto)


            val siniestroReal = db.buscarSiniestroSeguro(numeroIngresado, idUsuario)

            if (siniestroReal != null) {
                if (siniestroReal.etapaNombre.equals("Cerrado", ignoreCase = true)) {
                    SiniestroCerradoScreen(
                        siniestro = siniestroReal,
                        onVolver = { navController.popBackStack() }
                    )
                } else {
                    DetalleSiniestroScreen(
                        siniestro = siniestroReal,
                        onVolver = { navController.popBackStack() },
                        onAdjuntarEvidencia = {
                            navController.navigate("evidencia/${siniestroReal.numero}")
                        }
                    )
                }
            } else {

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Siniestro no encontrado o acceso denegado", fontWeight = FontWeight.Bold)
                        TextButton(onClick = { navController.popBackStack() }) {
                            Text("Volver al buscador")
                        }
                    }
                }
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

@Composable
fun PlaceholderPantalla(titulo: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        TextButton(onClick = onClick) { Text(text = titulo) }
    }
}