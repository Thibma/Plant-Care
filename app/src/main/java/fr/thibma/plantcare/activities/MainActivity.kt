package fr.thibma.plantcare.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.thibma.plantcare.R

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var floatingActionButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        floatingActionButton = findViewById(R.id.floatingAddRobot)
        floatingActionButton.setOnClickListener {
            startActivity(Intent(this, AddRobotActivity::class.java))
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

    private fun optionButton() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

}