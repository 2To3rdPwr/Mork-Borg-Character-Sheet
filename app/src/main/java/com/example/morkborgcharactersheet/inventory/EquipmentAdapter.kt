package com.example.morkborgcharactersheet.inventory

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.morkborgcharactersheet.R
import com.example.morkborgcharactersheet.database.Inventory
import com.example.morkborgcharactersheet.models.ItemType
import com.example.morkborgcharactersheet.databinding.ListItemInventoryBinding
import com.example.morkborgcharactersheet.models.AbilityType
import kotlinx.android.synthetic.main.list_item_inventory.view.*

class EquipmentAdapter(val clickListener: EquipmentListener): ListAdapter<Inventory, EquipmentAdapter.ViewHolder>(EquipmentDiffCallback()) {
    // TODO: Instead of Inventory, use a custom model specifically for this recyclerview.
    //      Custom model would contain expandability info as well as replace ${D1} and ${D2} in the description

    // TODO: Allow OTHER items to be used

    // Enum used to tell InventoryViewModel which button was pressed in EquipmentListener.
    enum class EquiptmentRecyclerViewButton {
        EQUIP,
        RELOAD,
        EDIT,
        DELETE
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemInventoryBinding): RecyclerView.ViewHolder(binding.root) {
        var expandDescription = false

        // TODO: All these bindings should be done between the model and the xml directly
        fun bind(clickListener: EquipmentListener, item: Inventory) {
            binding.inventory = item

            Log.i("Uses", item.uses.toString())
            when (item.uses) {
                0, -1 -> {
                    binding.inventoryUsesOneIcon.visibility = View.INVISIBLE
                    binding.inventoryUsesTwoIcon.visibility = View.INVISIBLE
                    binding.inventoryUsesThreeIcon.visibility = View.INVISIBLE
                }
                1 -> {
                    binding.inventoryUsesOneIcon.visibility = View.VISIBLE
                    binding.inventoryUsesTwoIcon.visibility = View.INVISIBLE
                    binding.inventoryUsesThreeIcon.visibility = View.INVISIBLE
                }
                2 -> {
                    binding.inventoryUsesOneIcon.visibility = View.VISIBLE
                    binding.inventoryUsesTwoIcon.visibility = View.VISIBLE
                    binding.inventoryUsesThreeIcon.visibility = View.INVISIBLE
                }
                3 -> {
                    binding.inventoryUsesOneIcon.visibility = View.VISIBLE
                    binding.inventoryUsesTwoIcon.visibility = View.VISIBLE
                    binding.inventoryUsesThreeIcon.visibility = View.VISIBLE
                    binding.inventoryUsesThreeIcon.setImageResource(R.drawable.dot)
                }
                else -> {
                    binding.inventoryUsesOneIcon.visibility = View.VISIBLE
                    binding.inventoryUsesTwoIcon.visibility = View.VISIBLE
                    binding.inventoryUsesThreeIcon.visibility = View.VISIBLE
                    binding.inventoryUsesThreeIcon.setImageResource(R.drawable.plus)
                }
            }

            // Not all item types are equip-able.
            if (item.type == ItemType.OTHER.id)
                binding.usingCheckBox.visibility = View.INVISIBLE
            else
                binding.usingCheckBox.visibility = View.VISIBLE

            binding.equipmentDescriptionText.text = formatDescription(item)

            binding.equipmentExpandableGroup.visibility = View.GONE

            binding.clickListener = clickListener

            binding.executePendingBindings()
        }

        private fun formatDescription(inventory: Inventory): String {
            var dice1: String; var dice2: String
            if (inventory.type == ItemType.WEAPON.id) {
                dice1 = inventory.dice2Amount.toString() + "D" + inventory.dice2Value.toString()
                if (inventory.dice2Bonus != 0) {
                    dice1 += "+" + inventory.dice2Bonus.toString()
                }
                if (inventory.dice2Ability != AbilityType.UNTYPED.id) {
                    dice1 += "+" + AbilityType.get(inventory.dice2Ability).toString()
                }

                dice2 = "\${D2}"
            } else {
                dice1 = inventory.dice1Amount.toString() + "D" + inventory.dice1Value.toString()
                if (inventory.dice1Bonus != 0) {
                    dice1 += "+" + inventory.dice1Bonus.toString()
                }
                if (inventory.dice1Ability != AbilityType.UNTYPED.id) {
                    dice1 += "+" + AbilityType.get(inventory.dice1Ability).toString()
                }

                dice2 = inventory.dice2Amount.toString() + "D" + inventory.dice2Value.toString()
                if (inventory.dice2Bonus != 0) {
                    dice2 += "+" + inventory.dice2Bonus.toString()
                }
                if (inventory.dice2Ability != AbilityType.UNTYPED.id) {
                    dice2 += "+" + AbilityType.get(inventory.dice2Ability).toString()
                }
            }

            var description = inventory.description.replace("\${D1}", dice1)
            description = description.replace("\${D2}", dice2)

            return description
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemInventoryBinding.inflate(layoutInflater, parent, false)
                var viewHolder = ViewHolder(binding)

                // TODO: Replace this with bindings to model values
                viewHolder.itemView.equipment_name.setOnClickListener {
                    viewHolder.expandDescription = !viewHolder.expandDescription
                    viewHolder.itemView.equipment_expandable_group.visibility =
                            if (viewHolder.expandDescription) View.VISIBLE else View.GONE
                }

                return viewHolder
            }
        }
    }
}

// Click listener
class EquipmentListener(val clickListener: (equipment: Inventory, type: EquipmentAdapter.EquiptmentRecyclerViewButton) -> Unit) {
    fun onClick(equipment: Inventory, type: EquipmentAdapter.EquiptmentRecyclerViewButton) = clickListener(equipment, type)
}

class EquipmentDiffCallback : DiffUtil.ItemCallback<Inventory>() {
    override fun areItemsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
        return oldItem.inventoryId == newItem.inventoryId
    }

    override fun areContentsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
        return oldItem == newItem
    }
}