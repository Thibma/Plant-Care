package fr.thibma.plantcare.models.response

import com.google.gson.annotations.SerializedName

// Réponse a l'api Kotlin Food Facts
class ApiResponse (
    @SerializedName("error") var error: Boolean,
    @SerializedName("message") var message: Any
)
