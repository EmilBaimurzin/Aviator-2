package com.avia2.game.ui.avia

import androidx.lifecycle.ViewModel

class PauseCallback : ViewModel() {
    var callback: (() -> Unit)? = null
}