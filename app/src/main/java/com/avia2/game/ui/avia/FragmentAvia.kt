package com.avia2.game.ui.avia

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.avia2.game.R
import com.avia2.game.databinding.FragmentAviaBinding
import com.avia2.game.ui.other.ViewBindingFragment
import io.github.hyuwah.draggableviewlib.DraggableListener
import io.github.hyuwah.draggableviewlib.DraggableView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class FragmentAvia : ViewBindingFragment<FragmentAviaBinding>(FragmentAviaBinding::inflate) {
    private val args: FragmentAviaArgs by navArgs()
    private val viewModel: AviaViewModel by viewModels()
    private val sp by lazy {
        requireActivity().getSharedPreferences("SP", Context.MODE_PRIVATE)
    }
    private val callbackViewModel: PauseCallback by activityViewModels()
    private lateinit var playerPlaneView: DraggableView<ImageView>
    private val xy by lazy {
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        Pair(size.x, size.y)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPlayerView()

        if (args.isContinue && savedInstanceState == null) {
            val energy = sp.getInt("ENERGY", 60)
            val scores = sp.getInt("SCORES", 0)
            if (energy != 0) {
            viewModel.setArgs(energy, scores)
            }
        }

        binding.home.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.pause.setOnClickListener {
            viewModel.stop()
            viewModel.pauseState = true
            findNavController().navigate(R.id.action_fragmentAvia_to_dialogPause)
        }

        viewModel.endCallback = {
            end()
        }
        callbackViewModel.callback = {
            viewModel.pauseState = false
            viewModel.start(
                dpToPx(120),
                dpToPx(110),
                binding.line1.x.toInt() + dpToPx(1),
                xy.second,
                binding.player.width,
                binding.player.height,
                dpToPx(50),
                dpToPx(50)
            )
        }

        viewModel.enemies.observe(viewLifecycleOwner) {
            binding.enemyLayout.removeAllViews()
            it.forEach { enemy ->
                val enemyView = ImageView(requireContext())
                enemyView.layoutParams = ViewGroup.LayoutParams(dpToPx(120), dpToPx(110))
                enemyView.setImageResource(R.drawable.enemy)
                enemyView.x = enemy.first
                enemyView.y = enemy.second
                binding.enemyLayout.addView(enemyView)
            }
        }

        viewModel.scores.observe(viewLifecycleOwner) {
            binding.scores.text = it.toString()
        }

        viewModel.energy.observe(viewLifecycleOwner) {
            val minutes = (it % 3600) / 60
            val seconds = it % 60

            binding.energy.text = String.format("%02d:%02d", minutes, seconds)

            if (it == 0 && viewModel.gameState) {
                end()
            }
        }

        viewModel.energyList.observe(viewLifecycleOwner) {
            binding.energyLayout.removeAllViews()
            it.forEach { energy ->
                val energyView = ImageView(requireContext())
                energyView.layoutParams = ViewGroup.LayoutParams(dpToPx(50), dpToPx(50))
                energyView.setImageResource(R.drawable.energy)
                energyView.x = energy.first
                energyView.y = energy.second
                binding.energyLayout.addView(energyView)
            }
        }

        viewModel.bombList.observe(viewLifecycleOwner) {
            binding.bombsLayout.removeAllViews()
            it.forEach { bomb ->
                val bombView = ImageView(requireContext())
                bombView.layoutParams = ViewGroup.LayoutParams(dpToPx(50), dpToPx(50))
                bombView.setImageResource(R.drawable.bomb)
                bombView.x = bomb.first
                bombView.y = bomb.second
                binding.bombsLayout.addView(bombView)
            }
        }

        lifecycleScope.launch {
            delay(20)
            if (viewModel.playerXY == 0f to 0f) {
                viewModel.setPlayerXY(
                    ((xy.first / 2) - (binding.player.width / 2)).toFloat(),
                    xy.second - dpToPx(180).toFloat()
                )
                playerPlaneView.getView().x = viewModel.playerXY.first
                playerPlaneView.getView().y = viewModel.playerXY.second
            }
            if (viewModel.gameState && !viewModel.pauseState) {
                viewModel.start(
                    dpToPx(120),
                    dpToPx(110),
                    binding.line1.x.toInt() + dpToPx(1),
                    xy.second,
                    binding.player.width,
                    binding.player.height,
                    dpToPx(50),
                    dpToPx(50)
                )
            }
        }
    }

    private fun end() {
        viewModel.gameState = false
        viewModel.stop()
        lifecycleScope.launch(Dispatchers.Main) {
            findNavController().navigate(
                FragmentAviaDirections.actionFragmentAviaToDialogEnding(
                    viewModel.scores.value!!
                )
            )
        }
    }

    private fun setupPlayerView() {
        playerPlaneView = DraggableView.Builder(binding.player)
            .setListener(object : DraggableListener {
                override fun onPositionChanged(view: View) {
                    viewModel.setPlayerXY(x = view.x, y = view.y)
                }
            })
            .build()
        playerPlaneView.getView().x = viewModel.playerXY.first
        playerPlaneView.getView().y = viewModel.playerXY.second
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("1", 2)
    }

    override fun onPause() {
        super.onPause()
        viewModel.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (viewModel.gameState) {
            sp.edit().putInt("ENERGY", viewModel.energy.value!!).apply()
            sp.edit().putInt("SCORES", viewModel.scores.value!!).apply()
        } else {
            sp.edit().putInt("ENERGY", 0).apply()
            sp.edit().putInt("SCORES", 0).apply()
        }
    }
}