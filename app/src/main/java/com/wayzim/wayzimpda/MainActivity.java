package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.wayzim.wayzimpda.tools.Icon;
import com.wayzim.wayzimpda.tools.MyAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
   // private Button btnInstore,btnOutstore;
    private Context mContext;
    private GridView grid_photo;
    private BaseAdapter mAdapter = null;
    private ArrayList<Icon> mData = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        grid_photo = (GridView) findViewById(R.id.grid_photo);

        mData = new ArrayList<Icon>();

        mData.add(new Icon(R.mipmap.iv_icon_5, "入库"));
        mData.add(new Icon(R.mipmap.iv_icon_6, "盘点"));
        mData.add(new Icon(R.mipmap.iv_icon_7, "出库"));

        mAdapter = new MyAdapter<Icon>(mData, R.layout.item_grid_icon) {
            @Override
            public void bindView(ViewHolder holder, Icon obj) {
                holder.setImageResource(R.id.img_icon, obj.getiId());
                holder.setText(R.id.txt_icon, obj.getiName());
            }
        };

        grid_photo.setAdapter(mAdapter);

        grid_photo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent ;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              //  Toast.makeText(mContext, "你点击了~" + position + "~项", Toast.LENGTH_SHORT).show();
                switch (position){
                    case 0:
                        intent = new Intent(MainActivity.this,Instore1Activity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this,Outstore1Activity.class);
                        startActivity(intent);
                        break;
                    default:

                        break;
                }

            }
        });

/*
        btnInstore = findViewById(R.id.btn_instore);
        btnOutstore = findViewById(R.id.btn_outstore);
        btnOutstore.setOnClickListener(v);
        btnInstore.setOnClickListener(v);

 */
    }
/*
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
*/

}
