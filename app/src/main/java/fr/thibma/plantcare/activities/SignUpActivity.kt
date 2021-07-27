package fr.thibma.plantcare.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import fr.thibma.plantcare.R
import fr.thibma.plantcare.models.User
import fr.thibma.plantcare.models.requests.UserRequest
import fr.thibma.plantcare.models.response.UserResponse
import fr.thibma.plantcare.services.Network
import fr.thibma.plantcare.services.NetworkListener
import fr.thibma.plantcare.utils.DialogLoading
import fr.thibma.plantcare.utils.DialogOK
import fr.thibma.plantcare.utils.DialogOkListener

class SignUpActivity : AppCompatActivity() {

    private lateinit var pseudoEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var signUpButton: Button

    private val dialogLoading = DialogLoading(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        pseudoEditText = findViewById(R.id.editTextTextPersonName)
        passwordEditText = findViewById(R.id.editTextTextPassword)
        confirmPasswordEditText = findViewById(R.id.editTextTextConfirmPassword)
        signUpButton = findViewById(R.id.buttonSignUp)

        signUpButton.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        val pseudo = pseudoEditText.text.toString()

        if (passwordEditText.text.toString() != confirmPasswordEditText.text.toString()) {
            DialogOK(
                "Les mots de passe ne correspondent pas",
                "Erreur lors de l'inscription",
                this
            ).startDialog(object : DialogOkListener {
                override fun onOkClick(alertDialog: AlertDialog) {
                    alertDialog.dismiss()
                }
            })
            return
        }

        val password = passwordEditText.text.toString()

        if (pseudo == "" || password == "") {
            DialogOK(
                "Merci de remplir tous les champs",
                "Erreur lors de l'inscription",
                this
            ).startDialog(object : DialogOkListener {
                override fun onOkClick(alertDialog: AlertDialog) {
                    alertDialog.dismiss()
                }
            })
            return
        }
        dialogLoading.startDialog()
        val userRequest = UserRequest(pseudo, password)
        Network.signUp(userRequest, object : NetworkListener<String> {
            override fun onSuccess(data: String) {
                signIn(userRequest)
            }

            override fun onErrorApi(message: String) {
                onError(Throwable(message))
            }

            override fun onError(t: Throwable) {
                dialogLoading.stopDialog()
                DialogOK(
                    t.toString(),
                    "Erreur de connexion",
                    this@SignUpActivity
                ).startDialog(object : DialogOkListener {
                    override fun onOkClick(alertDialog: AlertDialog) {
                        alertDialog.dismiss()
                        return
                    }
                })
            }

        })
    }

    private fun signIn(userRequest: UserRequest) {
        Network.signIn(userRequest, object : NetworkListener<String> {
            override fun onSuccess(data: String) {
                val userResponse = Gson().fromJson(data, UserResponse::class.java)
                dialogLoading.stopDialog()
                val bundle = Bundle()
                bundle.putString("token", userResponse.token)
                bundle.putSerializable("user", userResponse.user)
                val intent = Intent(this@SignUpActivity, MainActivity::class.java)
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
                    this@SignUpActivity
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