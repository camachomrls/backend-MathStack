package com.mathstack.admin.application

class GenerateAvatarUseCase {
    operator fun invoke(seed: String, style: String = "bottts"): String {
        return "https://api.dicebear.com/9.x/$style/svg?seed=$seed"
    }
}
