package com.avia2.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.avia2.game.R
import com.avia2.game.core.library.ViewBindingDialog
import com.avia2.game.databinding.DialogEndingBinding
import com.avia2.game.ui.start.FragmentStartDirections

class DialogEnding: ViewBindingDialog<DialogEndingBinding>(DialogEndingBinding::inflate) {
    private val args: DialogEndingArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack(R.id.fragmentMain, false, false)
                true
            } else {
                false
            }
        }
        binding.scores.text = args.scores.toString()

        binding.home.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }

        binding.restart.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
            findNavController().navigate(FragmentStartDirections.actionFragmentMainToFragmentAvia(false))
        }

        val background = when (args.scores) {
            in 0..250 -> R.drawable.complete01
            in 251..500 -> R.drawable.complete02
            in 501..750 -> R.drawable.complete03
            else -> R.drawable.complete04
        }

        binding.container.setBackgroundResource(background)
    }
}