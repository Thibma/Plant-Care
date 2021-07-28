package fr.thibma.plantcare.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Robot(
    @SerializedName("_id") var id: String,
    @SerializedName("name") var name: String,
    @SerializedName("mac") var mac: String,
    @SerializedName("user") var user: String,
    @SerializedName("mainPlant") var mainPlant: Plant,
    @SerializedName("allPlants") var allPlants: List<String>,
): Serializable {}