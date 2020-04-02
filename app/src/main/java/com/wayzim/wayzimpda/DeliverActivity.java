package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.wayzim.wayzimpda.tools.Icon;
import com.wayzim.wayzimpda.tools.MyAdapter;

import java.util.ArrayList;

public class DeliverActivity extends AppCompatActivity {
    private Context mContext;
    private GridView grid_deliver;
    private BaseAdapter mAdapter4 = null;
    private ArrayList<Icon> mData4 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver);//发货

        mContext = DeliverActivity.this;
        grid_deliver = (GridView) findViewById(R.id.grid_deliver);

        mData4 = new ArrayList<Icon>();
        mData4.add(new Icon(R.mipmap.deliver1, "按订单发货"));//0
        mData4.add(new Icon(R.mipmap.deliver2, "按波次发货"));
        mData4.add(new Icon(R.mipmap.deliver3, "按装车发货"));
        mData4.add(new Icon(R.mipmap.deliver4, "按ID发货"));
       // mData4.add(new Icon(R.mipmap.picking5, "仓库直拣"));

        mAdapter4 = new MyAdapter<Icon>(mData4, R.layout.item_grid_icon) {
            @Override
            public void bindView(MyAdapter.ViewHolder holder, Icon obj) {
                holder.setImageResource(R.id.img_icon, obj.getiId());
                holder.setText(R.id.txt_icon, obj.getiName());
            }
        };
        grid_deliver.setAdapter(mAdapter4);

    }
}
