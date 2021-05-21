package com.example.morkborgcharactersheet.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.morkborgcharactersheet.charactersheet.CharacterSheetViewModel
import com.example.morkborgcharactersheet.databinding.DialogDefenceBinding

class DefenceDialogFragment : DialogFragment() {
    val viewModel: CharacterSheetViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private var _binding: DialogDefenceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogDefenceBinding.inflate(LayoutInflater.from(context))
        _binding!!.viewModel = viewModel

        viewModel.showDefenceEvent.observe(this, Observer {
            if (it == false) {
                dismiss()
            }
        })

        // DialogFragment doesn't seem to get LiveData updates naturally, so we have to observe them manually
        viewModel.defenceDialogStep.observe(this, Observer { step ->
            if (step == 2) {
                binding.defenceDialogTohitGroup.visibility = View.GONE
                binding.defenceDialogDamageRollGroup.visibility = View.VISIBLE
            } else if (step == 3) {
                binding.defenceDialogDamageRollGroup.visibility = View.GONE
                binding.defenceDialogDamageGroup.visibility = View.VISIBLE
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