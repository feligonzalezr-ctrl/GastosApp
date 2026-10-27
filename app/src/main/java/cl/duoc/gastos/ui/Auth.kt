package cl.duoc.gastos.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.gastos.viewmodel.AuthViewModel

@Composable
fun LoginScreen(vm: AuthViewModel, irRegistro: () -> Unit, alEntrar: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mis Gastos", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Text("Inicia sesión para continuar")
        Spacer(Modifier.height(8.dp))
        Campo(vm.email, { vm.email = it; vm.error = null }, "Correo", Icons.Default.Email, vm.errEmail, KeyboardType.Email)
        Campo(vm.clave, { vm.clave = it; vm.error = null }, "Contraseña", Icons.Default.Lock, vm.errClaveLogin, esClave = true)
        AnimatedVisibility(vm.error != null) {
            Text(vm.error ?: "", color = MaterialTheme.colorScheme.error)
        }
        BotonAnimado("Ingresar") { vm.login(alEntrar) }
        TextButton(onClick = { vm.limpiar(); irRegistro() }) { Text("¿No tienes cuenta? Regístrate") }
    }
}

@Composable
fun RegistroScreen(vm: AuthViewModel, volver: () -> Unit, alEntrar: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Campo(vm.nombre, { vm.nombre = it }, "Nombre", Icons.Default.Person, vm.errNombre)
        Campo(vm.email, { vm.email = it; vm.error = null }, "Correo", Icons.Default.Email, vm.errEmail, KeyboardType.Email)
        Campo(vm.clave, { vm.clave = it }, "Contraseña", Icons.Default.Lock, vm.errClave, esClave = true)
        AnimatedVisibility(vm.error != null) {
            Text(vm.error ?: "", color = MaterialTheme.colorScheme.error)
        }
        BotonAnimado("Registrarme") { vm.registrar(alEntrar) }
        TextButton(onClick = { vm.limpiar(); volver() }) { Text("Ya tengo cuenta") }
    }
}
