package com.medi.marcin.medicalendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static com.medi.marcin.medicalendar.FeedReaderContract.listReminders;
import static com.medi.marcin.medicalendar.FeedReaderContract.getReminderInfo;

/**
 * Created by marcin on 21.05.17.
 */

public class RemindersListActivity extends AppCompatActivity {

    public static final String REMINDER_ID = "medi.REMINDER_ID";

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        this.username = intent.getStringExtra(MainActivity.PROFILENAME_MESSAGE);

        final ListView profilesList = (ListView)findViewById(R.id.reminders_list);

        final List remindersIDs = listReminders(getApplicationContext(), this.username);
        List remindersNames = new ArrayList<>();
        for (int i=0; i<remindersIDs.size(); i++){
            Hashtable<String, String> reminderData = new Hashtable<>();
            String reminderID = remindersIDs.get(i).toString();
            reminderData = getReminderInfo(getApplicationContext(), reminderID);
            String reminderName = reminderData.get("title") + "\n"
                    + reminderData.get("comment") + "\n"
                    + reminderData.get("date") + " " + reminderData.get("time") + "\n";
            remindersNames.add(reminderName);
        }

        ArrayAdapter<String> profilesListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, remindersNames);

        profilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                String reminderID = (String) remindersIDs.get(position);
                // go to reminder
                goToReminderView(reminderID);
            }
        });
        profilesList.setAdapter(profilesListAdapter);
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

    public void goToReminderView(String id){
        Intent intent = new Intent(this, ReminderActivity.class);
        intent.putExtra(MainActivity.PROFILENAME_MESSAGE, this.username);
        intent.putExtra(this.REMINDER_ID, id);
        startActivity(intent);
    }

}
