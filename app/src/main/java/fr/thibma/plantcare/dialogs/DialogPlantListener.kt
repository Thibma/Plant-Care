package fr.thibma.plantcare.dialogs

import fr.thibma.plantcare.models.requests.PlantRequest

interface DialogPlantListener {
    fun onPlantSent(plantRequest: PlantRequest)
}