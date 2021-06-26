package com.twotothirdpower.morkborgcharactersheet.charactersheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.twotothirdpower.morkborgcharactersheet.R
import com.twotothirdpower.morkborgcharactersheet.databinding.ListItemPowerBinding
import com.twotothirdpower.morkborgcharactersheet.models.Equipment

class PowerAdapter(val clickListener: PowerListener): ListAdapter<Equipment, PowerAdapter.ViewHolder>(PowerDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemPowerBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: PowerListener, item: Equipment) {
            binding.inventory = item

            // TODO: Power icon saved as separate inventory value
            // TODO: Move into XML
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
class PowerListener(val clickListener: (power: Equipment) -> Unit) {
    fun onClick(power: Equipment) = clickListener(power)
}

class PowerDiffCallback : DiffUtil.ItemCallback<Equipment>() {
    override fun areItemsTheSame(oldItem: Equipment, newItem: Equipment): Boolean {
        return oldItem.inventoryId == newItem.inventoryId
    }

    override fun areContentsTheSame(oldItem: Equipment, newItem: Equipment): Boolean {
        return oldItem == newItem
    }
}