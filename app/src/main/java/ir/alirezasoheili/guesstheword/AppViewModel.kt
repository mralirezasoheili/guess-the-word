package ir.alirezasoheili.guesstheword

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import kotlin.random.Random

class AppViewModel(context: Context) : ViewModel() {

    private lateinit var names: List<String>

    private var index: Int = 0

    private var totalCoins: Int = 0

    private var isFirstRun = true

    private val sharedPreferences = MySharedPreferences(context)

    init {
        // (read) the total Coins from shared preferences then return the value
        totalCoins = sharedPreferences.getTotalCoins()
        isFirstRun = sharedPreferences.isFirstRun()
        setEnglishNames(context)
        getNewRandomIndex()
    }

    private fun setEnglishNames(context: Context) {
        names = getAllEnglishNames(context)
    }

    private fun getAllEnglishNames(context: Context): List<String> {
        val jsonContent = readJsonFile(context, R.raw.names)
        return Gson().fromJson(jsonContent, Array<String>::class.java).asList()
    }

    private fun readJsonFile(context: Context, fileName: Int): String {
        val inputStream = context.resources.openRawResource(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charsets.UTF_8)
    }

    private fun getNewRandomIndex() {
        index = Random.nextInt(names.size)
    }

    fun getNewName(): String {
        getNewRandomIndex()
        return names[index]
    }

    fun getName() = names[index]

    fun filter(answer: String): String {
        return answer.ifEmpty { "---" }
    }

    fun isCorrectAnswer(answer: String): Boolean {
        return getName().lowercase() == answer.lowercase()
    }

    fun increaseTotalCoins(seconds: Int): Int {
        return if (seconds <= 3) {
            totalCoins += 3
            3
        } else if (seconds <= 8) {
            totalCoins += 2
            2
        } else {
            ++totalCoins
            1
        }
    }

    fun greetingGift() {
        totalCoins += 20
        sharedPreferences.setFirstRun(false)
    }

    fun decreaseTotalCoins() {
        if (totalCoins > 0) {
            --totalCoins
        } else {
            throw IllegalArgumentException("Can't show the answer because you don't have enough coins!")
        }
    }

    fun decreaseTwoCoinsFromTotalCoins() {
        if (totalCoins >= 2) {
            totalCoins -= 2
        } else {
            throw IllegalArgumentException("Can't skip this level because you don't have enough coins!")
        }
    }

    fun getTotalCoins() = totalCoins

    fun isFirstRun() = isFirstRun

    // (write) the total Coins into shared preferences
    fun saveTotalCoins() {
        sharedPreferences.saveValue(totalCoins)
    }

    fun getShuffledName(name: String) = name.toList().shuffled().joinToString("")

}