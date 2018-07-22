package com.example.nagi.studyfocus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

public class MainActivity extends AppCompatActivity {
    protected BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chart = (BarChart) findViewById(R.id.chart1);

        //get data to display
        BarData data = new BarData(getBarData());
        chart.setData(data);

        //Y axis
        YAxis left = chart.getAxisLeft();
        left.setAxisMinimum(0);
        left.setAxisMaximum(100);
        left.setLabelCount(5);
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

        //X axis
        XAxis xAxis = chart.getXAxis();
        //X軸に表示するLabelのリスト(最初の""は原点の位置)
        final String[] labels = {"","Day1", "Day2", "Day3"};
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
   //表示させるデータ
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, 60));
        entries.add(new BarEntry(2, 80));
        entries.add(new BarEntry(3, 70));
        List<IBarDataSet> bars = new ArrayList<>();
        BarDataSet dataSet = new BarDataSet(entries, "bar");

        //整数で表示
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return "" + (int) value;
            }
        });
        //ハイライトさせない
        dataSet.setHighlightEnabled(false);

        //Barの色をセット
        dataSet.setColors(new int[]{R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark}, this);
        bars.add(dataSet);

        return bars;
    }

}
