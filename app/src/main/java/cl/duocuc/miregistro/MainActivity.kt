package cl.duocuc.miregistro
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import cl.duocuc.miregistro.ui.navigation.AppNavigation
import cl.duocuc.miregistro.ui.theme.MiRegistroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // BORRAMOS enableEdgeToEdge() para evitar la pantalla negra en el emulador

        setContent {
            MiRegistroTheme(dynamicColor = false) {
                // Surface fuerza a renderizar el color de fondo de inmediato
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        // Aquí llamamos al archivo de rutas
                        AppNavigation(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}