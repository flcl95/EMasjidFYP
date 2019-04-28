package com.fyp.emasjid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ContactOrganizerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_organizer);
        //set actionbar title
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Contact");
    }
}
