package com.twotothirdpower.morkborgcharactersheet.addinventory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.twotothirdpower.morkborgcharactersheet.databinding.ListItemDefaultInventoryBinding
import com.twotothirdpower.morkborgcharactersheet.models.ExpandableEquipment
import com.twotothirdpower.morkborgcharactersheet.util.EquipmentDiffCallback
import com.twotothirdpower.morkborgcharactersheet.util.EquipmentListener

class DefaultEquipmentAdapter(val clickListener: EquipmentListener): ListAdapter<ExpandableEquipment, DefaultEquipmentAdapter.ViewHolder>(
    EquipmentDiffCallback()
) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item.position = position
        holder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemDefaultInventoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: EquipmentListener, item: ExpandableEquipment) {
            binding.equipment = item
            // Override Recyclerview's touch to allow description scrolling
            binding.inventoryDefaultItemDescription.setOnTouchListener{ v, event ->
                binding.defaultInventoryDescriptionScrollable.onTouchEvent(event)
                v.parent.requestDisallowInterceptTouchEvent(true)
                true
            }
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemDefaultInventoryBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}