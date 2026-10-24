package cl.duocuc.miregistro.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duocuc.miregistro.ui.screens.ConsultaSiniestroScreen
import cl.duocuc.miregistro.ui.screens.LoginScreen
import cl.duocuc.miregistro.ui.screens.PanelLiquidadorScreen
import cl.duocuc.miregistro.ui.screens.RecuperarClaveScreen
import cl.duocuc.miregistro.ui.screens.RegistroScreen

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
                onRecuperarExitoso = {
                    navController.navigate("login") {
                        popUpTo("recuperar_clave") { inclusive = true }
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

        // Marcador: reemplazar por la pantalla de Ignacio
        composable("estado/{numero}") { entry ->
            val numero = entry.arguments?.getString("numero") ?: ""
            PlaceholderPantalla("Estado del siniestro $numero") {}
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