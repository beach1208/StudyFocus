package com.example.nagi.studyfocus;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private TextView timerView;
    private Button setButton;
    private Button startButton;
    private Button pauseButton;
    private Button resetButton;
    private Button checkResultBtn;
    private Switch switchButton;

    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;


    private boolean mTimerRunning;
    private long startTimeInMillis;
    private long timeLeftInMills;
    private long endTime;
    private long millsInput;
    private String timeInput;
    private Integer timeData;

    private RealmResults<Day> days;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        timerView = findViewById(R.id.timerView);
        setButton = findViewById(R.id.setButton);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.stopButton);
        resetButton = findViewById(R.id.resetButton);
        checkResultBtn = findViewById(R.id.checkGraphBtn);
        switchButton = findViewById(R.id.switchbtn);


        switchButton.setChecked(false);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.relaxmusic);
                    mediaPlayer.start();
                    Toast.makeText(MainActivity.this, "MUSIC ON", Toast.LENGTH_SHORT).show();
                } else {
                    mediaPlayer.stop();
                    Toast.makeText(MainActivity.this, "MUSIC OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });



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
                                        which = 40;
                                        break;
                                    case 3:
                                        which = 60;
                                        break;
                                    default:
                                        break;
                                }
                                timeData = which;
                                timeInput = Integer.toString(which);
                                millsInput = Long.parseLong(timeInput) * 60000;
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

        checkResultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,GraphActivity.class);
                intent.putExtra("main","test");
                startActivity(intent);

            }

        });

        Day daysGetter = new Day();
        days = daysGetter.getDays(this);

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
                Date date = new Date();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String formatedDate = sdf.format(date);

                Realm.init(MainActivity.this);
                Realm realm = Realm.getDefaultInstance();

                if (realm.where(Day.class).contains("dayName", formatedDate).count() == 0) {
                    Day day = new Day(formatedDate);
                    day.setTimeData(timeData);
                    realm.beginTransaction();
                    realm.copyToRealm(day);
                    realm.commitTransaction();

                } else {
                    realm.beginTransaction();
                    Day dayInDB = realm.where(Day.class).contains("dayName", formatedDate).findFirst();
                    int oldTime = dayInDB.getTimeData();
                    dayInDB.setTimeData(oldTime+timeData);
                    realm.commitTransaction();
                }


                updateCountDownText();
            }
        }.start();

        mTimerRunning = true;
    }




    private void pauseTimer() {
        countDownTimer.cancel();
        mTimerRunning = false;
    }



    private void resetTimer() {
        timeLeftInMills = startTimeInMillis;
        updateCountDownText();
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



    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }





}
