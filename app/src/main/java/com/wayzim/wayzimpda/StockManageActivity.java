package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.GridView;

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
        mData5.add(new Icon(R.mipmap.stock1, "补货"));
        mData5.add(new Icon(R.mipmap.stock2, "移库"));
        mData5.add(new Icon(R.mipmap.stock3, "盘点"));
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


    }
}
