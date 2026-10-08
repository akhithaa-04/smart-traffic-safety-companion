package com.stsc.android.service;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Speaks the rotating pre-journey safety reminder returned by
 * POST /api/journeys/start (field: safetyReminder) and posts a matching
 * short notification, so the user gets the message even if they glance at
 * the phone instead of listening.
 *
 * Keep messages short (they already are, by design on the backend) so the
 * user doesn't need to interact with the phone while starting to drive.
 */
public class SafetyReminderPlayer {

    private final TextToSpeech tts;

    public SafetyReminderPlayer(Context context) {
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.getDefault());
            }
        });
    }

    public void speak(String message) {
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "safety_reminder");
        // TODO: also post a short-lived Android Notification with the same text,
        // and a "Got it" action button that calls
        // POST /api/journeys/{id}/confirm-safety-check on tap.
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
