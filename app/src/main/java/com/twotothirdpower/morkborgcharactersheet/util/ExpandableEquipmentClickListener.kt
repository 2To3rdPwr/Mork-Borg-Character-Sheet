package com.twotothirdpower.morkborgcharactersheet.util

import androidx.recyclerview.widget.DiffUtil
import com.twotothirdpower.morkborgcharactersheet.models.ExpandableEquipment

// Click listener and diff utils for Recyclerviews containing ExpandableEquipment

enum class EquipmentRecyclerViewButton {
    EQUIP,
    EXPAND,
    ADD,
    USE,
    EDIT,
    DELETE
}

class EquipmentListener(val clickListener: (equipment: ExpandableEquipment, type: EquipmentRecyclerViewButton) -> Unit) {
    fun onClick(equipment: ExpandableEquipment, type: EquipmentRecyclerViewButton) = clickListener(equipment, type)
}

class EquipmentDiffCallback : DiffUtil.ItemCallback<ExpandableEquipment>() {
    override fun areItemsTheSame(oldItem: ExpandableEquipment, newItem: ExpandableEquipment): Boolean {
        return oldItem.joinId == newItem.joinId
    }

    override fun areContentsTheSame(oldItem: ExpandableEquipment, newItem: ExpandableEquipment): Boolean {
        return oldItem == newItem
    }
}