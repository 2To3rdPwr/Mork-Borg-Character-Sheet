package com.twotothirdpower.morkborgcharactersheet.inventory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.twotothirdpower.morkborgcharactersheet.databinding.ListItemInventoryBinding
import com.twotothirdpower.morkborgcharactersheet.models.ExpandableEquipment

class EquipmentAdapter(val clickListener: EquipmentListener): ListAdapter<ExpandableEquipment, EquipmentAdapter.ViewHolder>(EquipmentDiffCallback()) {

    // Enum used to tell InventoryViewModel which button was pressed in EquipmentListener.
    enum class EquipmentRecyclerViewButton {
        EQUIP,
        EXPAND,
        USE,
        EDIT,
        DELETE
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item.position = position
        holder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemInventoryBinding): RecyclerView.ViewHolder(binding.root) {
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

// Click listener
class EquipmentListener(val clickListener: (equipment: ExpandableEquipment, type: EquipmentAdapter.EquipmentRecyclerViewButton) -> Unit) {
    fun onClick(equipment: ExpandableEquipment, type: EquipmentAdapter.EquipmentRecyclerViewButton) = clickListener(equipment, type)
}

class EquipmentDiffCallback : DiffUtil.ItemCallback<ExpandableEquipment>() {
    override fun areItemsTheSame(oldItem: ExpandableEquipment, newItem: ExpandableEquipment): Boolean {
        return oldItem.joinId == newItem.joinId
    }

    override fun areContentsTheSame(oldItem: ExpandableEquipment, newItem: ExpandableEquipment): Boolean {
        return oldItem == newItem
    }
}