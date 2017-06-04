package ee.ioc.phon.android.speechdemo1;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

/**
 * The launcher activity just to provide a convenient access to the app settings
 * (where one can enable the recording permission), to the service settings, and to the
 * Android voice input settings.
 * All of these are available via the general Android settings, but are relatively deep there.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_settings_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        findViewById(R.id.button_settings_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
            }
        });

        findViewById(R.id.button_settings_voice_input).setOnClickListener(new View.OnClickListener() {
            @Override
            @TargetApi(21)
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_VOICE_INPUT_SETTINGS);
                    startActivity(intent);
                } else {
                    // TODO: open something else
                }
            }
        });
    }
}