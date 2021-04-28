package com.example.morkborgcharactersheet.charactersheetviewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.morkborgcharactersheet.charactersheet.CharacterSheetFragment
import com.example.morkborgcharactersheet.inventory.InventoryFragment
import com.example.morkborgcharactersheet.selectcharacter.SelectCharacterFragment

class ViewPagerAdapter(fragment: Fragment, var characterId: Long) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SelectCharacterFragment(characterId)
            1 -> CharacterSheetFragment(characterId)
            else -> InventoryFragment(characterId)
        }
    }
}