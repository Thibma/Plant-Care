package fr.thibma.plantcare.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import fr.thibma.plantcare.adapter.RobotListAdapter
import fr.thibma.plantcare.models.Robot
import fr.thibma.plantcare.models.User
import fr.thibma.plantcare.services.Network
import fr.thibma.plantcare.services.NetworkListener
import fr.thibma.plantcare.utils.DialogOK
import fr.thibma.plantcare.utils.DialogOkListener

class RobotListActivity(private var user: User, private var token: String) : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var robotListAdapter: RobotListAdapter

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //recyclerView = requireViewById(R.layout.)
        refreshList()
    }

    private fun refreshList() {
        Network.getAllRobotByUser(user.id, token, object : NetworkListener<String> {
            override fun onSuccess(data: String) {
                val robotList: List<Robot> = Gson().fromJson(data, Array<Robot>::class.java).toList()
                setRecyclerView(robotList)
            }

            override fun onErrorApi(message: String) {
                onError(Throwable(message))
            }

            override fun onError(t: Throwable) {
                DialogOK(
                    t.toString(),
                    "Erreur de connexion",
                    this@RobotListActivity
                ).startDialog(object : DialogOkListener {
                    override fun onOkClick(alertDialog: androidx.appcompat.app.AlertDialog) {
                        alertDialog.dismiss()
                    }
                })
            }
        })
    }

    private fun setRecyclerView(robotList: List<Robot>) {
        robotListAdapter = RobotListAdapter(robotList)
        recyclerView.adapter = robotListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.applicationContext)
        recyclerView.setHasFixedSize((true))
    }
}