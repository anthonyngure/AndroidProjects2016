package ke.co.elmaxdevelopers.eventskenya.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;


public class Helper {

    public static Snackbar snackbar;

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static int getRandomColor() {
        SecureRandom rgen = new SecureRandom();
        return Color.HSVToColor(150, new float[]{
                rgen.nextInt(359), 1, 1
        });
    }

    private static void hideSnackBar(){
        if (snackbar != null && snackbar.isShown()){
            snackbar.dismiss();
            snackbar = null;
        }
    }

    public static void snack(View view, String msg){
        hideSnackBar();
        snackbar = Snackbar.make(view, msg,
                Snackbar.LENGTH_LONG);
        TextView tvSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tvSnackBar.setTextColor(Color.parseColor("#49910e"));
        snackbar.show();
    }

    public static TextWatcher createTextWatcher(final int maxLength, final TextInputLayout inputLayout,
                                         final EditText et, final String hint) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = maxLength - et.length(); //
                if (count < maxLength) {
                    inputLayout.setHint(hint + "\t\t" + String.valueOf(count));
                } else {
                    inputLayout.setHint(hint);
                }
            }
        };
    }
}
