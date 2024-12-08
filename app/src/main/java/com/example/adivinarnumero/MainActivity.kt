package com.example.adivinarnumero

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var etUserGuess: EditText
    private lateinit var btnGuess: Button
    private lateinit var btnReset: Button
    private lateinit var tvFeedback: TextView
    private lateinit var tvAttemptsLeft: TextView
    private lateinit var gameLogic: GameLogic

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        etUserGuess = findViewById(R.id.etUserGuess)
        btnGuess = findViewById(R.id.btnGuess)
        btnReset = findViewById(R.id.btnReset)
        tvFeedback = findViewById(R.id.tvFeedback)
        tvAttemptsLeft = findViewById(R.id.tvAttemptsLeft)

        // Inicializar lógica del juego
        gameLogic = GameLogic()

        // Configurar botón de adivinar
        btnGuess.setOnClickListener {
            val userGuessText = etUserGuess.text.toString()

            if (userGuessText.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa un número", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userGuess = userGuessText.toIntOrNull()

            if (userGuess == null) {
                Toast.makeText(this, "Número inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            processGuess(userGuess)
        }

        // Configurar botón de reiniciar
        btnReset.setOnClickListener {
            resetGame()
        }

        // Actualizar intentos iniciales
        updateAttemptsLeft()
    }

    private fun processGuess(userGuess: Int) {
        when (val result = gameLogic.makeGuess(userGuess)) {
            GameLogic.GuessResult.CORRECT -> {
                tvFeedback.text = "¡Correcto! Has adivinado el número."
                disableGuessing()
            }
            GameLogic.GuessResult.TOO_HIGH -> {
                tvFeedback.text = "Demasiado alto. Intenta con un número más bajo."
            }
            GameLogic.GuessResult.TOO_LOW -> {
                tvFeedback.text = "Demasiado bajo. Intenta con un número más alto."
            }
            GameLogic.GuessResult.INVALID_GUESS -> {
                Toast.makeText(this, "Por favor, ingresa un número entre 1 y 100", Toast.LENGTH_SHORT).show()
                return
            }
            GameLogic.GuessResult.GAME_OVER -> {
                tvFeedback.text = "¡Has perdido! El número era ${gameLogic.getTargetNumber()}."
                disableGuessing()
            }
        }

        updateAttemptsLeft()
        etUserGuess.text.clear()
    }

    private fun updateAttemptsLeft() {
        tvAttemptsLeft.text = "Intentos restantes: ${gameLogic.getAttemptsLeft()}"
    }

    private fun resetGame() {
        gameLogic.resetGame()
        etUserGuess.text.clear()
        tvFeedback.text = ""
        updateAttemptsLeft()
        enableGuessing()
    }

    private fun disableGuessing() {
        btnGuess.isEnabled = false
        etUserGuess.isEnabled = false
    }

    private fun enableGuessing() {
        btnGuess.isEnabled = true
        etUserGuess.isEnabled = true
    }
}

class GameLogic {
    private var targetNumber: Int = 0
    private var attemptsLeft: Int = 10
    private val MIN_NUMBER = 1
    private val MAX_NUMBER = 100

    init {
        resetGame()
    }

    fun resetGame() {
        targetNumber = (MIN_NUMBER..MAX_NUMBER).random()
        attemptsLeft = 10
    }

    fun makeGuess(userGuess: Int): GuessResult {
        if (userGuess < MIN_NUMBER || userGuess > MAX_NUMBER) {
            return GuessResult.INVALID_GUESS
        }

        attemptsLeft--

        return when {
            userGuess == targetNumber -> {
                GuessResult.CORRECT
            }
            attemptsLeft == 0 -> {
                GuessResult.GAME_OVER
            }
            userGuess > targetNumber -> {
                GuessResult.TOO_HIGH
            }
            else -> {
                GuessResult.TOO_LOW
            }
        }
    }

    fun getTargetNumber() = targetNumber
    fun getAttemptsLeft() = attemptsLeft

    enum class GuessResult {
        CORRECT,
        TOO_HIGH,
        TOO_LOW,
        INVALID_GUESS,
        GAME_OVER
    }
}