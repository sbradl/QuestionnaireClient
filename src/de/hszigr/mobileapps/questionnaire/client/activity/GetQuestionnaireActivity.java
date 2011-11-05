package de.hszigr.mobileapps.questionnaire.client.activity;

import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import de.hszigr.mobileapps.R;
import de.hszigr.mobileapps.questionnaire.client.model.Questionnaire;
import de.hszigr.mobileapps.questionnaire.client.util.QuestionnaireService;

public class GetQuestionnaireActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_questionnaire);
        
        initialize();
    }

    private void initialize() {
        fetchQuestionnaire();
    }

    private void fetchQuestionnaire() {
        QuestionnaireService service = new QuestionnaireService();

        try {
            Questionnaire questionnaire = service.getQuestionnaire(getIntent().getExtras().getString("baseURL"), "1");

            Toast toast = Toast.makeText(getApplicationContext(), "Fetched questions: " + questionnaire.getQuestions().size(), Toast.LENGTH_SHORT);
            toast.show();
          toast.show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "IOException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (XPathExpressionException e) {
            Toast.makeText(getApplicationContext(), "XPathException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } 
    }
 
}
