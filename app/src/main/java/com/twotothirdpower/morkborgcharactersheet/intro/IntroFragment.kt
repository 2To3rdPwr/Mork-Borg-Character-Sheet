package com.twotothirdpower.morkborgcharactersheet.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.twotothirdpower.morkborgcharactersheet.R
import com.twotothirdpower.morkborgcharactersheet.database.CharacterDatabase
import com.twotothirdpower.morkborgcharactersheet.databinding.FragmentIntroBinding

class IntroFragment : Fragment() {
    private lateinit var viewModel: IntroFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentIntroBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_intro, container, false)
        val application = requireNotNull(this.activity).application

        val dataSource = CharacterDatabase.getInstance(application).characterDatabaseDAO
        val viewModelFactory = IntroViewModelFactory(dataSource)

        viewModel = ViewModelProvider(this, viewModelFactory).get(IntroFragmentViewModel::class.java)

        binding.autoGenerateCharacterButton.setOnClickListener{
            genNewCharacter()
        }

        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.viewModel = viewModel

        /**
         * Observers for events
         */
        viewModel.autoGenerateEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                viewModel.onAutoGenerateComplete()
                findNavController().navigate(IntroFragmentDirections.actionIntroFragmentToCharacterSheetViewPagerFragment(viewModel.newCharacterId.value!!))
            }
        })

        viewModel.manualGenerateEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                viewModel.onManualGenerateComplete()
                findNavController().navigate(IntroFragmentDirections.actionIntroFragmentToEditCharacterFragment())
            }
        })

        return binding.root
    }

    private fun genNewCharacter() {
        val names = resources.getStringArray(R.array.character_names_array)
        val backstories = resources.getStringArray(R.array.character_backstories_array)
        val traits = resources.getStringArray(R.array.character_traits_array)
        val quirks = resources.getStringArray(R.array.character_quirks_array)
        val appearance = resources.getStringArray(R.array.character_appearance_array)

        viewModel.generateNewCharacter(names, backstories, traits, quirks, appearance)
    }
}