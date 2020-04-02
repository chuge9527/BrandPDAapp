package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.wayzim.wayzimpda.tools.Icon;
import com.wayzim.wayzimpda.tools.MyAdapter;

import java.util.ArrayList;

public class PickingActivity extends AppCompatActivity {
    private Context mContext;
    private GridView grid_picking;
    private BaseAdapter mAdapter3 = null;
    private ArrayList<Icon> mData3 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picking);//拣货

        mContext = PickingActivity.this;
        grid_picking = (GridView) findViewById(R.id.grid_picking);

        mData3 = new ArrayList<Icon>();
        mData3.add(new Icon(R.mipmap.picking1, "订单拣货"));//0
        mData3.add(new Icon(R.mipmap.picking2, "标签拣货"));
        mData3.add(new Icon(R.mipmap.picking3, "人工拣货"));
        mData3.add(new Icon(R.mipmap.picking4, "波次拣货"));
        mData3.add(new Icon(R.mipmap.picking5, "仓库直拣"));

        mAdapter3 = new MyAdapter<Icon>(mData3, R.layout.item_grid_icon) {
            @Override
            public void bindView(MyAdapter.ViewHolder holder, Icon obj) {
                holder.setImageResource(R.id.img_icon, obj.getiId());
                holder.setText(R.id.txt_icon, obj.getiName());
            }
        };
        grid_picking.setAdapter(mAdapter3);


    }
}
