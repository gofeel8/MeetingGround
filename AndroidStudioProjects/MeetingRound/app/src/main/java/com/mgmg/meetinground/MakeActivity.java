package com.mgmg.meetinground;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MakeActivity extends AppCompatActivity {

    int y, m, d, hrs, min;
    EditText etName;
    TextView tvDate;
    Button btnMakeRoom;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    TimePickerDialog.OnTimeSetListener onTimeSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make);

        Calendar calendar = Calendar.getInstance();
        y = calendar.get(Calendar.YEAR);
        m = calendar.get(Calendar.MONTH);
        d = calendar.get(Calendar.DATE);
        hrs = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);

        etName = findViewById(R.id.etRoomName);
        tvDate = findViewById(R.id.tvDate);
        btnMakeRoom = findViewById(R.id.btnMakeRoom);
        tvDate.setText(y + "년 " + (m+1) + "월 " + d + "일 " + hrs + "시 " + min + "분");

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                y = year;
                m = month;
                d = dayOfMonth;
                tvDate.setText(y + "년 " + (m+1) + "월 " + d + "일 " + hrs + "시 " + min + "분");
            }
        };

        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hrs = hourOfDay;
                min = minute;
                tvDate.setText(y + "년 " + (m+1) + "월 " + d + "일 " + hrs + "시 " + min + "분");
            }
        };

        Intent intent = getIntent();
        etName.setText(intent.getStringExtra("name") + "의 모임");
        etName.requestFocus();



        btnMakeRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(y, m, d, hrs, min,0);

                Intent intent = new Intent();
                intent.putExtra("roomName", etName.getText().toString());
                intent.putExtra("timestamp", calendar.getTimeInMillis());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public void setDate(View view) {
        new DatePickerDialog(this, R.style.DialogTheme,onDateSetListener, y, m, d).show();
    }

    public void setTime(View view) {
        new TimePickerDialog(this,R.style.DialogTheme, onTimeSetListener, hrs, min, true).show();
    }
}