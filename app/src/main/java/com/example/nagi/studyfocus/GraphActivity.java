package com.example.nagi.studyfocus;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

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
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class GraphActivity extends AppCompatActivity {

    protected BarChart chart;
private RealmResults<Day> days;


    // Table
    // SELECT * FROM time WHERE end_date = ?
    // add all timeData for a certain day
    // display

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);



        Intent intent = getIntent();
        String main_intent = intent.getStringExtra("main");



        chart = (BarChart) findViewById(R.id.chart1);

        //get data to display
        BarData data = new BarData(getBarData());
        chart.setData(data);

        //Y axis
        YAxis left = chart.getAxisLeft();
        left.setAxisMinimum(0);
        left.setAxisMaximum(200);
        left.setLabelCount(6);
        left.setDrawTopYLabelEntry(true);
        //Convert to integer
        left.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "" + (int)value;
            }
        });

        //Y axis right side
        YAxis right = chart.getAxisRight();
        right.setDrawLabels(false);
        right.setDrawGridLines(false);
        right.setDrawZeroLine(true);
        right.setDrawTopYLabelEntry(true);

        // Realm
        Day daysGetter = new Day();
        days = daysGetter.getDays(this);

        //X axis
        XAxis xAxis = chart.getXAxis();
        //X軸に表示するLabelのリスト(最初の""は原点の位置)
        ArrayList<String> labels = new ArrayList<>();
        labels.add("");
        for (int i = 0; i < days.size(); i++) {
            Day day = days.get(i);

            labels.add(day.getDayName());
        }
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        XAxis bottomAxis = chart.getXAxis();
        bottomAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        bottomAxis.setDrawLabels(true);
        bottomAxis.setDrawGridLines(false);
        bottomAxis.setDrawAxisLine(true);

        //グラフ上の表示
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setClickable(false);

        //凡例
        chart.getLegend().setEnabled(false);

        chart.setScaleEnabled(false);
        //アニメーション
        chart.animateY(1200, Easing.EasingOption.Linear);
    }

    //棒グラフのデータを取得
    private List<IBarDataSet> getBarData(){

        Day timeDataGetter = new Day();
        days = timeDataGetter.getDays(this);

        //表示させるデータ
        ArrayList<BarEntry> entries = new ArrayList<>();
        int count = 1;
        for (int i = 0; i < days.size(); i++){
            Day day = days.get(i);
            entries.add(new BarEntry(count, day.getTimeData()));
            count++;
        }

        List<IBarDataSet> bars = new ArrayList<>();
        BarDataSet dataSet = new BarDataSet(entries, "bar");

        //整数で表示
//        dataSet.setValueFormatter(new IValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
//                return "" + (int) value;
//            }
//        });
        //ハイライトさせない
        dataSet.setHighlightEnabled(false);

        //Barの色をセット
        dataSet.setColors(new int[]{R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark}, this);
        bars.add(dataSet);

        return bars;
    }
}
