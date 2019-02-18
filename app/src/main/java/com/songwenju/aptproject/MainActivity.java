package com.songwenju.aptproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AutoCreat;
import com.example.DIActivity;
import com.example.DIView;


@AutoCreat
@DIActivity
public class MainActivity extends AppCompatActivity {

    @DIView(R.id.hello)
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DIMainActivity.bindView(this);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "点击", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
