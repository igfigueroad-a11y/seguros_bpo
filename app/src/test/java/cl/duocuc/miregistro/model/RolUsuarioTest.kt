package cl.duocuc.miregistro.model

import org.junit.Assert.assertEquals
import org.junit.Test

// =====================================================================
//  NUEVO (Pruebas unitarias) — RolUsuarioTest.kt
//  ---------------------------------------------------------------------
//  Pantalla: Login.
//  Después de iniciar sesión, la app decide a qué pantalla ir según el rol
//  (LIQUIDADOR -> panel, ASEGURADO -> consulta).
//  RolUsuario.desdeId() convierte el id_rol de la base de datos en ese rol.
//
//  RolUsuario ya es Kotlin puro (un enum), así que no hubo que cambiar nada
//  para poder probarlo.
// =====================================================================

class RolUsuarioTest {

    @Test
    fun desdeId_conId2_devuelveLiquidador() {
        // Preparar
        val idRol = 2L

        // Ejecutar
        val rol = RolUsuario.desdeId(idRol)

        // Verificar
        assertEquals(RolUsuario.LIQUIDADOR, rol)
    }
}
