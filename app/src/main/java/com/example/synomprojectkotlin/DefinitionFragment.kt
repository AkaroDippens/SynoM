package com.example.synomprojectkotlin

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.example.synomprojectkotlin.models.Word
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.util.concurrent.ThreadLocalRandom

class DefinitionFragment : Fragment() {
    private lateinit var wordTextView: TextView
    private lateinit var scoreTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var btnStart: Button
    private lateinit var option1: Button
    private lateinit var option2: Button
    private lateinit var option3: Button

    private lateinit var timeSlider: ProgressBar

    private lateinit var dataBaseHelper: DataBaseHelper

    private lateinit var buttonList: List<Button>
    private val usedWords = HashSet<String>()
    private val wordIndices = HashMap<String, Int>()

    var arr = ArrayList<Word>()
    var position = 1
    var score = 0
    private var timeLeftInMillis: Long = 30000
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_definition, container, false)
    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        wordTextView = view.findViewById(R.id.randomText)
        scoreTextView = view.findViewById(R.id.scoreText)
        timerTextView = view.findViewById(R.id.timerText)
        option1 = view.findViewById(R.id.option1)
        option2 = view.findViewById(R.id.option2)
        option3 = view.findViewById(R.id.option3)
        btnStart = view.findViewById(R.id.btnStart)
        timeSlider = view.findViewById(R.id.timeSlider)

        buttonList = listOf(option1, option2, option3)

        parseJson()

        enableButtons(false)

        btnStart.setOnClickListener {
            startGame()
        }

        dataBaseHelper = DataBaseHelper(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }

    private fun startGame() {
        enableButtons(true)
        resetGame()
        startTimer()
        setQ()
    }

    private fun resetGame() {
        score = 0
        scoreTextView.text = score.toString()
        timeLeftInMillis = 30000 // 30 seconds
        timeSlider.progress = timeLeftInMillis.toInt() / 1000
        usedWords.clear()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                timeSlider.progress = timeLeftInMillis.toInt() / 1000
                timerTextView.text = "${timeSlider.progress} s"
            }

            override fun onFinish() {
                endGame()
            }
        }.start()
    }

    private fun updateTimer(extraTimeInMillis: Long) {
        timeLeftInMillis += extraTimeInMillis
        if (timeLeftInMillis <= 0) {
            endGame()
        } else {
            countDownTimer?.cancel()
            startTimer()
        }
    }

    private fun endGame() {
        enableButtons(false)
        wordTextView.text = "Время вышло! Итоговый результат: $score"

        val sharedPref = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val email = sharedPref.getString("EMAIL", null)
        val password = sharedPref.getString("PASSWORD", null)

        email?.let {
            val user = dataBaseHelper.readUserForScore(email, password ?: "")

            if (user != null) {
                val newMaxScore = maxOf(score, user.maxDefinitionScore)
                dataBaseHelper.updateUserScores(email, user.maxSynonymScore, newMaxScore)
            }
        }
    }

    private fun enableButtons(enable: Boolean) {
        for (button in buttonList) {
            if (enable) {
                button.setBackgroundResource(R.drawable.button_options_style_enable)
            } else {
                button.setBackgroundResource(R.drawable.button_options_style_disable)
            }
            button.isEnabled = enable
        }
        btnStart.isEnabled = !enable
    }

    private fun setQ() {
        var wordQ = arr[getPosition()]
        wordTextView.text = wordQ.definition

        var name = wordQ.name
        var correctAnswerIndex = ThreadLocalRandom.current().nextInt(0, buttonList.size)
        buttonList[correctAnswerIndex].text = name

        for (i in 0 until buttonList.size) {
            if (i != correctAnswerIndex) {
                var incorrectAnswer = arr[getPosition()]
                var incorrectName = incorrectAnswer.name
                buttonList[i].text = incorrectName
            }
        }

        for (i in 0 until buttonList.size) {
            buttonList[i].setOnClickListener {
                if (buttonList[i].text == name) {
                    score++
                    scoreTextView.text = score.toString()
                    updateTimer(5000)
                    setQ()
                } else {
                    updateTimer(-10000)
                    setQ()
                }
            }
        }
    }

    private fun parseJson() {
        val json: String?
        try{
            val inputStream: InputStream = requireContext().assets.open("scratch_1_translated.json")
            json = inputStream.bufferedReader().use { it.readText() }
            val jsonArr = JSONArray(json)
            for (i in 1 until jsonArr.length()){
                var jsonObj = jsonArr.getJSONObject(i)
                var id = jsonObj.getInt("id")
                var word = jsonObj.getString("name")
                var definition = jsonObj.getString("definition")
                val synonymsJson = jsonObj.optJSONObject("synonyms")
                var translate = jsonObj.getString("translate")

                if (definition == null || definition == "Не найдено" || definition == "") {
                    continue
                }

                val synonyms = jsonObjectToMap(synonymsJson)

                arr.add(Word(id, word, definition, synonyms, translate))
            }
        }
        catch (e: Exception){}
    }

    @JvmName("functionOfKotlin")
    private fun getPosition(): Int{
        position = ThreadLocalRandom.current().nextInt(1, arr.size)
        return position
    }

    private fun jsonObjectToMap(jsonObject: JSONObject?): Map<String, String>? {
        if (jsonObject == null) return null
        val map = mutableMapOf<String, String>()
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = jsonObject.getString(key)
            map[key] = value
        }
        return map
    }
}