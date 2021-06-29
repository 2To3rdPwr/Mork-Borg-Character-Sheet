package com.twotothirdpower.morkborgcharactersheet.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.twotothirdpower.morkborgcharactersheet.R
import kotlinx.android.synthetic.main.dialog_character_description.view.*

class CharacterDescriptionDialogFragment : DialogFragment() {
    companion object {
        const val TAG = "DescriptionDialog"

        private const val KEY_NAME = "KEY_NAME"
        private const val KEY_DESCRIPTION = "KEY_DESCRIPTION"

        fun newInstance(name: String, description: String): CharacterDescriptionDialogFragment {
            val args = Bundle()
            args.putString(KEY_NAME, name)
            args.putString(KEY_DESCRIPTION, description)

            val fragment = CharacterDescriptionDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_character_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.description_dialog_name.text = arguments?.getString(KEY_NAME)
        view.description_dialog_text.text = arguments?.getString(KEY_DESCRIPTION)
    }
}