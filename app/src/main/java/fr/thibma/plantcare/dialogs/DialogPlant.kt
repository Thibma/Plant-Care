package fr.thibma.plantcare.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import fr.thibma.plantcare.R
import fr.thibma.plantcare.models.requests.PlantRequest
import fr.thibma.plantcare.utils.DialogOK
import fr.thibma.plantcare.utils.DialogOkListener

class DialogPlant(private val activity: Activity) {

    private var nameEditText: EditText
    private var tempTextView: TextView
    private var tempSeekBar: SeekBar
    private var humTextView: TextView
    private var humSeekBar: SeekBar
    private var lumTextView: TextView
    private var lumSeekBar: SeekBar
    private var confirmButton: Button

    private var alertDialog: AlertDialog

    private val dialogPlantListener: DialogPlantListener = activity as DialogPlantListener

    init {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_plant, null)
        nameEditText = dialogView.requireViewById(R.id.editTextPlantName)
        tempTextView = dialogView.requireViewById(R.id.textViewPlantTemp)
        tempSeekBar = dialogView.requireViewById(R.id.seekBarPlantTemp)
        humTextView = dialogView.requireViewById(R.id.textViewPlantHum)
        humSeekBar = dialogView.requireViewById(R.id.seekBarPlantHum)
        lumTextView = dialogView.requireViewById(R.id.textViewPlantLum)
        lumSeekBar = dialogView.requireViewById(R.id.seekBarPlantLum)
        confirmButton = dialogView.requireViewById(R.id.createPlantButton)

        listOf(tempSeekBar, humSeekBar, lumSeekBar).forEach { seekBar ->
            seekBar.max = 4
            seekBar.progress = 2
        }

        tempSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when(progress) {
                    0 -> tempTextView.text = "Température : Très faible"
                    1 -> tempTextView.text = "Température : Faible"
                    2 -> tempTextView.text = "Température : Normale"
                    3 -> tempTextView.text = "Température : Élevée"
                    4 -> tempTextView.text = "Température : Très élevée"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        humSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when(progress) {
                    0 -> humTextView.text = "Humidité : Très faible"
                    1 -> humTextView.text = "Humidité : Faible"
                    2 -> humTextView.text = "Humidité : Normale"
                    3 -> humTextView.text = "Humidité : Élevée"
                    4 -> humTextView.text = "Humidité : Très élevée"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        lumSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when(progress) {
                    0 -> lumTextView.text = "Luminosité : Très faible"
                    1 -> lumTextView.text = "Luminosité : Faible"
                    2 -> lumTextView.text = "Luminosité : Normale"
                    3 -> lumTextView.text = "Luminosité : Élevée"
                    4 -> lumTextView.text = "Luminosité : Très élevée"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        // Builder de la dialog
        val builder = AlertDialog.Builder(activity)
            .setView(dialogView)
            .setTitle("Définissez les valeurs de votre plante")
            .setCancelable(false)

        // Affichage de la dialog
        alertDialog = builder.show()

        confirmButton.setOnClickListener {
            sendPlant()
        }
    }

    private fun sendPlant() {
        val tempValue: String = when(tempSeekBar.progress) {
            0 -> "very-low"
            1 -> "low"
            2 -> "medium"
            3 -> "high"
            4 -> "very-high"
            else -> "?"
        }

        val humValue: String = when(humSeekBar.progress) {
            0 -> "very-low"
            1 -> "low"
            2 -> "medium"
            3 -> "high"
            4 -> "very-high"
            else -> "?"
        }

        val lumValue: String = when(lumSeekBar.progress) {
            0 -> "very-low"
            1 -> "low"
            2 -> "medium"
            3 -> "high"
            4 -> "very-high"
            else -> "?"
        }

        if (nameEditText.text.toString() == "") {
            DialogOK(
                "Merci de remplir tous les champs",
                "Erreur lors de l'ajout de la plante",
                activity
            ).startDialog(object : DialogOkListener {
                override fun onOkClick(alertDialog: androidx.appcompat.app.AlertDialog) {
                    alertDialog.dismiss()
                }
            })
            return
        }
        val plantName = nameEditText.text.toString()

        alertDialog.dismiss()
        dialogPlantListener.onPlantSent(PlantRequest(plantName, tempValue, humValue, lumValue))
    }

}