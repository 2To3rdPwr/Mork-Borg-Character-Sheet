package com.twotothirdpower.morkborgcharactersheet.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.twotothirdpower.morkborgcharactersheet.databinding.DialogCharacterDescriptionBinding

class CharacterDescriptionDialogFragment : DialogFragment() {
    private var _binding: DialogCharacterDescriptionBinding? = null
    private val binding get() = _binding!!
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
    ): View {
        _binding = DialogCharacterDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.descriptionDialogName.text = arguments?.getString(KEY_NAME)
        binding.descriptionDialogText.text = arguments?.getString(KEY_DESCRIPTION)
    }
}