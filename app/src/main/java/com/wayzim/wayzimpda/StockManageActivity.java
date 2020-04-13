package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.wayzim.wayzimpda.stockdetail.InventoryActivity;
import com.wayzim.wayzimpda.tools.Icon;
import com.wayzim.wayzimpda.tools.MyAdapter;

import java.util.ArrayList;

public class StockManageActivity extends AppCompatActivity {
    private Context mContext;
    private GridView grid_stock;
    private BaseAdapter mAdapter5 = null;
    private ArrayList<Icon> mData5 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_manage);//库存管理

        mContext = StockManageActivity.this;
        grid_stock = (GridView) findViewById(R.id.grid_stock);

        mData5 = new ArrayList<Icon>();
        mData5.add(new Icon(R.mipmap.stock1, "盘点"));
        mData5.add(new Icon(R.mipmap.stock2, "移库"));
        mData5.add(new Icon(R.mipmap.stock3, "补货"));
        mData5.add(new Icon(R.mipmap.stock4, "养护"));
        mData5.add(new Icon(R.mipmap.stock5, "查询"));

        mAdapter5 = new MyAdapter<Icon>(mData5, R.layout.item_grid_icon) {
            @Override
            public void bindView(MyAdapter.ViewHolder holder, Icon obj) {
                holder.setImageResource(R.id.img_icon, obj.getiId());
                holder.setText(R.id.txt_icon, obj.getiName());
            }
        };
        grid_stock.setAdapter(mAdapter5);
        grid_stock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent ;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //  Toast.makeText(mContext, "你点击了~" + position + "~项", Toast.LENGTH_SHORT).show();
                switch (position){
                    case 0:
                        intent = new Intent(mContext , InventoryActivity.class);
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
                    case 4:
                        intent = new Intent(mContext ,PickingActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(mContext ,DeliverActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent = new Intent(mContext ,StockManageActivity.class);
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
