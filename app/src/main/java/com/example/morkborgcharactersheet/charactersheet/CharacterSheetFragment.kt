package com.example.morkborgcharactersheet.charactersheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.morkborgcharactersheet.R
import com.example.morkborgcharactersheet.charactersheetviewpager.CharacterSheetViewPagerFragmentArgs
import com.example.morkborgcharactersheet.charactersheetviewpager.CharacterSheetViewPagerFragmentDirections
import com.example.morkborgcharactersheet.database.CharacterDatabase
import com.example.morkborgcharactersheet.databinding.FragmentCharacterSheetBinding
import com.example.morkborgcharactersheet.dialogs.AttackResultDialogFragment
import com.example.morkborgcharactersheet.dialogs.DefenceDialogFragment
import com.example.morkborgcharactersheet.dialogs.DiceResultDialogFragment
import com.example.morkborgcharactersheet.dialogs.PowerResultDialogFragment
import com.example.morkborgcharactersheet.models.AbilityType
import com.example.morkborgcharactersheet.models.DiceValue
import com.example.morkborgcharactersheet.util.DataBindingConverter

class CharacterSheetFragment(var characterId: Long) : Fragment(){
    // Creating a viewmodel without ViewModelProvider allows me to share it with other fragments
    private val characterSheetViewModel: CharacterSheetViewModel by viewModels(ownerProducer = { this }) { CharacterSheetViewModelFactory(characterId, requireNotNull(this.activity).application) }
    private lateinit var binding: FragmentCharacterSheetBinding

    // TODO: Save all data onStop
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

        // TODO: Refactor dialogs. Ideally we can just pass them an Equipment and whatever rolled values they need and be done with it.
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
                popPowerResultDialog(characterSheetViewModel.rolledValue.value!!, characterSheetViewModel.recentEquipment!!.name, characterSheetViewModel.powerDescriptionText?:"")
                characterSheetViewModel.onShowPowerEventDone()
            }
        })

        characterSheetViewModel.showDefenceEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                popDefenceDialog(characterSheetViewModel.rolledValue.value!!, characterSheetViewModel.rolledValue2.value!!)
                characterSheetViewModel.onShowDefenceEventDone()
            }
        })

        characterSheetViewModel.editCharacterEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                characterSheetViewModel.oneditCharacterEventDone()
                findNavController().navigate(CharacterSheetViewPagerFragmentDirections.actionCharacterSheetViewPagerFragmentToEditCharacterFragment(characterSheetViewModel.character.value?.characterId?:-1))
            }
        })

        return binding.root
    }

    /**
     * Dialog Builders
     * TODO: MVVM-ize dialogs
     *      https://proandroiddev.com/dialogs-in-android-mvvm-5d6e1ca53b19
     */
    fun getViewModel(): CharacterSheetViewModel {
        return characterSheetViewModel
    }

    private fun popPowerResultDialog(presTest: Int, name: String, description: String) {
        val newFragment = PowerResultDialogFragment()
        val bundle = Bundle()
        bundle.putInt("PRESTEST", presTest)
        bundle.putString("NAME", name)
        bundle.putString("DESC", description)
        newFragment.arguments = bundle

        newFragment.apply {
            listener = object :PowerResultDialogFragment.DialogListener {
                override fun onPowerSuccess(omenUsed: Boolean) {
                    characterSheetViewModel.onPowerComplete(false)
                }

                override fun onPowerFailed() {
                    characterSheetViewModel.onPowerComplete(true)
                }
            }
        }

        newFragment.show(parentFragmentManager, "Power")
    }

    private fun popDefenceDialog(aglTest: Int, damageReduction: Int) {
        val newFragment = DefenceDialogFragment()
        val bundle = Bundle()
        bundle.putInt("AGLTEST", aglTest)
        bundle.putInt("DR", damageReduction)
        newFragment.arguments = bundle

        newFragment.apply {
            listener = object  :DefenceDialogFragment.DialogListener {
                override fun onDamageTaken(damage: Int) {
                    characterSheetViewModel.onDefenceComplete(damage, false)
                }

                override fun onDamageAvoided(shieldUsed: Boolean) {
                    characterSheetViewModel.onDefenceComplete(0, shieldUsed)
                }
            }
        }

        newFragment.show(parentFragmentManager, "Defence")
    }
}