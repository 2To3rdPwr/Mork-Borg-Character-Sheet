package com.example.morkborgcharactersheet.charactersheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.morkborgcharactersheet.R
import com.example.morkborgcharactersheet.database.Inventory
import com.example.morkborgcharactersheet.models.ItemType.*
import com.example.morkborgcharactersheet.databinding.ListItemAttackBinding

class AttackAdapter(val clickListener: AttackListener): ListAdapter<Inventory, AttackAdapter.ViewHolder>(AttackDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemAttackBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: AttackListener, item: Inventory) {
            binding.inventory = item

            binding.attackNameText.text = item.name

            binding.diceValueText.text = formatDiceString(item)

            // TODO: Let user set weapon icons
            if(item.type == WEAPON.id) {
                binding.attackIcon.setImageResource(R.drawable.sword)
            } else {
                binding.attackIcon.setImageResource(R.drawable.bow)
            }

            when (item.uses) {
                0, -1 -> {
                    binding.usesOneIcon.visibility = View.INVISIBLE
                    binding.usesTwoIcon.visibility = View.INVISIBLE
                    binding.usesThreePlusIcon.visibility = View.INVISIBLE
                }
                1 -> {
                    binding.usesOneIcon.visibility = View.VISIBLE
                    binding.usesTwoIcon.visibility = View.INVISIBLE
                    binding.usesThreePlusIcon.visibility = View.INVISIBLE
                }
                2 -> {
                    binding.usesOneIcon.visibility = View.VISIBLE
                    binding.usesTwoIcon.visibility = View.VISIBLE
                    binding.usesThreePlusIcon.visibility = View.INVISIBLE
                }
                3 -> {
                    binding.usesOneIcon.visibility = View.VISIBLE
                    binding.usesTwoIcon.visibility = View.VISIBLE
                    binding.usesThreePlusIcon.visibility = View.VISIBLE
                    binding.usesThreePlusIcon.setImageResource(R.drawable.dot)
                }
                else -> {
                    binding.usesOneIcon.visibility = View.VISIBLE
                    binding.usesTwoIcon.visibility = View.VISIBLE
                    binding.usesThreePlusIcon.visibility = View.VISIBLE
                    binding.usesThreePlusIcon.setImageResource(R.drawable.plus)
                }
            }

            binding.clickListener = clickListener

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemAttackBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        private fun formatDiceString(item: Inventory): String {
            // TODO: add ability bonus
            //      (Might need to get ability bonus from bundle)
            var diceString = ""
            if(item.dice1Amount > 0)
                diceString = item.dice1Amount.toString()
            diceString = diceString + "D" + item.dice1Value
            if(item.dice1Bonus > 0)
                diceString = diceString + "+" + item.dice1Bonus
            else if(item.dice1Bonus < 0)
                diceString = diceString + item.dice1Bonus.toString()

            return diceString
        }
    }
}

// Click listener
class AttackListener(val clickListener: (attack: Inventory) -> Unit) {
    fun onClick(attack: Inventory) = clickListener(attack)
}

class AttackDiffCallback : DiffUtil.ItemCallback<Inventory>() {
    override fun areItemsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
        return oldItem.inventoryId == newItem.inventoryId
    }

    override fun areContentsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
        return oldItem == newItem
    }
}