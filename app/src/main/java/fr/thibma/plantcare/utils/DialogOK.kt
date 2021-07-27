package fr.thibma.plantcare.utils

import android.app.Activity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import fr.thibma.plantcare.R

// Classe permettant d'afficher une fenêtre de dialogue avec un bouton OK
class DialogOK(
    // Données
    private val textDialog: String,
    private val titleDialog: String,
    private val activity: Activity
) {
    // Affichage de la dialog
    fun startDialog(dialogOkListener: DialogOkListener) {
        // Inflate + association des données
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_ok, null)
        val textView = dialogView.findViewById(R.id.textViewDialogOk) as TextView
        val buttonView = dialogView.findViewById(R.id.buttonDialogOk) as Button

        textView.text = textDialog

        // Builder de l'alertDialog
        val builder = AlertDialog.Builder(activity)
            .setView(dialogView)
            .setTitle(titleDialog)
            .setCancelable(true)

        // Affichage
        val alertDialog = builder.show()

        // Fermeture de la dialog au clic sur le bouton
        buttonView.setOnClickListener {
            dialogOkListener.onOkClick(alertDialog)
        }
    }

}