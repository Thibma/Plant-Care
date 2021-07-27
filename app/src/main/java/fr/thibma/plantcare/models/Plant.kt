package fr.thibma.plantcare.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Plant(
    @SerializedName("_id") var id: String,
    @SerializedName("name") var name: String,
    @SerializedName("temperature") var temperature: String,
    @SerializedName("humidity") var humidity: String,
    @SerializedName("light") var light: String
): Serializable {
}