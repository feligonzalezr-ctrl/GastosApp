package cl.duoc.gastos.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TemaGastos(contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF1B7F5C),
            secondary = Color(0xFF3F6B8C),
            primaryContainer = Color(0xFFCDEFE1)
        ),
        content = contenido
    )
}

/** Campo con ícono, validación visual (borde rojo + ícono de alerta) y mensaje animado. */
@Composable
fun Campo(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String?,
    teclado: KeyboardType = KeyboardType.Text,
    esClave: Boolean = false
) {
    var ultimoError by remember { mutableStateOf("") }
    if (error != null) ultimoError = error
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = {
            if (error != null) Icon(Icons.Default.Warning, "Error", tint = MaterialTheme.colorScheme.error)
        },
        isError = error != null,
        supportingText = {
            AnimatedVisibility(
                visible = error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) { Text(ultimoError) }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        visualTransformation = if (esClave) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth()
    )
}

/** Botón que se encoge levemente al presionarlo (retroalimentación animada). */
@Composable
fun BotonAnimado(texto: String, alClick: () -> Unit) {
    val fuente = remember { MutableInteractionSource() }
    val presionado by fuente.collectIsPressedAsState()
    val escala by animateFloatAsState(if (presionado) 0.95f else 1f, label = "escala")
    Button(
        onClick = alClick,
        interactionSource = fuente,
        modifier = Modifier.fillMaxWidth().scale(escala)
    ) { Text(texto) }
}

fun clp(valor: Double): String =
    NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply { maximumFractionDigits = 0 }.format(valor)

fun fechaTexto(ms: Long): String = SimpleDateFormat("dd/MM/yyyy", Locale("es", "CL")).format(Date(ms))
