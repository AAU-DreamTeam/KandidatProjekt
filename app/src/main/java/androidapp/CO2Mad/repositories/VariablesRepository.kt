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

    fun loadShowIcons(): Boolean {
        return highScoreDao.loadShowIcons()
    }

    fun saveShowIcons(enableGame: Boolean) {
        highScoreDao.saveShowIcons(enableGame)
    }

    fun close(){
        highScoreDao.close()
    }
}