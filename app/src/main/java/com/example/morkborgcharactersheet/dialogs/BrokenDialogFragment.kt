package com.example.morkborgcharactersheet.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.morkborgcharactersheet.R
import com.example.morkborgcharactersheet.charactersheet.CharacterSheetViewModel
import com.example.morkborgcharactersheet.databinding.DialogBrokenBinding
import com.example.morkborgcharactersheet.charactersheet.CharacterSheetViewModel.BrokenEventType

class BrokenDialogFragment : DialogFragment() {
    // Use parent's viewModel
    val viewModel: CharacterSheetViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private var _binding: DialogBrokenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogBrokenBinding.inflate(LayoutInflater.from(context))
        _binding!!.viewModel = viewModel

        binding.brokenDescriptionText.text = when (viewModel.brokenType.value) {
            BrokenEventType.DEAD -> getString(R.string.broken_dead_string)
            BrokenEventType.BLEEDING -> getString(R.string.broken_bleed_string_1) + viewModel.brokenRoll.value + getString(R.string.broken_bleed_string_2)
            BrokenEventType.UNCONSCIOUS -> getString(R.string.broken_unconscious_string_1) + viewModel.brokenRoll.value + getString(R.string.broken_unconscious_string_2)
            BrokenEventType.LOSE_EYE -> getString(R.string.broken_eye_string_1) + viewModel.brokenRoll.value + getString(R.string.broken_eye_limb_string_2)
            BrokenEventType.LOSE_LIMB -> getString(R.string.broken_limb_string_1) + viewModel.brokenRoll.value + getString(R.string.broken_eye_limb_string_2)
            else -> "None"
        }

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onBrokenDialogDone()
        _binding = null
    }
}