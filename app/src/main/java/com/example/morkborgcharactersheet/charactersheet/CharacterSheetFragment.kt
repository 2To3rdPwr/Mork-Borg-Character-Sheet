package com.example.morkborgcharactersheet.charactersheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.morkborgcharactersheet.R
import com.example.morkborgcharactersheet.charactersheetviewpager.CharacterSheetViewPagerFragmentDirections
import com.example.morkborgcharactersheet.databinding.FragmentCharacterSheetBinding
import com.example.morkborgcharactersheet.dialogs.AttackResultDialogFragment
import com.example.morkborgcharactersheet.dialogs.BrokenDialogFragment
import com.example.morkborgcharactersheet.dialogs.DefenceDialogFragment
import com.example.morkborgcharactersheet.dialogs.PowerResultDialogFragment
import com.example.morkborgcharactersheet.models.AbilityType
import com.example.morkborgcharactersheet.models.DiceValue
import com.example.morkborgcharactersheet.util.DataBindingConverter
import com.google.android.material.snackbar.Snackbar

class CharacterSheetFragment(var characterId: Long) : Fragment(){
    // Creating a viewmodel without ViewModelProvider allows me to share it with other fragments
    private val characterSheetViewModel: CharacterSheetViewModel by viewModels(ownerProducer = { this }) { CharacterSheetViewModelFactory(characterId, requireNotNull(this.activity).application) }
    private lateinit var binding: FragmentCharacterSheetBinding

    override fun onResume() {
        // Reload character onResume to catch changes made by other fragments in the ViewPager
        characterSheetViewModel.loadCharacter()
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Get a reference to the binding object and inflate the fragment views.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_character_sheet, container, false)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.characterSheetViewModel = characterSheetViewModel

        /**
         * Recyclerview bindings
         */
        val attackAdapter = AttackAdapter(AttackListener { attack ->
            characterSheetViewModel.onAttackClicked(attack)
        })
        binding.attacksList.adapter = attackAdapter

        characterSheetViewModel.attacks.observe(viewLifecycleOwner, Observer {
            it?.let {
                attackAdapter.submitList(it)
            }
        })

        val powerAdapter = PowerAdapter(PowerListener { power ->
            characterSheetViewModel.onPowerClicked(power)
        })
        binding.powersList.adapter = powerAdapter

        characterSheetViewModel.powers.observe(viewLifecycleOwner, Observer {
            it?.let {
                powerAdapter.submitList(it)
            }
        })

        /**
         * Observers for events
         */
        // For some reason I can't get Spinners to two-way bind consistently. Manually binding instead
        // Damage
        binding.sheetCustomRoller.customDiceRollerDiceValueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Can't select nothing
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                characterSheetViewModel.setCustomRollerDiceValue(DataBindingConverter.convertSpinnerPositionToDiceValue(position)?: DiceValue.D2) // Probably better to throw an error instead of just defaulting to D2
            }
        }

        binding.sheetCustomRoller.customDiceRollerAbilitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Can't select nothing
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                characterSheetViewModel.setCustomRollerAbility(DataBindingConverter.convertSpinnerPositionToAbilityType(position)?: AbilityType.UNTYPED)
            }
        }

        characterSheetViewModel.showAttackEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                AttackResultDialogFragment().show(childFragmentManager, "Attack")

                // Force refresh: Sometimes the uses indicators decide not to update.
                // notifyDataSetChanged() is not ideal, but this is a small list that doesn't *really*
                // need to be using RecyclerView, so I think we'll be fine.
                attackAdapter.notifyDataSetChanged()

                characterSheetViewModel.onShowAttackEventDone()
            }
        })

        characterSheetViewModel.showPowerEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                PowerResultDialogFragment().show(childFragmentManager, "Power")
            }
        })

        characterSheetViewModel.showDefenceEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                DefenceDialogFragment().show(childFragmentManager, "Defence")
            }
        })

        characterSheetViewModel.brokenType.observe(viewLifecycleOwner, Observer {
            if (it !== CharacterSheetViewModel.BrokenEventType.NOT_BROKEN) {
                BrokenDialogFragment().show(childFragmentManager, "Broken")
            }
        })

        characterSheetViewModel.editCharacterEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                characterSheetViewModel.onEditCharacterEventDone()
                findNavController().navigate(CharacterSheetViewPagerFragmentDirections.actionCharacterSheetViewPagerFragmentToEditCharacterFragment(characterSheetViewModel.character.value?.characterId?:-1))
            }
        })

        /**
         * Snackbar
         */
        characterSheetViewModel.snackbarText.observe(viewLifecycleOwner, Observer { text ->
            if(text != null) {
                val snackbarText = when (text) {
                    CharacterSheetViewModel.CharacterSheetSnackbarType.NO_USES -> getString(R.string.no_uses_snackbar_string)
                    CharacterSheetViewModel.CharacterSheetSnackbarType.NO_POWERS -> getString(R.string.no_powers_snackbar_string)
                    CharacterSheetViewModel.CharacterSheetSnackbarType.WEARING_ARMOR -> getString(R.string.no_power_wearing_armor_string)
                    else -> throw IllegalArgumentException("Invalid snackbar type")
                }
                Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        snackbarText,
                        Snackbar.LENGTH_LONG
                ).show()
                characterSheetViewModel.onSnackbarDone()
            }
        })

        return binding.root
    }

    // Ensure that changes made are saved when changing tabs
    override fun onPause() {
        super.onPause()
        characterSheetViewModel.onPause()
    }

    override fun onStop() {
        super.onStop()
        characterSheetViewModel.onPause()
    }
}