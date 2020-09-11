package com.tamersarioglu.speechtotexttospeech

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_SPEECH_TO_TEXT = 10
    }

    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(this, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                textToSpeechEngine.language = Locale.ENGLISH
            }
        })
    }

    override fun onPause() {
        textToSpeechEngine.stop()
        super.onPause()
    }

    override fun onDestroy() {
        textToSpeechEngine.shutdown()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonSpeak.setOnClickListener {
            //get recognize intent
            val speakIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            //get language model for defining purpose
            speakIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            // Adding an extra language, you can use any language from the Locale class.
            speakIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

            // Text that shows up on the Speech input prompt.
            speakIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!")

            try {
                // Start the intent for a result, and pass in our request code.
                startActivityForResult(speakIntent, REQUEST_CODE_SPEECH_TO_TEXT)
            } catch (e: ActivityNotFoundException) {
                // Handling error when the service is not available.
                e.printStackTrace()
                Toast.makeText(this, "Your device does not support SPEECH TO TEXT.", Toast.LENGTH_LONG).show()
            }
        }

        buttonListen.setOnClickListener {

            //get text from edit text listen
            val writtenText = editTextListen.text.toString().trim()

            //check user let the editTextListen empty
            if (writtenText.isNotEmpty()) {

                //Lollipop and above required for this
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    //calling lollipop+ function
                    textToSpeechEngine.speak(writtenText, TextToSpeech.QUEUE_FLUSH, null, "tts1")
                } else {

                    //call legacy func
                    textToSpeechEngine.speak(writtenText, TextToSpeech.QUEUE_FLUSH, null)
                }
            } else {
                Toast.makeText(this, "Text cannot be empty", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SPEECH_TO_TEXT -> {

                // Safety checks to ensure data is available.
                if (resultCode == Activity.RESULT_OK && data != null) {

                    // Retrieve the result array.
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    // Ensure result array is not null or empty to avoid errors.
                    if (!result.isNullOrEmpty()) {

                        // Recognized text is in the first position.
                        val recognizedText = result[0]

                        // Do what you want with the recognized text.
                        editTextSpeak.setText(recognizedText)
                    }
                }
            }
        }
    }
}