package de.hszigr.mobileapps.questionnaire.client.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import de.hszigr.mobileapps.R;
import de.hszigr.mobileapps.questionnaire.client.model.Question;
import de.hszigr.mobileapps.questionnaire.client.model.Questionnaire;
import de.hszigr.mobileapps.questionnaire.client.util.ActivityUtils;
import de.hszigr.mobileapps.questionnaire.client.util.QuestionInputFactory;
import de.hszigr.mobileapps.questionnaire.client.util.QuestionnaireService;
import de.hszigr.mobileapps.questionnaire.client.util.Settings;
import de.hszigr.mobileapps.questionnaire.client.util.XmlUtils;

public class QuestionnaireActivity extends Activity {

    private LinearLayout layout;

    private Questionnaire questionnaire;

    private Map<String, View> viewMap = new HashMap<String, View>();

    private String baseUrl;
    private int currentQuestionNumber;

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
        retreiveBaseUrl();
        tryToRetreiveQuestionnaire();

        if (questionnaire != null) {
            resetCurrentQuestionNumber();
            buildQuestionnaireView();
        }
    }

    private void buildQuestionnaireView() {
        createQuestionViews();
        createSendButton();
    }

    private void resetCurrentQuestionNumber() {
        currentQuestionNumber = 1;
    }

    private void createSendButton() {
        Button button = new Button(this);
        button.setText(R.string.SEND);

        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                sendData();
            }
        });

        layout.addView(button);
    }

    private void createQuestionViews() {
        for (Question question : questionnaire.getQuestions()) {
            View v = createViewFor(question);
            layout.addView(v);
        }
    }

    private void retreiveBaseUrl() {
        baseUrl = Settings.getBaseUrl(this);
    }

    private void tryToRetreiveQuestionnaire() {
        try {
            QuestionnaireService service = new QuestionnaireService();
            questionnaire = service.getQuestionnaire(baseUrl,
                    getIntent().getExtras().getString("qid"));

        } catch (IOException e) {
            ActivityUtils.showErrorMessage(this, e);
        }
    }

    private View createViewFor(Question question) {
        final String text = "" + currentQuestionNumber + ". " + question.getText();
        currentQuestionNumber++;

        final LinearLayout questionLayout = createQuestionLayout();

        createQuestionTextView(text, questionLayout);

        createQuestionInput(question, questionLayout);

        viewMap.put(question.getId(), questionLayout);

        return questionLayout;
    }

    private void createQuestionInput(Question question, final LinearLayout questionLayout) {
        final View inputView = QuestionInputFactory.createViewFor(question, this);
        questionLayout.addView(inputView);
    }

    private void createQuestionTextView(final String text, final LinearLayout questionLayout) {
        final TextView questionTextView = new TextView(this);
        questionTextView.setText(text);
        questionLayout.addView(questionTextView);
    }

    private LinearLayout createQuestionLayout() {
        final LinearLayout questionLayout = new LinearLayout(this);
        questionLayout.setOrientation(LinearLayout.VERTICAL);

        return questionLayout;
    }

    private void sendData() {
        
        Document doc = XmlUtils.createEmptyDocument();

        final Element answersElement = createAnswersElement(doc);
        createAnswerElements(doc, answersElement);

        final QuestionnaireService service = new QuestionnaireService();
        final Document validationResult = service.validate(baseUrl, doc);
        final Element validationElement = validationResult.getDocumentElement();
        final String status = validationElement.getAttribute("status");

        if ("success".equals(status)) {
            final Document sendResult = service.send(baseUrl, doc);

            final Element resultElement = sendResult.getDocumentElement();

            if ("success".equals(resultElement.getNodeName())) {
                ActivityUtils.showInfoMessage(this, R.string.THANKS_FOR_PARTICIPATING);
                finish();
            } else {
                ActivityUtils.showErrorMessage(this, R.string.ERROR_SENDING_DATA);
            }
        } else {
            final Element messageElement = (Element) validationElement.getElementsByTagName("message").item(0);
            
            final String error = messageElement.getAttribute("error");
            
            final Element inputElement = (Element) messageElement.getElementsByTagName("input").item(0);
            
            final String input = inputElement.getTextContent();
            
            if("INVALID_ID".equals(error)) {
                ActivityUtils.showErrorMessage(this, R.string.INVALID_ID, input);
            } else if("INVALID_QUESTION_ID".equals(error)) {
                ActivityUtils.showErrorMessage(this, R.string.INVALID_QUESTION_ID, input);
            } else if("INVALID_TEXT".equals(error)) {
                ActivityUtils.showErrorMessage(this, R.string.INVALID_TEXT, input);
            } else if("INVALID_LOCATION".equals(error)) {
                ActivityUtils.showErrorMessage(this, R.string.INVALID_LOCATION, input);
            } else if("INVALID_CHOICE".equals(error)) {
                ActivityUtils.showErrorMessage(this, R.string.INVALID_CHOICE);
            }
            
        }
        
    }

    private void createAnswerElements(Document doc, final Element answersElement) {
        for (Question question : questionnaire.getQuestions()) {
            final Element answerElement = createAnswerElement(doc, answersElement, question);

            final LinearLayout questionLayout = (LinearLayout) viewMap.get(question.getId());
            final LinearLayout layout = (LinearLayout) questionLayout.getChildAt(1);

            getAnswer(question, answerElement, layout);
        }
    }

    private void getAnswer(Question question, final Element answerElement, final LinearLayout layout) {
        switch (question.getType()) {
        case TEXT:
            getAnswerForText(answerElement, layout);
            break;

        case LOCATION:
            getAnswerForLocation(answerElement, layout);
            break;

        case CHOICE:
            getAnswerForChoice(answerElement, layout);
            break;

        case MULTICHOICE:
            getAnswerForMultiChoice(answerElement, layout);
            break;
        }
    }

    private void getAnswerForMultiChoice(final Element answerElement, final LinearLayout layout) {
        String data = "";
        for (int i = 0; i < layout.getChildCount(); ++i) {
            CheckBox cb = (CheckBox) layout.getChildAt(i);

            if (cb.isChecked()) {
                data += (String) cb.getTag() + ",";
            }
        }

        if (data.endsWith(",")) {
            data = data.substring(0, data.length() - 1);
        }

        answerElement.setTextContent(data);
    }

    private void getAnswerForChoice(final Element answerElement, final LinearLayout layout) {
        RadioGroup radios = (RadioGroup) layout.getChildAt(0);

        for (int i = 0; i != radios.getChildCount(); ++i) {
            RadioButton rb = (RadioButton) radios.getChildAt(i);

            if (rb.isChecked()) {
                answerElement.setTextContent((String) rb.getTag());
                break;
            }
        }
    }

    private void getAnswerForLocation(final Element answerElement, final LinearLayout layout) {
        TextView text = (TextView) layout.getChildAt(0);
        answerElement.setTextContent(text.getText().toString());
    }

    private void getAnswerForText(final Element answerElement, final LinearLayout layout) {
        EditText edit = (EditText) layout.getChildAt(0);
        answerElement.setTextContent(edit.getText().toString());
    }

    private Element createAnswerElement(Document doc, final Element answersElement,
            Question question) {
        Element answerElement = doc.createElement("answer");
        answerElement.setAttribute("forQuestion", question.getId());
        answersElement.appendChild(answerElement);
        return answerElement;
    }

    private Element createAnswersElement(Document doc) {
        final Element answersElement = doc.createElement("answers");
        answersElement.setAttribute("forQuestionnaire", questionnaire.getId());
        doc.appendChild(answersElement);
        return answersElement;
    }
}
