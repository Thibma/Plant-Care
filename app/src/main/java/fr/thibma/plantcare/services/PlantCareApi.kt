package fr.thibma.plantcare.services

import fr.thibma.plantcare.models.requests.PlantRequest
import fr.thibma.plantcare.models.requests.RobotRequest
import fr.thibma.plantcare.models.requests.UserRequest
import fr.thibma.plantcare.models.response.ApiResponse
import retrofit2.Call
import retrofit2.http.*

interface PlantCareApi {

    // Token de l'api (à sécuriser)
    private val apiToken: String
        get() = "N7ADR2mK64caX6CMOzIN3p5H67xRro3dkujs3phl"

    // Inscription
    @Headers("Content-Type: application/json")
    @POST("/users")
    fun signUp(@Body userSignupRequest: UserRequest, @Header("api-token") token: String = apiToken): Call<ApiResponse>

    // Connexion
    @Headers("Content-Type: application/json")
    @POST("/signin")
    fun signIn(@Body userSignupRequest: UserRequest, @Header("api-token") token: String = apiToken): Call<ApiResponse>

    // Créer une plante
    @Headers("Content-Type: application/json")
    @POST("/plants")
    fun createPlant(@Body plantRequest: PlantRequest, @Header("jwt-token") jwtToken: String, @Header("api-token") token: String = apiToken): Call<ApiResponse>

    // Créer un robot
    @Headers("Content-Type: application/json")
    @POST("/robots")
    fun createRobot(@Body robotRequest: RobotRequest, @Header("jwt-token") jwtToken: String, @Header("api-token") token: String = apiToken): Call<ApiResponse>
}