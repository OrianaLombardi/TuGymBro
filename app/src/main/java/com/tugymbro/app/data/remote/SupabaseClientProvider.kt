package com.tugymbro.app.data.remote

import com.tugymbro.app.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

/**
 * Punto único de acceso a Supabase (proyecto real conectado).
 *
 * Auth: se usa sesión anónima (signInAnonymously) porque todavía no hay
 * pantalla de registro/login en la app. Esto le da a cada instalación un
 * auth.uid() estable, que es lo que usan las políticas de Row Level
 * Security en supabase/schema.sql para saber "quién es el dueño" de cada
 * fila. Hay que habilitar "Anonymous Sign-ins" en el dashboard de Supabase
 * (Authentication > Settings) para que esto funcione.
 *
 * Cuando se agregue un login real (email, Google, etc.), alcanza con
 * reemplazar ensureSignedIn() por el flujo correspondiente: el resto del
 * código (repositorios, RLS) no cambia porque todo se basa en auth.uid().
 */
object SupabaseClientProvider {

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }

    /** Garantiza que haya una sesión activa antes de cualquier llamada que dependa de auth.uid(). */
    suspend fun ensureSignedIn(): String {
        val currentUser = client.auth.currentUserOrNull()
        if (currentUser != null) return currentUser.id

        client.auth.signInAnonymously()
        return checkNotNull(client.auth.currentUserOrNull()?.id) {
            "No se pudo crear una sesión anónima. Revisar que 'Anonymous Sign-ins' " +
                "esté habilitado en Authentication > Settings del proyecto de Supabase."
        }
    }
}
