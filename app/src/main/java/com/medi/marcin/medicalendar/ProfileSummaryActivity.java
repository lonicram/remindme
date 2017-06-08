package com.medi.marcin.medicalendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Hashtable;

import static com.medi.marcin.medicalendar.FeedReaderContract.getUserInfo;
import static com.medi.marcin.medicalendar.FeedReaderContract.updateProfile;
import static com.medi.marcin.medicalendar.FeedReaderContract.deleteProfileInDatabase;


/**
 * Created by marcin on 21.05.17.
 */

public class ProfileSummaryActivity extends AppCompatActivity {
    public String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        this.username = intent.getStringExtra(MainActivity.PROFILENAME_MESSAGE);
        Hashtable<String, String> userData = getUserInfo(getApplicationContext(), this.username);
        System.out.println(userData);
        // fill fields with user profile data
        ((EditText)findViewById(R.id.txt_first_name)).setText(userData.get("firstName"));
        ((EditText)findViewById(R.id.txt_last_name)).setText(userData.get("lastName"));
        ((EditText)findViewById(R.id.txt_mobile_phone)).setText(userData.get("mobilePhone"));
        ((TextView)findViewById(R.id.txt_date_of_birth)).setText(userData.get("dateOfBirth"));

        // hide original button and show new one
        findViewById(R.id.btn_save_profile).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_reminders).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_update_profile).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_delete_profile).setVisibility(View.VISIBLE);
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

    /**
     * Handle click event, when user adds a profile
     * @param view
     */
    public void onEditClick(View view){

        String firstName = ((EditText)findViewById(R.id.txt_first_name)).getText().toString();
        String lastName = ((EditText)findViewById(R.id.txt_last_name)).getText().toString();
        String mobilePhone = ((EditText)findViewById(R.id.txt_mobile_phone)).getText().toString();
        String dateOfBirth = ((TextView)findViewById(R.id.txt_date_of_birth)).getText().toString();
        updateProfile(
                getApplicationContext(), this.username, firstName, lastName, dateOfBirth, mobilePhone);
        goToMain();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(
                ((TextView) findViewById(R.id.txt_date_of_birth))
        );
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void goToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToReminders(View view){
        Intent intent = new Intent(this, RemindersListActivity.class);
        intent.putExtra(MainActivity.PROFILENAME_MESSAGE, this.username);
        startActivity(intent);
    }

    public void deleteProfile(View view){
        final Intent intent = getIntent();
        final String username = intent.getStringExtra(MainActivity.PROFILENAME_MESSAGE);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileSummaryActivity.this);
        builder1.setMessage("Are you sure?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteProfileInDatabase(getApplicationContext(), username);
                        goToMain();
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
}
