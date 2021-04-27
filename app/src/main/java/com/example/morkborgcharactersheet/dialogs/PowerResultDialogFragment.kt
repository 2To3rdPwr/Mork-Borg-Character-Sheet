package com.example.morkborgcharactersheet.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.morkborgcharactersheet.R
import kotlinx.android.synthetic.main.dialog_dice_roll_result.view.*
import kotlinx.android.synthetic.main.dialog_power_result.view.*
import java.lang.IllegalStateException

class PowerResultDialogFragment : DialogFragment() {
    var presenceTest: Int = 0
    var powerName: String = ""
    // TODO: Display dice rolls in powerDescription
    var powerDescription: String = ""

    var listener: DialogListener? = null

    interface DialogListener {
        fun onPowerSuccess(omenUsed: Boolean)
        fun onPowerFailed()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Get arguments
            val args = arguments
            presenceTest = args?.get("PRESTEST") as Int
            powerName = args?.get("NAME") as String
            powerDescription = args?.get("DESC") as String

            // Build and inflate
            val inflater = it.layoutInflater
            val view = inflater.inflate(R.layout.dialog_power_result, null)

            view.power_pres_test_text.text = presenceTest.toString()
            view.dialog_power_name_text.text = powerName
            view.dialog_power_description_text.text = powerDescription

            val builder = AlertDialog.Builder(it, R.style.Theme_MorkBorgCharacterSheet_DialogBox)
            builder
                    .setView(view)
                    .setNegativeButton("Failure") { _, _ ->
                        listener?.onPowerFailed()
                        dismiss()
                    }
                    .setPositiveButton("Success") { _, _ ->
                        // TODO: Handle omen use
                        listener?.onPowerSuccess(false)
                        dismiss()
                    }
                    .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}