package fr.thibma.plantcare.services

import com.google.gson.Gson
import fr.thibma.plantcare.models.response.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Classe générique qui traite toutes les réceptions de l'api
class Handler (private val listener: NetworkListener<String>) : Callback<ApiResponse> {

    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
        if (response.code() == 404 || response.code() == 401) {
            // Erreurs
            listener.onError(Throwable(response.message()))
        }

        val responseBody = response.body()
        if (responseBody != null) {
            // GOOD
            responseBody.let {
                val responseGson = Gson().toJson(responseBody.message)
                listener.onSuccess(responseGson)
            }
        }
        else {
            listener.onErrorApi(response.message())
        }
    }

    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
        listener.onError(t)
    }
}