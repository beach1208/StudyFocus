package com.example.nagi.studyfocus;

import android.content.Context;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Day extends RealmObject {
    @PrimaryKey
    @Required
    private String id;

    private String dayName;
    private int timeData;

    public Day() {

    }

    public Day(String dayName) {
        this.dayName = dayName;

        id = UUID.randomUUID().toString();
        timeData = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public int getTimeData() {
        return timeData;
    }

    public void setTimeData(int timeData) {
        this.timeData = timeData;
    }

    public RealmResults<Day> getDays(Context context) {
        // Open Realm database
        Realm.init(context);

        //
        Realm realm = Realm.getDefaultInstance();

        return realm.where(Day.class).findAll();
    }
}
