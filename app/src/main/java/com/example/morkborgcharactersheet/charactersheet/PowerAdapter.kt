package com.example.morkborgcharactersheet.charactersheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.morkborgcharactersheet.R
import com.example.morkborgcharactersheet.database.Inventory
import com.example.morkborgcharactersheet.databinding.ListItemPowerBinding

class PowerAdapter(val clickListener: PowerListener): ListAdapter<Inventory, PowerAdapter.ViewHolder>(PowerDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemPowerBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: PowerListener, item: Inventory) {
            binding.inventory = item

            binding.powerNameText.text = item.name

            // TODO: Power icon saved as separate inventory value
            binding.powerIcon.setImageResource(R.drawable.ancient_scroll_2)

            binding.clickListener = clickListener

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemPowerBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

// Click listener
class PowerListener(val clickListener: (power: Inventory) -> Unit) {
    fun onClick(power: Inventory) = clickListener(power)
}

class PowerDiffCallback : DiffUtil.ItemCallback<Inventory>() {
    override fun areItemsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
        return oldItem.inventoryId == newItem.inventoryId
    }

    override fun areContentsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
        return oldItem == newItem
    }
}