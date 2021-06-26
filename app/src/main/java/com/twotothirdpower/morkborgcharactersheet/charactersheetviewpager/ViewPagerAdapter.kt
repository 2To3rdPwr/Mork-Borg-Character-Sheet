package com.twotothirdpower.morkborgcharactersheet.charactersheetviewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.twotothirdpower.morkborgcharactersheet.charactersheet.CharacterSheetFragment
import com.twotothirdpower.morkborgcharactersheet.inventory.InventoryFragment
import com.twotothirdpower.morkborgcharactersheet.selectcharacter.SelectCharacterFragment

class ViewPagerAdapter(fragment: Fragment, var characterId: Long) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SelectCharacterFragment.newInstance(characterId)
            1 -> CharacterSheetFragment.newInstance(characterId)
            else -> InventoryFragment.newInstance(characterId)
        }
    }
}