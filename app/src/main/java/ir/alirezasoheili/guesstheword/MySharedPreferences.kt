package ir.alirezasoheili.guesstheword

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {

    private val totalCoinsKey = "totalCoins"

    private val isFirstRunKey = "isFirstRun"

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "MyPref", Context.MODE_PRIVATE
    )

    fun saveValue(value: Int) {
        sharedPreferences.edit().putInt(totalCoinsKey, value).apply()
    }

    fun setFirstRun(state: Boolean) {
        sharedPreferences.edit().putBoolean(isFirstRunKey, state).apply()
    }

    fun getTotalCoins() = sharedPreferences.getInt(totalCoinsKey, 0)

    fun isFirstRun() = sharedPreferences.getBoolean(isFirstRunKey, true)

}