package de.hszigr.mobileapps.questionnaire.client.util;

import android.content.Context;
import android.widget.Toast;

public final class ActivityUtils {

    public static void showInfoMessage(Context context, int message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    
    public static void showErrorMessage(Context context, Throwable ex) {
        Toast.makeText(context, ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }
    
    public static void showErrorMessage(Context context, int message, Object... args) {
        Toast.makeText(context, context.getString(message, args), Toast.LENGTH_LONG).show();
    }
    
    private ActivityUtils() {
        
    }
}
