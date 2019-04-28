package com.fyp.emasjid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactOrganizerActivity extends AppCompatActivity {

    private DatabaseReference ref;
    private String eventId;
    private String name, email, message;
    private EditText uName, uEmail, uMessage;
    private TextInputLayout lName, lEmail, lMessage;
    private Button cSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_organizer);
        //set actionbar title
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Contact");

        eventId = getIntent().getExtras().get("eventId").toString();
        ref = FirebaseDatabase.getInstance().getReference().child("Message").child(eventId).push();

        initializeView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        cSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //create regex for name with no number
                String noNum = "(.)*(\\d)(.)*";
                Pattern noNumPattern = Pattern.compile(noNum);
                Matcher noNumMatcher = noNumPattern.matcher(uName.getText().toString());
                boolean noNumIsMatched = noNumMatcher.matches();

                //regex for valid email
                String validEmail = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
                Pattern validEmailPattern = Pattern.compile(validEmail,Pattern.CASE_INSENSITIVE);
                Matcher validEmailMatcher = validEmailPattern.matcher(uEmail.getText().toString());
                boolean validEmailIsMatced = validEmailMatcher.matches();

                Boolean isValid = true;

                //check name input
                if(uName.getText().toString().isEmpty()){
                    lName.setError("This field cannot be empty!");
                    isValid = false;
                }
                else if(noNumIsMatched){
                    lName.setError("Name must not contains any number");
                    isValid = false;
                }
                else{
                    lName.setError(null);
                }

                //check email input
                if(uEmail.getText().toString().isEmpty()){
                    lEmail.setError("This field cannot be empty!");
                    isValid = false;
                }
                else if(!validEmailIsMatced){
                    lEmail.setError("Invalid email address!");
                    isValid = false;
                }
                else{
                    lEmail.setError(null);
                }

                //check message input
                if(uMessage.getText().toString().isEmpty()){
                    lMessage.setError("This field cannot be empty!");
                    isValid = false;
                }
                else{
                    lMessage.setError(null);
                }

                //validated and submit application to db
                if(isValid){
                    submitRegistration();
                    Toast.makeText(ContactOrganizerActivity.this,"Submitted", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void initializeView() {
        //initialize EditText
        uName = (EditText) findViewById(R.id.cuName);
        uEmail = (EditText) findViewById(R.id.cuEmail);
        uMessage = (EditText) findViewById(R.id.cuMessage);

        //initialize inputLayout
        lName = (TextInputLayout) findViewById(R.id.clName);
        lEmail = (TextInputLayout) findViewById(R.id.clEmail);
        lMessage = (TextInputLayout) findViewById(R.id.clMessage);

        cSubmitButton = (Button) this.findViewById(R.id.button_cSubmit);
    }

    private void submitRegistration (){

        name = uName.getText().toString();
        email = uEmail.getText().toString();
        message = uMessage.getText().toString();
        writeNewMessage(name, email, message);
    }

    private void writeNewMessage(String name, String email, String message) {

        ContactOrganizer user = new ContactOrganizer(name, email, message);
        ref.setValue(user);
    }
}
