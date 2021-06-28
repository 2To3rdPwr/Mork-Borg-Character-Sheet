package com.twotothirdpower.morkborgcharactersheet.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.twotothirdpower.morkborgcharactersheet.databinding.DialogEquipmentUseBinding
import com.twotothirdpower.morkborgcharactersheet.inventory.InventoryViewModel

class UseEquipmentDialogFragment : DialogFragment() {
    val viewModel: InventoryViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private var _binding: DialogEquipmentUseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEquipmentUseBinding.inflate(LayoutInflater.from(context))
        _binding!!.viewModel = viewModel

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.viewModel?.onUseItemDone()
        _binding = null
    }
}