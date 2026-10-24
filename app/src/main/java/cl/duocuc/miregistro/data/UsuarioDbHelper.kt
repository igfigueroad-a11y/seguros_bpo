package cl.duocuc.miregistro.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.duocuc.miregistro.model.DetalleSiniestro
import cl.duocuc.miregistro.model.Documento
import cl.duocuc.miregistro.model.ResumenPanel
import cl.duocuc.miregistro.model.SiniestroResumen
import cl.duocuc.miregistro.model.Usuario
import java.security.MessageDigest

class UsuarioDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "seguimiento_siniestros.db"
        private const val VERSION_BD = 4

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

        // Tabla siniestros
        private const val TABLA_SINIESTROS = "siniestros"
        private const val SIN_ID = "id_siniestro"
        private const val SIN_NUMERO = "numero"
        private const val SIN_TIPO = "tipo"
        private const val SIN_DESCRIPCION = "descripcion"
        private const val SIN_FECHA = "fecha"
        private const val SIN_ESTADO = "estado"
        private const val SIN_PLACA = "placa"
        private const val SIN_PRIORIDAD = "prioridad"
        private const val SIN_DOCS_PENDIENTES = "docs_pendientes"
        private const val SIN_COMENTARIO = "comentario"
        private const val SIN_ASEGURADO = "id_asegurado"
        private const val SIN_LIQUIDADOR = "id_liquidador"

        // Tabla documentos
        private const val TABLA_DOCUMENTOS = "documentos"
        private const val DOC_ID = "id_documento"
        private const val DOC_SINIESTRO = "numero_siniestro"
        private const val DOC_NOMBRE = "nombre"
        private const val DOC_RECIBIDO = "recibido"

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

        private const val SQL_CREAR_SINIESTROS =
            "CREATE TABLE $TABLA_SINIESTROS (" +
                    "$SIN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$SIN_NUMERO TEXT NOT NULL UNIQUE, " +
                    "$SIN_TIPO TEXT NOT NULL, " +
                    "$SIN_DESCRIPCION TEXT, " +
                    "$SIN_FECHA TEXT NOT NULL, " +
                    "$SIN_ESTADO TEXT NOT NULL, " +
                    "$SIN_PLACA TEXT, " +
                    "$SIN_PRIORIDAD TEXT NOT NULL DEFAULT 'Media', " +
                    "$SIN_DOCS_PENDIENTES INTEGER NOT NULL DEFAULT 0, " +
                    "$SIN_COMENTARIO TEXT NOT NULL DEFAULT '', " +
                    "$SIN_ASEGURADO INTEGER NOT NULL REFERENCES $TABLA_USUARIOS($USU_ID), " +
                    "$SIN_LIQUIDADOR INTEGER REFERENCES $TABLA_USUARIOS($USU_ID))"

        private const val SQL_CREAR_DOCUMENTOS =
            "CREATE TABLE $TABLA_DOCUMENTOS (" +
                    "$DOC_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$DOC_SINIESTRO TEXT NOT NULL REFERENCES $TABLA_SINIESTROS($SIN_NUMERO), " +
                    "$DOC_NOMBRE TEXT NOT NULL, " +
                    "$DOC_RECIBIDO INTEGER NOT NULL DEFAULT 0)"

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
        db.execSQL(SQL_CREAR_SINIESTROS)
        precargarSiniestros(db)
        db.execSQL(SQL_CREAR_DOCUMENTOS)
        precargarDocumentos(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_DOCUMENTOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_SINIESTROS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_USUARIOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_ROLES")
        onCreate(db)
    }

    // ---------- Datos de ejemplo ----------

    // Asegurados: rol 1. Liquidadores: rol 2. Analista: rol 3.
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

    private fun precargarSiniestros(db: SQLiteDatabase) {
        val ejemplos = listOf(
            // numero, tipo, descripcion, fecha, estado, placa, prioridad, docsPend, asegurado, liquidador
            arrayOf("SIN-26-008942", "Daño vehicular", "Choque trasero", "2026-10-23", "En evaluación", "ABC123", "Media", 1, 1L, 4L),
            arrayOf("SIN-26-006871", "Robo total", "Robo del vehículo", "2026-10-22", "Recibido", "DEF456", "Alta", 0, 2L, 4L),
            arrayOf("SIN-26-006810", "Daño a propiedad", "Daño a portón", "2026-10-22", "En liquidación", "GHI789", "Baja", 0, 3L, 4L),
            arrayOf("SIN-26-004567", "Daño vehicular", "Granizo en techo", "2026-10-05", "Cerrado", "JKL012", "Baja", 0, 1L, 4L),
            arrayOf("SIN-26-001234", "Robo parcial", "Robo de accesorios", "2026-10-12", "En evaluación", "MNO345", "Media", 2, 2L, 5L)
        )
        ejemplos.forEach { e ->
            val v = ContentValues().apply {
                put(SIN_NUMERO, e[0] as String)
                put(SIN_TIPO, e[1] as String)
                put(SIN_DESCRIPCION, e[2] as String)
                put(SIN_FECHA, e[3] as String)
                put(SIN_ESTADO, e[4] as String)
                put(SIN_PLACA, e[5] as String)
                put(SIN_PRIORIDAD, e[6] as String)
                put(SIN_DOCS_PENDIENTES, e[7] as Int)
                put(SIN_ASEGURADO, e[8] as Long)
                put(SIN_LIQUIDADOR, e[9] as Long)
            }
            db.insert(TABLA_SINIESTROS, null, v)
        }
    }

    private fun precargarDocumentos(db: SQLiteDatabase) {
        val docs = listOf(
            Triple("SIN-26-008942", "Reporte inicial", 1),
            Triple("SIN-26-008942", "Fotos del daño", 1),
            Triple("SIN-26-008942", "Presupuesto de reparación", 0),
            Triple("SIN-26-006871", "Reporte inicial", 1),
            Triple("SIN-26-006810", "Reporte inicial", 1)
        )
        docs.forEach { (numero, nombre, recibido) ->
            val v = ContentValues().apply {
                put(DOC_SINIESTRO, numero)
                put(DOC_NOMBRE, nombre)
                put(DOC_RECIBIDO, recibido)
            }
            db.insert(TABLA_DOCUMENTOS, null, v)
        }
    }

    // ---------- Usuarios y login ----------

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

    fun registrarUsuario(
        nombre: String,
        rut: String,
        correo: String,
        claveTextoPlano: String,
        idRol: Long = 1L
    ): Boolean {
        val valores = ContentValues().apply {
            put(USU_NOMBRE, nombre)
            put(USU_RUT, rut)
            put(USU_CORREO, correo.trim().lowercase())
            put(USU_CLAVE, hashearClave(claveTextoPlano))
            put(USU_ACTIVO, 1)
            put(USU_ROL, idRol) // 1L corresponde a ASEGURADO por defecto
        }
        // insert devuelve -1 si hay un error (por ejemplo, correo o RUT repetidos)
        return writableDatabase.insert(TABLA_USUARIOS, null, valores) != -1L
    }

    // ---------- Consulta de siniestro (asegurado) ----------

    fun existeSiniestro(numero: String): Boolean =
        readableDatabase.rawQuery(
            "SELECT 1 FROM $TABLA_SINIESTROS WHERE $SIN_NUMERO = ?",
            arrayOf(numero)
        ).use { it.moveToFirst() }

    // ---------- Panel del liquidador ----------

    fun siniestrosDeLiquidador(idLiquidador: Long): List<SiniestroResumen> {
        val lista = mutableListOf<SiniestroResumen>()
        readableDatabase.rawQuery(
            "SELECT s.$SIN_NUMERO, u.$USU_NOMBRE, s.$SIN_TIPO, s.$SIN_ESTADO, s.$SIN_FECHA " +
                    "FROM $TABLA_SINIESTROS s " +
                    "INNER JOIN $TABLA_USUARIOS u ON s.$SIN_ASEGURADO = u.$USU_ID " +
                    "WHERE s.$SIN_LIQUIDADOR = ? ORDER BY s.$SIN_FECHA DESC",
            arrayOf(idLiquidador.toString())
        ).use { c ->
            while (c.moveToNext()) {
                lista.add(
                    SiniestroResumen(
                        c.getString(0), c.getString(1), c.getString(2),
                        c.getString(3), c.getString(4)
                    )
                )
            }
        }
        return lista
    }

    fun resumenLiquidador(idLiquidador: Long): ResumenPanel =
        readableDatabase.rawQuery(
            "SELECT COUNT(*), " +
                    "SUM(CASE WHEN $SIN_ESTADO = 'En evaluación' THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN $SIN_DOCS_PENDIENTES > 0 THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN $SIN_ESTADO = 'Cerrado' AND $SIN_FECHA >= date('now','start of month') THEN 1 ELSE 0 END) " +
                    "FROM $TABLA_SINIESTROS WHERE $SIN_LIQUIDADOR = ?",
            arrayOf(idLiquidador.toString())
        ).use { c ->
            c.moveToFirst()
            ResumenPanel(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3))
        }

    fun detalleSiniestro(numero: String): DetalleSiniestro? {
        val db = readableDatabase
        val docs = mutableListOf<Documento>()
        db.rawQuery(
            "SELECT $DOC_NOMBRE, $DOC_RECIBIDO FROM $TABLA_DOCUMENTOS WHERE $DOC_SINIESTRO = ?",
            arrayOf(numero)
        ).use { c ->
            while (c.moveToNext()) {
                docs.add(Documento(c.getString(0), c.getInt(1) == 1))
            }
        }
        return db.rawQuery(
            "SELECT s.$SIN_NUMERO, u.$USU_NOMBRE, s.$SIN_PLACA, s.$SIN_PRIORIDAD, s.$SIN_ESTADO, s.$SIN_COMENTARIO " +
                    "FROM $TABLA_SINIESTROS s " +
                    "INNER JOIN $TABLA_USUARIOS u ON s.$SIN_ASEGURADO = u.$USU_ID " +
                    "WHERE s.$SIN_NUMERO = ?",
            arrayOf(numero)
        ).use { c ->
            if (c.moveToFirst()) {
                DetalleSiniestro(
                    numero = c.getString(0),
                    cliente = c.getString(1),
                    placa = c.getString(2) ?: "",
                    prioridad = c.getString(3),
                    estado = c.getString(4),
                    comentario = c.getString(5) ?: "",
                    documentos = docs
                )
            } else null
        }
    }

    fun actualizarSiniestro(numero: String, estado: String, comentario: String): Boolean {
        val valores = ContentValues().apply {
            put(SIN_ESTADO, estado)
            put(SIN_COMENTARIO, comentario)
        }
        return writableDatabase.update(
            TABLA_SINIESTROS, valores, "$SIN_NUMERO = ?", arrayOf(numero)
        ) > 0
    }

    fun solicitarDocumento(numero: String, nombre: String) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.execSQL(
                "INSERT INTO $TABLA_DOCUMENTOS ($DOC_SINIESTRO, $DOC_NOMBRE, $DOC_RECIBIDO) VALUES (?, ?, 0)",
                arrayOf<Any?>(numero, nombre)
            )
            db.execSQL(
                "UPDATE $TABLA_SINIESTROS SET $SIN_DOCS_PENDIENTES = $SIN_DOCS_PENDIENTES + 1 WHERE $SIN_NUMERO = ?",
                arrayOf<Any?>(numero)
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}