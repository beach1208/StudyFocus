package com.example.nagi.studyfocus;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView timerView;
    private Button setButton;
    private Button startButton;
    private Button pauseButton;
    private Button resetButton;

    private CountDownTimer countDownTimer;

    private boolean mTimerRunning;

    private long startTimeInMillis;
    private long timeLeftInMills;
    private long endTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        timerView = findViewById(R.id.timerView);
        setButton = findViewById(R.id.setButton);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.stopButton);
        resetButton = findViewById(R.id.resetButton);




        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.time_pick)
                        .items(R.array.time)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                switch (which){
                                    case 0:
                                        which = 10;
                                        break;
                                    case 1:
                                        which = 20;
                                        break;
                                    case 2:
                                        which = 30;
                                        break;
                                    case 3:
                                        which = 40;
                                        break;
                                    case 4:
                                        which = 50;
                                        break;
                                    case 5:
                                        which = 60;
                                        break;
                                    default:
                                        break;
                                }
                                String input = Integer.toString(which);
                                long millsInput = Long.parseLong(input) * 60000;
                                setTime(millsInput);
                                return true;
                            }
                        })
                        .positiveText(R.string.okbutton)
                        .show();
                

            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning){
                    pauseTimer();
                }

            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

    }


    private void setTime(long milliseconds) {
        startTimeInMillis = milliseconds;
        resetTimer();
        closeKeyboard();
    }




    private void startTimer() {
        endTime = System.currentTimeMillis() + timeLeftInMills;

        countDownTimer = new CountDownTimer(timeLeftInMills,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMills = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                updateCountDownText();
            }
        }.start();

        mTimerRunning = true;
//        updateWatchInterface();
    }


    private void pauseTimer() {
        countDownTimer.cancel();
        mTimerRunning = false;
//        updateWatchInterface();
    }



    private void resetTimer() {
        timeLeftInMills = startTimeInMillis;
        updateCountDownText();
//        updateWatchInterface();
    }



    private void updateCountDownText() {
        int hours = (int)(timeLeftInMills / 1000 / 3600);
        int minutes = (int)((timeLeftInMills / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMills / 1000) % 60;

        String timeLeftFormatted;
        if (hours > 0){
            timeLeftFormatted = String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes,seconds);
        }else {
            timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        }

        timerView.setText(timeLeftFormatted);

    }


//    private void updateWatchInterface() {
//    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}
