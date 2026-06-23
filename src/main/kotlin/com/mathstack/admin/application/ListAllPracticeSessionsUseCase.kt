package com.mathstack.admin.application

import com.mathstack.practice.domain.model.PracticeSession
import com.mathstack.practice.domain.repository.PracticeRepository

class ListAllPracticeSessionsUseCase(private val practiceRepository: PracticeRepository) {
    operator fun invoke(): List<PracticeSession> {
        return practiceRepository.findAllSessions()
    }
}
