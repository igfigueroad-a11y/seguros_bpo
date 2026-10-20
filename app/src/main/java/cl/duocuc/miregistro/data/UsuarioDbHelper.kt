package cl.duocuc.miregistro.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.duocuc.miregistro.model.Usuario
import java.security.MessageDigest

class UsuarioDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "seguimiento_siniestros.db"
        private const val VERSION_BD = 1

        // Tabla roles
        private const val TABLA_ROLES = "roles"
        private const val ROL_ID = "id_rol"
        private const val ROL_NOMBRE = "nombre_rol"

        // Tabla usuarios
        private const val TABLA_USUARIOS = "usuarios"
        private const val USU_ID = "id_usuario"
        private const val USU_NOMBRE = "nombre"
        private const val USU_RUT = "rut"
        private const val USU_CORREO = "correo"
        private const val USU_CLAVE = "clave_hash"
        private const val USU_ACTIVO = "activo"
        private const val USU_ROL = "id_rol"

        // Clave de prueba para todos los usuarios de ejemplo (solo desarrollo)
        private const val CLAVE_DEMO = "Clave123"

        private const val SQL_CREAR_ROLES =
            "CREATE TABLE $TABLA_ROLES (" +
                    "$ROL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$ROL_NOMBRE TEXT NOT NULL UNIQUE)"

        private const val SQL_CREAR_USUARIOS =
            "CREATE TABLE $TABLA_USUARIOS (" +
                    "$USU_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$USU_NOMBRE TEXT NOT NULL, " +
                    "$USU_RUT TEXT NOT NULL UNIQUE, " +
                    "$USU_CORREO TEXT NOT NULL UNIQUE, " +
                    "$USU_CLAVE TEXT NOT NULL, " +
                    "$USU_ACTIVO INTEGER NOT NULL DEFAULT 1, " +
                    "$USU_ROL INTEGER NOT NULL REFERENCES $TABLA_ROLES($ROL_ID))"

        private const val SQL_PRECARGAR_ROLES =
            "INSERT INTO $TABLA_ROLES ($ROL_ID, $ROL_NOMBRE) VALUES " +
                    "(1, 'ASEGURADO'), (2, 'LIQUIDADOR'), (3, 'ANALISTA')"

        private const val SQL_SELECT_USUARIOS =
            "SELECT u.*, r.$ROL_NOMBRE AS $ROL_NOMBRE " +
                    "FROM $TABLA_USUARIOS u " +
                    "INNER JOIN $TABLA_ROLES r ON u.$USU_ROL = r.$ROL_ID"

        // Convierte la clave en un hash (nunca se guarda en texto plano)
        fun hashearClave(clave: String): String =
            MessageDigest.getInstance("SHA-256")
                .digest(clave.toByteArray())
                .joinToString("") { "%02x".format(it) }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREAR_ROLES)
        db.execSQL(SQL_CREAR_USUARIOS)
        db.execSQL(SQL_PRECARGAR_ROLES)
        precargarUsuarios(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_USUARIOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_ROLES")
        onCreate(db)
    }

    // Usuarios de ejemplo (Anexo 9). Asegurados: rol 1. Liquidadores: rol 2. Analista: rol 3.
    private fun precargarUsuarios(db: SQLiteDatabase) {
        val ejemplos = listOf(
            arrayOf("Camila Rojas Fuentes", "15842371-5", "camila.rojas@correo.ejemplo", 1L),
            arrayOf("Jorge Pizarro Contreras", "9418552-1", "jorge.pizarro@correo.ejemplo", 1L),
            arrayOf("Valentina Soto Muñoz", "19205884-8", "vale.soto@correo.ejemplo", 1L),
            arrayOf("Paulina Fernández Ruiz", "11111111-1", "paulina.fernandez@andes-bpo.ejemplo", 2L),
            arrayOf("Matías Gallardo Silva", "12222222-2", "matias.gallardo@andes-bpo.ejemplo", 2L),
            arrayOf("Natalia Reyes Campos", "13333333-3", "natalia.reyes@andes-bpo.ejemplo", 3L)
        )
        val hash = hashearClave(CLAVE_DEMO)
        ejemplos.forEach { e ->
            val valores = ContentValues().apply {
                put(USU_NOMBRE, e[0] as String)
                put(USU_RUT, e[1] as String)
                put(USU_CORREO, e[2] as String)
                put(USU_CLAVE, hash)
                put(USU_ACTIVO, 1)
                put(USU_ROL, e[3] as Long)
            }
            db.insert(TABLA_USUARIOS, null, valores)
        }
    }

    // LOGIN: devuelve el usuario si correo y clave coinciden y está activo; si no, null
    fun validarLogin(correo: String, clave: String): Usuario? =
        consultarUsuarios(
            "$SQL_SELECT_USUARIOS WHERE u.$USU_CORREO = ? AND u.$USU_CLAVE = ? AND u.$USU_ACTIVO = 1",
            arrayOf(correo.trim().lowercase(), hashearClave(clave))
        ).firstOrNull()

    // Para recuperar la sesión guardada
    fun buscarPorId(id: Long): Usuario? =
        consultarUsuarios("$SQL_SELECT_USUARIOS WHERE u.$USU_ID = ?", arrayOf(id.toString()))
            .firstOrNull()

    private fun consultarUsuarios(sql: String, args: Array<String>?): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        readableDatabase.rawQuery(sql, args).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(leerUsuario(cursor))
            }
        }
        return lista
    }

    private fun leerUsuario(cursor: Cursor) = Usuario(
        id = cursor.getLong(cursor.getColumnIndexOrThrow(USU_ID)),
        nombre = cursor.getString(cursor.getColumnIndexOrThrow(USU_NOMBRE)),
        rut = cursor.getString(cursor.getColumnIndexOrThrow(USU_RUT)),
        correo = cursor.getString(cursor.getColumnIndexOrThrow(USU_CORREO)),
        rolId = cursor.getLong(cursor.getColumnIndexOrThrow(USU_ROL)),
        rolNombre = cursor.getString(cursor.getColumnIndexOrThrow(ROL_NOMBRE))
    )
}