package fr.thibma.plantcare.services

// Interface quand une callback est appelée
interface NetworkListener<T> {
    fun onSuccess(data: T)
    fun onErrorApi(message: String)
    fun onError(t: Throwable)
}