package com.mathstack.admin.application

import com.mathstack.practice.domain.model.DiagnosticResult
import com.mathstack.practice.domain.repository.PracticeRepository

class ListAllDiagnosticsUseCase(private val practiceRepository: PracticeRepository) {
    operator fun invoke(): List<DiagnosticResult> {
        return practiceRepository.findAllDiagnostics()
    }
}
