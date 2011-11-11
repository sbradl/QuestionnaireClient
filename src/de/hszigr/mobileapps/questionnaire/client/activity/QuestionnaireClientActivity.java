package de.hszigr.mobileapps.questionnaire.client.activity;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.hszigr.mobileapps.R;
import de.hszigr.mobileapps.questionnaire.client.util.QuestionnaireService;
import de.hszigr.mobileapps.questionnaire.client.util.Settings;

public class QuestionnaireClientActivity extends Activity {

    private static final int SELECT_ACTION_DIALOG = 0;

    private ListView listViewQuestionnaires;

    private Intent intentSettings;
    private Intent intentQuestionnaire;

    private Map<String, String> questionnaireOverview;

    protected String selectedQuestionnaire;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case SELECT_ACTION_DIALOG:
            final String items[] = { getString(R.string.PARTICIPATE),
                    getString(R.string.SHOW_STATISTICS) };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.SELECT_ACTION);
            builder.setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                        intentQuestionnaire.putExtra("qid", selectedQuestionnaire);
                        startActivity(intentQuestionnaire);
                        break;

                    default:
                        break;
                    }
                }
            });

            return builder.create();

        default:
            return null;
        }
    }

    private void initialize() {
        findViewReferences();
        prepareActivities();
        setViewListeners();
    }

    private void findViewReferences() {
        listViewQuestionnaires = (ListView) findViewById(R.id.questionnaireList);
    }

    private void prepareActivities() {
        intentSettings = new Intent(this, SettingsActivity.class);
        intentQuestionnaire = new Intent(this, QuestionnaireActivity.class);
    }

    private void updateQuestionnaireList() {
        listViewQuestionnaires.clearChoices();

        SharedPreferences settings = getSharedPreferences(Settings.NAME, 0);
        String baseUrl = settings.getString(Settings.BASE_URL,
                Settings.DEFAULT_BASE_URL);

        QuestionnaireService service = new QuestionnaireService();

        try {
            questionnaireOverview = service.getQuestionnaireOverview(baseUrl);

            String[] data = new String[questionnaireOverview.size()];
            questionnaireOverview.keySet().toArray(data);

            ListAdapter adapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    android.R.layout.simple_list_item_1, data);
            listViewQuestionnaires.setAdapter(adapter);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    R.string.ERROR_FETCHING_OVERVIEW, Toast.LENGTH_LONG).show();
        }
    }

    private void setViewListeners() {
        listViewQuestionnaires
                .setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                            int arg2, long arg3) {
                        TextView item = (TextView) arg1;
                        String title = item.getText().toString();
                        selectedQuestionnaire = questionnaireOverview
                                .get(title);

                        showDialog(SELECT_ACTION_DIALOG);
                    }
                });
    }
}