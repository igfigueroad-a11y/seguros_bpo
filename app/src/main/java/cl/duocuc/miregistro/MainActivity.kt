package cl.duocuc.miregistro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import cl.duocuc.miregistro.model.Usuario
import cl.duocuc.miregistro.ui.screens.LoginScreen
import cl.duocuc.miregistro.ui.theme.MiRegistroTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MiRegistroTheme {
                var usuarioActual by remember {
                    mutableStateOf<Usuario?>(null)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    val usuario = usuarioActual

                    if (usuario == null) {
                        LoginScreen(
                            onLoginExitoso = { usuarioIngresado ->
                                usuarioActual = usuarioIngresado
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        // Pantalla temporal para comprobar el login.
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${usuario.nombre}\n${usuario.rolNombre}",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}