package cl.duoc.gastos.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gastos.data.AppDatabase
import cl.duoc.gastos.data.Gasto
import cl.duoc.gastos.util.Validador
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class GastosViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).gastoDao()
    private val correo = MutableStateFlow("")

    val gastos = correo.flatMapLatest { dao.listar(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val total = gastos.map { lista -> lista.sumOf { it.monto } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Estado del formulario
    var editandoId by mutableStateOf<Int?>(null)
    var titulo by mutableStateOf("")
    var monto by mutableStateOf("")
    var categoria by mutableStateOf("")
    var fotoUri by mutableStateOf<String?>(null)
    var lat by mutableStateOf<Double?>(null)
    var lng by mutableStateOf<Double?>(null)
    var intentado by mutableStateOf(false)
    private var fechaOriginal: Long? = null

    val categorias = listOf("Comida", "Transporte", "Hogar", "Ocio", "Salud", "Otros")

    val errTitulo get() = if (intentado) Validador.titulo(titulo) else null
    val errMonto get() = if (intentado) Validador.monto(monto) else null
    val errCategoria get() = if (intentado) Validador.categoria(categoria) else null

    fun setUsuario(email: String) { correo.value = email }

    fun nuevo() {
        editandoId = null; titulo = ""; monto = ""; categoria = ""
        fotoUri = null; lat = null; lng = null; intentado = false; fechaOriginal = null
    }

    fun editar(g: Gasto) {
        editandoId = g.id; titulo = g.titulo; monto = g.monto.toString(); categoria = g.categoria
        fotoUri = g.fotoUri; lat = g.lat; lng = g.lng; intentado = false; fechaOriginal = g.fecha
    }

    fun guardar(alExito: () -> Unit) {
        intentado = true
        if (errTitulo != null || errMonto != null || errCategoria != null) return
        val g = Gasto(
            id = editandoId ?: 0,
            titulo = titulo.trim(),
            monto = monto.replace(',', '.').toDouble(),
            categoria = categoria,
            fecha = fechaOriginal ?: System.currentTimeMillis(),
            fotoUri = fotoUri, lat = lat, lng = lng,
            usuarioEmail = correo.value
        )
        viewModelScope.launch {
            if (editandoId == null) dao.insertar(g) else dao.actualizar(g)
            alExito()
        }
    }

    fun borrar(g: Gasto) { viewModelScope.launch { dao.borrar(g) } }
}
