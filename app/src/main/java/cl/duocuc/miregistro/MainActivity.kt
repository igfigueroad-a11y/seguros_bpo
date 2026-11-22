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
import cl.duocuc.miregistro.data.AjustesTema
import cl.duocuc.miregistro.ui.navigation.AppNavigation
import cl.duocuc.miregistro.ui.theme.MiRegistroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AjustesTema.cargar(this)
        setContent {
            MiRegistroTheme(
                darkTheme = AjustesTema.oscuro,
                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        AppNavigation(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}