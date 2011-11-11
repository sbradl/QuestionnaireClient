package de.hszigr.mobileapps.questionnaire.client.activity;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import de.hszigr.mobileapps.R;
import de.hszigr.mobileapps.questionnaire.client.model.Choice;
import de.hszigr.mobileapps.questionnaire.client.model.Question;
import de.hszigr.mobileapps.questionnaire.client.model.Questionnaire;
import de.hszigr.mobileapps.questionnaire.client.util.QuestionnaireService;
import de.hszigr.mobileapps.questionnaire.client.util.Settings;

public class QuestionnaireActivity extends Activity {

    private LinearLayout layout;

    private Questionnaire questionnaire;

    private Map<String, View> viewMap = new HashMap<String, View>();

    private String baseUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionnaire);

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
            SharedPreferences settings = getSharedPreferences(Settings.NAME, 0);
            baseUrl = settings.getString(Settings.BASE_URL, Settings.DEFAULT_BASE_URL);
            questionnaire = service.getQuestionnaire(baseUrl,
                getIntent().getExtras().getString("qid"));

            int nr = 1;
            for (Question question : questionnaire.getQuestions()) {
                View v = createViewFor(question, nr);
                layout.addView(v);
                ++nr;
            }

            Button button = new Button(this);
            button.setText(R.string.SEND);

            button.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    sendData();
                }
            });

            layout.addView(button);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "IOException: " + e.getMessage(),
                Toast.LENGTH_LONG).show();
        } catch (XPathExpressionException e) {
            Toast.makeText(getApplicationContext(), "XPathException: " + e.getMessage(),
                Toast.LENGTH_LONG).show();
        }
    }

    private View createViewFor(Question question, int nr) {
        final String text = "" + nr + ". " + question.getText();

        LinearLayout questionLayout = new LinearLayout(getApplicationContext());
        questionLayout.setOrientation(LinearLayout.VERTICAL);

        TextView questionText = new TextView(getApplicationContext());
        questionText.setText(text);
        questionLayout.addView(questionText);

        switch (question.getType()) {
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

            for (Choice c : question.getChoices()) {
                RadioButton radio = new RadioButton(getApplicationContext());

                radio.setText(c.getValue());
                radio.setTag(c.getId());
                radios.addView(radio);
            }

            questionLayout.addView(radios);
            break;

        case MULTICHOICE:
            for (Choice c : question.getChoices()) {
                CheckBox cb = new CheckBox(getApplicationContext());
                cb.setText(c.getValue());
                cb.setTag(c.getId());
                questionLayout.addView(cb);
            }
            break;
        }

        viewMap.put(question.getId(), questionLayout);

        return questionLayout;
    }

    private void sendData() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            return;
        }

        Document doc = builder.newDocument();

        Element answersElement = doc.createElement("answers");
        answersElement.setAttribute("forQuestionnaire", questionnaire.getId());
        doc.appendChild(answersElement);

        for (Question question : questionnaire.getQuestions()) {
            Element answerElement = doc.createElement("answer");
            answerElement.setAttribute("forQuestion", question.getId());
            answersElement.appendChild(answerElement);

            LinearLayout layout = (LinearLayout) viewMap.get(question.getId());

            switch (question.getType()) {
            case TEXT:
                EditText edit = (EditText) layout.getChildAt(1);
                answerElement.setTextContent(edit.getText().toString());
                break;

            case LOCATION:
                TextView text = (TextView) layout.getChildAt(1);
                answerElement.setTextContent(text.getText().toString());
                break;

            case CHOICE:
                RadioGroup radios = (RadioGroup) layout.getChildAt(1);

                for (int i = 0; i != radios.getChildCount(); ++i) {
                    RadioButton rb = (RadioButton) radios.getChildAt(i);

                    if (rb.isChecked()) {
                        answerElement.setTextContent((String) rb.getTag());
                        break;
                    }
                }
                break;

            case MULTICHOICE:
                String data = "";
                for (int i = 1; i < layout.getChildCount(); ++i) {
                    CheckBox cb = (CheckBox) layout.getChildAt(i);

                    if (cb.isChecked()) {
                        data += (String) cb.getTag() + ",";
                    }
                }

                if (data.endsWith(",")) {
                    data = data.substring(0, data.length() - 1);
                }

                answerElement.setTextContent(data);
                break;
            }
        }

        StringWriter sw = new StringWriter();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;

        try {
            transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
        } catch (Exception e) {
            e.printStackTrace();
        }

        QuestionnaireService service = new QuestionnaireService();
        
        // TODO: check validation status
        Toast.makeText(getApplicationContext(), service.validate(baseUrl, sw.toString()), Toast.LENGTH_LONG).show();

        // TODO: send data if validation was successful
        
        // Toast.makeText(getApplicationContext(), sw.toString(),
        // Toast.LENGTH_LONG).show();
    }
}
