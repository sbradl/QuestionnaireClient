package de.hszigr.mobileapps.questionnaire.client.util;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import de.hszigr.mobileapps.questionnaire.client.model.Choice;
import de.hszigr.mobileapps.questionnaire.client.model.Question;

public class QuestionInputFactory {

    public static View createViewFor(Question question, Context context) {
        switch (question.getType()) {
        case TEXT:
            return createTextInput(context);

        case LOCATION:
            return createLocationInput(context);

        case CHOICE:
            return createChoiceInput(question, context);

        case MULTICHOICE:
            return createMultiChoiceInput(question, context);

        default:
            throw new RuntimeException("Invalid question type: " + question.getType());
        }
    }
    
    private static View createTextInput(Context context) {
        final LinearLayout layout = new LinearLayout(context);
        
        final EditText edit = new EditText(context);
        layout.addView(edit);
        edit.getLayoutParams().width = LayoutParams.FILL_PARENT;
        
        return layout;
    }
    
    private static View createLocationInput(Context context) {
        final LinearLayout layout = new LinearLayout(context);
        
        final TextView location = new TextView(context);
        location.setText("0.0, 0.0");
        layout.addView(location);
        
        return layout;
    }
    
    private static View createChoiceInput(Question question, Context context) {
        final LinearLayout layout = new LinearLayout(context);
        
        final RadioGroup radios = new RadioGroup(context);

        for (Choice choice : question.getChoices()) {
            RadioButton radio = new RadioButton(context);

            radio.setText(choice.getValue());
            radio.setTag(choice.getId());
            radios.addView(radio);
        }

        layout.addView(radios);
        
        return layout;
    }
    
    private static View createMultiChoiceInput(Question question, Context context) {
        final LinearLayout layout = new LinearLayout(context);
        
        layout.setOrientation(LinearLayout.VERTICAL);
        
        for (Choice c : question.getChoices()) {
            CheckBox cb = new CheckBox(context);
            cb.setText(c.getValue());
            cb.setTag(c.getId());
            layout.addView(cb);
        }
        
        return layout;
    }

}
