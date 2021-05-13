package com.example.morkborgcharactersheet.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.morkborgcharactersheet.R
import com.example.morkborgcharactersheet.charactersheet.CharacterSheetViewModel
import com.example.morkborgcharactersheet.databinding.DialogDefenceBinding
import kotlinx.android.synthetic.main.dialog_defence.view.*
import kotlinx.android.synthetic.main.dialog_power_result.view.*
import java.lang.IllegalStateException

class DefenceDialogFragment : DialogFragment() {
    val viewModel: CharacterSheetViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private var _binding: DialogDefenceBinding? = null
    private val binding get() = _binding!!

    // TODO: Handle shields & omens
    // TODO: Hide shield/armor areas when none equipped
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogDefenceBinding.inflate(LayoutInflater.from(context))
        _binding!!.viewModel = viewModel

        viewModel.showDefenceEvent.observe(requireParentFragment(), Observer {
            if (it == false) {
                dismiss()
            }
        })

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }
}