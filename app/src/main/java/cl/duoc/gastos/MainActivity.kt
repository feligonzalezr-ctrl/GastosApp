package cl.duoc.gastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.gastos.ui.*
import cl.duoc.gastos.viewmodel.AuthViewModel
import cl.duoc.gastos.viewmodel.GastosViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TemaGastos { AppNav() } }
    }
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val auth: AuthViewModel = viewModel()
    val gastos: GastosViewModel = viewModel()

    val irALista = {
        gastos.setUsuario(auth.usuario ?: "")
        nav.navigate("lista") { popUpTo("login") { inclusive = true } }
    }

    NavHost(
        navController = nav,
        startDestination = "login",
        enterTransition = { slideInHorizontally { it } + fadeIn() },
        exitTransition = { slideOutHorizontally { -it / 3 } + fadeOut() },
        popEnterTransition = { slideInHorizontally { -it / 3 } + fadeIn() },
        popExitTransition = { slideOutHorizontally { it } + fadeOut() }
    ) {
        composable("login") { LoginScreen(auth, { nav.navigate("registro") }, irALista) }
        composable("registro") { RegistroScreen(auth, { nav.popBackStack() }, irALista) }
        composable("lista") {
            ListaScreen(
                gastos,
                alNuevo = { gastos.nuevo(); nav.navigate("form") },
                alEditar = { gastos.editar(it); nav.navigate("form") },
                alSalir = {
                    auth.cerrarSesion()
                    nav.navigate("login") { popUpTo("lista") { inclusive = true } }
                }
            )
        }
        composable("form") { FormGastoScreen(gastos) { nav.popBackStack() } }
    }
}
