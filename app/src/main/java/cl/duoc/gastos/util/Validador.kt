package cl.duoc.gastos.util

/** Toda la lógica de validación vive aquí, sin depender de la interfaz. Devuelve el mensaje de error o null si es válido. */
object Validador {
    private val EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun nombre(v: String): String? = when {
        v.isBlank() -> "El nombre es obligatorio"
        v.trim().length < 3 -> "Mínimo 3 caracteres"
        else -> null
    }

    fun email(v: String): String? = when {
        v.isBlank() -> "El correo es obligatorio"
        !EMAIL.matches(v.trim()) -> "Formato de correo inválido"
        else -> null
    }

    fun clave(v: String): String? = when {
        v.isBlank() -> "La contraseña es obligatoria"
        v.length < 6 -> "Mínimo 6 caracteres"
        v.none { it.isDigit() } -> "Debe incluir al menos un número"
        else -> null
    }

    fun titulo(v: String): String? = when {
        v.isBlank() -> "El título es obligatorio"
        v.trim().length < 3 -> "Mínimo 3 caracteres"
        else -> null
    }

    fun monto(v: String): String? {
        val n = v.replace(',', '.').toDoubleOrNull()
        return when {
            v.isBlank() -> "El monto es obligatorio"
            n == null -> "Ingresa un número válido"
            n <= 0 -> "Debe ser mayor a 0"
            else -> null
        }
    }

    fun categoria(v: String): String? = if (v.isBlank()) "Selecciona una categoría" else null
}
