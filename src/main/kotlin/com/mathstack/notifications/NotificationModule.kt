package com.mathstack.notifications

import com.mathstack.notifications.application.RegisterDeviceTokenUseCase
import com.mathstack.notifications.application.UpdateNotificationPreferencesUseCase
import com.mathstack.notifications.domain.repository.NotificationRepository
import com.mathstack.notifications.infrastructure.persistence.PostgresNotificationRepository
import org.koin.dsl.module

val notificationModule = module {
    single<NotificationRepository> { PostgresNotificationRepository() }
    single { UpdateNotificationPreferencesUseCase(get()) }
    single { RegisterDeviceTokenUseCase(get()) }
}
