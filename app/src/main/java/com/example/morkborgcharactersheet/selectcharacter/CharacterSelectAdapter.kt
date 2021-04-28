package com.example.morkborgcharactersheet.selectcharacter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.morkborgcharactersheet.database.ListedCharacter
import com.example.morkborgcharactersheet.databinding.ListItemCharacterBinding
import com.example.morkborgcharactersheet.inventory.EquipmentAdapter

class CharacterSelectAdapter(val clickListener: CharacterSelectListener): ListAdapter<ListedCharacter, CharacterSelectAdapter.ViewHolder>(CharacterSelectDiffCallback()) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemCharacterBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: CharacterSelectListener, item: ListedCharacter) {
            binding.character = item
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCharacterBinding.inflate(layoutInflater, parent, false)
                var viewHolder = ViewHolder(binding)
                return viewHolder
            }
        }
    }
}

class CharacterSelectListener(val clickListener: (characterId: Long, type: CharacterSelectButton) -> Unit) {

    enum class CharacterSelectButton {
        SELECT,
        DELETE
    }

    fun onClick(characterId: Long, type: CharacterSelectButton) = clickListener(characterId, type)
}

class CharacterSelectDiffCallback : DiffUtil.ItemCallback<ListedCharacter>() {
    override fun areItemsTheSame(oldItem: ListedCharacter, newItem: ListedCharacter): Boolean {
        return oldItem.characterId == newItem.characterId
    }

    override fun areContentsTheSame(oldItem: ListedCharacter, newItem: ListedCharacter): Boolean {
        return oldItem == newItem
    }
}