package com.example.morkborgcharactersheet.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.example.morkborgcharactersheet.R
import kotlinx.android.synthetic.main.dialog_dice_roll_result.view.*
import java.lang.IllegalStateException

class DiceResultDialogFragment : DialogFragment() {
    var result: Int? = null
    var listener: DialogListener? = null

    /**
     * Even though the Dice Roll dialog doesn't impact anything else once it completes, I'm still
     * leaving in the overridable listener as an example.
     *
     * I could simplify things quite a bit by just calling dismiss() in setPositiveButton's lambda
     */
    interface DialogListener {
        fun onDiceRollCompleted()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Get arguments
            val args = arguments
            result = args?.get("RESULT") as Int?

            // Build and inflate
            val inflater = it.layoutInflater
            val view = inflater.inflate(R.layout.dialog_dice_roll_result, null)
            view.result_text.text = result.toString()

            val builder = AlertDialog.Builder(it, R.style.Theme_MorkBorgCharacterSheet_DialogBox)
            builder
                    .setView(view)
                    .setPositiveButton("OK") { _, _ ->
                        listener?.onDiceRollCompleted()
                    }
                    .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}