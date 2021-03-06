package com.medi.marcin.medicalendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Hashtable;
import java.util.List;

import static com.medi.marcin.medicalendar.FeedReaderContract.deleteProfileInDatabase;
import static com.medi.marcin.medicalendar.FeedReaderContract.getReminderInfo;
import static com.medi.marcin.medicalendar.FeedReaderContract.addReminder;
import static com.medi.marcin.medicalendar.FeedReaderContract.updateReminder;
import static com.medi.marcin.medicalendar.FeedReaderContract.deleteReminder;
/**
 * Created by marcin on 21.05.17.
 */

public class ReminderActivity extends AppCompatActivity {
    String username;
    String reminderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();

        this.username = intent.getStringExtra(
                MainActivity.PROFILENAME_MESSAGE
        );
        ((TextView)findViewById(R.id.label_profile_name)).setText(username);
        this.reminderId = intent.getStringExtra(
                RemindersListActivity.REMINDER_ID
        );
        if(this.reminderId != null){
            Hashtable<String, String> reminderInfo = getReminderInfo(
                    getApplicationContext(), this.reminderId);

            ((EditText)findViewById(R.id.txt_entry_title)).setText(reminderInfo.get("title"));
            ((TextView)findViewById(R.id.txt_entry_date)).setText(reminderInfo.get("date"));
            ((TextView)findViewById(R.id.txt_entry_time)).setText(reminderInfo.get("time"));
            ((EditText)findViewById(R.id.txt_entry_comment)).setText(reminderInfo.get("comment"));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveReminder(View view){
        String title = ((EditText)findViewById(R.id.txt_entry_title)).getText().toString();
        String date = ((TextView)findViewById(R.id.txt_entry_date)).getText().toString();
        String time = ((TextView)findViewById(R.id.txt_entry_time)).getText().toString();
        String comment = ((EditText)findViewById(R.id.txt_entry_comment)).getText().toString();
        if((title.isEmpty()) || (date.isEmpty()) || (time.isEmpty())) {
            ((TextView)findViewById(R.id.txt_alert)).setText("Fill all fields");
            return;
        }
        if(this.reminderId != null){
            updateReminder(getApplicationContext(), comment, date, time, title, this.reminderId);
        }else{
            addReminder(getApplicationContext(), comment, date, time, title, this.username);
        }


        ((TextView) findViewById(R.id.status)).setText("Successfully added");
        Intent intent = new Intent(this, RemindersListActivity.class);
        intent.putExtra(MainActivity.PROFILENAME_MESSAGE, this.username);
        startActivity(intent);
    }

    public void showTimePickerDialog(View v){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(),"TimePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(
                ((TextView)findViewById(R.id.txt_entry_date))
        );
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void handleDeleteReminder(View view){
        final Intent intent = getIntent();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ReminderActivity.this);
        builder1.setMessage("Are you sure?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String reminderId = intent.getStringExtra(
                                RemindersListActivity.REMINDER_ID
                        );
                        deleteReminder(getApplicationContext(), reminderId);
                        goToRemindersList();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    public void goToRemindersList(){
        Intent intent = new Intent(this, RemindersListActivity.class);
        intent.putExtra(MainActivity.PROFILENAME_MESSAGE, this.username);
        startActivity(intent);
    }
}
