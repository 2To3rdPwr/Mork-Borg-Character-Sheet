package com.twotothirdpower.morkborgcharactersheet.selectcharacter

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
import com.twotothirdpower.morkborgcharactersheet.charactersheetviewpager.CharacterSheetViewPagerFragmentDirections
import com.twotothirdpower.morkborgcharactersheet.database.CharacterDatabase
import com.twotothirdpower.morkborgcharactersheet.databinding.FragmentSelectCharacterBinding

class SelectCharacterFragment : Fragment() {
    companion object {
        private const val CHARACTER_ID = "characterId"

        fun newInstance(characterId: Long) = SelectCharacterFragment().apply {
            arguments = Bundle(1).apply {
                putLong(CHARACTER_ID, characterId)
            }
        }
    }

    private lateinit var viewModel: SelectCharacterViewModel
    private lateinit var binding: FragmentSelectCharacterBinding
    private var characterId: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_character, container, false)
        val application = requireNotNull(this.activity).application

        arguments?.let {
            characterId = it.getLong(CHARACTER_ID)
        }

        // Create an instance of the ViewModel Factory.
        val dataSource = CharacterDatabase.getInstance(application).characterDatabaseDAO
        val viewModelFactory = SelectCharacterViewModelFactory(characterId!!, dataSource)

        viewModel = ViewModelProvider(this, viewModelFactory).get(SelectCharacterViewModel::class.java)

        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        /**
         * Observers for events
         */
        viewModel.newCharacterEvent.observe(viewLifecycleOwner, Observer { event ->
            if(event) {
                findNavController().navigate(CharacterSheetViewPagerFragmentDirections.actionCharacterSheetViewPagerFragmentToIntroFragment())
                viewModel.onNewCharacterEventDone()
            }
        })

        viewModel.selectedCharacter.observe(viewLifecycleOwner, Observer { characterId ->
            if(characterId >= 0) {
                findNavController().navigate(CharacterSheetViewPagerFragmentDirections.actionCharacterSheetViewPagerFragmentSelf(characterId))
                viewModel.onNewCharacterEventDone()
            }
        })

        /**
         * Recyclerview bindings
         */
        val characterSelectAdapter = CharacterSelectAdapter(CharacterSelectListener() { characterId, type  ->
            viewModel.onCharacterListClick(characterId, type)
        })
        binding.characterSelectRecyclerview.adapter = characterSelectAdapter

        viewModel.characters.observe(viewLifecycleOwner, Observer {
            it?.let {
                characterSelectAdapter.submitList(it)
            }
        })

        return binding.root
    }
}