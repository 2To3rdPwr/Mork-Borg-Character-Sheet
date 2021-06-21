package com.example.morkborgcharactersheet.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.morkborgcharactersheet.R
import com.example.morkborgcharactersheet.charactersheetviewpager.CharacterSheetViewPagerFragmentDirections
import com.example.morkborgcharactersheet.database.CharacterDatabase
import com.example.morkborgcharactersheet.databinding.FragmentInventoryBinding

class InventoryFragment(var characterId: Long) : Fragment() {
    private lateinit var inventoryViewModel: InventoryViewModel
    private lateinit var binding: FragmentInventoryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inventory, container, false)
        val application = requireNotNull(this.activity).application

        // Create an instance of the ViewModel Factory.
        val dataSource = CharacterDatabase.getInstance(application).characterDatabaseDAO
        val viewModelFactory = InventoryViewModelFactory(characterId, dataSource)

        inventoryViewModel = ViewModelProvider(this, viewModelFactory).get(InventoryViewModel::class.java)

        binding.setLifecycleOwner(this)
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
            if(item !== null) {
                findNavController().navigate(CharacterSheetViewPagerFragmentDirections.actionCharacterSheetViewPagerFragmentToEditInventoryFragment(inventoryViewModel.characterId, item))
                inventoryViewModel.onEditItemDone()
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