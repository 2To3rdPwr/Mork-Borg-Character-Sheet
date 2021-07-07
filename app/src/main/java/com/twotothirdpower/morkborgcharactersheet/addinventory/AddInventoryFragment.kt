package com.twotothirdpower.morkborgcharactersheet.addinventory

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
import com.twotothirdpower.morkborgcharactersheet.databinding.FragmentInventoryAddBinding
import com.twotothirdpower.morkborgcharactersheet.util.EquipmentListener

class AddInventoryFragment : Fragment() {
    private lateinit var viewModel: AddInventoryViewModel
    private lateinit var binding: FragmentInventoryAddBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inventory_add, container, false)

        val application = requireNotNull(this.activity).application
        val arguments by navArgs<AddInventoryFragmentArgs>()

        val dataSource = CharacterDatabase.getInstance(application).characterDatabaseDAO
        val viewModelFactory = AddInventoryViewModelFactory(arguments.characterId, dataSource)

        viewModel = ViewModelProvider(this, viewModelFactory).get(
            AddInventoryViewModel::class.java)

        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.viewModel = viewModel

        /**
         * Recyclerview bindings
         */
        val equipmentAdapter = DefaultEquipmentAdapter(EquipmentListener { equipment, type  ->
            viewModel.onEquipmentClick(equipment, type)
        })
        binding.defaultInventoryList.adapter = equipmentAdapter

        viewModel.filteredEquipmentList.observe(viewLifecycleOwner, Observer {
            it?.let {
                equipmentAdapter.submitList(it)
                equipmentAdapter.notifyDataSetChanged()
            }
        })

        /**
         * Observers for livedata events
         */
        viewModel.customInventoryEvent.observe(viewLifecycleOwner, Observer { item ->
            if (item) {
                findNavController().navigate(AddInventoryFragmentDirections.actionAddInventoryFragmentToEditInventoryFragment(viewModel.characterId, -1L))
                viewModel.onCustomInventoryEventDone()
            }
        })

        return binding.root
    }
}