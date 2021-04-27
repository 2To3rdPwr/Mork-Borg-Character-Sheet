package com.example.morkborgcharactersheet.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.morkborgcharactersheet.R
import kotlinx.android.synthetic.main.dialog_defence.view.*
import kotlinx.android.synthetic.main.dialog_power_result.view.*
import java.lang.IllegalStateException

class DefenceDialogFragment : DialogFragment() {
    private var agilityTest = 0
    private var damageReduction = 0
    private var drApplied = false

    var listener: DialogListener? = null

    interface DialogListener {
        fun onDamageTaken(damage: Int)
        fun onDamageAvoided(shielded: Boolean)
    }

    // TODO: Handle shields & omens
    // TODO: Link the edittext directly to a variable instead of whatever this is
    // TODO: Hide shield/armor areas when none equipped
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Get arguments
            val args = arguments
            agilityTest = args?.get("AGLTEST") as Int
            damageReduction = args?.get("DR") as Int

            // Build and inflate
            val inflater = it.layoutInflater
            val view = inflater.inflate(R.layout.dialog_defence, null)

            view.defence_dialog_tohit_rolled_text.text = agilityTest.toString()
            view.defence_dialog_blocked_rolled_text.text = damageReduction.toString()

            view.defence_dialog_armor_icon.setOnClickListener {
                toggleDR(view, it)
            }

            val builder = AlertDialog.Builder(it, R.style.Theme_MorkBorgCharacterSheet_DialogBox)
            builder
                    .setView(view)
                    .setNegativeButton("Damaged") { _, _ ->
                        if(view!!.defence_dialog_damage_input_edittext.text.isEmpty() ) {
                            Log.i("Dam", "no")
                            Toast.makeText(this.context, "Requires input", Toast.LENGTH_SHORT)
                        } else {
                            listener?.onDamageTaken(Integer.parseInt(view.defence_dialog_damage_input_edittext.text.toString()))
                            dismiss()
                        }
                    }
                    .setPositiveButton("Dodged") { _, _ ->
                        // TODO: Handle shield
                        listener?.onDamageAvoided(false)
                        dismiss()
                    }
                    .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun toggleDR(view: View, icon: View) {
        drApplied = !drApplied
        // TODO: don't reduce more damage than amount taken

        // TODO: Figure out a better way of showing the icon was clicked. Perhaps get two different shield icons
        val editText = view!!.defence_dialog_damage_input_edittext
        if(drApplied) {
            editText.setText((Integer.parseInt(editText.text.toString()) + damageReduction).toString(), TextView.BufferType.EDITABLE)
            icon.setBackgroundColor(getResources().getColor(R.color.mb_pink))
        } else {
            editText.setText((Integer.parseInt(editText.text.toString()) - damageReduction).toString(), TextView.BufferType.EDITABLE)
            icon.setBackgroundColor(getResources().getColor(R.color.black))
        }
    }
}