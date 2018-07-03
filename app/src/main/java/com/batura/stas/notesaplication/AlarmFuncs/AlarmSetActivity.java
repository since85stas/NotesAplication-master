package com.batura.stas.notesaplication.AlarmFuncs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.batura.stas.notesaplication.R;

import java.util.Calendar;

/**
 * Created by seeyo on 03.07.2018.
 */

public class AlarmSetActivity extends AppCompatActivity {

    final static int RQS_TIME = 1;
    private TextView mTimeTextView;

    // Слушатель выбора времени
    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                // Если выбранное время на сегодня прошло,
                // то переносим на завтра
                calSet.add(Calendar.DATE, 1);
            }
            setAlarm(calSet);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarm);

        mTimeTextView = (TextView) findViewById(R.id.textViewAlarmPrompt);

        Button openTimeDialogButton = (Button) findViewById(R.id.butttonShowDialog);
        openTimeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimeTextView.setText("");
                openTimePickerDialog(true);
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

    // Вызываем диалоговое окно выбора времени
    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), is24r);
        timePickerDialog.setTitle("Set time");
        timePickerDialog.show();
    }

    private void setAlarm(Calendar targetCal) {
        mTimeTextView.setText("Alarm is on ");
        mTimeTextView.append(String.valueOf(targetCal.getTime()));

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), RQS_TIME, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
                pendingIntent);
    }

    private void cancelAlarm() {
        mTimeTextView.setText("Сигнализация отменена!");

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), RQS_TIME, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
