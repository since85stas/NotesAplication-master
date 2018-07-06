package com.batura.stas.notesaplication.AlarmFuncs;

import android.app.AlarmManager;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.batura.stas.notesaplication.R;

import java.util.Calendar;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

/**
 * Created by seeyo on 03.07.2018.
 */

public class AlarmSetActivity extends AppCompatActivity {

    Context mContext ;


    public final static String NOTE_BODY =  AlarmSetActivity.class.getPackage() + ".NOTE_BODY";

    public final static String NOTIF_IS_ON =  AlarmSetActivity.class.getPackage() + ".NOTIF_IS_ON";

    String notificationText;

    final static int RQS_TIME = 1;
    private TextView mTimeTextView;
    private TextView mDateTextView;

    private Calendar calSet;

    // Слушатель выбора времени
    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

//            if (calSet.compareTo(calNow) <= 0) {
//                // Если выбранное время на сегодня прошло,
//                // то переносим на завтра
//                calSet.add(Calendar.DATE, 1);
//            }
//            setAlarm(calSet);
        }
    };


    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calSet.set(Calendar.YEAR, year);
            calSet.set(Calendar.MONTH, month);
            calSet.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notificationText = getIntent().getExtras().getString(NOTE_BODY); // получаем текст для напоминания

        Calendar calNow = Calendar.getInstance();
        calSet = (Calendar) calNow.clone();

        setContentView(R.layout.activity_alarm);

        mTimeTextView = (TextView) findViewById(R.id.textViewSetTime);

        Button openTimeDialogButton = (Button) findViewById(R.id.buttonShowTimePickDialog);
        openTimeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimeTextView.setText("");
                openTimePickerDialog(true);
            }
        });

        mDateTextView = (TextView) findViewById(R.id.textViewSetDate);

        Button openDateDialogButton = (Button) findViewById(R.id.buttonShowDatePickDialog);
        openDateDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDateTextView.setText("");
                openDatePickerDialog();
            }
        });

        Button setButton =(Button)findViewById(R.id.buttonSetAlarm);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent answIntent = new Intent();

                answIntent.putExtra(NOTIF_IS_ON, true);
                setResult(RESULT_OK, answIntent); // отвечаем что напоминание установлено

                setAlarm(calSet);

                finish();
            }
        });

        // Кнопка отмены сигнализации
        Button cancelButton = (Button) findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                cancelAlarm();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Вызываем диалоговое окно выбора времени
    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), is24r);
        timePickerDialog.setTitle(R.string.set_time);
        timePickerDialog.show();
    }

    private void openDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_WEEK));
        datePickerDialog.setTitle(R.string.set_date);
        datePickerDialog.show();
    }

    private void setAlarm(Calendar targetCal) {
        mTimeTextView.setText(R.string.alarm_on);
        mTimeTextView.append(String.valueOf(targetCal.getTime()));

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.NOTIF_TEXT,notificationText);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), RQS_TIME, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
                pendingIntent);
    }

    private void cancelAlarm() {
        mTimeTextView.setText(R.string.cancel_alarm);

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), RQS_TIME, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
