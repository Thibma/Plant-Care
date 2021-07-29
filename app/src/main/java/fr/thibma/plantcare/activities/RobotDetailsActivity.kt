package fr.thibma.plantcare.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.google.gson.Gson
import fr.thibma.plantcare.R
import fr.thibma.plantcare.models.PlantData
import fr.thibma.plantcare.models.Robot
import fr.thibma.plantcare.services.Network
import fr.thibma.plantcare.services.NetworkListener
import fr.thibma.plantcare.utils.DialogLoading
import fr.thibma.plantcare.utils.DialogOK
import fr.thibma.plantcare.utils.DialogOkListener

class RobotDetailsActivity: AppCompatActivity() {

    private var robot: Robot? = null
    private var token: String? = null

    private lateinit var humidityTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var lightTextView: TextView
    private lateinit var plantnameTextView: TextView

    private lateinit var temperatureValue: TextView
    private lateinit var humidityValue: TextView
    private lateinit var lumValue: TextView

    private lateinit var tempChart: AAChartView
    private lateinit var humChart: AAChartView

    private lateinit var buttonControl: Button

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var listData: List<PlantData> = ArrayList()

    private val dialogLoading = DialogLoading(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_robot_details)

        val bundle = intent.extras
        if (bundle != null) {
            robot = bundle.getSerializable("robot") as Robot
            token = bundle.getString("token")
        }

        humidityTextView = findViewById(R.id.humTextViewRobotDetails)
        temperatureTextView = findViewById(R.id.tempTextViewRobotDetails)
        lightTextView = findViewById(R.id.lumTextViewRobotDetails)
        plantnameTextView = findViewById(R.id.plantNameTextViewRobotDetails)

        temperatureValue = findViewById(R.id.textViewTempVal)
        humidityValue = findViewById(R.id.textViewHumVal)
        lumValue = findViewById(R.id.textViewLumVal)

        tempChart = findViewById(R.id.charTemp)
        humChart = findViewById(R.id.charHum)

        buttonControl = findViewById(R.id.buttonControl)

        humidityTextView.text = transformString(robot!!.mainPlant!!.humidity)
        temperatureTextView.text = transformString(robot!!.mainPlant!!.temperature)
        lightTextView.text = transformString(robot!!.mainPlant!!.light)
        plantnameTextView.text = robot!!.mainPlant!!.name

        swipeRefreshLayout = findViewById(R.id.refreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            getAllPlantData()
        }

        buttonControl.setOnClickListener {
            controlRobot()
        }

        getAllPlantData()
    }

    private fun transformString(string: String): String {
        return when(string) {
            "very-low" -> "Très faible"
            "low" -> "Faible"
            "medium" -> "Normale"
            "high" -> "Élevée"
            "very-high" -> "Très Élevée"
            else -> "?"
        }
    }

    private fun getAllPlantData() {
        Network.getPlantData(robot!!.mainPlant!!.id, token!!, object : NetworkListener<String> {
            override fun onSuccess(data: String) {
                listData = Gson().fromJson(data, Array<PlantData>::class.java).toList()
                setData()
            }

            override fun onErrorApi(message: String) {
                onError(Throwable(message))
            }

            override fun onError(t: Throwable) {
                DialogOK(
                    t.toString(),
                    "Erreur de connexion",
                    this@RobotDetailsActivity
                ).startDialog(object : DialogOkListener {
                    override fun onOkClick(alertDialog: androidx.appcompat.app.AlertDialog) {
                        alertDialog.dismiss()
                    }
                })
            }

        })
    }

    private fun setData() {
        val tempList: ArrayList<Double> = ArrayList()
        val humList: ArrayList<Double> = ArrayList()
        listData.forEach {
            tempList.add(it.temperature)
            humList.add(it.humidity)
        }

        temperatureValue.text = "${tempList[0]}°C"
        val tempChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Température °C")
                        .data(tempList.toTypedArray())
                )
            )

        humidityValue.text = "${humList[0]}%"
        val humChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Humidité %")
                        .data(humList.toTypedArray())
                )
            )

        lumValue.text = transformString(listData[0].light)

        tempChart.aa_drawChartWithChartModel(tempChartModel)
        humChart.aa_drawChartWithChartModel(humChartModel)
        swipeRefreshLayout.isRefreshing = false
    }

    private fun controlRobot() {
        val bundle = Bundle()
        bundle.putSerializable("robot", robot)
        val intent = Intent(this, ControlRobotActivity::class.java)
        intent.putExtras(bundle)
        dialogLoading.startDialog()
        onControlRobotActivityResult.launch(intent)
    }

    private val onControlRobotActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        dialogLoading.stopDialog()
    }

}
