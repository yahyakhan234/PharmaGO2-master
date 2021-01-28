package com.fyp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class customer_book_test extends AppCompatActivity {

    public static final String SELECTED_DATE_TEXT="Selected Date: ";
    public static final String SELECTED_TIME_TEXT="Selected Time: ";
    public static final String TIMESTAMP_KEY="timestamp";
    public static final String TEST_NAME_KEY="name";
    public static final String TEST_DETAILS_KEY="details";
    public static final String SIDENOTE_KEY="side_note";

    private Map<String,Object> order_map;
    String date,time,sdtf,testID,testName,testDetails;
    MaterialButton proceed,dateButton,timeButton;
    TextView dateTextView,timeTextView;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    TextInputLayout sidenotes;
    ProgressDialog wait;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_book_test);
        testID=getIntent().getStringExtra("testID");
        proceed=findViewById(R.id.search_deliverer);
        dateButton=findViewById(R.id.date_button);
        timeButton=findViewById(R.id.time_button);
        dateTextView=findViewById(R.id.date_text_view);
        timeTextView=findViewById(R.id.time_text_view);
        sidenotes=findViewById(R.id.side_note);
        db=FirebaseFirestore.getInstance();
        CalendarConstraints.Builder calendarConstraintBuilder = new CalendarConstraints.Builder();
        calendarConstraintBuilder.setValidator(DateValidatorPointForward.now());
        mAuth=FirebaseAuth.getInstance();
        inflateDetails();

        order_map=new HashMap<>();
        final MaterialDatePicker.Builder materialDatePickerBuilder = MaterialDatePicker.Builder.datePicker();
        materialDatePickerBuilder.setTitleText("SELECT A DATE");
        materialDatePickerBuilder.setCalendarConstraints(calendarConstraintBuilder.build());
        final MaterialDatePicker materialDatePicker = materialDatePickerBuilder.build();

        timeButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);

                new TimePickerDialog(customer_book_test.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                time=sHour + "/" + sMinute;
                                timeTextView.setText(SELECTED_TIME_TEXT + sHour+":"+sMinute);
                            }
                        }, hour, minutes, false)
                    .show();

            }
        });
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        // now update the selected date preview
                        date=materialDatePicker.getHeaderText();
                        dateTextView.setText(SELECTED_DATE_TEXT +date );
                    }
                });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdtf=date+"/"+time;
                wait=ProgressDialog.show(customer_book_test.this,"Processing","Please wait");
                long ctime=System.currentTimeMillis();
                long ftime=milliseconds(sdtf);
                long hourCheckTime=milliseconds(sdtf);
                if ((System.currentTimeMillis()+3600000)>hourCheckTime){
                    wait.dismiss();
                    Toast.makeText(customer_book_test.this,"Please Select a date and time one hour in future",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    order_map.put(lab_price_order.UID_KEY, Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                    order_map.put(lab_price_order.DATE_REQUESTED_KEY,date);
                    order_map.put(lab_price_order.TIME_REQUESTED_KEY,time);
                    order_map.put(lab_price_order.UEMAIL_KEY,mAuth.getCurrentUser().getEmail());
                    order_map.put(SIDENOTE_KEY,sidenotes.getEditText().getText().toString().trim());
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String dateString = formatter.format(new Date(System.currentTimeMillis()));
                    order_map.put(TIMESTAMP_KEY,dateString);
                    order_map.put(lab_price_order.TEST_TYPE_NAME_KEY,testName);
                    order_map.put(lab_price_order.TEST_TYPE_KEY,testID);
                    db.collection("orders_lab").document(mAuth.getCurrentUser().getEmail()).set(order_map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    wait.dismiss();
                                    new AlertDialog.Builder(customer_book_test.this)
                                    .setIcon(R.drawable.logo_splash)
                                    .setTitle("Done!")
                                    .setMessage("Your Order Has been placed, you will be informed when your order is accepted")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .show();
                                }
                            });
                    Toast.makeText(customer_book_test.this,"good",Toast.LENGTH_SHORT).show();
                }
             //   startActivity(new Intent(customer_book_test.this, searching_lab.class));
               // finish();
            }

        });

    }
    private void inflateDetails(){
        db.collection("available_lab_tests").document(testID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
            testName=documentSnapshot.getString(TEST_NAME_KEY);
            testDetails=documentSnapshot.getString(TEST_DETAILS_KEY);

                TextView textView=findViewById(R.id.test_type);
                textView.setText(testName);
                textView=findViewById(R.id.test_details);
                textView.setText(testDetails);
            }
        });
    }
    public long milliseconds(String date)
    {
        //String date_ = date;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy/kk/mm");
        try
        {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
            return timeInMilliseconds;
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }
}