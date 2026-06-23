package com.mathstack.store.infrastructure.rest

import com.mathstack.store.application.BuyItemUseCase
import com.mathstack.store.application.CreateItemTypeUseCase
import com.mathstack.store.application.CreateStoreItemUseCase
import com.mathstack.store.application.EquipItemUseCase
import com.mathstack.store.application.ListInventoryUseCase
import com.mathstack.store.application.ListItemTypesUseCase
import com.mathstack.store.application.ListStoreItemsUseCase
import com.mathstack.store.infrastructure.rest.dto.CreateItemTypeRequest
import com.mathstack.store.infrastructure.rest.dto.CreateStoreItemRequest
import com.mathstack.store.infrastructure.rest.dto.ItemActionRequest
import com.mathstack.store.infrastructure.rest.dto.itemUuid
import com.mathstack.store.infrastructure.rest.dto.toCommand
import com.mathstack.store.infrastructure.rest.dto.toResponse
import com.mathstack.store.infrastructure.rest.dto.toUuid
import com.mathstack.store.infrastructure.rest.dto.validName
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import com.mathstack.shared.infrastructure.plugins.authorize

fun Route.storeRouting() {
    val createItemType by inject<CreateItemTypeUseCase>()
    val listItemTypes by inject<ListItemTypesUseCase>()
    val createStoreItem by inject<CreateStoreItemUseCase>()
    val listStoreItems by inject<ListStoreItemsUseCase>()
    val buyItem by inject<BuyItemUseCase>()
    val equipItem by inject<EquipItemUseCase>()
    val listInventory by inject<ListInventoryUseCase>()

    authenticate("auth-jwt") {
        route("/api/v1/store") {
            get("/item-types") { call.respond(listItemTypes().map { it.toResponse() }) }
            get("/items") { call.respond(listStoreItems().map { it.toResponse() }) }
            
            authorize("ADMIN") {
                post("/item-types") {
                    val type = createItemType(call.receive<CreateItemTypeRequest>().validName())
                    call.respond(HttpStatusCode.Created, type.toResponse())
                }
                post("/items") {
                    val item = createStoreItem(call.receive<CreateStoreItemRequest>().toCommand())
                    call.respond(HttpStatusCode.Created, item.toResponse())
                }
            }
            get("/users/{userId}/inventory") {
                val userId = (call.parameters["userId"] ?: "").toUuid("userId")
                call.respond(listInventory(userId).map { it.toResponse() })
            }
            post("/users/{userId}/buy") {
                val userId = (call.parameters["userId"] ?: "").toUuid("userId")
                val item = buyItem(userId, call.receive<ItemActionRequest>().itemUuid())
                call.respond(HttpStatusCode.Created, item.toResponse())
            }
            post("/users/{userId}/equip") {
                val userId = (call.parameters["userId"] ?: "").toUuid("userId")
                val item = equipItem(userId, call.receive<ItemActionRequest>().itemUuid())
                call.respond(HttpStatusCode.OK, item.toResponse())
            }
        }
    }
}
