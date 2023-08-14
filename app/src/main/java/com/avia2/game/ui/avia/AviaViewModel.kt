package com.avia2.game.ui.avia

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avia2.game.core.library.random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AviaViewModel : ViewModel() {
    var gameState = true
    var pauseState = false
    var endCallback: (()-> Unit)? = null

    private var gameScope = CoroutineScope(Dispatchers.Default)
    private val _enemies = MutableLiveData<List<Pair<Float, Float>>>(emptyList())
    val enemies: LiveData<List<Pair<Float, Float>>> = _enemies

    private val _energyList = MutableLiveData<List<Pair<Float, Float>>>(emptyList())
    val energyList: LiveData<List<Pair<Float, Float>>> = _energyList

    private val _bombList = MutableLiveData<List<Pair<Float, Float>>>(emptyList())
    val bombList: LiveData<List<Pair<Float, Float>>> = _bombList

    private val _energy = MutableLiveData(60)
    val energy: LiveData<Int> = _energy

    private val _scores = MutableLiveData(0)
    val scores: LiveData<Int> = _scores

    var playerXY = 0f to 0f


    fun stop() {
        gameScope.cancel()
    }

    fun start(
        enemyWidth: Int,
        enemyHeight: Int,
        sectionWidth: Int,
        maxY: Int,
        playerWidth: Int,
        playerHeight: Int,
        energySize: Int,
        bombsSize: Int
    ) {
        gameScope = CoroutineScope(Dispatchers.Default)
        startTimer()
        generateEnemies(enemyWidth, enemyHeight, sectionWidth)
        letEnemiesMove(maxY, enemyHeight, enemyWidth, playerWidth, playerHeight)
        generateEnergy(energySize,sectionWidth)
        letEnergyMove(maxY, energySize, playerWidth, playerHeight)
        generateBombs(bombsSize ,sectionWidth)
        letBombsMove(maxY, bombsSize, playerWidth, playerHeight)
    }

    private fun startTimer() {
        gameScope.launch {
            while (true) {
                delay(1000)
                _energy.postValue(_energy.value!! - 1)
                _scores.postValue(_scores.value!! + 1)
            }
        }
    }

    fun setArgs(energy: Int, scores: Int) {
        _scores.postValue(scores)
        _energy.postValue(energy)
    }

    fun setPlayerXY(x: Float, y: Float) {
        playerXY = x to y
    }

    private fun generateEnemies(
        enemyWidth: Int,
        enemyHeight: Int,
        sectionWidth: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(4000)
                val newEnemies = _enemies.value!!.toMutableList()
                val randomSection = 1 random 3
                val enemyX = (sectionWidth - enemyWidth) / 2 + when (randomSection) {
                    1 -> 0
                    2 -> sectionWidth
                    else -> sectionWidth * 2
                }
                newEnemies.add(enemyX.toFloat() to 0f - enemyHeight)
                _enemies.postValue(newEnemies)
            }
        }
    }

    private fun generateEnergy(
        energySize: Int,
        sectionWidth: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(20000)
                val newEnergy = _energyList.value!!.toMutableList()
                val randomSection = 1 random 3
                val energyX = (sectionWidth - energySize) / 2 + when (randomSection) {
                    1 -> 0
                    2 -> sectionWidth
                    else -> sectionWidth * 2
                }
                newEnergy.add(energyX.toFloat() to 0f - energySize)
                _energyList.postValue(newEnergy)
            }
        }
    }

    private fun generateBombs(
        bombsSize: Int,
        sectionWidth: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(10000)
                val newBombs = _bombList.value!!.toMutableList()
                val randomSection = 1 random 3
                val bombX = (sectionWidth - bombsSize) / 2 + when (randomSection) {
                    1 -> 0
                    2 -> sectionWidth
                    else -> sectionWidth * 2
                }
                newBombs.add(bombX.toFloat() to 0f - bombsSize)
                _bombList.postValue(newBombs)
            }
        }
    }

    private fun letEnemiesMove(
        maxY: Int,
        enemyHeight: Int,
        enemyWidth: Int,
        playerWidth: Int,
        playerHeight: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(16)
                val currentList = _enemies.value!!
                val newList = mutableListOf<Pair<Float, Float>>()
                currentList.forEach { enemy ->
                    if (enemy.second <= maxY) {
                        val enemyX = enemy.first.toInt()..enemy.first.toInt() + enemyWidth
                        val enemyY = enemy.second.toInt()..enemy.second.toInt() + enemyHeight
                        val playerX = playerXY.first.toInt()..playerXY.first.toInt() + playerWidth
                        val playerY =
                            playerXY.second.toInt()..playerXY.second.toInt() + playerHeight
                        if (playerX.any { it in enemyX } && playerY.any { it in enemyY }) {
                            endCallback?.invoke()
                        } else {
                            newList.add(enemy.first to enemy.second + 5)
                        }
                    }
                }
                _enemies.postValue(newList)
            }
        }
    }

    private fun letEnergyMove(
        maxY: Int,
        energySize: Int,
        playerWidth: Int,
        playerHeight: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(16)
                val currentList = _energyList.value!!
                val newList = mutableListOf<Pair<Float, Float>>()
                currentList.forEach { energy ->
                    if (energy.second <= maxY) {
                        val energyX = energy.first.toInt()..energy.first.toInt() + energySize
                        val energyY = energy.second.toInt()..energy.second.toInt() + energySize
                        val playerX = playerXY.first.toInt()..playerXY.first.toInt() + playerWidth
                        val playerY =
                            playerXY.second.toInt()..playerXY.second.toInt() + playerHeight
                        if (playerX.any { it in energyX } && playerY.any { it in energyY }) {
                            if (_energy.value!! + 30 > 60) {
                                _energy.postValue(60)
                            } else {
                                _energy.postValue(_energy.value!! + 30)
                            }
                        } else {
                            newList.add(energy.first to energy.second + 5)
                        }
                    }
                }
                _energyList.postValue(newList)
            }
        }
    }

    private fun letBombsMove(
        maxY: Int,
        bombSize: Int,
        playerWidth: Int,
        playerHeight: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(16)
                val currentList = _bombList.value!!
                val newList = mutableListOf<Pair<Float, Float>>()
                currentList.forEach { bomb ->
                    if (bomb.second <= maxY) {
                        val bombX = bomb.first.toInt()..bomb.first.toInt() + bombSize
                        val bombY = bomb.second.toInt()..bomb.second.toInt() + bombSize
                        val playerX = playerXY.first.toInt()..playerXY.first.toInt() + playerWidth
                        val playerY =
                            playerXY.second.toInt()..playerXY.second.toInt() + playerHeight
                        if (playerX.any { it in bombX } && playerY.any { it in bombY }) {
                            endCallback?.invoke()
                        } else {
                            newList.add(bomb.first to bomb.second + 5)
                        }
                    }
                }
                _bombList.postValue(newList)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameScope.cancel()
    }
}