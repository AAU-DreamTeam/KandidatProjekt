package androidapp.CO2Mad.repositories

import android.content.Context
import androidapp.CO2Mad.models.daos.VariablesDao

class VariablesRepository(context: Context) {
    private val highScoreDao = VariablesDao(context)

    fun loadHighScore(): Int {
        return highScoreDao.loadHighScore()
    }

    fun saveHighScore(score: Int) {
        highScoreDao.saveHighScore(score)
    }

    fun loadEnableGame(): Boolean {
        return highScoreDao.loadEnableGame()
    }

    fun saveEnableGame(enableGame: Boolean) {
        highScoreDao.saveEnableGame(enableGame)
    }

    fun close(){
        highScoreDao.close()
    }
}