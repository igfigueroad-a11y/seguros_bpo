package cl.duocuc.miregistro.model
enum class RolUsuario (val id: Long) {
    ASEGURADO(1L),
    LIQUIDADOR(2L),
    ANALISTA(3L);

    companion object {
        // Convierte el id_rol de la base de datos en el rol de la app
        fun desdeId(id: Long): RolUsuario? = entries.firstOrNull { it.id == id }
    }
}