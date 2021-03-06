package com.twotothirdpower.morkborgcharactersheet.editinventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.twotothirdpower.morkborgcharactersheet.R
import com.twotothirdpower.morkborgcharactersheet.models.ItemType.*
import com.twotothirdpower.morkborgcharactersheet.database.CharacterDatabase
import com.twotothirdpower.morkborgcharactersheet.databinding.FragmentInventoryEditBinding
import com.twotothirdpower.morkborgcharactersheet.models.AbilityType
import com.twotothirdpower.morkborgcharactersheet.models.DiceValue
import com.twotothirdpower.morkborgcharactersheet.models.ItemType
import com.twotothirdpower.morkborgcharactersheet.util.DataBindingConverter
import com.google.android.material.snackbar.Snackbar

class EditInventoryFragment : Fragment(){
    private lateinit var editInventoryViewModel: EditInventoryViewModel
    private lateinit var binding: FragmentInventoryEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inventory_edit, container, false)

        val application = requireNotNull(this.activity).application
        val arguments by navArgs<EditInventoryFragmentArgs>()


        // Create an instance of the ViewModel Factory.
        val dataSource = CharacterDatabase.getInstance(application).characterDatabaseDAO
        val viewModelFactory = EditInventoryViewModelFactory(arguments.inventoryId, arguments.characterId, dataSource)

        editInventoryViewModel = ViewModelProvider(this, viewModelFactory).get(EditInventoryViewModel::class.java)

        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.editInventoryViewModel = editInventoryViewModel

        /**
         * FE Event Listeners
         */
        // For some reason I can't get Spinners to two-way bind consistently. Manually binding instead
        // Damage
        binding.fragmentItemNewDamageRoller.customDiceRollerDiceValueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Can't select nothing
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                editInventoryViewModel.setDamageRollerDiceValue(DataBindingConverter.convertSpinnerPositionToDiceValue(position)?:DiceValue.D0) // Probably better to throw an error instead of just defaulting to D0
            }
        }

        binding.fragmentItemNewDamageRoller.customDiceRollerAbilitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Can't select nothing
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                editInventoryViewModel.setDamageRollerAbility(DataBindingConverter.convertSpinnerPositionToAbilityType(position)?:AbilityType.UNTYPED)
            }
        }

        // Description1
        binding.fragmentItemNewDescriptionRoller1.customDiceRollerDiceValueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Can't select nothing
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                editInventoryViewModel.setDescription1RollerDiceValue(DataBindingConverter.convertSpinnerPositionToDiceValue(position)?:DiceValue.D0) // Probably better to throw an error instead of just defaulting to D0
            }
        }

        binding.fragmentItemNewDescriptionRoller1.customDiceRollerAbilitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Can't select nothing
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                editInventoryViewModel.setDescription1RollerAbility(DataBindingConverter.convertSpinnerPositionToAbilityType(position)?:AbilityType.UNTYPED)
            }
        }

        // Description2
        binding.fragmentItemNewDescriptionRoller2.customDiceRollerDiceValueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Can't select nothing
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                editInventoryViewModel.setDescription2RollerDiceValue(DataBindingConverter.convertSpinnerPositionToDiceValue(position)?:DiceValue.D0) // Probably better to throw an error instead of just defaulting to D0
            }
        }

        binding.fragmentItemNewDescriptionRoller2.customDiceRollerAbilitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Can't select nothing
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                editInventoryViewModel.setDescription2RollerAbility(DataBindingConverter.convertSpinnerPositionToAbilityType(position)?:AbilityType.UNTYPED)
            }
        }
        binding.executePendingBindings()
        // Done with bindings for spinners

        /**
         * LiveData observers
         */

        editInventoryViewModel.equipmentType.observe(viewLifecycleOwner, Observer { newType ->
            setVisibility(newType)
        })

        editInventoryViewModel.limitedUses.observe(viewLifecycleOwner, Observer { limited ->
            setUsesVisibility(limited)
        })

        /**
         * Event Observers
         */
        editInventoryViewModel.saveItemEvent.observe(viewLifecycleOwner, Observer { event ->
            if(event) {
                findNavController().popBackStack()
                editInventoryViewModel.onItemSaveComplete()
            }
        })

        // Not really a toast. Shows a snackbar alerting the user to set the item's name.
        editInventoryViewModel.showToastEvent.observe(viewLifecycleOwner, Observer { event ->
            if(event) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.name_required_string),
                    Snackbar.LENGTH_LONG
                ).show()
                editInventoryViewModel.onShowToastEventComplete()
            }
        })

        setVisibility(editInventoryViewModel.equipment.value?.type ?: WEAPON)

        return binding.root
    }

    /**
     * Conditional visibility logic
     * I could probably bind these directly to a livedata that keeps track of the needed visibility
     * states of different parts of the view, but that doesn't seem to align with MVVM standards of
     * keeping the view-specific logic here in the view
     */
    private fun setVisibility (type: ItemType?) {
        when (type) {
            WEAPON -> {
                binding.fragmentItemNewAbilitySelectText.visibility = View.VISIBLE
                binding.fragmentItemNewItemAbilityRadioGroup.visibility = View.VISIBLE
                binding.fragmentItemNewDamageRollerLabel.visibility = View.VISIBLE
                binding.fragmentItemNewDamageRoller.visible = true
                binding.fragmentItemNewTypeSpecificLayout.visibility = View.VISIBLE
                binding.fragmentItemNewLimitedUsesToggle.visibility = View.VISIBLE
                setUsesVisibility(binding.editInventoryViewModel?.limitedUses?.value == true)

                binding.fragmentItemNewDescriptionTutorialText.text = getString(R.string.new_item_1_roll_description_tip)
                binding.fragmentItemNewArmorTierLabel.visibility = View.GONE
                binding.fragmentItemNewArmorTierRadioGroup.visibility = View.GONE
            }
            ARMOR -> {
                binding.fragmentItemNewAbilitySelectText.visibility = View.GONE
                binding.fragmentItemNewItemAbilityRadioGroup.visibility = View.GONE
                binding.fragmentItemNewDamageRollerLabel.visibility = View.GONE
                binding.fragmentItemNewDamageRoller.visible = false
                binding.fragmentItemNewLimitedUsesToggle.visibility = View.GONE
                binding.fragmentItemNewUsesGroup.visibility = View.GONE
                binding.fragmentItemNewTypeSpecificLayout.visibility = View.VISIBLE

                binding.fragmentItemNewDescriptionTutorialText.text = getString(R.string.new_item_2_roll_description_tip)
                binding.fragmentItemNewArmorTierLabel.visibility = View.VISIBLE
                binding.fragmentItemNewArmorTierRadioGroup.visibility = View.VISIBLE
            }
            POWER -> {
                binding.fragmentItemNewTypeSpecificLayout.visibility = View.GONE
                binding.fragmentItemNewLimitedUsesToggle.visibility = View.GONE
                binding.fragmentItemNewUsesGroup.visibility = View.GONE

                binding.fragmentItemNewDescriptionTutorialText.text = getString(R.string.new_item_2_roll_description_tip)
            }
            SHIELD -> {
                binding.fragmentItemNewTypeSpecificLayout.visibility = View.GONE
                binding.fragmentItemNewLimitedUsesToggle.visibility = View.GONE
                binding.fragmentItemNewUsesGroup.visibility = View.GONE

                binding.fragmentItemNewDescriptionTutorialText.text = getString(R.string.new_item_2_roll_description_tip)
            }
            OTHER -> {
                binding.fragmentItemNewTypeSpecificLayout.visibility = View.GONE
                binding.fragmentItemNewLimitedUsesToggle.visibility = View.VISIBLE
                setUsesVisibility(binding.editInventoryViewModel?.limitedUses?.value == true)

                binding.fragmentItemNewDescriptionTutorialText.text = getString(R.string.new_item_2_roll_description_tip)
            }
            else -> {
                binding.fragmentItemNewLimitedUsesToggle.visibility = View.GONE
                binding.fragmentItemNewTypeSpecificLayout.visibility = View.GONE
                binding.fragmentItemNewUsesGroup.visibility = View.GONE

                binding.fragmentItemNewDescriptionTutorialText.text = getString(R.string.new_item_2_roll_description_tip)
            }
        }
    }

    private fun setUsesVisibility (limited: Boolean) {
        // Observers for toggles seem to fire off upon view creation, calling this too soon, so we need to better enforce calling this
        if (binding.editInventoryViewModel?.equipment?.value?.type !== WEAPON && binding.editInventoryViewModel?.equipment?.value?.type !== OTHER) {
            return
        }

        if (limited) {
            binding.fragmentItemNewStaticUsesEditText.visibility = View.VISIBLE
            binding.fragmentItemNewStaticUsesLabel.visibility = View.VISIBLE
        } else {
            binding.fragmentItemNewStaticUsesEditText.visibility = View.GONE
            binding.fragmentItemNewStaticUsesLabel.visibility = View.GONE
        }
    }
}