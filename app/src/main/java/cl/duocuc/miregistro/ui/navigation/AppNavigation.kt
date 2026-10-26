package cl.duocuc.miregistro.ui.navigation

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

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "registro",
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
                        "ASEGURADO" -> "consulta"
                        else -> "home_temporal"
                    }
                    navController.navigate(destino) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavegarRecuperar = { navController.navigate("recuperar_clave") }
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
            PanelLiquidadorScreen(idLiquidador = id)
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
                comentario = "" // <--- Agregado para cumplir con el modelo
            )

            DetalleSiniestroScreen(
                siniestro = siniestroAMostrar,
                onVolver = { navController.popBackStack() },
                onAdjuntarEvidencia = {
                    navController.navigate("evidencia/${siniestroAMostrar.numero}")
                }
            )
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