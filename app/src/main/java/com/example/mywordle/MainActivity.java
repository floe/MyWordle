package com.example.mywordle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static String tag = "mywordle";
    TextView tv[] = new TextView[3];
    int current = 0;

    EditText et;

    String myword = "SPICE";

    Spannable wordle_compare(String guess) {
        // https://stackoverflow.com/a/53573169/838719
        Spannable result = new SpannableString(guess);
        for (int i = 0; i < myword.length(); i++) {
            //Log.d(tag,"target: "+myword.charAt(i)+" guess: "+guess.charAt(i));
            if (myword.charAt(i) == guess.charAt(i)) {
                // correct character, correct position
                result.setSpan(new BackgroundColorSpan(0xFF00FF00), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (myword.contains(String.valueOf(guess.charAt(i)))) {
                // correct character, wrong position
                result.setSpan(new BackgroundColorSpan(0xFFFFFF00), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                // wrong character
                result.setSpan(new BackgroundColorSpan(0xFF808080), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv[0] = findViewById(R.id.textView1);
        tv[1] = findViewById(R.id.textView2);
        tv[2] = findViewById(R.id.textView3);

        et = findViewById(R.id.editTextText);
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String guess = textView.getText().toString().toUpperCase();
                tv[current].setText(wordle_compare(guess));
                current += 1;
                Log.d(tag,"current:" + current);
                if (current >= tv.length) {
                    // you lost
                    for (TextView mytv: tv) { mytv.setText("_____"); }
                    // for (int i = 0; i < tv.length; i++) { tv[i].setText(""); }
                    current = 0;
                    Toast.makeText(MainActivity.this, "You lost!", Toast.LENGTH_SHORT).show();
                }
                // https://stackoverflow.com/questions/1109022/how-can-i-close-hide-the-android-soft-keyboard-programmatically
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                return true;
            }
        });

        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) return;
                et.setText("");
            }
        });
    }
}