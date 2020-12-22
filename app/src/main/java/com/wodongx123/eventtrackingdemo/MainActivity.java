package com.wodongx123.eventtrackingdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.wodongx123.eventtrackingdemo.annotation.ClickTracking;
import com.wodongx123.eventtrackingdemo.annotation.TrackingKeyName;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test("users","111",  "123456");
            }
        });
    }

    @ClickTracking
    private void test(@TrackingKeyName("name") String name, String a, @TrackingKeyName("password") String password) {
        Log.e(TAG, "test: ");
    }
}