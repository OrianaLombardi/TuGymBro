package com.tugymbro.app.di

import com.tugymbro.app.data.repository.SupabaseChatRepository
import com.tugymbro.app.data.repository.SupabaseDiscoveryRepository
import com.tugymbro.app.data.repository.SupabaseMatchRepository
import com.tugymbro.app.data.repository.SupabaseUserRepository
import com.tugymbro.app.domain.repository.ChatRepository
import com.tugymbro.app.domain.repository.DiscoveryRepository
import com.tugymbro.app.domain.repository.MatchRepository
import com.tugymbro.app.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Conectado al proyecto real de Supabase (ver data/repository/SupabaseRepositories.kt
 * y supabase/schema.sql). Si en algún momento se quiere volver a probar la UI
 * sin backend (por ejemplo, sin conexión a internet), alcanza con cambiar
 * estos cuatro @Binds por los Mock*Repository de data/repository/MockRepositories.kt.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindUserRepository(impl: SupabaseUserRepository): UserRepository

    @Binds
    abstract fun bindDiscoveryRepository(impl: SupabaseDiscoveryRepository): DiscoveryRepository

    @Binds
    abstract fun bindMatchRepository(impl: SupabaseMatchRepository): MatchRepository

    @Binds
    abstract fun bindChatRepository(impl: SupabaseChatRepository): ChatRepository
}
