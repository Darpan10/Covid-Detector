package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText name,password;
    Button btn_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         name =  (EditText)findViewById(R.id.editTextTextPersonName3);
         password = (EditText)findViewById(R.id.editTextTextPassword);


    }
    public void btn_click(View v)
    {

        String  names=name.getText().toString();
        String  passwords= password.getText().toString();

        if ((names.equals("a")) && (passwords.equals("a")))
        {
            Toast.makeText(MainActivity.this,"Welcome to My App!",Toast.LENGTH_SHORT).show();
        }

        else
        {
            Toast.makeText(MainActivity.this,"Wrong credentials",Toast.LENGTH_SHORT).show();
        }

    }



}