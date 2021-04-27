package com.example.morkborgcharactersheet.charactersheetviewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.morkborgcharactersheet.charactersheet.CharacterSheetFragment
import com.example.morkborgcharactersheet.inventory.InventoryFragment

class ViewPagerAdapter(fragment: Fragment, var characterId: Long) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if(position == 0)
            CharacterSheetFragment(characterId)
        else
            InventoryFragment(characterId)
    }
}