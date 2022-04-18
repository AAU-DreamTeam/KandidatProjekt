package androidapp.CO2Mad.models.daos

import android.content.ContentValues
import android.content.Context
import androidapp.CO2Mad.models.tools.DBManager

class VariablesDao(private val dbManager: DBManager){
    constructor(context: Context): this(DBManager(context))

    fun loadHighScore(): Int {
        val query = "SELECT $COLUMN_SCORE FROM $TABLE;"

        return dbManager.select<Int>(query){
            it.getInt(0)
        }!!
    }

    fun saveHighScore(score: Int) {
        val contentValues = ContentValues()

        contentValues.put(COLUMN_SCORE, score)

        dbManager.update(TABLE, contentValues, COLUMN_ID, "1")
    }

    fun close() {
        dbManager.close()
    }

    fun loadEnableGame(): Boolean {
        val query = "SELECT $COLUMN_ENABLE_GAME FROM $TABLE;"

        return dbManager.select<Boolean>(query){
            it.getInt(0) != 0
        }!!
    }

    fun saveEnableGame(enableGame: Boolean) {
        val contentValues = ContentValues()

        contentValues.put(COLUMN_ENABLE_GAME, dbManager.booleanToInt(enableGame))

        dbManager.update(TABLE, contentValues, COLUMN_ID, "1")
    }

    companion object {
        val TABLE = "variables"
        val COLUMN_ID = "id"
        val COLUMN_SCORE = "score"
        val COLUMN_ENABLE_GAME = "enableGame"
    }
}