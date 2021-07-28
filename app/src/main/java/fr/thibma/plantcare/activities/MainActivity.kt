package fr.thibma.plantcare.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import fr.thibma.plantcare.R
import fr.thibma.plantcare.adapter.DiscoverBluetoothAdapter
import fr.thibma.plantcare.adapter.RobotListAdapter
import fr.thibma.plantcare.models.Robot
import fr.thibma.plantcare.models.User
import fr.thibma.plantcare.services.Network
import fr.thibma.plantcare.services.NetworkListener
import fr.thibma.plantcare.utils.DialogLoading
import fr.thibma.plantcare.utils.DialogOK
import fr.thibma.plantcare.utils.DialogOkListener

class MainActivity : AppCompatActivity(), RobotListAdapter.OnRobotClickListener {

    private lateinit var toolbar: Toolbar
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var statusText: TextView

    private var user: User? = null
    private var token: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var robotListAdapter: RobotListAdapter

    private var robotList: List<Robot> = ArrayList()

    private val dialogLoading = DialogLoading(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val extras = intent.extras
        if (extras != null) {
            user = extras.getSerializable("user") as User
            token = extras.getString("token")
        }

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        floatingActionButton = findViewById(R.id.floatingAddRobot)
        statusText = findViewById(R.id.emptyList)
        floatingActionButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("token", token)
            bundle.putSerializable("user", user)
            val intent = Intent(this, AddRobotActivity::class.java)
            intent.putExtras(bundle)
            onAddRobotActivityResult.launch(intent)
        }

        recyclerView = findViewById(R.id.recyclerViewRobotList)
        refreshList()

        Network.getAllRobotByUser(user!!.id, token!!, object : NetworkListener<String> {
            override fun onSuccess(data: String) {
                robotList = Gson().fromJson(data, Array<Robot>::class.java).toMutableList()
                setRecyclerView(robotList)
            }

            override fun onErrorApi(message: String) {
                onError(Throwable(message))
            }

            override fun onError(t: Throwable) {
                DialogOK(
                    t.toString(),
                    "Erreur de connexion",
                    this@MainActivity
                ).startDialog(object : DialogOkListener {
                    override fun onOkClick(alertDialog: androidx.appcompat.app.AlertDialog) {
                        alertDialog.dismiss()
                    }
                })
            }
        })

    }

    private fun setRecyclerView(robotList: List<Robot>) {
        robotListAdapter = RobotListAdapter(robotList, this)
        recyclerView.adapter = robotListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.applicationContext)
        recyclerView.setHasFixedSize((true))

        if (robotList.isEmpty()) {
            statusText.visibility = View.VISIBLE
        }
        else {
            statusText.visibility = View.INVISIBLE
        }
    }

    private fun refreshList() {
        Network.getAllRobotByUser(user!!.id, token!!, object : NetworkListener<String> {
            override fun onSuccess(data: String) {
                robotList = Gson().fromJson(data, Array<Robot>::class.java).toMutableList()
                setRecyclerView(robotList)
            }

            override fun onErrorApi(message: String) {
                onError(Throwable(message))
            }

            override fun onError(t: Throwable) {
                DialogOK(
                    t.toString(),
                    "Erreur de connexion",
                    this@MainActivity
                ).startDialog(object : DialogOkListener {
                    override fun onOkClick(alertDialog: androidx.appcompat.app.AlertDialog) {
                        alertDialog.dismiss()
                    }
                })
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.optionButton -> {
            optionButton()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private val onAddRobotActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            refreshList()
        }
    }

    private val onControlRobotActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        dialogLoading.stopDialog()
    }


    private fun optionButton() {
        finish()
    }

    override fun onItemClick(position: Int) {
        val bundle = Bundle()
        bundle.putSerializable("robot", robotList[position])
        val intent = Intent(this, ControlRobotActivity::class.java)
        intent.putExtras(bundle)
        dialogLoading.startDialog()
        onControlRobotActivityResult.launch(intent)
    }

}