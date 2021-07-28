package fr.thibma.plantcare.activities

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.media.session.MediaSession
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.thibma.plantcare.R
import fr.thibma.plantcare.dialogs.DialogPlant
import fr.thibma.plantcare.models.User
import fr.thibma.plantcare.utils.DialogOK
import fr.thibma.plantcare.utils.DialogOkListener

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var floatingActionButton: FloatingActionButton

    private var user: User? = null
    private var token: String? = null

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
        floatingActionButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("token", token)
            bundle.putSerializable("user", user)
            val intent = Intent(this, AddRobotActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
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
            // REFRESH RECYCLER VIEW
        }
    }

    private fun optionButton() {
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}