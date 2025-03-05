package com.example.practice;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class FakeCallActivity extends Activity {

    private MediaPlayer ringtone;
    private TextToSpeech textToSpeech;
    private Button answerButton, declineButton;
    private TextView callerName, callerNumber;
    private ImageView callerImage;
    private boolean isSpeaking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_call);

        // Initialize UI elements
        callerName = findViewById(R.id.callerName);
        callerNumber = findViewById(R.id.callerNumber);
        callerImage = findViewById(R.id.callerImage);
        answerButton = findViewById(R.id.answerButton);
        declineButton = findViewById(R.id.declineButton);

        // Get caller details from Intent
        String phoneNumber = getIntent().getStringExtra("caller_number");
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            callerName.setText("Unknown Caller");
            callerNumber.setText(phoneNumber);
        } else {
            callerName.setText("Private Number");
            callerNumber.setText("Unknown");
        }

        // Set caller image (Use a default image)
        callerImage.setImageResource(R.drawable.download);

        // Play ringtone
        ringtone = MediaPlayer.create(this, R.raw.aa);
        if (ringtone != null) {
            ringtone.setLooping(true);
            ringtone.start();
        }

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("FakeCallActivity", "TTS language not supported.");
                }
            } else {
                Log.e("FakeCallActivity", "TTS initialization failed.");
            }
        });

        // Answer button: Stop ringtone and start TTS conversation
        answerButton.setOnClickListener(v -> {
            stopRingtone();
            startConversation();
        });

        // Decline button: Stop ringtone and close activity
        declineButton.setOnClickListener(v -> {
            stopRingtone();
            stopConversation();
            finish();
        });
    }

    // Stops the ringtone
    private void stopRingtone() {
        if (ringtone != null) {
            if (ringtone.isPlaying()) {
                ringtone.stop();
            }
            ringtone.release();
            ringtone = null;
        }
    }

    // Start voice conversation with TTS
    private void startConversation() {
        String callerSpeech = "Hello! This is " + callerName.getText().toString() + ". How are you?";

        if (textToSpeech != null) {
            textToSpeech.speak(callerSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
            isSpeaking = true;
        }
    }

    // Stop Text-to-Speech if speaking
    private void stopConversation() {
        if (textToSpeech != null && isSpeaking) {
            textToSpeech.stop();
            isSpeaking = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRingtone();
        stopConversation();

        if (textToSpeech != null) {
            textToSpeech.shutdown();
            textToSpeech = null;
        }
    }
}
