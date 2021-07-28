package fr.thibma.plantcare.activities

import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.icu.util.TimeUnit
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import fr.thibma.plantcare.R
import fr.thibma.plantcare.adapter.DiscoverBluetoothAdapter
import fr.thibma.plantcare.models.Robot
import fr.thibma.plantcare.models.User
import fr.thibma.plantcare.services.BluetoothService
import fr.thibma.plantcare.services.MESSAGE_READ
import fr.thibma.plantcare.utils.DialogLoading
import fr.thibma.plantcare.utils.DialogOK
import fr.thibma.plantcare.utils.DialogOkListener
import java.io.IOException
import java.lang.Exception
import java.util.*

class ControlRobotActivity : AppCompatActivity() {

    private lateinit var buttonForward: ImageButton
    private lateinit var buttonRight: ImageButton
    private lateinit var buttonBack: ImageButton
    private lateinit var buttonLeft: ImageButton
    private lateinit var buttonReturn: Button

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothService: BluetoothService.ConnectedThread? = null

    private lateinit var connectThread: ConnectThread

    private var found = false

    private val timer = Timer("Bluetooth", false)

    val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

    private var robot: Robot? = null

    private var bluetoothResponse: MutableLiveData<String> = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_robot)

        val extras = intent.extras
        if (extras != null) {
            robot = extras.getSerializable("robot") as Robot
        }

        buttonForward = findViewById(R.id.buttonForward)
        buttonRight = findViewById(R.id.buttonRight)
        buttonBack = findViewById(R.id.buttonBack)
        buttonLeft = findViewById(R.id.buttonLeft)
        buttonReturn = findViewById(R.id.buttonRetour)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!bluetoothAdapter.isEnabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            resultBluetooth.launch(intent)
        }
        else {
            discoverBluetooth()
        }

        buttonReturn.setOnClickListener {
            bluetoothService!!.write("@".toByteArray())
        }

        buttonForward.setOnClickListener {
            bluetoothService!!.write("f".toByteArray())
        }
        buttonRight.setOnClickListener {
            bluetoothService!!.write("r".toByteArray())
        }
        buttonBack.setOnClickListener {
            bluetoothService!!.write("b".toByteArray())
        }
        buttonLeft.setOnClickListener {
            bluetoothService!!.write("l".toByteArray())
        }

    }

    private var resultBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            discoverBluetooth()
        }
    }

    private fun discoverBluetooth() {
        timer.schedule(object : TimerTask() {

            override fun run() {
                DialogOK(
                    "Le robot n'a pas été trouvé...",
                    "Erreur de connexion bluetooth",
                    this@ControlRobotActivity
                ).startDialog(object : DialogOkListener {
                    override fun onOkClick(alertDialog: AlertDialog) {
                        alertDialog.dismiss()
                        leave()
                    }
                })
            }

        }, 10000)

        val alreadyPaired: List<BluetoothDevice> = bluetoothAdapter.bondedDevices.toList()
        alreadyPaired.forEach { device ->
            if (device.address == robot!!.mac) {
                found = true
                robotFound(device)
            }
        }
        if (!found) {
            bluetoothAdapter.startDiscovery()
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(receiver, filter)
        }
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device?.address == robot!!.mac) {
                        robotFound(device)
                    }
                }
            }
        }
    }

    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(uuid)
        }

        override fun run() {
            super.run()
            bluetoothAdapter.cancelDiscovery()
            mmSocket?.let { socket ->
                try {
                    socket.connect()
                    bluetoothConnected(socket)
                }
                catch (e: Exception) {
                    DialogOK(
                        "Le robot n'a pas été trouvé...",
                        "Erreur de connexion bluetooth",
                        this@ControlRobotActivity
                    ).startDialog(object : DialogOkListener {
                        override fun onOkClick(alertDialog: AlertDialog) {
                            alertDialog.dismiss()
                            leave()
                        }
                    })
                }
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

    private fun robotFound(device: BluetoothDevice) {
        connectThread = ConnectThread(device)
        connectThread.run()
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
        bluetoothService!!.start()
        bluetoothService!!.write("m".toByteArray())
        bluetoothResponse.observe(this) { response ->
            when(response) {
                "leave" -> {
                    leave()
                }
            }
        }
        timer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!found) {
            unregisterReceiver(receiver)
        }
        if (bluetoothService != null) {
            bluetoothService!!.cancel()
        }
        timer.cancel()
    }

    override fun onBackPressed() {
        if (bluetoothService != null) {
            bluetoothService!!.write("@".toByteArray())
        }

    }

    private fun leave() {
        if (!found) {
            unregisterReceiver(receiver)
        }
        if (bluetoothService != null) {
            bluetoothService!!.cancel()
        }
        timer.cancel()
        setResult(RESULT_OK)
        finish()
    }
}