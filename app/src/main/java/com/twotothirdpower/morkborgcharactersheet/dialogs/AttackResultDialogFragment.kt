package com.twotothirdpower.morkborgcharactersheet.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.twotothirdpower.morkborgcharactersheet.R
import com.twotothirdpower.morkborgcharactersheet.charactersheet.CharacterSheetViewModel
import com.twotothirdpower.morkborgcharactersheet.databinding.DialogAttackResultBinding


class AttackResultDialogFragment : DialogFragment() {
    // TODO: Display attack description if appropriate
    // Use parent's viewModel
    val viewModel: CharacterSheetViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private var _binding: DialogAttackResultBinding? = null
    // This property is only valid in between onCreateDialog and onDestroyView
    // Otherwise we get memory leaks
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAttackResultBinding.inflate(LayoutInflater.from(context))
        _binding!!.viewModel = viewModel

        if (viewModel.crit.value == true) {
            binding.attackDialgCritText.text = getText(R.string.attack_crit_string)
            binding.attackDialgCritText.visibility = View.VISIBLE
        } else if (viewModel.fumble.value == true) {
            binding.attackDialgCritText.text = getText(R.string.attack_fumble_string)
            binding.attackDialgCritText.visibility = View.VISIBLE
        }

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}