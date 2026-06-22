package com.mathstack.store

import com.mathstack.store.application.BuyItemUseCase
import com.mathstack.store.application.CreateItemTypeUseCase
import com.mathstack.store.application.CreateStoreItemUseCase
import com.mathstack.store.application.EquipItemUseCase
import com.mathstack.store.application.ListInventoryUseCase
import com.mathstack.store.application.ListItemTypesUseCase
import com.mathstack.store.application.ListStoreItemsUseCase
import com.mathstack.store.domain.repository.StoreRepository
import com.mathstack.store.infrastructure.persistence.PostgresStoreRepository
import org.koin.dsl.module

val storeModule = module {
    single<StoreRepository> { PostgresStoreRepository() }
    single { CreateItemTypeUseCase(get()) }
    single { ListItemTypesUseCase(get()) }
    single { CreateStoreItemUseCase(get()) }
    single { ListStoreItemsUseCase(get()) }
    single { BuyItemUseCase(get()) }
    single { EquipItemUseCase(get()) }
    single { ListInventoryUseCase(get()) }
}
