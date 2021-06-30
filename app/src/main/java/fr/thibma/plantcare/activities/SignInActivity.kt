package fr.thibma.plantcare.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import fr.thibma.plantcare.R

class SignInActivity : AppCompatActivity() {

    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        button = findViewById(R.id.buttonSignIn)

        button.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}