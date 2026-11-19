package cl.duocuc.miregistro.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.duocuc.miregistro.model.AlertaDocumento
import cl.duocuc.miregistro.model.DetalleSiniestro
import cl.duocuc.miregistro.model.Documento
import cl.duocuc.miregistro.model.EvidenciaArchivo
import cl.duocuc.miregistro.model.FiltroSiniestro
import cl.duocuc.miregistro.model.ResumenAsegurado
import cl.duocuc.miregistro.model.ResumenPanel
import cl.duocuc.miregistro.model.SiniestroAsegurado
import cl.duocuc.miregistro.model.SiniestroResumen
import cl.duocuc.miregistro.model.Usuario
import cl.duocuc.miregistro.ui.components.BANCO_PREGUNTAS_SEGURIDAD
import java.security.MessageDigest

data class SiniestroAdmin(
    val numero: String,
    val tipo: String,
    val descripcion: String,
    val fecha: String,
    val estado: String,
    val placa: String,
    val prioridad: String,
    val aseguradoNombre: String,
    val liquidadorId: Long?,
    val liquidadorNombre: String?
)

class UsuarioDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "seguimiento_siniestros.db"
        private const val VERSION_BD = 9

        val ORDEN_ESTADOS = listOf(
            "Recibido",
            "En evaluación",
            "En liquidación",
            "Cerrado"
        )

        fun estadosPermitidosDesde(estadoActual: String): List<String> {
            val indice = ORDEN_ESTADOS.indexOfFirst { it.equals(estadoActual, ignoreCase = true) }
            return if (indice == -1) ORDEN_ESTADOS else ORDEN_ESTADOS.drop(indice)
        }

        private const val TABLA_ROLES = "roles"
        private const val ROL_ID = "id_rol"
        private const val ROL_NOMBRE = "nombre_rol"

        private const val TABLA_USUARIOS = "usuarios"
        private const val USU_ID = "id_usuario"
        private const val USU_NOMBRE = "nombre"
        private const val USU_RUT = "rut"
        private const val USU_CORREO = "correo"
        private const val USU_CLAVE = "clave_hash"
        private const val USU_ACTIVO = "activo"
        private const val USU_ROL = "id_rol"

        private const val USU_PREG1 = "pregunta1"
        private const val USU_RESP1 = "respuesta1"
        private const val USU_PREG2 = "pregunta2"
        private const val USU_RESP2 = "respuesta2"
        private const val USU_PREG3 = "pregunta3"
        private const val USU_RESP3 = "respuesta3"

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

        private const val TABLA_DOCUMENTOS = "documentos"
        private const val DOC_ID = "id_documento"
        private const val DOC_SINIESTRO = "numero_siniestro"
        private const val DOC_NOMBRE = "nombre"
        private const val DOC_RECIBIDO = "recibido"

        private const val TABLA_EVIDENCIA = "evidencia"
        private const val EVI_ID = "id_evidencia"
        private const val EVI_SINIESTRO = "numero_siniestro"
        private const val EVI_NOMBRE = "nombre_archivo"
        private const val EVI_ES_IMAGEN = "es_imagen"
        private const val EVI_IMAGEN = "imagen_blob"
        private const val EVI_FECHA = "fecha_subida"

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
                    "$USU_ROL INTEGER NOT NULL REFERENCES $TABLA_ROLES($ROL_ID), " +
                    "$USU_PREG1 TEXT, " +
                    "$USU_RESP1 TEXT, " +
                    "$USU_PREG2 TEXT, " +
                    "$USU_RESP2 TEXT, " +
                    "$USU_PREG3 TEXT, " +
                    "$USU_RESP3 TEXT)"

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

        private const val SQL_CREAR_EVIDENCIA =
            "CREATE TABLE $TABLA_EVIDENCIA (" +
                    "$EVI_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$EVI_SINIESTRO TEXT NOT NULL REFERENCES $TABLA_SINIESTROS($SIN_NUMERO), " +
                    "$EVI_NOMBRE TEXT NOT NULL, " +
                    "$EVI_ES_IMAGEN INTEGER NOT NULL, " +
                    "$EVI_IMAGEN BLOB NOT NULL, " +
                    "$EVI_FECHA DATETIME DEFAULT CURRENT_TIMESTAMP)"

        private const val SQL_PRECARGAR_ROLES =
            "INSERT INTO $TABLA_ROLES ($ROL_ID, $ROL_NOMBRE) VALUES " +
                    "(1, 'ASEGURADO'), (2, 'LIQUIDADOR'), (3, 'ANALISTA'), (4, 'ADMIN')"

        private const val SQL_SELECT_USUARIOS =
            "SELECT u.*, r.$ROL_NOMBRE AS $ROL_NOMBRE " +
                    "FROM $TABLA_USUARIOS u " +
                    "INNER JOIN $TABLA_ROLES r ON u.$USU_ROL = r.$ROL_ID"

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
        db.execSQL(SQL_CREAR_EVIDENCIA)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_EVIDENCIA")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_DOCUMENTOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_SINIESTROS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_USUARIOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_ROLES")
        onCreate(db)
    }

    private fun precargarUsuarios(db: SQLiteDatabase) {
        val p1 = BANCO_PREGUNTAS_SEGURIDAD[0]
        val p2 = BANCO_PREGUNTAS_SEGURIDAD[1]
        val p3 = BANCO_PREGUNTAS_SEGURIDAD[2]

        val ejemplos = listOf(
            arrayOf("Camila Rojas Fuentes", "15842371-5", "camila.rojas@correo.ejemplo", 1L, "Cachupin", "Santiago", "Titanic"),
            arrayOf("Jorge Pizarro Contreras", "9418552-1", "jorge.pizarro@correo.ejemplo", 1L, "Bobby", "Valparaiso", "Avatar"),
            arrayOf("Valentina Soto Muñoz", "19205884-8", "vale.soto@correo.ejemplo", 1L, "Luna", "Concepcion", "Inception"),
            arrayOf("Paulina Fernández Ruiz", "11111111-1", "paulina.fernandez@andes-bpo.ejemplo", 2L, "Max", "Temuco", "Matrix"),
            arrayOf("Matías Gallardo Silva", "12222222-2", "matias.gallardo@andes-bpo.ejemplo", 2L, "Rocky", "La Serena", "Gladiador"),
            arrayOf("Natalia Reyes Campos", "13333333-3", "natalia.reyes@andes-bpo.ejemplo", 3L, "Kira", "Antofagasta", "Interstellar"),
            arrayOf("Administrador General", "10000000-0", "admin@andes-bpo.ejemplo", 4L, "Rex", "Santiago", "Matrix")
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
                put(USU_PREG1, p1)
                put(USU_RESP1, e[4] as String)
                put(USU_PREG2, p2)
                put(USU_RESP2, e[5] as String)
                put(USU_PREG3, p3)
                put(USU_RESP3, e[6] as String)
            }
            db.insert(TABLA_USUARIOS, null, valores)
        }
    }

    private fun precargarSiniestros(db: SQLiteDatabase) {
        val ejemplos = listOf(
            arrayOf<Any?>("SIN-26-008942", "Daño vehicular", "Choque trasero", "2026-10-23", "En evaluación", "ABC123", "Media", 1, 1L, 4L),
            arrayOf<Any?>("SIN-26-006871", "Robo total", "Robo del vehículo", "2026-10-22", "Recibido", "DEF456", "Alta", 0, 2L, 4L),
            arrayOf<Any?>("SIN-26-006810", "Daño a propiedad", "Daño a portón", "2026-10-22", "En liquidación", "GHI789", "Baja", 0, 3L, 4L),
            arrayOf<Any?>("SIN-26-004567", "Daño vehicular", "Granizo en techo", "2026-10-05", "Cerrado", "JKL012", "Baja", 0, 1L, 4L),
            arrayOf<Any?>("SIN-26-001234", "Robo parcial", "Robo de accesorios", "2026-10-12", "En evaluación", "MNO345", "Media", 2, 2L, 5L),
            arrayOf<Any?>("SIN-26-009105", "Daño vehicular", "Impacto lateral", "2026-11-15", "Recibido", "PQR987", "Alta", 0, 1L, null),
            arrayOf<Any?>("SIN-26-009210", "Robo parcial", "Robo de espejos", "2026-11-16", "Recibido", "XYZ555", "Media", 0, 3L, null)
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
                val idLiquidador = e[9] as Long?
                if (idLiquidador != null) put(SIN_LIQUIDADOR, idLiquidador) else putNull(SIN_LIQUIDADOR)
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

    fun guardarEvidencia(numeroSiniestro: String, nombre: String, esImagen: Boolean, imagenBlob: ByteArray): Boolean {
        val valores = ContentValues().apply {
            put(EVI_SINIESTRO, numeroSiniestro)
            put(EVI_NOMBRE, nombre)
            put(EVI_ES_IMAGEN, if (esImagen) 1 else 0)
            put(EVI_IMAGEN, imagenBlob)
        }
        return writableDatabase.insert(TABLA_EVIDENCIA, null, valores) != -1L
    }

    fun obtenerEvidencias(numeroSiniestro: String): List<EvidenciaArchivo> {
        val lista = mutableListOf<EvidenciaArchivo>()
        readableDatabase.rawQuery(
            "SELECT $EVI_NOMBRE, $EVI_ES_IMAGEN, $EVI_IMAGEN FROM $TABLA_EVIDENCIA WHERE $EVI_SINIESTRO = ?",
            arrayOf(numeroSiniestro)
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    EvidenciaArchivo(
                        nombre = cursor.getString(0),
                        esImagen = cursor.getInt(1) == 1,
                        bytes = cursor.getBlob(2)
                    )
                )
            }
        }
        return lista
    }

    fun validarLogin(correo: String, clave: String): Usuario? =
        consultarUsuarios(
            "$SQL_SELECT_USUARIOS WHERE u.$USU_CORREO = ? AND u.$USU_CLAVE = ? AND u.$USU_ACTIVO = 1",
            arrayOf(correo.trim().lowercase(), hashearClave(clave))
        ).firstOrNull()

    fun buscarPorId(id: Long): Usuario? =
        consultarUsuarios("$SQL_SELECT_USUARIOS WHERE u.$USU_ID = ?", arrayOf(id.toString()))
            .firstOrNull()

    fun listarLiquidadores(): List<Usuario> =
        consultarUsuarios(
            "$SQL_SELECT_USUARIOS WHERE u.$USU_ROL = 2 AND u.$USU_ACTIVO = 1 ORDER BY u.$USU_NOMBRE ASC",
            null
        )

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
        preg1: String,
        resp1: String,
        preg2: String,
        resp2: String,
        preg3: String,
        resp3: String,
        idRol: Long = 1L
    ): Boolean {
        val valores = ContentValues().apply {
            put(USU_NOMBRE, nombre.trim())
            put(USU_RUT, rut.trim())
            put(USU_CORREO, correo.trim().lowercase())
            put(USU_CLAVE, hashearClave(claveTextoPlano))
            put(USU_ACTIVO, 1)
            put(USU_ROL, idRol)
            put(USU_PREG1, preg1.trim())
            put(USU_RESP1, resp1.trim())
            put(USU_PREG2, preg2.trim())
            put(USU_RESP2, resp2.trim())
            put(USU_PREG3, preg3.trim())
            put(USU_RESP3, resp3.trim())
        }
        return writableDatabase.insert(TABLA_USUARIOS, null, valores) != -1L
    }

    fun existeSiniestro(numero: String): Boolean =
        readableDatabase.rawQuery("SELECT 1 FROM $TABLA_SINIESTROS WHERE $SIN_NUMERO = ?", arrayOf(numero)).use { it.moveToFirst() }

    fun todosLosSiniestrosAdmin(): List<SiniestroAdmin> {
        val lista = mutableListOf<SiniestroAdmin>()
        val sql = """
            SELECT s.$SIN_NUMERO, s.$SIN_TIPO, s.$SIN_DESCRIPCION, s.$SIN_FECHA,
                   s.$SIN_ESTADO, s.$SIN_PLACA, s.$SIN_PRIORIDAD,
                   a.$USU_NOMBRE, s.$SIN_LIQUIDADOR, l.$USU_NOMBRE
            FROM $TABLA_SINIESTROS s
            INNER JOIN $TABLA_USUARIOS a ON s.$SIN_ASEGURADO = a.$USU_ID
            LEFT JOIN $TABLA_USUARIOS l ON s.$SIN_LIQUIDADOR = l.$USU_ID
            ORDER BY CASE WHEN s.$SIN_LIQUIDADOR IS NULL THEN 0 ELSE 1 END, s.$SIN_FECHA DESC
        """.trimIndent()

        readableDatabase.rawQuery(sql, null).use { c ->
            while (c.moveToNext()) {
                val idLiq = if (c.isNull(8)) null else c.getLong(8)
                val nomLiq = if (c.isNull(9)) null else c.getString(9)
                lista.add(
                    SiniestroAdmin(
                        numero = c.getString(0),
                        tipo = c.getString(1),
                        descripcion = c.getString(2) ?: "",
                        fecha = c.getString(3),
                        estado = c.getString(4),
                        placa = c.getString(5) ?: "",
                        prioridad = c.getString(6),
                        aseguradoNombre = c.getString(7),
                        liquidadorId = idLiq,
                        liquidadorNombre = nomLiq
                    )
                )
            }
        }
        return lista
    }

    fun asignarLiquidador(numeroSiniestro: String, idLiquidador: Long): Boolean {
        val valores = ContentValues().apply {
            put(SIN_LIQUIDADOR, idLiquidador)
        }
        return writableDatabase.update(
            TABLA_SINIESTROS,
            valores,
            "$SIN_NUMERO = ?",
            arrayOf(numeroSiniestro)
        ) > 0
    }

    fun siniestrosDeLiquidador(idLiquidador: Long): List<SiniestroResumen> {
        val lista = mutableListOf<SiniestroResumen>()
        readableDatabase.rawQuery(
            "SELECT s.$SIN_NUMERO, u.$USU_NOMBRE, s.$SIN_TIPO, s.$SIN_ESTADO, s.$SIN_FECHA " +
                    "FROM $TABLA_SINIESTROS s INNER JOIN $TABLA_USUARIOS u ON s.$SIN_ASEGURADO = u.$USU_ID " +
                    "WHERE s.$SIN_LIQUIDADOR = ? ORDER BY s.$SIN_FECHA DESC",
            arrayOf(idLiquidador.toString())
        ).use { c ->
            while (c.moveToNext()) {
                lista.add(SiniestroResumen(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4)))
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
        db.rawQuery("SELECT $DOC_NOMBRE, $DOC_RECIBIDO FROM $TABLA_DOCUMENTOS WHERE $DOC_SINIESTRO = ?", arrayOf(numero)).use { c ->
            while (c.moveToNext()) { docs.add(Documento(c.getString(0), c.getInt(1) == 1)) }
        }

        val evidenciasBlobs = obtenerEvidencias(numero)

        return db.rawQuery(
            "SELECT s.$SIN_NUMERO, u.$USU_NOMBRE, s.$SIN_PLACA, s.$SIN_PRIORIDAD, s.$SIN_ESTADO, s.$SIN_COMENTARIO " +
                    "FROM $TABLA_SINIESTROS s INNER JOIN $TABLA_USUARIOS u ON s.$SIN_ASEGURADO = u.$USU_ID " +
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
                    documentos = docs,
                    evidencias = evidenciasBlobs
                )
            } else null
        }
    }

    fun actualizarEstadoSiniestro(numero: String, estado: String): Boolean {
        val actual = readableDatabase.rawQuery(
            "SELECT $SIN_ESTADO FROM $TABLA_SINIESTROS WHERE $SIN_NUMERO = ?",
            arrayOf(numero)
        ).use { if (it.moveToFirst()) it.getString(0) else null } ?: return false

        val indiceActual = ORDEN_ESTADOS.indexOfFirst { it.equals(actual, ignoreCase = true) }
        val indiceNuevo = ORDEN_ESTADOS.indexOfFirst { it.equals(estado, ignoreCase = true) }

        if (indiceNuevo == -1 || (indiceActual != -1 && indiceNuevo < indiceActual)) {
            return false
        }

        val valores = ContentValues().apply { put(SIN_ESTADO, estado) }
        return writableDatabase.update(TABLA_SINIESTROS, valores, "$SIN_NUMERO = ?", arrayOf(numero)) > 0
    }

    fun actualizarComentarioSiniestro(numero: String, comentario: String): Boolean {
        val valores = ContentValues().apply { put(SIN_COMENTARIO, comentario) }
        return writableDatabase.update(TABLA_SINIESTROS, valores, "$SIN_NUMERO = ?", arrayOf(numero)) > 0
    }

    fun solicitarDocumento(numero: String, nombre: String) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.execSQL("INSERT INTO $TABLA_DOCUMENTOS ($DOC_SINIESTRO, $DOC_NOMBRE, $DOC_RECIBIDO) VALUES (?, ?, 0)", arrayOf<Any?>(numero, nombre))
            db.execSQL("UPDATE $TABLA_SINIESTROS SET $SIN_DOCS_PENDIENTES = $SIN_DOCS_PENDIENTES + 1 WHERE $SIN_NUMERO = ?", arrayOf<Any?>(numero))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun buscarSiniestroPorNumero(numero: String): cl.duocuc.miregistro.model.Siniestro? {
        val db = readableDatabase
        val query = """
            SELECT s.$SIN_ID, s.$SIN_NUMERO, u.$USU_NOMBRE, s.$SIN_TIPO, s.$SIN_FECHA, s.$SIN_ESTADO, s.$SIN_COMENTARIO
            FROM $TABLA_SINIESTROS s
            INNER JOIN $TABLA_USUARIOS u ON s.$SIN_ASEGURADO = u.$USU_ID
            WHERE s.$SIN_NUMERO = ?
        """.trimIndent()

        var siniestro: cl.duocuc.miregistro.model.Siniestro? = null
        db.rawQuery(query, arrayOf(numero.trim().uppercase())).use { cursor ->
            if (cursor.moveToFirst()) {
                val estadoStr = cursor.getString(5)
                val comentarioLiquidador = cursor.getString(6) ?: ""

                val idEtapaCalculado = when (estadoStr) {
                    "Recibido" -> 1L
                    "En evaluación" -> 2L
                    "En liquidación" -> 3L
                    "Cerrado" -> 4L
                    else -> 1L
                }

                siniestro = cl.duocuc.miregistro.model.Siniestro(
                    idSiniestro = cursor.getLong(0),
                    numero = cursor.getString(1),
                    aseguradoNombre = cursor.getString(2),
                    companiaNombre = "Andes BPO",
                    tipoSiniestro = cursor.getString(3),
                    fechaOcurrencia = cursor.getString(4),
                    idEtapa = idEtapaCalculado,
                    etapaNombre = estadoStr,
                    fechaUltimaActualizacion = cursor.getString(4),
                    comentario = comentarioLiquidador
                )
            }
        }
        return siniestro
    }

    fun buscarSiniestroSeguro(numero: String, idUsuario: Long): cl.duocuc.miregistro.model.Siniestro? {
        val db = readableDatabase
        val query = """
            SELECT s.$SIN_ID, s.$SIN_NUMERO, u.$USU_NOMBRE, s.$SIN_TIPO, s.$SIN_FECHA, s.$SIN_ESTADO, s.$SIN_COMENTARIO
            FROM $TABLA_SINIESTROS s
            INNER JOIN $TABLA_USUARIOS u ON s.$SIN_ASEGURADO = u.$USU_ID
            WHERE s.$SIN_NUMERO = ? AND s.$SIN_ASEGURADO = ?
        """.trimIndent()

        var siniestro: cl.duocuc.miregistro.model.Siniestro? = null
        db.rawQuery(query, arrayOf(numero.trim().uppercase(), idUsuario.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                val estadoStr = cursor.getString(5)
                val comentarioLiquidador = cursor.getString(6) ?: ""

                val idEtapaCalculado = when (estadoStr) {
                    "Recibido" -> 1L
                    "En evaluación" -> 2L
                    "En liquidación" -> 3L
                    "Cerrado" -> 4L
                    else -> 1L
                }

                siniestro = cl.duocuc.miregistro.model.Siniestro(
                    idSiniestro = cursor.getLong(0),
                    numero = cursor.getString(1),
                    aseguradoNombre = cursor.getString(2),
                    companiaNombre = "Andes BPO",
                    tipoSiniestro = cursor.getString(3),
                    fechaOcurrencia = cursor.getString(4),
                    idEtapa = idEtapaCalculado,
                    etapaNombre = estadoStr,
                    fechaUltimaActualizacion = cursor.getString(4),
                    comentario = comentarioLiquidador
                )
            }
        }
        return siniestro
    }

    fun validarRespuestasSeguridad(
        correo: String,
        preg1: String,
        resp1: String,
        preg2: String,
        resp2: String,
        preg3: String,
        resp3: String
    ): Boolean {
        val query = """
            SELECT $USU_PREG1, $USU_RESP1, $USU_PREG2, $USU_RESP2, $USU_PREG3, $USU_RESP3
            FROM $TABLA_USUARIOS
            WHERE $USU_CORREO = ? AND $USU_ACTIVO = 1
        """.trimIndent()

        readableDatabase.rawQuery(query, arrayOf(correo.trim().lowercase())).use { cursor ->
            if (!cursor.moveToFirst()) return false

            val guardadas = mapOf(
                (cursor.getString(0) ?: "") to (cursor.getString(1) ?: "").trim().lowercase(),
                (cursor.getString(2) ?: "") to (cursor.getString(3) ?: "").trim().lowercase(),
                (cursor.getString(4) ?: "") to (cursor.getString(5) ?: "").trim().lowercase()
            )

            val ingresadas = listOf(
                preg1.trim() to resp1.trim().lowercase(),
                preg2.trim() to resp2.trim().lowercase(),
                preg3.trim() to resp3.trim().lowercase()
            )

            if (ingresadas.map { it.first }.toSet().size != 3) return false
            return ingresadas.all { (pregunta, respuesta) ->
                respuesta.isNotBlank() && guardadas[pregunta] == respuesta
            }
        }
    }

    fun actualizarContrasena(correo: String, nuevaClave: String): Boolean {
        val valores = ContentValues().apply {
            put("clave_hash", hashearClave(nuevaClave))
        }
        return writableDatabase.update("usuarios", valores, "correo = ?", arrayOf(correo.trim().lowercase())) > 0
    }

    fun actualizarPerfil(idUsuario: Long, nombre: String, correo: String): Boolean {
        val correoLimpio = correo.trim().lowercase()
        val correoDuplicado = readableDatabase.rawQuery(
            "SELECT 1 FROM $TABLA_USUARIOS WHERE $USU_CORREO = ? AND $USU_ID <> ?",
            arrayOf(correoLimpio, idUsuario.toString())
        ).use { it.moveToFirst() }

        if (correoDuplicado) return false

        val valores = ContentValues().apply {
            put(USU_NOMBRE, nombre.trim())
            put(USU_CORREO, correoLimpio)
        }
        return writableDatabase.update(
            TABLA_USUARIOS,
            valores,
            "$USU_ID = ?",
            arrayOf(idUsuario.toString())
        ) > 0
    }

    fun resumenAsegurado(idAsegurado: Long): ResumenAsegurado =
        readableDatabase.rawQuery(
            "SELECT COUNT(*), " +
                    "SUM(CASE WHEN $SIN_ESTADO <> 'Cerrado' THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN $SIN_ESTADO = 'Cerrado' THEN 1 ELSE 0 END) " +
                    "FROM $TABLA_SINIESTROS WHERE $SIN_ASEGURADO = ?",
            arrayOf(idAsegurado.toString())
        ).use { c ->
            c.moveToFirst()
            ResumenAsegurado(c.getInt(0), c.getInt(1), c.getInt(2))
        }

    fun siniestrosDeAsegurado(idAsegurado: Long, filtro: FiltroSiniestro): List<SiniestroAsegurado> {
        val condicion = when (filtro) {
            FiltroSiniestro.TODOS -> ""
            FiltroSiniestro.EN_PROCESO -> " AND $SIN_ESTADO <> 'Cerrado'"
            FiltroSiniestro.COMPLETADOS -> " AND $SIN_ESTADO = 'Cerrado'"
        }
        val lista = mutableListOf<SiniestroAsegurado>()
        readableDatabase.rawQuery(
            "SELECT $SIN_NUMERO, $SIN_TIPO, $SIN_DESCRIPCION, $SIN_ESTADO, $SIN_FECHA " +
                    "FROM $TABLA_SINIESTROS WHERE $SIN_ASEGURADO = ?$condicion " +
                    "ORDER BY $SIN_FECHA DESC",
            arrayOf(idAsegurado.toString())
        ).use { c ->
            while (c.moveToNext()) {
                lista.add(
                    SiniestroAsegurado(
                        c.getString(0), c.getString(1), c.getString(2) ?: "",
                        c.getString(3), c.getString(4)
                    )
                )
            }
        }
        return lista
    }

    fun alertasDocumentos(idAsegurado: Long): List<AlertaDocumento> {
        val lista = mutableListOf<AlertaDocumento>()
        readableDatabase.rawQuery(
            "SELECT d.$DOC_SINIESTRO, d.$DOC_NOMBRE FROM $TABLA_DOCUMENTOS d " +
                    "INNER JOIN $TABLA_SINIESTROS s ON d.$DOC_SINIESTRO = s.$SIN_NUMERO " +
                    "WHERE s.$SIN_ASEGURADO = ? AND d.$DOC_RECIBIDO = 0 " +
                    "ORDER BY d.$DOC_ID DESC",
            arrayOf(idAsegurado.toString())
        ).use { c ->
            while (c.moveToNext()) {
                lista.add(AlertaDocumento(c.getString(0), c.getString(1)))
            }
        }
        return lista
    }

    fun marcarDocumentoRecibido(numero: String, nombre: String) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.execSQL(
                "UPDATE $TABLA_DOCUMENTOS SET $DOC_RECIBIDO = 1 " +
                        "WHERE $DOC_SINIESTRO = ? AND $DOC_NOMBRE = ?",
                arrayOf<Any?>(numero, nombre)
            )
            db.execSQL(
                "UPDATE $TABLA_SINIESTROS SET $SIN_DOCS_PENDIENTES = " +
                        "MAX($SIN_DOCS_PENDIENTES - 1, 0) WHERE $SIN_NUMERO = ?",
                arrayOf<Any?>(numero)
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun cambiarClave(idUsuario: Long, claveActual: String, claveNueva: String): Boolean {
        val db = writableDatabase
        val coincide = db.rawQuery(
            "SELECT 1 FROM $TABLA_USUARIOS WHERE $USU_ID = ? AND $USU_CLAVE = ?",
            arrayOf(idUsuario.toString(), hashearClave(claveActual))
        ).use { it.moveToFirst() }
        if (!coincide) return false
        val valores = ContentValues().apply { put(USU_CLAVE, hashearClave(claveNueva)) }
        return db.update(TABLA_USUARIOS, valores, "$USU_ID = ?", arrayOf(idUsuario.toString())) > 0
    }
}