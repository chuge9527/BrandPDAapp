package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.Bundle;

public class Outstore1Activity extends AppCompatActivity {
     private Detail1Fragment detailfm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outstore1);
        Detail1Fragment detail1Fra = new Detail1Fragment();
    getSupportFragmentManager().beginTransaction().add(R.id.frame1,detail1Fra).commit();
    }
}
