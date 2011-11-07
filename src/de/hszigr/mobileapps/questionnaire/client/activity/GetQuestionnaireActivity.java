package de.hszigr.mobileapps.questionnaire.client.activity;

import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import de.hszigr.mobileapps.R;
import de.hszigr.mobileapps.questionnaire.client.model.Choice;
import de.hszigr.mobileapps.questionnaire.client.model.Question;
import de.hszigr.mobileapps.questionnaire.client.model.Questionnaire;
import de.hszigr.mobileapps.questionnaire.client.util.QuestionnaireService;

public class GetQuestionnaireActivity extends Activity {

    private LinearLayout layout;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_questionnaire);
        
        initialize();
    }

    private void initialize() {
        findViewReferences();
        fetchQuestionnaire();
    }

    private void findViewReferences() {
        layout = (LinearLayout) findViewById(R.id.questionnaireLinearLayout);
    }

    private void fetchQuestionnaire() {
        QuestionnaireService service = new QuestionnaireService();

        try {
            Questionnaire questionnaire = service.getQuestionnaire(getIntent().getExtras().getString("baseURL"), "1");

            int nr = 1;
            for(Question question : questionnaire.getQuestions()) {
                View v = createViewFor(question, nr);
                layout.addView(v);
                
                ++nr;
            }
            
            Toast toast = Toast.makeText(getApplicationContext(), "Fetched questions: " + questionnaire.getQuestions().size(), Toast.LENGTH_SHORT);
            toast.show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "IOException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (XPathExpressionException e) {
            Toast.makeText(getApplicationContext(), "XPathException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } 
    }

    private View createViewFor(Question question, int nr) {
        final String text = "" + nr + ". " + question.getText();
        
        LinearLayout questionLayout = new LinearLayout(getApplicationContext());
        questionLayout.setOrientation(LinearLayout.VERTICAL);
        
        TextView questionText = new TextView(getApplicationContext());
        questionText.setText(text);
        questionLayout.addView(questionText);
        
        switch(question.getType()) {
        case TEXT:
            EditText edit = new EditText(getApplicationContext());
            questionLayout.addView(edit);
            break;
            
        case LOCATION:
            TextView location = new TextView(getApplicationContext());
            location.setText("Location not determined yet");
            questionLayout.addView(location);
            break;
            
        case CHOICE:
            RadioGroup radios = new RadioGroup(getApplicationContext());
            
            for(Choice c : question.getChoices()) {
                RadioButton radio = new RadioButton(getApplicationContext());
                
                radio.setText(c.getValue());
                radios.addView(radio);
            }
            
            questionLayout.addView(radios);
            break;
            
        case MULTICHOICE:
            for(Choice c : question.getChoices()) {
                CheckBox cb = new CheckBox(getApplicationContext());
                cb.setText(c.getValue());
                questionLayout.addView(cb);
            }
            break;
        }
        
        return questionLayout;
    }
 
}
