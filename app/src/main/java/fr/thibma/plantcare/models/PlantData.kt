package fr.thibma.plantcare.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PlantData(
    @SerializedName("_id") var id: String,
    @SerializedName("temperature") var temperature: Double,
    @SerializedName("humidity") var humidity: Double,
    @SerializedName("light") var light: String,
    @SerializedName("plant") var plant: Plant
): Serializable {
}