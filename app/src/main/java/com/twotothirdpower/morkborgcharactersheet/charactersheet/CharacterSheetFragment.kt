package com.twotothirdpower.morkborgcharactersheet.charactersheet

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
import com.twotothirdpower.morkborgcharactersheet.R
import com.twotothirdpower.morkborgcharactersheet.charactersheetviewpager.CharacterSheetViewPagerFragmentDirections
import com.twotothirdpower.morkborgcharactersheet.databinding.FragmentCharacterSheetBinding
import com.twotothirdpower.morkborgcharactersheet.models.AbilityType
import com.twotothirdpower.morkborgcharactersheet.models.DiceValue
import com.twotothirdpower.morkborgcharactersheet.util.DataBindingConverter
import com.google.android.material.snackbar.Snackbar
import com.twotothirdpower.morkborgcharactersheet.dialogs.*

class CharacterSheetFragment : Fragment(){
    // Companion object allows us to pass args from ViewPager
    companion object {
        private const val CHARACTER_ID = "characterId"

        fun newInstance(characterId: Long) = CharacterSheetFragment().apply {
            arguments = Bundle(1).apply {
                putLong(CHARACTER_ID, characterId)
            }
        }
    }
    private lateinit var characterSheetViewModel: CharacterSheetViewModel
    private lateinit var binding: FragmentCharacterSheetBinding
    private var characterId: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Get a reference to the binding object and inflate the fragment views.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_character_sheet, container, false)

        arguments?.let {
            characterId = it.getLong(CHARACTER_ID)
        }

        // Creating a viewmodel without ViewModelProvider allows me to share it with other fragments
        val mCharacterSheetViewModel: CharacterSheetViewModel by viewModels(ownerProducer = { this }) { CharacterSheetViewModelFactory(characterId!!, requireNotNull(this.activity).application) }
        characterSheetViewModel = mCharacterSheetViewModel

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

        characterSheetViewModel.showDescriptionEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                CharacterDescriptionDialogFragment.newInstance(characterSheetViewModel.character.value?.characterName ?: throw IllegalArgumentException("No character name"), characterSheetViewModel.character.value?.description ?: throw IllegalArgumentException("No description")).show(childFragmentManager, CharacterDescriptionDialogFragment.TAG)
                characterSheetViewModel.onShowDescriptionDone()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        // Reload character onResume to catch changes made by other fragments in the ViewPager
        characterSheetViewModel.loadCharacter()
        super.onResume()
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