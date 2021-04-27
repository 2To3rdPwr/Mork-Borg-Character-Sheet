package com.example.morkborgcharactersheet.editinventory

import android.os.Bundle
import android.util.Log
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
import com.example.morkborgcharactersheet.R
import com.example.morkborgcharactersheet.models.ItemType.*
import com.example.morkborgcharactersheet.database.CharacterDatabase
import com.example.morkborgcharactersheet.databinding.FragmentInventoryEditBinding
import com.example.morkborgcharactersheet.models.AbilityType
import com.example.morkborgcharactersheet.models.DiceValue
import com.example.morkborgcharactersheet.models.ItemType
import com.example.morkborgcharactersheet.util.DataBindingConverter
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
                Log.i("Position Selected", position.toString())
                editInventoryViewModel.setDamageRollerDiceValue(DataBindingConverter.convertSpinnerPositionToDiceValue(position)?:DiceValue.D2) // Probably better to throw an error instead of just defaulting to D2
            }
        }

        binding.fragmentItemNewDamageRoller.customDiceRollerAbilitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Can't select nothing
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.i("Position Selected", position.toString())
                editInventoryViewModel.setDamageRollerAbility(DataBindingConverter.convertSpinnerPositionToAbilityType(position)?:AbilityType.UNTYPED)
            }
        }

        // Uses
        binding.fragmentItemNewUsesRoller.customDiceRollerDiceValueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Can't select nothing
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.i("Position Selected", position.toString())
                editInventoryViewModel.setUsesRollerDiceValue(DataBindingConverter.convertSpinnerPositionToDiceValue(position)?:DiceValue.D2) // Probably better to throw an error instead of just defaulting to D2
            }
        }

        binding.fragmentItemNewUsesRoller.customDiceRollerAbilitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Can't select nothing
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.i("Position Selected", position.toString())
                editInventoryViewModel.setUsesRollerAbility(DataBindingConverter.convertSpinnerPositionToAbilityType(position)?:AbilityType.UNTYPED)
            }
        }

        // Description1
        binding.fragmentItemNewDescriptionRoller1.customDiceRollerDiceValueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Can't select nothing
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.i("Position Selected", position.toString())
                editInventoryViewModel.setDescription1RollerDiceValue(DataBindingConverter.convertSpinnerPositionToDiceValue(position)?:DiceValue.D2) // Probably better to throw an error instead of just defaulting to D2
            }
        }

        binding.fragmentItemNewDescriptionRoller1.customDiceRollerAbilitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Can't select nothing
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.i("Position Selected", position.toString())
                editInventoryViewModel.setDescription1RollerAbility(DataBindingConverter.convertSpinnerPositionToAbilityType(position)?:AbilityType.UNTYPED)
            }
        }

        // Description2
        binding.fragmentItemNewDescriptionRoller2.customDiceRollerDiceValueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Can't select nothing
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.i("Position Selected", position.toString())
                editInventoryViewModel.setDescription2RollerDiceValue(DataBindingConverter.convertSpinnerPositionToDiceValue(position)?:DiceValue.D2) // Probably better to throw an error instead of just defaulting to D2
            }
        }

        binding.fragmentItemNewDescriptionRoller2.customDiceRollerAbilitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.i("AAA", "AAA")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.i("Position Selected", position.toString())
                editInventoryViewModel.setDescription2RollerAbility(DataBindingConverter.convertSpinnerPositionToAbilityType(position)?:AbilityType.UNTYPED)
            }
        }
        binding.executePendingBindings()
        // Done with bindings for spinners

        /**
         * LiveData observers
         */

        editInventoryViewModel.itemType.observe(viewLifecycleOwner, Observer { newType ->
            setVisibility(newType)
        })

        editInventoryViewModel.limitedUse.observe(viewLifecycleOwner, Observer { limited ->
            setUsesVisibility(limited, editInventoryViewModel.rolledMaxUses.value == true, editInventoryViewModel.refillableUses.value == true)
        })

        editInventoryViewModel.rolledMaxUses.observe(viewLifecycleOwner, Observer { rolledUses ->
            setUsesVisibility(editInventoryViewModel.limitedUse.value == true, rolledUses, editInventoryViewModel.refillableUses.value == true)
        })

        editInventoryViewModel.refillableUses.observe(viewLifecycleOwner, Observer { reusable ->
            setUsesVisibility(editInventoryViewModel.limitedUse.value == true, editInventoryViewModel.rolledMaxUses.value == true, reusable)
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

        editInventoryViewModel.showToastEvent.observe(viewLifecycleOwner, Observer { event ->
            if(event && editInventoryViewModel.toastText.value != null) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    editInventoryViewModel.toastText.value!!,
                    Snackbar.LENGTH_LONG
                ).show()
                editInventoryViewModel.onShowToastEventComplete()
            }
        })

        setVisibility(editInventoryViewModel.itemType.value)

        return binding.root
    }

    /**
     * Conditional visibility logic
     * I could probably bind these directly to livedata that keeps track of the needed visibility
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
                setUsesVisibility(
                        binding.editInventoryViewModel?.limitedUse?.value == true,
                        binding.editInventoryViewModel?.rolledMaxUses?.value == true,
                        binding.editInventoryViewModel?.refillableUses?.value == true)

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
                binding.fragmentItemNewUsesTogglesGroup.visibility = View.GONE
                binding.fragmentItemNewTypeSpecificLayout.visibility = View.VISIBLE

                binding.fragmentItemNewDescriptionTutorialText.text = getString(R.string.new_item_2_roll_dectription_tip)
                binding.fragmentItemNewArmorTierLabel.visibility = View.VISIBLE
                binding.fragmentItemNewArmorTierRadioGroup.visibility = View.VISIBLE
            }
            POWER -> {
                Log.i("Power", "Pow")
                binding.fragmentItemNewTypeSpecificLayout.visibility = View.GONE
                binding.fragmentItemNewLimitedUsesToggle.visibility = View.GONE
                binding.fragmentItemNewUsesGroup.visibility = View.GONE
                binding.fragmentItemNewUsesTogglesGroup.visibility = View.GONE

                binding.fragmentItemNewDescriptionTutorialText.text = getString(R.string.new_item_2_roll_dectription_tip)
            }
            SHIELD -> {
                binding.fragmentItemNewTypeSpecificLayout.visibility = View.GONE
                binding.fragmentItemNewLimitedUsesToggle.visibility = View.GONE
                binding.fragmentItemNewUsesGroup.visibility = View.GONE
                binding.fragmentItemNewUsesTogglesGroup.visibility = View.GONE

                binding.fragmentItemNewDescriptionTutorialText.text = getString(R.string.new_item_2_roll_dectription_tip)
            }
            OTHER -> {
                binding.fragmentItemNewTypeSpecificLayout.visibility = View.GONE
                binding.fragmentItemNewLimitedUsesToggle.visibility = View.VISIBLE
                setUsesVisibility(
                        binding.editInventoryViewModel?.limitedUse?.value == true,
                        binding.editInventoryViewModel?.rolledMaxUses?.value == true,
                        binding.editInventoryViewModel?.refillableUses?.value == true)

                binding.fragmentItemNewDescriptionTutorialText.text = getString(R.string.new_item_2_roll_dectription_tip)
            }
            else -> {
                binding.fragmentItemNewLimitedUsesToggle.visibility = View.GONE
                binding.fragmentItemNewTypeSpecificLayout.visibility = View.GONE
                binding.fragmentItemNewUsesGroup.visibility = View.GONE
                binding.fragmentItemNewUsesTogglesGroup.visibility = View.GONE

                binding.fragmentItemNewDescriptionTutorialText.text = getString(R.string.new_item_2_roll_dectription_tip)
            }
        }
    }

    private fun setUsesVisibility (limited: Boolean, rolled: Boolean, refillable: Boolean) {
        // Observers for toggles seem to fire off upon view creation, calling this too soon, so we need to better enforce calling this
        if (binding.editInventoryViewModel?.itemType?.value != WEAPON && binding.editInventoryViewModel?.itemType?.value != OTHER) {
            return
        }

        if (limited) {
            binding.fragmentItemNewLimitedUsesToggle.text = getText(R.string.limited_uses_string)

            binding.fragmentItemNewLimitedUsesGenerationToggle.visibility = View.VISIBLE
            binding.fragmentItemNewRefillableToggle.visibility = View.VISIBLE

            if (rolled) {
                binding.fragmentItemNewLimitedUsesGenerationToggle.text = getText(R.string.rolled_uses_string)

                binding.fragmentItemNewStaticUsesLabel.visibility = View.GONE
                binding.fragmentItemNewStaticUsesEditText.visibility = View.GONE
                binding.fragmentItemNewUsesRoller.visible = true
            } else {
                binding.fragmentItemNewLimitedUsesGenerationToggle.text = getText(R.string.static_string)

                binding.fragmentItemNewStaticUsesLabel.visibility = View.VISIBLE
                binding.fragmentItemNewStaticUsesEditText.visibility = View.VISIBLE
                binding.fragmentItemNewUsesRoller.visible = false
            }

            if (refillable) {
                binding.fragmentItemNewRefillableToggle.text = getText(R.string.refillable_string)
            } else {
                binding.fragmentItemNewRefillableToggle.text = getText(R.string.non_refillable_string)
            }
        } else {
            binding.fragmentItemNewUsesTogglesGroup.visibility = View.INVISIBLE
            binding.fragmentItemNewUsesGroup.visibility = View.GONE
            binding.fragmentItemNewLimitedUsesToggle.text = getText(R.string.infinite_uses_string)
        }
    }
}