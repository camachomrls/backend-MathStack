package com.mathstack.shared.infrastructure.email

interface EmailService {
    fun sendEmail(to: String, subject: String, htmlContent: String)
}
