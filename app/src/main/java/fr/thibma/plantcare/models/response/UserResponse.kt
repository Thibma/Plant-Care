package fr.thibma.plantcare.models.response

import com.google.gson.annotations.SerializedName
import fr.thibma.plantcare.models.User
import java.io.Serializable

class UserResponse(
    @SerializedName("token") var token: String,
    @SerializedName("user") var user: User
): Serializable {
}