package fr.thibma.plantcare.activities

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import fr.thibma.plantcare.R
import fr.thibma.plantcare.adapter.DiscoverBluetoothAdapter
import fr.thibma.plantcare.dialogs.DialogPlant
import fr.thibma.plantcare.dialogs.DialogPlantListener
import fr.thibma.plantcare.dialogs.DialogWifi
import fr.thibma.plantcare.dialogs.DialogWifiListener
import fr.thibma.plantcare.models.Plant
import fr.thibma.plantcare.models.User
import fr.thibma.plantcare.models.requests.PlantRequest
import fr.thibma.plantcare.models.requests.RobotRequest
import fr.thibma.plantcare.services.*
import fr.thibma.plantcare.utils.DialogLoading
import fr.thibma.plantcare.utils.DialogOK
import fr.thibma.plantcare.utils.DialogOkListener
import java.io.IOError
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList

class AddRobotActivity : AppCompatActivity(), DiscoverBluetoothAdapter.OnDiscoveryBlutoothClickListener, DialogWifiListener, DialogPlantListener {

    private lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var selectedDeviceTextView: TextView
    private lateinit var addRobotButton: Button
    private lateinit var robotNameEditText: EditText

    private lateinit var discoverBluetoothAdapter: DiscoverBluetoothAdapter

    private val pairedDevice: ArrayList<BluetoothDevice> = ArrayList()
    private var selectedDevice: BluetoothDevice? = null

    private lateinit var bluetoothService: BluetoothService.ConnectedThread
    private var bluetoothResponse: MutableLiveData<String> = MutableLiveData<String>()

    private var user: User? = null
    private var token: String? = null

    private var ssid: String? = null
    private var password: String? = null

    private var plant: Plant? = null

    val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

    private val loadingDialog = DialogLoading(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_robot)

        recyclerView = findViewById(R.id.recyclerViewDiscover)
        progressBar = findViewById(R.id.progresBarAddRobot)
        selectedDeviceTextView = findViewById(R.id.textViewSelectedDeviceName)
        addRobotButton = findViewById(R.id.buttonAddRobot)
        robotNameEditText = findViewById(R.id.editTextRobotName)

        val extras = intent.extras
        if (extras != null) {
            user = extras.getSerializable("user") as User
            token = extras.getString("token")
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!bluetoothAdapter.isEnabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            resultBluetooth.launch(intent)
        }
        else {
            discoverBluetooth()
        }

        addRobotButton.setOnClickListener {
            addRobot()
        }

    }

    private var resultBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            discoverBluetooth()
        }
    }

    private fun discoverBluetooth() {
        val alreadyPaired: List<BluetoothDevice> = bluetoothAdapter.bondedDevices.toList()

        alreadyPaired.forEach { device ->
            if (device.name == "Plancare Robot") {
                pairedDevice.add(device)
            }
        }

        discoverBluetoothAdapter = DiscoverBluetoothAdapter(pairedDevice, this)

        recyclerView.adapter = discoverBluetoothAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        bluetoothAdapter.startDiscovery()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device?.name == "Plantcare Robot") {
                        if (!pairedDevice.contains(device)) {
                            progressBar.visibility = View.INVISIBLE
                            pairedDevice.add(device)
                            discoverBluetoothAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(position: Int) {
        selectedDevice = pairedDevice[position]
        selectedDeviceTextView.text = selectedDevice?.name
    }

    private fun addRobot() {
        val name = robotNameEditText.text.toString()
        if (name == "" || selectedDevice == null) {
            DialogOK(
                "Merci de remplir tous les champs",
                "Erreur lors de l'ajout d'un robot",
                this
            ).startDialog(object : DialogOkListener {
                override fun onOkClick(alertDialog: AlertDialog) {
                    alertDialog.dismiss()
                }
            })
            return
        }

        loadingDialog.startDialog()
        val connectThread = ConnectThread(selectedDevice!!)
        connectThread.run()
    }

    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(uuid)
        }

        override fun run() {
            super.run()
            bluetoothAdapter.cancelDiscovery()
            mmSocket?.let { socket ->
                socket.connect()
                bluetoothConnected(socket)
            }
        }

        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e("SOCKET", "Could not close the client socket", e)
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper()) { message ->
        when(message.what) {
            MESSAGE_READ -> {
                val readBuff = message.obj as ByteArray
                val tempMsg = String(readBuff, 0, message.arg1)
                 bluetoothResponse.value = tempMsg
            }
        }
        true
    }

    private fun bluetoothConnected(bluetoothSocket: BluetoothSocket) {
        bluetoothService = BluetoothService(handler).ConnectedThread(bluetoothSocket)
        bluetoothService.start()
        bluetoothResponse.observe(this, { response ->
            when (response) {
                "ssid ?" -> {
                    loadingDialog.stopDialog()
                    DialogWifi(this)
                }
                "ssid received" -> {
                    bluetoothService.write("p".toByteArray())
                }
                "pwd ?" -> {
                    bluetoothService.write(password!!.toByteArray())
                }
                "pwd received" -> {
                    loadingDialog.stopDialog()
                    DialogPlant(this)
                }
                "id ?" -> {
                    bluetoothService.write(plant!!.id.toByteArray())
                }
                "id received" -> {
                    bluetoothService.write("t".toByteArray())
                }
                "token ?" -> {
                    bluetoothService.write(token!!.toByteArray())
                }
                "token received" -> {
                    bluetoothService.write("l".toByteArray())
                }
                "Starting config lum" -> {
                    when(plant?.light.toString()) {
                        "very-low" -> bluetoothService.write("w".toByteArray())
                        "low" -> bluetoothService.write("l".toByteArray())
                        "medium" -> bluetoothService.write("m".toByteArray())
                        "high" -> bluetoothService.write("h".toByteArray())
                        "very-high" -> bluetoothService.write("v".toByteArray())
                    }
                }
                "lux received" -> {
                    val robotRequest = RobotRequest(selectedDevice!!.address, robotNameEditText.text.toString(), user!!.id)
                    Network.createRobot(robotRequest, token!!, object : NetworkListener<String> {
                        override fun onSuccess(data: String) {
                            setResult(RESULT_OK)
                            finish()
                        }

                        override fun onErrorApi(message: String) {
                            onError(Throwable(message))
                        }

                        override fun onError(t: Throwable) {
                            DialogOK(
                                t.toString(),
                                "Erreur de connexion",
                                this@AddRobotActivity
                            ).startDialog(object : DialogOkListener {
                                override fun onOkClick(alertDialog: AlertDialog) {
                                    alertDialog.dismiss()
                                    return
                                }
                            })
                        }

                    })
                }
            }
        })
        bluetoothService.write("w".toByteArray())
    }

    override fun onWifiSend(ssid: String, password: String) {
        loadingDialog.startDialog()
        this.ssid = ssid
        this.password = password
        bluetoothService.write(ssid.toByteArray())
    }

    override fun onPlantSent(plantRequest: PlantRequest) {
        loadingDialog.startDialog()
        Network.createPlant(plantRequest, token!!, object : NetworkListener<String> {
            override fun onSuccess(data: String) {
                plant = Gson().fromJson(data, Plant::class.java)
                bluetoothService.write("i".toByteArray())
            }

            override fun onErrorApi(message: String) {
                onError(Throwable(message))
            }

            override fun onError(t: Throwable) {
                DialogOK(
                    t.toString(),
                    "Erreur de connexion",
                    this@AddRobotActivity
                ).startDialog(object : DialogOkListener {
                    override fun onOkClick(alertDialog: AlertDialog) {
                        alertDialog.dismiss()
                        return
                    }
                })
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        bluetoothService.cancel()
    }
}