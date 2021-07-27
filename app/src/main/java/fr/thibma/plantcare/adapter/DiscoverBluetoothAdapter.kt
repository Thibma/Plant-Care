package fr.thibma.plantcare.adapter

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.thibma.plantcare.R

class DiscoverBluetoothAdapter(private val bluetoothDevices: List<BluetoothDevice>, private val listener: OnDiscoveryBlutoothClickListener) : RecyclerView.Adapter<DiscoverBluetoothAdapter.DiscoverBluetoothViewHolder>() {

    inner class DiscoverBluetoothViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val bluetoothName: TextView = itemView.findViewById(R.id.textViewBluetoothName)
        val bluetoothMac: TextView = itemView.findViewById(R.id.textViewBluetoothMac)

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

    interface OnDiscoveryBlutoothClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverBluetoothViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_add_robot, parent, false)
        return DiscoverBluetoothViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiscoverBluetoothViewHolder, position: Int) {
        val currentItem = bluetoothDevices[position]

        holder.bluetoothName.text = currentItem.name
        holder.bluetoothMac.text = currentItem.address
    }

    override fun getItemCount(): Int = bluetoothDevices.size

}