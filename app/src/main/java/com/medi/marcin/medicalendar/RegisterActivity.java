package com.medi.marcin.medicalendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import static com.medi.marcin.medicalendar.FeedReaderContract.addProfile;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
    public void onRegisterClick(View view){
        String firstName = ((EditText)findViewById(R.id.txt_first_name)).getText().toString();
        String lastName = ((EditText)findViewById(R.id.txt_last_name)).getText().toString();
        String mobilePhone = ((EditText)findViewById(R.id.txt_mobile_phone)).getText().toString();
        String dateOfBirth = ((TextView)findViewById(R.id.txt_date_of_birth)).getText().toString();
        addProfile(getApplicationContext(), firstName, lastName, mobilePhone, dateOfBirth);
        Intent successfullyAddedProfile = new Intent(this, MainActivity.class);
        startActivity(successfullyAddedProfile);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(
                ((TextView)findViewById(R.id.txt_date_of_birth))
        );
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}
