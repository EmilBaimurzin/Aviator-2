package com.avia2.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.avia2.game.R
import com.avia2.game.core.library.ViewBindingDialog
import com.avia2.game.databinding.DialogPauseBinding
import com.avia2.game.ui.avia.PauseCallback

class DialogPause: ViewBindingDialog<DialogPauseBinding>(DialogPauseBinding::inflate) {
    private val callbackViewModel: PauseCallback by activityViewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                callbackViewModel.callback?.invoke()
                findNavController().popBackStack()
                true
            } else {
                false
            }
        }
        binding.play.setOnClickListener {
            callbackViewModel.callback?.invoke()
            findNavController().popBackStack()
        }
    }
}