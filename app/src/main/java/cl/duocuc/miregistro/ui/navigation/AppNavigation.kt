package cl.duocuc.miregistro.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duocuc.miregistro.ui.screens.LoginScreen
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
        // 1. Pantalla de Registro (Pantalla de inicio)
        composable("registro") {
            RegistroScreen(
                onRegistroExitoso = {
                    // Al registrarse entra directo al Home o Login
                    navController.navigate("home_temporal")
                },
                onNavegarLogin = {
                    navController.navigate("login")
                }
            )
        }

        // 2. Pantalla de Iniciar Sesión
        composable("login") {
            LoginScreen(
                onLoginExitoso = { usuario ->
                    navController.navigate("home_temporal")
                },
                onNavegarRecuperar = {
                    navController.navigate("recuperar_clave")
                }
            )
        }

        // 3. Pantalla de Recuperar Contraseña (3 preguntas)
        composable("recuperar_clave") {
            RecuperarClaveScreen(
                onVolver = {
                    navController.popBackStack() // Vuelve a la pantalla anterior (Login)
                },
                onRecuperarExitoso = {
                    // Acción al validar bien las respuestas, por ahora al home temporal
                    navController.navigate("home_temporal")
                }
            )
        }

        // 4. Home temporal
        composable("home_temporal") {
            PlaceholderPantalla("Home (Mis Siniestros)") {
                // Sin acción por ahora
            }
        }
    }
}

@Composable
fun PlaceholderPantalla(titulo: String, onClick: () -> Unit) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.TextButton(onClick = onClick) {
            androidx.compose.material3.Text(text = titulo)
        }
    }
}