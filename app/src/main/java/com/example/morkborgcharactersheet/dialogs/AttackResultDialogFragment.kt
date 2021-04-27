package com.example.morkborgcharactersheet.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.morkborgcharactersheet.R
import kotlinx.android.synthetic.main.dialog_attack_result.view.*
import java.lang.IllegalStateException


class AttackResultDialogFragment : DialogFragment() {
    var toHit: Int? = null
    var damage: Int? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Get arguments
            val args = arguments
            toHit = args?.get("TOHIT") as Int?
            damage = args?.get("DAMAGE") as Int?

            // Build and inflate
            val inflater = it.layoutInflater
            val view = inflater.inflate(R.layout.dialog_attack_result, null)
            view.attack_tohit_text.text = toHit.toString()
            view.attack_damage_text.text = damage.toString()

            val builder = AlertDialog.Builder(it, R.style.Theme_MorkBorgCharacterSheet_DialogBox)
            builder
                    .setView(view)
                    .setPositiveButton("OK") { _, _ ->
                        dismiss()
                    }
                    .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}