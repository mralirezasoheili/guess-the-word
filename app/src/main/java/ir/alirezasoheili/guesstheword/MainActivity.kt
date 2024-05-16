package ir.alirezasoheili.guesstheword

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.alirezasoheili.guesstheword.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: AppViewModel

    private lateinit var currentName: String

    private var startTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this, ViewModelFactory(this))[AppViewModel::class.java]
        currentName = viewModel.getName()
        initViews()
        startTime = getCurrentTime()

        binding.btnCheck.setOnClickListener {
            checkAnswer(viewModel.filter(binding.etUserInput.text.toString()))
        }

        binding.btnShow.setOnClickListener {
            showAlertDialog(
                "This Action Decrease From Your Wallet a Coin", R.drawable.ic_receive_cash
            ) {
                showRightAnswer()
            }
        }

        binding.btnNext.setOnClickListener {
            showAlertDialog(
                "This Action Decrease From Your Wallet 2 Coins", R.drawable.ic_receive_cash
            ) {
                skipThisWord()
            }
        }

        if (viewModel.isFirstRun()) {
            showGreetingDialog()
        }
    }

    // (write) save the value of total coins here in shared preferences
    override fun onStop() {
        super.onStop()
        viewModel.saveTotalCoins()
    }

    private fun hideRightAnswerContainer() {
        binding.rightAnswerContainer.visibility = View.GONE
    }

    private fun setShowAndHideBtnIcon() {
        if (isRightAnswerContainerVisible()) {
            hideRightAnswerContainer()
            setShowButtonIcon(R.drawable.ic_show)
        } else {
            showRightAnswerContainer()
            setShowButtonIcon(R.drawable.ic_hide)
        }
    }

    private fun setShowButtonIcon(icon: Int) {
        binding.btnShow.setImageResource(icon)
    }

    private fun showRightAnswerContainer() {
        binding.rightAnswerContainer.visibility = View.VISIBLE
    }

    private fun isRightAnswerContainerVisible() =
        binding.rightAnswerContainer.visibility == View.VISIBLE

    private fun isRightAnswerContainerNotVisible() =
        binding.rightAnswerContainer.visibility != View.VISIBLE

    private fun initViews() {
        setTotalCoinsValue()
        binding.txtRightAnswer.text = currentName
        binding.txtRandomText.text = viewModel.getShuffledName(currentName)
    }

    private fun setTotalCoinsValue() {
        binding.txtTotalCoins.text = viewModel.getTotalCoins().toString()
    }

    private fun checkAnswer(answer: String) {
        if (viewModel.isCorrectAnswer(answer)) {
            currentName = viewModel.getNewName()
            val coins = viewModel.increaseTotalCoins(getTime())
            startTime = getCurrentTime()
            initViews()
            emptyEditText()
            hideRightAnswerContainer()
            showDialog("Bravo!\n\nYou earned: $coins Coin(s)", R.drawable.ic_coins)
        } else {
            showDialog("Try again...", R.drawable.ic_folded_hands)
        }
    }

    private fun emptyEditText() {
        binding.etUserInput.text = Editable.Factory.getInstance().newEditable("")
    }

    private fun showDialog(message: String, icon: Int) {
        val dialog = getDialog(message, icon)
        val btnHide = dialog.findViewById<Button>(R.id.btnConfirmDialog)
        btnHide.visibility = View.GONE

        val btnExit = dialog.findViewById<Button>(R.id.btnExit)
        btnExit.width = LinearLayout.LayoutParams.MATCH_PARENT
        btnExit.text = getString(R.string.ok)

        btnExit.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun getDialog(message: String, icon: Int): Dialog {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.correct_dialog)
        dialog.findViewById<TextView>(R.id.tvTitle).text = message
        dialog.findViewById<ImageView>(R.id.imgDialog).setImageResource(icon)
        dialog.show()
        return dialog
    }

    private fun skipThisWord() {
        try {
            viewModel.decreaseTwoCoinsFromTotalCoins()
            setTotalCoinsValue()
            currentName = viewModel.getNewName()
            startTime = getCurrentTime()
            initViews()
            hideRightAnswerContainer()
        } catch (e: IllegalArgumentException) {
            showDialog(e.message!!, R.drawable.ic_private)
        }
    }

    private fun showAlertDialog(message: String, icon: Int, function: () -> Unit) {
        val dialog = getDialog(message, icon)
        val btnHide = dialog.findViewById<Button>(R.id.btnConfirmDialog)
        val btnExit = dialog.findViewById<Button>(R.id.btnExit)
        // set text of two buttons in dialog 1.yes - 2. maybe later
        btnHide.text = getString(R.string.yes)
        btnExit.text = getString(R.string.maybe_later)
        btnHide.setOnClickListener {
            function()
            dialog.hide()
        }
        btnExit.setOnClickListener {
            dialog.hide()
        }
    }

    private fun showGreetingDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.greeting_dialog)
        dialog.show()
        dialog.findViewById<ImageButton>(R.id.giftBtn).setOnClickListener {
            viewModel.greetingGift()
            setTotalCoinsValue()
            dialog.hide()
        }
    }

    private fun showRightAnswer() {
        try {
            if (isRightAnswerContainerNotVisible()) {
                viewModel.decreaseTotalCoins()
                setTotalCoinsValue()
                setShowAndHideBtnIcon()
            }
        } catch (e: IllegalArgumentException) {
            showDialog(e.message!!, R.drawable.ic_private)
        }
    }

    private fun getCurrentTime() = System.currentTimeMillis()

    private fun getTime(): Int {
        val time = getCurrentTime() - startTime
        return ((time / 1000) % 60).toInt()
    }
}