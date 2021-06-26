package com.twotothirdpower.morkborgcharactersheet.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.twotothirdpower.morkborgcharactersheet.R
import com.twotothirdpower.morkborgcharactersheet.database.CharacterDatabase

class StartFragment: Fragment() {
    private lateinit var viewModel: StartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        val dataSource = CharacterDatabase.getInstance(application).characterDatabaseDAO

        val viewModelFactory = StartViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(StartViewModel::class.java)

        viewModel.characterId.observe(viewLifecycleOwner, Observer {
            if(it != -1L) {
                if(it == null) {
                    findNavController().navigate(StartFragmentDirections.actionStartFragmentToIntroFragment())
                    viewModel.onCharacterIdComplete()
                } else {
                    findNavController().navigate(StartFragmentDirections.actionStartFragmentToCharacterSheetViewPagerFragment(it))
                    viewModel.onCharacterIdComplete()
                }
            }
        })

        return inflater.inflate(R.layout.fragment_start, container, false)
    }
}