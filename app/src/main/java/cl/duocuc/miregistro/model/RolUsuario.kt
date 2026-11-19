package cl.duocuc.miregistro.model
enum class RolUsuario (val id: Long) {
    ASEGURADO(1L),
    LIQUIDADOR(2L),
    ANALISTA(3L);

    companion object {

        fun desdeId(id: Long): RolUsuario? = entries.firstOrNull { it.id == id }
    }
}