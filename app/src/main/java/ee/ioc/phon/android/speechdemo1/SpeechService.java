package ee.ioc.phon.android.speechdemo1;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

public class SpeechService extends RecognitionService {

    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord mAudioRecord;

    private String mLang = GetLanguageDetailsReceiver.DEFAULT_LANGUAGE_PREFERENCE;

    private Handler mHandler;
    private Runnable mRunnable;

    /**
     * The user wants to start listening
     */
    @Override
    protected void onStartListening(Intent intent, Callback callback) {

        // We do some initialization based on the given intent
        // and then signal that we a ready.

        String lang = intent.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE);

        if (GetLanguageDetailsReceiver.SUPPORTED_LANGUAGES.contains(lang)) {
            mLang = lang;
        } else {
            // We do not support this language
            finishWithError(callback, SpeechRecognizer.ERROR_CLIENT);
        }

        // Init the recorder
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, 1024 * 2);

        try {
            if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                mAudioRecord.startRecording();
                callback.readyForSpeech(new Bundle());

                // Monitor the audio until human speech is heard.
                // Various errors can occur in which case we respond with a relevant error code.

                boolean isBeginningOfSpeech = true;

                if (isBeginningOfSpeech) {
                    // Speech was heard
                    callback.beginningOfSpeech();
                    // Start returning the transcription
                    loop(callback);
                } else {
                    finishWithError(callback, SpeechRecognizer.ERROR_SPEECH_TIMEOUT);
                }
            } else {
                // Find out the cause of the failure and respond with the relevant error code.
                finishWithError(callback, SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS);
                //finishWithError(callback, SpeechRecognizer.ERROR_AUDIO);
            }
        } catch (RemoteException e) {
            finishWithError(callback, SpeechRecognizer.ERROR_CLIENT);
        }
    }

    /**
     * The user wants to stop and does not care about the remaining audio transcription.
     */
    @Override
    protected void onCancel(Callback callback) {
        cleanup();
    }

    /**
     * The user has stopped speaking, but still wants to get the remaining audio transcription.
     */
    @Override
    protected void onStopListening(Callback callback) {
        mAudioRecord.stop();
        // Return any remaining transcription
        try {
            callback.endOfSpeech();
            callback.partialResults(makeResultsBundle(mLang + ": stop-partial"));
            callback.results(makeResultsBundle(mLang + ": stop-final"));
        } catch (RemoteException e) {
            finishWithError(callback, SpeechRecognizer.ERROR_CLIENT);
        }
        cleanup();
    }

    /**
     * Return some fake transcription.
     */
    private void loop(final Callback callback) {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            int counter = 0;

            public void run() {
                counter++;
                try {
                    if (counter % 2 == 0) {
                        callback.rmsChanged(1.1f * counter);
                    }
                    if (counter % 3 == 0) {
                        callback.bufferReceived(new byte[]{1, 2, 3});
                    }
                    if (counter % 4 == 0) {
                        callback.partialResults(makeResultsBundle(mLang + ":" + counter));
                    }
                    if (counter == 7) {
                        callback.endOfSpeech();
                    }
                    if (counter >= 10) {
                        callback.results(makeResultsBundle(mLang + ": final"));
                        cleanup();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (counter < 10) {
                    mHandler.postDelayed(this, 400);
                }
            }
        };

        mHandler.postDelayed(mRunnable, 100);
    }

    /**
     * Various cleanup: stop recording, disconnect from the recognizer server, etc.
     */
    private void cleanup() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
            mHandler = null;
        }
        if (mAudioRecord != null) {
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }

    private void finishWithError(Callback callback, int code) {
        try {
            callback.error(code);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        cleanup();
    }

    /**
     * Package the transcription as a RESULTS_RECOGNITION array.
     */
    private static Bundle makeResultsBundle(String result) {
        ArrayList<String> results = new ArrayList<>();
        results.add(result);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, results);
        return bundle;
    }
}
