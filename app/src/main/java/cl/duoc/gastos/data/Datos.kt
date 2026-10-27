package cl.duoc.gastos.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey val email: String,
    val nombre: String,
    val claveHash: String
)

@Entity(tableName = "gastos")
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val monto: Double,
    val categoria: String,
    val fecha: Long,
    val fotoUri: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val usuarioEmail: String
)

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscar(email: String): Usuario?
}

@Dao
interface GastoDao {
    @Query("SELECT * FROM gastos WHERE usuarioEmail = :email ORDER BY fecha DESC")
    fun listar(email: String): Flow<List<Gasto>>

    @Insert suspend fun insertar(gasto: Gasto)
    @Update suspend fun actualizar(gasto: Gasto)
    @Delete suspend fun borrar(gasto: Gasto)
}

@Database(entities = [Usuario::class, Gasto::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun gastoDao(): GastoDao

    companion object {
        @Volatile private var instancia: AppDatabase? = null
        fun get(context: Context): AppDatabase = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(
                context.applicationContext, AppDatabase::class.java, "gastos.db"
            ).build().also { instancia = it }
        }
    }
}
