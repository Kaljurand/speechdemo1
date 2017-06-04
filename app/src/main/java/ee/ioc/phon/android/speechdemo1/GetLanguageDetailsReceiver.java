package ee.ioc.phon.android.speechdemo1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import java.util.ArrayList;

/**
 * Receiver that declares which languages as supported.
 * We declare several different languages for testing purposes.
 */
public class GetLanguageDetailsReceiver extends BroadcastReceiver {

    // Afrikaans (first language alphabetically, supported by Google Translate, Google Speech)
    public static final String DEFAULT_LANGUAGE_PREFERENCE = "af";

    public static final ArrayList<String> SUPPORTED_LANGUAGES;

    static {
        ArrayList<String> aList = new ArrayList<>();
        aList.add(DEFAULT_LANGUAGE_PREFERENCE);
        aList.add("en"); // English (widely supported)
        aList.add("et"); // Estonian (supported by Kõnele, Google Translate, but not by Google Speech)
        aList.add("lv"); // Latvian (supported by Google Translate, but not by Google Speech)
        aList.add("vro"); // Võro (not supported by any apps)
        SUPPORTED_LANGUAGES = aList;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<String> langs = new ArrayList<>();
        langs.add(DEFAULT_LANGUAGE_PREFERENCE);

        Bundle extras = new Bundle();
        extras.putString(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, DEFAULT_LANGUAGE_PREFERENCE);
        extras.putStringArrayList(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, SUPPORTED_LANGUAGES);
        setResultExtras(extras);
    }
}