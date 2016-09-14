package com.example.harley.vanselow_habittracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

public class HabitDetails extends AppCompatActivity {
    private Habit habit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_details);
        String habitClicked = getIntent().getStringExtra(MainActivity.HABIT);
        readHabitProvided(habitClicked);
        setTitle(habit.getName());
        setCompletionCount();
        setMissedCount();
        loadCompletionRecords();
    }

    private void setMissedCount() {
        Calendar creationDate = Calendar.getInstance();
        creationDate.setTime(habit.getCreation());
        for(Date checkDate =creationDate.getTime();checkDate.before(new Date());creationDate.add(Calendar.DATE,1)){
            boolean isCheckableDay = habit.getDays().contains(creationDate.get(Calendar.DAY_OF_WEEK));
            if(isCheckableDay){
                for(Date completionNoted:habit.getCompletionRecord()){
                    
                }
            }
        }
    }

    private void setCompletionCount() {
        TextView missedCountView = (TextView)findViewById(R.id.completionCount);
        missedCountView.setText(habit.getCompletionRecord().size());
    }

    private void readHabitProvided(String habitClicked) {
        Gson gson = new Gson();
        habit = gson.fromJson(habitClicked,Habit.class);
    }

    private void loadCompletionRecords() {
        LinearLayout habitCompletionList = (LinearLayout)findViewById(R.id.completionList);
        habitCompletionList.removeAllViews();
        List<Date> completionRecordList = habit.getCompletionRecord();
        for(Date completion: completionRecordList){
            getLayoutInflater().inflate(R.layout.habit_completion_record,habitCompletionList);
            RelativeLayout completionRecordContainer = (RelativeLayout) findViewById(R.id.newCompletionRecord);
            completionRecordContainer.setId(completionRecordList.indexOf(completion));
            completionRecordContainer.setTag(completion);

            TextView completionRecord = (TextView)completionRecordContainer.getChildAt(0);

            completionRecord.setText(formatDate(completion));

        }
    }

    private String formatDate(Date completion) {
        SimpleDateFormat format = new SimpleDateFormat("MMM d, HH:mm");
        return format.format(completion);
    }


    public void deleteHabit(View view) {
        try{HabitIO.deleteHabit(habit,this);}
        catch (NoSuchElementException e){
            Toast.makeText(HabitDetails.this, "Habit not found!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void deleteRecord(View view) {
        Date completionToDelete = (Date)((View)view.getParent()).getTag();
        habit.getCompletionRecord().remove(completionToDelete);
        HabitIO.saveHabitToFile(habit,this);
        loadCompletionRecords();
    }
}
