package fr.thibma.plantcare.models.requests

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RobotRequest(
    @SerializedName("mac") var mac: String,
    @SerializedName("name") var name: String,
    @SerializedName("user") var idUser: String
): Serializable {
}