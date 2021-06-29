package fr.thibma.plantcare.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import fr.thibma.plantcare.R

class WelcomeActivity : AppCompatActivity() {

    private lateinit var signUpButton: Button
    private lateinit var signInButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        signInButton = findViewById(R.id.buttonSignInWelcome)
        signUpButton = findViewById(R.id.buttonSignUpWelcome)

        signInButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        signUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}