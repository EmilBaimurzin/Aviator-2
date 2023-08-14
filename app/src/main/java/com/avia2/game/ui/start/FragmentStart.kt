package com.avia2.game.ui.start

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.avia2.game.databinding.FragmentStartBinding
import com.avia2.game.ui.other.ViewBindingFragment

class FragmentStart : ViewBindingFragment<FragmentStartBinding>(FragmentStartBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.newGameButton.setOnClickListener {
            findNavController().navigate(FragmentStartDirections.actionFragmentMainToFragmentAvia(false))
        }

        binding.continueButton.setOnClickListener {
            findNavController().navigate(FragmentStartDirections.actionFragmentMainToFragmentAvia(true))
        }

        binding.exitButton.setOnClickListener {
            requireActivity().finish()
        }

        binding.privacyText.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://www.google.com")
                )
            )
        }
    }
}