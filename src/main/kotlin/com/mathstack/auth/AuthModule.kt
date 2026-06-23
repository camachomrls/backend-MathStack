package com.mathstack.auth

import com.mathstack.auth.application.LoginUseCase
import com.mathstack.auth.application.RegisterUseCase
import com.mathstack.auth.domain.repository.PasswordHasher
import com.mathstack.auth.domain.repository.TokenService
import com.mathstack.auth.infrastructure.security.BCryptPasswordHasher
import com.mathstack.auth.infrastructure.security.JwtTokenService
import org.koin.dsl.module

val authModule = module {
    single<PasswordHasher> { BCryptPasswordHasher() }
    single<TokenService> { JwtTokenService(get()) }
    single { LoginUseCase(get(), get(), get()) }
    factory { RegisterUseCase(get(), get(), get()) }
    factory { com.mathstack.auth.application.LoginWithGoogleUseCase(get(), get()) }
}
