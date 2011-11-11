package de.hszigr.mobileapps.questionnaire.client.activity;

import de.hszigr.mobileapps.R;
import de.hszigr.mobileapps.questionnaire.client.util.Settings;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    private EditText textBaseUrl;
    private Button buttonSave;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences settings = getSharedPreferences(Settings.NAME, 0);
        textBaseUrl.setText(settings.getString(Settings.BASE_URL,
                Settings.DEFAULT_BASE_URL));
    }

    private void initialize() {
        findViewReferences();
        addViewListeners();
    }

    private void findViewReferences() {
        buttonSave = (Button) findViewById(R.id.buttonSave);
        textBaseUrl = (EditText) findViewById(R.id.textViewBaseUrl);
    }

    private void addViewListeners() {
        buttonSave.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences(
                        Settings.NAME, 0);
                Editor editor = preferences.edit();
                editor.putString(Settings.BASE_URL, textBaseUrl.getText()
                        .toString());
                
                if(editor.commit()) {
                    Toast.makeText(getApplicationContext(), R.string.SETTINGS_SAVED, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.ERROR_SAVING_SETTINGS, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
