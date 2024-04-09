package com.example.mywordle;

import static java.lang.Math.random;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

    public static String tag = "mywordle";
    TextView[] tv = new TextView[6];
    int current = 0;
    int correct = 0;
    EditText et;
    String myword = "SPICE";
    InputMethodManager imm;

    String[] read_wordlist() {
        // from https://stackoverflow.com/a/73633910
        Stream<String> text = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.wordlewords))).lines();
        String[] words = text.toArray(String[]::new);
        return words;
    }

    Spannable wordle_compare(String guess) {
        // https://stackoverflow.com/a/53573169/838719
        Spannable result = new SpannableString(guess);
        correct = 0;
        for (int i = 0; i < myword.length(); i++) {
            //Log.d(tag,"target: "+myword.charAt(i)+" guess: "+guess.charAt(i));
            if (myword.charAt(i) == guess.charAt(i)) {
                // correct character, correct position
                result.setSpan(new BackgroundColorSpan(0xFF00FF00), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                correct += 1;
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

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        String[] mywords = read_wordlist();
        long randnum = Math.round(Math.random() * mywords.length);
        myword = mywords[(int) randnum].toUpperCase();
        Log.d(tag,"number: "+Long.toString(randnum)+" word: "+myword);

        tv[0] = findViewById(R.id.textView1);
        tv[1] = findViewById(R.id.textView2);
        tv[2] = findViewById(R.id.textView3);
        tv[3] = findViewById(R.id.textView4);
        tv[4] = findViewById(R.id.textView5);
        tv[5] = findViewById(R.id.textView6);

        et = findViewById(R.id.editTextText);
        et.setOnEditorActionListener((textView, i, keyEvent) -> {
            //Log.d(tag,"i: "+i+" event: "+keyEvent);

            // only act on DOWN key events
            if (keyEvent != null && keyEvent.getAction() != KeyEvent.ACTION_DOWN) return true;

            // get guessed string in CAPS
            String guess = textView.getText().toString().toUpperCase();
            Log.d(tag,"entered text: "+guess);

            // guess is exactly 5 characters?
            if (guess.length() != 5) {
                // if not: inform the user and clear the text field
                Toast.makeText(MainActivity.this, "Enter exactly 5 characters", Toast.LENGTH_SHORT).show();
                et.setText("");
                return true;
            }

            tv[current].setText(wordle_compare(guess));

            // TODO: leave the correct guess visible
            if (correct == 5) { reset("You won!"); return true; }
            current += 1;
            //Log.d(tag,"current:" + current);

            if (current >= tv.length) { reset("You lost!"); }

            // https://stackoverflow.com/questions/1109022/how-can-i-close-hide-the-android-soft-keyboard-programmatically
            imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
            return true;
        });

        et.setOnFocusChangeListener((view, b) -> {
            if (!b) return;
            et.setText("");
        });
    }

    private void reset(String message) {
        for (TextView mytv: tv) { mytv.setText("_____"); }
        current = 0;
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        et.setText("");
    }
}