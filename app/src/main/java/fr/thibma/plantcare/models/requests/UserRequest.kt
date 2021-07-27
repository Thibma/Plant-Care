package fr.thibma.plantcare.models.requests

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserRequest(
    @SerializedName("pseudo") var pseudo: String,
    @SerializedName("password") var password: String
): Serializable {
}