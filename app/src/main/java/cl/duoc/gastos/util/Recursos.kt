package cl.duoc.gastos.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.File
import java.security.MessageDigest

/** Acceso a recursos nativos: cámara (archivo para la foto) y ubicación GPS. */
object Recursos {
    fun crearUriFoto(ctx: Context): Uri {
        val dir = File(ctx.filesDir, "fotos").apply { mkdirs() }
        val archivo = File.createTempFile("boleta_", ".jpg", dir)
        return FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", archivo)
    }

    @SuppressLint("MissingPermission") // el permiso se solicita antes en la pantalla
    fun ubicacion(ctx: Context, alExito: (Double, Double) -> Unit, alError: () -> Unit) {
        LocationServices.getFusedLocationProviderClient(ctx)
            .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .addOnSuccessListener { l -> if (l != null) alExito(l.latitude, l.longitude) else alError() }
            .addOnFailureListener { alError() }
    }

    fun hash(texto: String): String =
        MessageDigest.getInstance("SHA-256").digest(texto.toByteArray()).joinToString("") { "%02x".format(it) }
}
