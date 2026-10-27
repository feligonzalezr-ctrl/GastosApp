package cl.duoc.gastos.ui

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cl.duoc.gastos.data.Gasto
import cl.duoc.gastos.viewmodel.GastosViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(vm: GastosViewModel, alNuevo: () -> Unit, alEditar: (Gasto) -> Unit, alSalir: () -> Unit) {
    val gastos by vm.gastos.collectAsState()
    val total by vm.total.collectAsState()
    val totalAnimado by animateFloatAsState(total.toFloat(), tween(600), label = "total")
    var porBorrar by remember { mutableStateOf<Gasto?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis gastos") },
                actions = {
                    IconButton(onClick = alSalir) { Icon(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar sesión") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = alNuevo) { Icon(Icons.Default.Add, "Agregar gasto") }
        }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp)) {
                        Text("Total gastado")
                        Text(clp(totalAnimado.toDouble()), style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
            if (gastos.isEmpty()) item { Text("Aún no tienes gastos. Presiona + para agregar el primero.") }
            items(gastos, key = { it.id }) { g ->
                GastoCard(g, alEditar = { alEditar(g) }, alBorrar = { porBorrar = g })
            }
        }
    }

    porBorrar?.let { g ->
        AlertDialog(
            onDismissRequest = { porBorrar = null },
            title = { Text("Eliminar gasto") },
            text = { Text("¿Eliminar \"${g.titulo}\"?") },
            confirmButton = { TextButton(onClick = { vm.borrar(g); porBorrar = null }) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = { porBorrar = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun GastoCard(g: Gasto, alEditar: () -> Unit, alBorrar: () -> Unit) {
    Card(Modifier.fillMaxWidth().animateContentSize()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (g.fotoUri != null) {
                AsyncImage(
                    model = Uri.parse(g.fotoUri),
                    contentDescription = "Foto del gasto",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.width(12.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(g.titulo, style = MaterialTheme.typography.titleMedium)
                Text("${g.categoria} • ${fechaTexto(g.fecha)}", style = MaterialTheme.typography.bodySmall)
                if (g.lat != null && g.lng != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, Modifier.size(14.dp))
                        Text("%.4f, %.4f".format(g.lat, g.lng), style = MaterialTheme.typography.bodySmall)
                    }
                }
                Text(clp(g.monto), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = alEditar) { Icon(Icons.Default.Edit, "Editar") }
            IconButton(onClick = alBorrar) { Icon(Icons.Default.Delete, "Eliminar") }
        }
    }
}
