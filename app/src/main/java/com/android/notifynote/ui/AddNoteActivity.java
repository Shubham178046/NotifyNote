package com.android.notifynote.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatActivity;

import com.android.notifynote.R;
import com.android.notifynote.data.DatabaseHelper;
import com.android.notifynote.model.Alarm;
import com.android.notifynote.service.AlarmReceiver;
import com.android.notifynote.service.LoadAlarmsService;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Date;

public class AddNoteActivity extends AppCompatActivity {
    public static final String ALARM_EXTRA = "alarm_extra";
    public static final String MODE_EXTRA = "mode_extra";
    private EditText titleInput;
    private TextView select_time, select_date;
    private EditText textInput;
    Alarm alarmData;
    final Calendar myCalendar = Calendar.getInstance();
    Date dates;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({EDIT_ALARM, ADD_ALARM, UNKNOWN})
    @interface Mode {
    }

    public static final int EDIT_ALARM = 1;
    public static final int ADD_ALARM = 2;
    public static final int UNKNOWN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Alarm alarm = getAlarm();
        this.titleInput = (EditText) findViewById(R.id.note_title_input);
        this.textInput = (EditText) findViewById(R.id.note_text_input);
        select_time = (TextView) findViewById(R.id.select_time);
        select_date = (TextView) findViewById(R.id.select_date);
        if (alarm != null) {
            if (alarm.getLabel() != null) {
                titleInput.setText(alarm.getLabel());
            }
            if (alarm.getDescription() != null) {
                textInput.setText(alarm.getDescription());
            }
            if (alarm.getDate() != null) {
                select_date.setText(alarm.getDate());
            }
            if (alarm.getTimeString() != null) {
                select_time.setText(alarm.getTimeString());
            }
        }


        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(view);
            }
        });

        select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(AddNoteActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                myCalendar.set(Calendar.MINUTE, minute);
                                myCalendar.set(Calendar.SECOND, 0);
                                dates = myCalendar.getTime();
                                select_time.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
    }

    private Alarm getAlarm() {
        switch (getMode()) {
            case EDIT_ALARM:
                alarmData = getIntent().getParcelableExtra(ALARM_EXTRA);
                return getIntent().getParcelableExtra(ALARM_EXTRA);
            case ADD_ALARM:
                final long id = DatabaseHelper.getInstance(this).addAlarm();
                LoadAlarmsService.launchLoadAlarmsService(this);
                alarmData = new Alarm(id);
                return new Alarm(id);
            case UNKNOWN:
            default:
                throw new IllegalStateException("Mode supplied as intent extra for " +
                        AddNoteActivity.class.getSimpleName() + " must match value in " +
                        Mode.class.getSimpleName());
        }
    }

    private @Mode
    int getMode() {
        final @Mode int mode = getIntent().getIntExtra(MODE_EXTRA, UNKNOWN);
        return mode;
    }

    public static Intent buildAddEditAlarmActivityIntent(Context context, @Mode int mode, Alarm alarm) {
        final Intent i = new Intent(context, AddNoteActivity.class);
        i.putExtra(ALARM_EXTRA, alarm);
        i.putExtra(MODE_EXTRA, mode);
        return i;
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            select_date.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
            //updateLabel();
        }
    };

    public void setDate(View view) {
        new DatePickerDialog(
                this, date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void updateLabel() {

        //   scheduleNotification(getNotification( btnDate .getText().toString()) , date.getTime()) ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                if (select_date.getText().equals("Select Date")) {
                    Toast.makeText(this, "Please Select Specific Date", Toast.LENGTH_SHORT).show();
                }
                else if (select_time.getText().equals("Select Time")) {
                    Toast.makeText(this, "Please Select Specific Time", Toast.LENGTH_SHORT).show();
                } else {
                    final Alarm alarm;
                    if (alarmData != null) {
                        alarm = alarmData;
                    } else {
                        alarm = getAlarm();
                    }
                    String title = titleInput.getText().toString();
                    String text = textInput.getText().toString();
                    alarm.setTime(dates.getTime());
                    alarm.setLabel(title);
                    alarm.setDescription(text);
                    alarm.setDate(select_date.toString());
                    alarm.setTimeString(select_time.toString());
                    final int rowsUpdated = DatabaseHelper.getInstance(this).updateAlarm(alarm);
                    final int messageId = (rowsUpdated == 1) ? R.string.update_complete : R.string.update_failed;

                    Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();

                    AlarmReceiver.setReminderAlarm(this, alarm);
                    finish();
                }
                break;
        }

        return true;
    }
}
