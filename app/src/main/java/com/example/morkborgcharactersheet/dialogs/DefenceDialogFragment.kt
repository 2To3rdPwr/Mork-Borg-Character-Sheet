package com.example.morkborgcharactersheet.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.morkborgcharactersheet.charactersheet.CharacterSheetViewModel
import com.example.morkborgcharactersheet.databinding.DialogDefenceBinding
import com.example.morkborgcharactersheet.util.DataBindingConverter

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

                if (viewModel.armor.value != null || viewModel.shield.value != null) {
                    binding.defenceDialogArmorGroup.visibility = View.VISIBLE
                } else {
                    binding.defenceDialogArmorGroup.visibility = View.GONE
                }

                if (viewModel.shield.value != null) {
                    binding.defenceUseShieldButton.visibility = View.VISIBLE
                } else {
                    binding.defenceUseShieldButton.visibility = View.GONE
                }

                binding.defenceDialogBlockedRolledText.text = DataBindingConverter.convertIntToString(viewModel.armorRoll.value ?: 0)
            }
        })

        viewModel.minDefenceDamage.observe(this, Observer { damage ->
            binding.defenceDialogDamageRolledText.text = damage.toString()
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