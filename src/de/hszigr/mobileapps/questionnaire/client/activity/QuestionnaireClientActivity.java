package de.hszigr.mobileapps.questionnaire.client.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import de.hszigr.mobileapps.R;

public class QuestionnaireClientActivity extends Activity {
    
    private Button buttonGetQuestionnaire;
    
    private Intent intentGetQuestionnaire;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        initialize();
    }
    
    private void initialize() {
        findViewReferences();
        prepareActivities();
        
        addViewListeners();
    }

    private void addViewListeners() {
        buttonGetQuestionnaire.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                intentGetQuestionnaire.putExtra("baseURL", "http://192.168.2.200:8080/questionnaire");
                startActivity(intentGetQuestionnaire);
            }
        });
    }

    private void prepareActivities() {
        intentGetQuestionnaire = new Intent(this, GetQuestionnaireActivity.class);
    }

    private void findViewReferences() {
        buttonGetQuestionnaire = (Button) findViewById(R.id.buttonGetQuestionnaire);
    }
}