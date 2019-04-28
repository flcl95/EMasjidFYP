package com.fyp.emasjid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseReference ref;
    private String eventId;
    private String name, gender, age, email, phone;
    private EditText uName, uGender, uAge, uEmail, uPhone;
    private TextInputLayout lName, lGender, lAge, lEmail, lPhone;
    private Button rSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //set actionbar title
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Register");

        eventId = getIntent().getExtras().get("eventId").toString();
        ref = FirebaseDatabase.getInstance().getReference().child("Registration").child(eventId).push();

        initializeView();


    }

    @Override
    protected void onStart() {
        super.onStart();

        rSubmitButton.setOnClickListener(new View.OnClickListener() {
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

                //regex for gender
                String gender = "(?:m|M|male|Male|f|F|female|Female)$";
                Pattern genderPattern = Pattern.compile(gender);
                Matcher genderMatcher = genderPattern.matcher(uGender.getText().toString());
                boolean genderIsMatched = genderMatcher.matches();

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

                //check age input
                if(uAge.getText().toString().isEmpty()){
                    lAge.setError("This field cannot be empty!");
                    isValid = false;
                }
                else{
                    lAge.setError(null);
                }

                //check gender input
                if(uGender.getText().toString().isEmpty()){
                    lGender.setError("This field cannot be empty!");
                    isValid = false;
                }
                else if(!genderIsMatched){
                    lGender.setError("Invalid gender!");
                    isValid = false;
                }
                else{
                    lGender.setError(null);
                }

                //check phone input
                if(uPhone.getText().toString().isEmpty()){
                    lPhone.setError("This field cannot be empty!");
                    isValid = false;
                }
                else{
                    lPhone.setError(null);
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

                //validated and submit application to db
                if(isValid){
                    submitRegistration();
                    Toast.makeText(RegisterActivity.this,"Submitted", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void writeNewUser(String name, String gender, String age, String email, String phone) {

        UserRegister user = new UserRegister(name, gender, age, email, phone);
        ref.setValue(user);
    }

    private void initializeView(){
        //initialize EditText
        uName = (EditText) findViewById(R.id.uName);
        uAge = (EditText) findViewById(R.id.uAge);
        uGender = (EditText) findViewById(R.id.uGender);
        uPhone = (EditText) findViewById(R.id.uPhone);
        uEmail = (EditText) findViewById(R.id.uEmail);

        //initialize inputLayout
        lName = (TextInputLayout) findViewById(R.id.lName);
        lAge = (TextInputLayout) findViewById(R.id.lAge);
        lGender = (TextInputLayout) findViewById(R.id.lGender);
        lPhone = (TextInputLayout) findViewById(R.id.lPhone);
        lEmail = (TextInputLayout) findViewById(R.id.lEmail);

        rSubmitButton = (Button) this.findViewById(R.id.button_rSubmit);

    }

    private void submitRegistration (){

        name = uName.getText().toString();
        age = uAge.getText().toString();
        gender = uGender.getText().toString();
        phone = uPhone.getText().toString();
        email = uEmail.getText().toString();
        writeNewUser(name, age, gender, phone, email);
    }
}
