package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.wayzim.wayzimpda.tools.Icon;
import com.wayzim.wayzimpda.tools.MyAdapter;

import java.util.ArrayList;

public class ReceiptActivity extends AppCompatActivity {
    private Context mContext;
    private GridView grid_receipt;
    private BaseAdapter mAdapter1 = null;
    private ArrayList<Icon> mData1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);//收货
        mContext = ReceiptActivity.this;
        grid_receipt = (GridView) findViewById(R.id.grid_receipt);

        mData1 = new ArrayList<Icon>();
        mData1.add(new Icon(R.mipmap.receipt1, "RF收货"));//0
        mData1.add(new Icon(R.mipmap.receipt2, "序列号扫描"));
        mData1.add(new Icon(R.mipmap.receipt3, "快捷收货"));
        mData1.add(new Icon(R.mipmap.receipt4, "码盘收货"));

        mAdapter1 = new MyAdapter<Icon>(mData1, R.layout.item_grid_icon) {
          @Override
          public void bindView(MyAdapter.ViewHolder holder, Icon obj) {
               holder.setImageResource(R.id.img_icon, obj.getiId());
              holder.setText(R.id.txt_icon, obj.getiName());
           }
      };

        grid_receipt.setAdapter(mAdapter1);
        /*
        grid_receipt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        Intent intent ;
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //  Toast.makeText(mContext, "你点击了~" + position + "~项", Toast.LENGTH_SHORT).show();
            switch (position){
                case 0:
                    intent = new Intent(mContext ,Instore1Activity.class);
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(mContext ,Outstore1Activity.class);
                    startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(mContext ,ReceiptActivity.class);
                    startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(mContext ,ShelvesActivity.class);
                    startActivity(intent);
                    break;
                case 4:
                    intent = new Intent(mContext ,PickingActivity.class);
                    startActivity(intent);
                    break;

                default:

                    break;
            }

        }
    });

         */

}
}