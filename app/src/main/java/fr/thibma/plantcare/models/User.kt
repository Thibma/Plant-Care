package fr.thibma.plantcare.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class User(
    @SerializedName("_id") var id: String,
    @SerializedName("pseudo") var pseudo: String,
    @SerializedName("password") var password: String
): Serializable {
}