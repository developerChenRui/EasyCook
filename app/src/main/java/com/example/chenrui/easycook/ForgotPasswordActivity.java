package com.example.chenrui.easycook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView txt_remember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        txt_remember = (TextView)findViewById(R.id.txt_remember);
        txt_remember.setOnClickListener(this);


    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_remember:
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
        }

    }
}
