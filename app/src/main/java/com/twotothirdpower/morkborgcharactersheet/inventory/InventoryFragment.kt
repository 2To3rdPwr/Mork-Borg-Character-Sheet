package com.twotothirdpower.morkborgcharactersheet.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.twotothirdpower.morkborgcharactersheet.R
import com.twotothirdpower.morkborgcharactersheet.charactersheetviewpager.CharacterSheetViewPagerFragmentDirections
import com.twotothirdpower.morkborgcharactersheet.database.CharacterDatabase
import com.twotothirdpower.morkborgcharactersheet.databinding.FragmentInventoryBinding
import com.twotothirdpower.morkborgcharactersheet.dialogs.UseEquipmentDialogFragment

class InventoryFragment : Fragment() {
    // Companion object allows us to pass args from ViewPager
    companion object {
        private const val CHARACTER_ID = "characterId"

        fun newInstance(characterId: Long) = InventoryFragment().apply {
            arguments = Bundle(1).apply {
                putLong(CHARACTER_ID, characterId)
            }
        }
    }
    private lateinit var inventoryViewModel: InventoryViewModel
    private lateinit var binding: FragmentInventoryBinding
    private var characterId: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inventory, container, false)
        val application = requireNotNull(this.activity).application

        arguments?.let {
            characterId = it.getLong(CHARACTER_ID)
        }

        // Create an instance of the ViewModel Factory.
        val dataSource = CharacterDatabase.getInstance(application).characterDatabaseDAO
        val viewModelFactory = InventoryViewModelFactory(characterId!!, dataSource)

        val mInventoryViewModel: InventoryViewModel by viewModels(ownerProducer = { this }) { viewModelFactory }
        inventoryViewModel = mInventoryViewModel

        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.inventoryViewModel = inventoryViewModel

        /**
         * Recyclerview bindings
         */
        val equipmentAdapter = EquipmentAdapter(EquipmentListener { equipment, type  ->
            inventoryViewModel.onEquipmentClick(equipment, type)
        })
        binding.inventoryList.adapter = equipmentAdapter

        inventoryViewModel.expandableEquipmentList.observe(viewLifecycleOwner, Observer {
            it?.let {
                equipmentAdapter.submitList(it)
                // Sad I have to hit it with brute force like this ;_;
                // Might be able to remedy with PropertyAwareMutableLiveData
                equipmentAdapter.notifyDataSetChanged()
            }
        })

        /**
         * Observers for events
         */
        inventoryViewModel.editingItem.observe(viewLifecycleOwner, Observer { item ->
            if (item !== null) {
                findNavController().navigate(CharacterSheetViewPagerFragmentDirections.actionCharacterSheetViewPagerFragmentToEditInventoryFragment(inventoryViewModel.characterId, item))
                inventoryViewModel.onEditItemDone()
            }
        })

        inventoryViewModel.usedEquipmentDescription.observe(viewLifecycleOwner, Observer { equipment ->
            if (equipment !== null) {
                UseEquipmentDialogFragment().show(childFragmentManager, "Use")
            }
        })

        return binding.root
    }

    override fun onResume() {
        inventoryViewModel.loadInventory()
        super.onResume()
    }

    override fun onPause() {
        inventoryViewModel.onStop()
        super.onPause()
    }
}