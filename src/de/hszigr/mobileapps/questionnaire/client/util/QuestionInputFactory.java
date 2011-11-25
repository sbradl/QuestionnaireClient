package de.hszigr.mobileapps.questionnaire.client.util;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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

        case ATTACHMENT:
            return createAttachmentInput(question, context);

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
        final TextView locationTextView = new TextView(context);
        layout.addView(locationTextView);

        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                String lat = Double.toString(location.getLatitude());
                String lng = Double.toString(location.getLongitude());
                locationTextView.setText(String.format("%s, %s", lat, lng));
            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                locationListener);

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

    private static View createAttachmentInput(Question question, final Context context) {
        final LinearLayout layout = new LinearLayout(context);

        Button takePicture = new Button(context);

        layout.addView(takePicture);

        return layout;
    }

}
