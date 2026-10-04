package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

sealed class VoiceInputState {
    object Idle : VoiceInputState()
    data class Listening(val rmsDb: Float = 0f) : VoiceInputState()
    object Processing : VoiceInputState()
    data class Error(val message: String, val isPermissionDenied: Boolean = false) : VoiceInputState()
}

interface SpeechRecognitionController {
    val state: StateFlow<VoiceInputState>
    fun isAvailable(): Boolean
    fun startListening(onResult: (String) -> Unit)
    fun stopListening()
    fun cancel()
    fun reset()
}

/**
 * Standard implementation using Android SpeechRecognizer.
 * Strictly ephemeral: never stores, uploads, or persists raw audio recordings.
 */
class AndroidSpeechRecognizerController(
    private val context: Context
) : SpeechRecognitionController, RecognitionListener {

    private val _state = MutableStateFlow<VoiceInputState>(VoiceInputState.Idle)
    override val state: StateFlow<VoiceInputState> = _state.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var onResultCallback: ((String) -> Unit)? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun isAvailable(): Boolean {
        return try {
            SpeechRecognizer.isRecognitionAvailable(context)
        } catch (_: Exception) {
            false
        }
    }

    override fun startListening(onResult: (String) -> Unit) {
        if (!isAvailable()) {
            _state.value = VoiceInputState.Error("Voice input is unavailable on this device. You can continue using text input.")
            return
        }

        this.onResultCallback = onResult

        mainHandler.post {
            try {
                cancel() // clean up previous session if any

                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(this@AndroidSpeechRecognizerController)
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                }

                speechRecognizer?.startListening(intent)
                _state.value = VoiceInputState.Listening()
            } catch (e: Exception) {
                _state.value = VoiceInputState.Error("Failed to initialize voice input: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    override fun stopListening() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
                if (_state.value is VoiceInputState.Listening) {
                    _state.value = VoiceInputState.Processing
                }
            } catch (_: Exception) {}
        }
    }

    override fun cancel() {
        mainHandler.post {
            try {
                speechRecognizer?.cancel()
                speechRecognizer?.destroy()
            } catch (_: Exception) {
            } finally {
                speechRecognizer = null
                onResultCallback = null
                _state.value = VoiceInputState.Idle
            }
        }
    }

    override fun reset() {
        _state.value = VoiceInputState.Idle
    }

    // --- RecognitionListener implementation ---

    override fun onReadyForSpeech(params: Bundle?) {
        _state.value = VoiceInputState.Listening()
    }

    override fun onBeginningOfSpeech() {
        _state.value = VoiceInputState.Listening()
    }

    override fun onRmsChanged(rmsdB: Float) {
        if (_state.value is VoiceInputState.Listening) {
            _state.value = VoiceInputState.Listening(rmsdB)
        }
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        // Ephemeral audio streaming; intentionally not stored
    }

    override fun onEndOfSpeech() {
        _state.value = VoiceInputState.Processing
    }

    override fun onError(error: Int) {
        val (message, isPermDenied) = when (error) {
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                "Microphone permission is required for voice input. Please grant permission in settings." to true
            SpeechRecognizer.ERROR_AUDIO ->
                "Audio recording error. Please check your microphone." to false
            SpeechRecognizer.ERROR_NO_MATCH ->
                "No speech recognized. Please speak clearly and try again." to false
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                "Listening timed out. No speech was detected." to false
            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                "Network connection error during voice recognition." to false
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY, SpeechRecognizer.ERROR_CLIENT ->
                "Voice recognition service is busy. Please try again." to false
            SpeechRecognizer.ERROR_SERVER ->
                "Speech server error. Please try again or use text input." to false
            else ->
                "Voice input error (Code $error). You can continue using text input." to false
        }

        _state.value = VoiceInputState.Error(message, isPermDenied)
        mainHandler.post {
            try {
                speechRecognizer?.destroy()
                speechRecognizer = null
            } catch (_: Exception) {}
        }
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val recognizedText = matches?.firstOrNull()?.trim()

        if (!recognizedText.isNullOrBlank()) {
            _state.value = VoiceInputState.Idle
            onResultCallback?.invoke(recognizedText)
        } else {
            _state.value = VoiceInputState.Error("No words were recognized. Please try speaking again.")
        }

        mainHandler.post {
            try {
                speechRecognizer?.destroy()
                speechRecognizer = null
            } catch (_: Exception) {}
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {}

    override fun onEvent(eventType: Int, params: Bundle?) {}
}
