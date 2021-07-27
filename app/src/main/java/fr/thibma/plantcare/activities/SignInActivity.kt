package fr.thibma.plantcare.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import fr.thibma.plantcare.R
import fr.thibma.plantcare.models.requests.UserRequest
import fr.thibma.plantcare.models.response.UserResponse
import fr.thibma.plantcare.services.Network
import fr.thibma.plantcare.services.NetworkListener
import fr.thibma.plantcare.utils.DialogLoading
import fr.thibma.plantcare.utils.DialogOK
import fr.thibma.plantcare.utils.DialogOkListener

class SignInActivity : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var pseudoEditText: EditText
    private lateinit var passwordEditText: EditText

    private val dialogLoading = DialogLoading(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        button = findViewById(R.id.buttonSignIn)
        pseudoEditText = findViewById(R.id.editTextTextUsername)
        passwordEditText = findViewById(R.id.editTextPasswordSignIn)

        button.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val pseudo = pseudoEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (pseudo == "" || password == "") {
            DialogOK(
                "Merci de remplir tous les champs",
                "Erreur lors de la connexion",
                this
            ).startDialog(object : DialogOkListener {
                override fun onOkClick(alertDialog: AlertDialog) {
                    alertDialog.dismiss()
                }
            })
            return
        }

        dialogLoading.startDialog()
        Network.signIn(UserRequest(pseudo, password), object : NetworkListener<String> {

            override fun onSuccess(data: String) {
                val userResponse = Gson().fromJson(data, UserResponse::class.java)
                dialogLoading.stopDialog()
                val bundle = Bundle()
                bundle.putString("token", userResponse.token)
                bundle.putSerializable("user", userResponse.user)
                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
                finish()
            }

            override fun onErrorApi(message: String) {
                onError(Throwable(message))
            }

            override fun onError(t: Throwable) {
                dialogLoading.stopDialog()
                DialogOK(
                    t.toString(),
                    "Erreur de connexion",
                    this@SignInActivity
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