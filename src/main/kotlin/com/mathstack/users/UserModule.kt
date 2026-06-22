package com.mathstack.users

import com.mathstack.users.application.CreateUserUseCase
import com.mathstack.users.application.DeleteUserUseCase
import com.mathstack.users.application.GetUserProfileUseCase
import com.mathstack.users.application.UpdateGamificationStatsUseCase
import com.mathstack.users.application.UpdateUserUseCase
import com.mathstack.users.domain.repository.UserRepository
import com.mathstack.users.infrastructure.persistence.PostgresUserRepository
import org.koin.dsl.module

val userModule = module {
    single<UserRepository> { PostgresUserRepository() }

    single { CreateUserUseCase(get()) }
    single { DeleteUserUseCase(get()) }
    single { GetUserProfileUseCase(get()) }
    single { UpdateGamificationStatsUseCase(get()) }
    single { UpdateUserUseCase(get()) }
}
