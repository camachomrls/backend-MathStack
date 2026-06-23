package com.mathstack.admin

import com.mathstack.admin.application.GenerateAvatarUseCase
import com.mathstack.admin.application.ListAllUsersUseCase
import com.mathstack.admin.application.UpdateUserCoinsUseCase
import org.koin.dsl.module

val adminModule = module {
    factory { ListAllUsersUseCase(get()) }
    factory { GenerateAvatarUseCase() }
    factory { UpdateUserCoinsUseCase(get()) }
    factory { com.mathstack.admin.application.GetUserProfileUseCase(get()) }
    factory { com.mathstack.admin.application.ListAllDiagnosticsUseCase(get()) }
    factory { com.mathstack.admin.application.ListAllPracticeSessionsUseCase(get()) }
}
