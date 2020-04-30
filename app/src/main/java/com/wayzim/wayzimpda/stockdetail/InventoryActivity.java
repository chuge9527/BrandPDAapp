package com.wayzim.wayzimpda.stockdetail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.wayzim.wayzimpda.R;
import com.wayzim.wayzimpda.StockManageActivity;
import com.wayzim.wayzimpda.tools.Icon;
import com.wayzim.wayzimpda.tools.MyAdapter;

import java.util.ArrayList;


public class InventoryActivity extends AppCompatActivity {
    private Context mContext;
    private GridView grid_inventory;
    private BaseAdapter mAdapter6 = null;
    private ArrayList<Icon> mData6 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        mContext = InventoryActivity.this;
        grid_inventory = (GridView) findViewById(R.id.grid_inventory);

        mData6 = new ArrayList<Icon>();
        mData6.add(new Icon(R.mipmap.picking4, "产品盘点"));
        mData6.add(new Icon(R.mipmap.deliver4, "标签盘点"));
        mData6.add(new Icon(R.mipmap.receipt4, "申请盘点"));
      //  mData6.add(new Icon(R.mipmap.stock4, "养护"));
     //   mData6.add(new Icon(R.mipmap.stock5, "查询"));

        mAdapter6 = new MyAdapter<Icon>(mData6, R.layout.item_grid_icon) {
            @Override
            public void bindView(MyAdapter.ViewHolder holder, Icon obj) {
                holder.setImageResource(R.id.img_icon, obj.getiId());
                holder.setText(R.id.txt_icon, obj.getiName());
            }
        };
        grid_inventory.setAdapter(mAdapter6);
        grid_inventory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent ;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //  Toast.makeText(mContext, "你点击了~" + position + "~项", Toast.LENGTH_SHORT).show();
                switch (position){
                    case 0:
                        intent = new Intent(mContext , InventoryProductActivity.class);
                        startActivity(intent);
                        break;
                        /*
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

                         */

                    default:
                        break;
                }

            }
        });

    }
}
