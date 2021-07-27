package fr.thibma.plantcare.models.requests

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PlantRequest(
    @SerializedName("name") var name: String,
    @SerializedName("temperature") var temperature: String,
    @SerializedName("humidity") var humidity: String,
    @SerializedName("light") var light: String
): Serializable {
}