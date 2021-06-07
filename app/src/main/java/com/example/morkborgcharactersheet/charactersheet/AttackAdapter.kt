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
import com.example.morkborgcharactersheet.models.Equipment

class AttackAdapter(val clickListener: AttackListener): ListAdapter<Equipment, AttackAdapter.ViewHolder>(AttackDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemAttackBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: AttackListener, item: Equipment) {
            binding.inventory = item
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
    }
}

// Click listener
class AttackListener(val clickListener: (attack: Equipment) -> Unit) {
    fun onClick(attack: Equipment) = clickListener(attack)
}

class AttackDiffCallback : DiffUtil.ItemCallback<Equipment>() {
    override fun areItemsTheSame(oldItem: Equipment, newItem: Equipment): Boolean {
        return oldItem.joinId == newItem.joinId
    }

    override fun areContentsTheSame(oldItem: Equipment, newItem: Equipment): Boolean {
        return oldItem == newItem
    }
}