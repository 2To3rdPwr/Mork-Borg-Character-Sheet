package com.twotothirdpower.morkborgcharactersheet.editcharacter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.twotothirdpower.morkborgcharactersheet.R
import com.twotothirdpower.morkborgcharactersheet.database.CharacterDatabase
import com.twotothirdpower.morkborgcharactersheet.databinding.FragmentEditCharacterBinding
import com.google.android.material.snackbar.Snackbar

class EditCharacterFragment : Fragment() {
    private lateinit var viewModel: EditCharacterViewModel
    private lateinit var binding: FragmentEditCharacterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_character, container, false)

        val application = requireNotNull(this.activity).application
        val arguments by navArgs<EditCharacterFragmentArgs>()

        val dataSource = CharacterDatabase.getInstance(application).characterDatabaseDAO
        val viewModelFactory = EditCharacterViewModelFactory(arguments.characterId, dataSource)

        viewModel = ViewModelProvider(this, viewModelFactory).get(EditCharacterViewModel::class.java)

        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.viewModel = viewModel

        /**
         * Observers for events
         */
        viewModel.saveEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                viewModel.onSaveEventDone()
                findNavController().navigate(EditCharacterFragmentDirections.actionEditCharacterFragmentToCharacterSheetViewPagerFragment(viewModel.newCharacterId.value!!))
            }
        })

        viewModel.snackbar.observe(viewLifecycleOwner, Observer { snackbarTest ->
            if (snackbarTest != "") {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    snackbarTest,
                    Snackbar.LENGTH_LONG
                ).show()
                viewModel.onSnackbarComplete()
            }
        })

        return binding.root
    }
}