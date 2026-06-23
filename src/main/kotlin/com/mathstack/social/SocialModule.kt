package com.mathstack.social

import com.mathstack.social.application.AcceptFriendRequestUseCase
import com.mathstack.social.application.CreateChallengeUseCase
import com.mathstack.social.application.ListFriendsUseCase
import com.mathstack.social.application.SendFriendRequestUseCase
import com.mathstack.social.application.SubmitChallengeResultUseCase
import com.mathstack.social.domain.repository.SocialRepository
import com.mathstack.social.infrastructure.persistence.PostgresSocialRepository
import org.koin.dsl.module

val socialModule = module {
    single<SocialRepository> { PostgresSocialRepository() }
    single { SendFriendRequestUseCase(get()) }
    single { AcceptFriendRequestUseCase(get()) }
    single { ListFriendsUseCase(get()) }
    single { CreateChallengeUseCase(get()) }
    single { SubmitChallengeResultUseCase(get()) }
}
