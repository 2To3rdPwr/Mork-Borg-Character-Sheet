package com.example.morkborgcharactersheet.charactersheetviewpager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.morkborgcharactersheet.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.properties.Delegates

class CharacterSheetViewPagerFragment : Fragment() {
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager2
    private var characterId by Delegates.notNull<Long>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val arguments by navArgs<CharacterSheetViewPagerFragmentArgs>()
        characterId = arguments.characterId
        return inflater.inflate(R.layout.view_pager_character_sheet, container, false)
    }

    /**
     * TODO: Execute omens from anywhere. Change "Character" tab to the character's name.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPagerAdapter = ViewPagerAdapter(this, characterId)
        viewPager = view.findViewById(R.id.view_pager)
        viewPager.adapter = viewPagerAdapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // TODO: Use string resources / characterName
            tab.text = if(position == 0) "Character" else "Inventory"
        }.attach()
    }
}