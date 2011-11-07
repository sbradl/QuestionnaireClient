package de.hszigr.mobileapps.questionnaire.client.activity;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.xpath.XPathExpressionException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.hszigr.mobileapps.R;
import de.hszigr.mobileapps.questionnaire.client.util.QuestionnaireService;
import de.hszigr.mobileapps.questionnaire.client.util.Settings;

public class QuestionnaireClientActivity extends Activity {

    private TextView textViewBaseUrl;
    private ListView listViewQuestionnaires;

    private Intent intentSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateSettings();
        updateQuestionnaireList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.settings:
            startActivity(intentSettings);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void initialize() {
        findViewReferences();
        prepareActivities();
    }

    private void findViewReferences() {
        textViewBaseUrl = (TextView) findViewById(R.id.textViewBaseUrl);
        listViewQuestionnaires = (ListView) findViewById(R.id.questionnaireList);
    }

    private void prepareActivities() {
        intentSettings = new Intent(this, SettingsActivity.class);
    }

    private void updateSettings() {
        SharedPreferences settings = getSharedPreferences(Settings.NAME, 0);
        textViewBaseUrl.setText("Using: "
                + settings.getString(Settings.BASE_URL,
                        Settings.DEFAULT_BASE_URL));
    }
    
    private void updateQuestionnaireList() {
        listViewQuestionnaires.clearChoices();
        
        SharedPreferences settings = getSharedPreferences(Settings.NAME, 0);
        String baseUrl = settings.getString(Settings.BASE_URL, Settings.DEFAULT_BASE_URL);
        
        QuestionnaireService service = new QuestionnaireService();
        
        try {
            Map<String, String> questionnaires = service.getQuestionnaireOverview(baseUrl);
            
            String[] data = new String[questionnaires.size()];
            questionnaires.values().toArray(data);
            
            ListAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, data);
            listViewQuestionnaires.setAdapter(adapter);
         
        } catch (Exception e) {
            textViewBaseUrl.setText(e.getMessage());
//            Toast.makeText(getApplicationContext(), 
//                    R.string.ERROR_FETCHING_OVERVIEW, 
//                    Toast.LENGTH_LONG).show();
        }
    }
}