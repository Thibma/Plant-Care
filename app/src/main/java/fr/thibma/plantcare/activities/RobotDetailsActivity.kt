package fr.thibma.plantcare.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import fr.thibma.plantcare.R
import fr.thibma.plantcare.models.Plant
import fr.thibma.plantcare.models.Robot
import org.w3c.dom.Text

class RobotDetailsActivity(private val plant: Plant): AppCompatActivity() {

    private lateinit var humidityTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var lightTextView: TextView
    private lateinit var plantnameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_robot_details)

        humidityTextView = findViewById(R.id.humTextViewRobotDetails)
        temperatureTextView = findViewById(R.id.tempTextViewRobotDetails)
        lightTextView = findViewById(R.id.lumTextViewRobotDetails)
        plantnameTextView = findViewById(R.id.plantNameTextViewRobotDetails)

        humidityTextView.text = plant.humidity
        temperatureTextView.text = plant.temperature
        lightTextView.text = plant.light
        plantnameTextView.text = plant.name

    }
}
