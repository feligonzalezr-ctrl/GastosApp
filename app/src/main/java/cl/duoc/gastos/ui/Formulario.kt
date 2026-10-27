package cl.duoc.gastos.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cl.duoc.gastos.util.Recursos
import cl.duoc.gastos.viewmodel.GastosViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormGastoScreen(vm: GastosViewModel, volver: () -> Unit) {
    val ctx = LocalContext.current
    fun aviso(m: String) = Toast.makeText(ctx, m, Toast.LENGTH_SHORT).show()
    fun tiene(p: String) = ContextCompat.checkSelfPermission(ctx, p) == PackageManager.PERMISSION_GRANTED

    // ---- Recurso nativo 1: cámara ----
    var uriTemp by remember { mutableStateOf<Uri?>(null) }
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) uriTemp?.let { vm.fotoUri = it.toString() }
    }
    fun abrirCamara() { val u = Recursos.crearUriFoto(ctx); uriTemp = u; camara.launch(u) }
    val permisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) abrirCamara() else aviso("Se necesita permiso de cámara")
    }

    // ---- Recurso nativo 2: ubicación ----
    var buscando by remember { mutableStateOf(false) }
    fun pedirUbicacion() {
        buscando = true
        Recursos.ubicacion(ctx,
            { la, lo -> vm.lat = la; vm.lng = lo; buscando = false },
            { buscando = false; aviso("No se pudo obtener la ubicación") })
    }
    val permisoUbicacion = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { r ->
        if (r.values.any { it }) pedirUbicacion() else aviso("Se necesita permiso de ubicación")
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(if (vm.editandoId == null) "Nuevo gasto" else "Editar gasto") },
            navigationIcon = { IconButton(onClick = volver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
        )
    }) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Campo(vm.titulo, { vm.titulo = it }, "Título", Icons.Default.Edit, vm.errTitulo)
            Campo(vm.monto, { vm.monto = it }, "Monto", Icons.Default.AttachMoney, vm.errMonto, KeyboardType.Decimal)

            Text("Categoría", style = MaterialTheme.typography.labelLarge)
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                vm.categorias.forEach { c ->
                    FilterChip(selected = vm.categoria == c, onClick = { vm.categoria = c }, label = { Text(c) })
                }
            }
            AnimatedVisibility(vm.errCategoria != null) {
                Text(vm.errCategoria ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedButton(onClick = {
                if (tiene(Manifest.permission.CAMERA)) abrirCamara() else permisoCamara.launch(Manifest.permission.CAMERA)
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.CameraAlt, null); Spacer(Modifier.width(8.dp))
                Text(if (vm.fotoUri == null) "Tomar foto del comprobante" else "Cambiar foto")
            }
            AnimatedVisibility(vm.fotoUri != null) {
                AsyncImage(
                    model = vm.fotoUri?.let { Uri.parse(it) },
                    contentDescription = "Foto del comprobante",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))
                )
            }

            OutlinedButton(onClick = {
                if (tiene(Manifest.permission.ACCESS_FINE_LOCATION) || tiene(Manifest.permission.ACCESS_COARSE_LOCATION)) pedirUbicacion()
                else permisoUbicacion.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.LocationOn, null); Spacer(Modifier.width(8.dp))
                Text(if (buscando) "Buscando ubicación..." else "Agregar ubicación actual")
            }
            AnimatedVisibility(vm.lat != null && vm.lng != null) {
                Text("Ubicación: %.5f, %.5f".format(vm.lat ?: 0.0, vm.lng ?: 0.0))
            }

            BotonAnimado("Guardar") { vm.guardar(volver) }
        }
    }
}
