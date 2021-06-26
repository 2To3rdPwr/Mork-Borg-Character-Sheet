package com.twotothirdpower.morkborgcharactersheet.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.twotothirdpower.morkborgcharactersheet.charactersheet.CharacterSheetViewModel
import com.twotothirdpower.morkborgcharactersheet.databinding.DialogPowerResultBinding

class PowerResultDialogFragment : DialogFragment() {
    val viewModel: CharacterSheetViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private var _binding: DialogPowerResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogPowerResultBinding.inflate(LayoutInflater.from(context))
        _binding!!.viewModel = viewModel

        viewModel.showPowerEvent.observe(this, Observer {
            if (it == false) {
                dismiss()
            }
        })

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}