package fr.thibma.plantcare.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import fr.thibma.plantcare.R
import fr.thibma.plantcare.utils.DialogOK
import fr.thibma.plantcare.utils.DialogOkListener

class DialogWifi (private val activity: Activity) {

    private var ssidEditText: EditText
    private var passwordEditText: EditText
    private var sendButton: Button

    private var alertDialog: AlertDialog

    private val dialogWifiListener: DialogWifiListener = activity as DialogWifiListener

    init {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_wifi, null)
        ssidEditText = dialogView.findViewById(R.id.editTextSsid)
        passwordEditText = dialogView.findViewById(R.id.editTextWifiPasword)
        sendButton = dialogView.findViewById(R.id.sendWifiButton)

        // Builder de la dialog
        val builder = AlertDialog.Builder(activity)
            .setView(dialogView)
            .setTitle("Envoyez votre réseau WiFi au robot.")
            .setCancelable(false)

        // Affichage de la dialog
        alertDialog = builder.show()

        sendButton.setOnClickListener {
            sendWifi()
        }
    }

    private fun sendWifi() {
        val ssid = ssidEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (ssid == "" || password == "") {
            DialogOK(
                "Merci de remplir tous les champs",
                "Erreur lors de la transmission du WiFi",
                activity
            ).startDialog(object : DialogOkListener {
                override fun onOkClick(alertDialog: androidx.appcompat.app.AlertDialog) {
                    alertDialog.dismiss()
                }
            })
            return
        }

        alertDialog.dismiss()
        dialogWifiListener.onWifiSend(ssid, password)
    }

}