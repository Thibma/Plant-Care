package fr.thibma.plantcare.utils

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import fr.thibma.plantcare.R

// Boite de dialog de chargement
class DialogLoading(
    private val activity: Activity
) {
    private lateinit var alertDialog: AlertDialog

    fun startDialog() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_loading, null)

        var builder = AlertDialog.Builder(activity)
            .setView(dialogView)
            .setCancelable(false)

        alertDialog = builder.show()
    }

    fun stopDialog() {
        alertDialog.dismiss()
    }
}