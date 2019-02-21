package com.songwenju.aptproject;

import android.app.Activity;
import android.os.Bundle;

import com.example.ReplaceActivity;
@ReplaceActivity("com.songwenju.aptproject.MainActivity")
public class MainActivity_replace extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_replace);
    }
}
