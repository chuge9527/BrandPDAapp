package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btnInstore,btnOutstore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnInstore = findViewById(R.id.btn_instore);
        btnOutstore = findViewById(R.id.btn_outstore);
        btnOutstore.setOnClickListener(v);
        btnInstore.setOnClickListener(v);
    }

    View.OnClickListener v = new View.OnClickListener() {
        Intent intent ;
      //  startActivity(intent);
        @Override
        public void onClick(View view) {
          switch (view.getId()){
              case R.id.btn_instore:
                  intent = new Intent(MainActivity.this,Instore1Activity.class);
                  break;
              case R.id.btn_outstore:
                  intent = new Intent(MainActivity.this,Outstore1Activity.class);
                  break;
              default:
                  break;
          }
          startActivity(intent);
        }
    };
}
