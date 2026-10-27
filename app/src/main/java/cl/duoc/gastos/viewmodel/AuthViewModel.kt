package cl.duoc.gastos.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gastos.data.AppDatabase
import cl.duoc.gastos.data.Usuario
import cl.duoc.gastos.util.Recursos
import cl.duoc.gastos.util.Validador
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).usuarioDao()

    var nombre by mutableStateOf("")
    var email by mutableStateOf("")
    var clave by mutableStateOf("")
    var intentado by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var usuario by mutableStateOf<String?>(null)

    val errNombre get() = if (intentado) Validador.nombre(nombre) else null
    val errEmail get() = if (intentado) Validador.email(email) else null
    val errClave get() = if (intentado) Validador.clave(clave) else null
    val errClaveLogin get() = if (intentado && clave.isBlank()) "Ingresa tu contraseña" else null

    fun limpiar() {
        nombre = ""; email = ""; clave = ""; intentado = false; error = null
    }

    fun registrar(alExito: () -> Unit) {
        intentado = true
        if (errNombre != null || errEmail != null || errClave != null) return
        viewModelScope.launch {
            val correo = email.trim().lowercase()
            try {
                dao.insertar(Usuario(correo, nombre.trim(), Recursos.hash(clave)))
                usuario = correo
                limpiar()
                alExito()
            } catch (e: SQLiteConstraintException) {
                error = "Ese correo ya está registrado"
            }
        }
    }

    fun login(alExito: () -> Unit) {
        intentado = true
        if (errEmail != null || errClaveLogin != null) return
        viewModelScope.launch {
            val u = dao.buscar(email.trim().lowercase())
            if (u != null && u.claveHash == Recursos.hash(clave)) {
                usuario = u.email
                limpiar()
                alExito()
            } else {
                error = "Correo o contraseña incorrectos"
            }
        }
    }

    fun cerrarSesion() {
        usuario = null
        limpiar()
    }
}
