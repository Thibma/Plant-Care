package fr.thibma.plantcare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.thibma.plantcare.R
import fr.thibma.plantcare.models.Robot

class RobotListAdapter(private val robotList: List<Robot>, private val listener: RobotListAdapter.OnRobotClickListener): RecyclerView.Adapter<RobotListAdapter.RobotListViewHolder>() {

    inner class RobotListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewRobotItem)
        val robotNameTextView: TextView = itemView.findViewById(R.id.robotNameTextViewRobotItem)
        val plantNameTextView: TextView = itemView.findViewById(R.id.plantNameTextViewRobotItem)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

    }

    interface OnRobotClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RobotListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_robot, parent, false)
        return RobotListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RobotListViewHolder, position: Int) {
        val currentItem = robotList[position]
        holder.robotNameTextView.text = currentItem.name
        holder.plantNameTextView.text =  currentItem.mainPlant?.name;
    }

    override fun getItemCount(): Int = robotList.size


}