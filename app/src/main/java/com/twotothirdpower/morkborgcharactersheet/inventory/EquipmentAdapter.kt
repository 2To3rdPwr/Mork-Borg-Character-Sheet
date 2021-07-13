package com.twotothirdpower.morkborgcharactersheet.inventory

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.twotothirdpower.morkborgcharactersheet.databinding.ListItemInventoryBinding
import com.twotothirdpower.morkborgcharactersheet.models.ExpandableEquipment
import com.twotothirdpower.morkborgcharactersheet.util.EquipmentDiffCallback
import com.twotothirdpower.morkborgcharactersheet.util.EquipmentListener

class EquipmentAdapter(val clickListener: EquipmentListener): ListAdapter<ExpandableEquipment, EquipmentAdapter.ViewHolder>(
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

    class ViewHolder private constructor(val binding: ListItemInventoryBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ClickableViewAccessibility")
        fun bind(clickListener: EquipmentListener, item: ExpandableEquipment) {
            binding.equipment = item
            // Override Recyclerview's touch to allow description scrolling
            binding.equipmentDescriptionText.setOnTouchListener{ v, event ->
                binding.equipmentDescriptionScrollable.onTouchEvent(event)
                v.parent.requestDisallowInterceptTouchEvent(true)
                true
            }
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemInventoryBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}