package fr.thibma.plantcare.services

import fr.thibma.plantcare.models.requests.PlantRequest
import fr.thibma.plantcare.models.requests.UserRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

object Network {

    private val plantCareApi: PlantCareApi

    init {
        // Création du client
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        // Service de communication retrofit
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://mocprojects.spell.ovh:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        plantCareApi = retrofit.create(PlantCareApi::class.java)
    }

    // Inscription
    @Headers("Content-Type: application/json")
    fun signUp(userRequest: UserRequest, listener: NetworkListener<String>) {
        plantCareApi.signUp(userRequest).enqueue(Handler(listener))
    }

    // Connexion
    fun signIn(userRequest: UserRequest, listener: NetworkListener<String>) {
        plantCareApi.signIn(userRequest).enqueue(Handler(listener))
    }

    // Créer une plante
    fun createPlant(plantRequest: PlantRequest, token: String, listener: NetworkListener<String>) {
        plantCareApi.createPlant(plantRequest, token).enqueue(Handler(listener))
    }
}