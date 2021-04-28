package com.example.morkborgcharactersheet.charactersheetviewpager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnAttach
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.morkborgcharactersheet.R
import com.example.morkborgcharactersheet.database.CharacterDatabase
import com.example.morkborgcharactersheet.database.CharacterDatabaseDAO
import com.example.morkborgcharactersheet.editcharacter.EditCharacterFragmentDirections
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class CharacterSheetViewPagerFragment : Fragment() {
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var viewModel: CharacterSheetViewPagerViewModel
    private var characterId by Delegates.notNull<Long>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val arguments by navArgs<CharacterSheetViewPagerFragmentArgs>()
        characterId = arguments.characterId

        val application = requireNotNull(this.activity).application
        val dataSource = CharacterDatabase.getInstance(application).characterDatabaseDAO

        val viewModelFactory = CharacterSheetViewPagerViewModelFactory(characterId, dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CharacterSheetViewPagerViewModel::class.java)

        return inflater.inflate(R.layout.view_pager_character_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPagerAdapter = ViewPagerAdapter(this, characterId)
        viewPager = view.findViewById(R.id.view_pager)
        viewPager.adapter = viewPagerAdapter

        // We want to start on the characterSheet tab in the middle
        viewPager.doOnAttach {
            viewPager.setCurrentItem(1, false)
        }

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.character_select_string)
                1 -> ""
                else -> getString(R.string.inventory)
            }
        }.attach()

        // ViewModel might pull character's name after view creates, so we set the tab name here.
        viewModel.characterName.observe(viewLifecycleOwner, Observer {
            if(it != "") {
                tabLayout.getTabAt(1)?.setText(it)
            }
        })
    }
}