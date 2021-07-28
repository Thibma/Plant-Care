package fr.thibma.plantcare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.thibma.plantcare.R
import fr.thibma.plantcare.models.Robot

class RobotListAdapter(private val robotList: List<Robot>): RecyclerView.Adapter<RobotListAdapter.RobotListViewHolder>() {

    inner class RobotListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewRobotItem)
        val robotNameTextView: TextView = itemView.findViewById(R.id.robotNameTextViewRobotItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RobotListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_robot, parent, false)
        return RobotListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RobotListViewHolder, position: Int) {
        val currentItem = robotList[position]
        holder.robotNameTextView.text = currentItem.name
    }

    override fun getItemCount(): Int = robotList.size


}