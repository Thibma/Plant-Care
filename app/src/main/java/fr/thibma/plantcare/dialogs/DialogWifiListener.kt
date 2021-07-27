package fr.thibma.plantcare.dialogs

interface DialogWifiListener {
    fun onWifiSend(ssid: String, password: String)
}