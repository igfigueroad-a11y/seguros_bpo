package cl.duocuc.miregistro.ui.components



import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duocuc.miregistro.R

val AzulPrincipal = Color(0xFF1565C0)
val AzulOscuro = Color(0xFF0D47A1)
val AzulFondoSuave = Color(0xFFF3F8FF)

val BANCO_PREGUNTAS_SEGURIDAD = listOf(
    "¿Cuál es el nombre de tu primera mascota?",
    "¿En qué ciudad naciste?",
    "¿Cuál es tu película favorita?",
    "¿Cuál es el nombre de tu colegio básico?",
    "¿Cuál es tu comida favorita?",
    "¿Cuál es el segundo nombre de tu madre?"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloquePreguntaSeguridad(
    numeroPregunta: Int,
    preguntaSeleccionada: String,
    preguntasExcluidas: List<String>,
    onPreguntaSelected: (String) -> Unit,
    respuesta: String,
    onRespuestaChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }
    val opcionesDisponibles = BANCO_PREGUNTAS_SEGURIDAD.filter { it !in preguntasExcluidas }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Pregunta de seguridad $numeroPregunta",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = AzulPrincipal,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expandido,
            onExpandedChange = { expandido = !expandido },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = preguntaSeleccionada,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Selecciona una pregunta...") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AzulPrincipal,
                    unfocusedBorderColor = AzulPrincipal.copy(alpha = 0.5f),
                    focusedTrailingIconColor = AzulPrincipal,
                    unfocusedTrailingIconColor = AzulPrincipal
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false },
                containerColor = Color.White
            ) {
                opcionesDisponibles.forEach { opcion ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = opcion,
                                color = Color(0xFF1C1B1F)
                            )
                        },
                        onClick = {
                            onPreguntaSelected(opcion)
                            expandido = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        CampoTexto(
            valor = respuesta,
            onValorCambia = onRespuestaChange,
            etiqueta = "Respuesta $numeroPregunta",
            icono = R.drawable.ic_candado
        )
    }
}