package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.wayzim.wayzimpda.tools.Icon;
import com.wayzim.wayzimpda.tools.MyAdapter;

import java.util.ArrayList;

public class ShelvesActivity extends AppCompatActivity {
    private Context mContext;
    private GridView grid_shelves;
    private BaseAdapter mAdapter2 = null;
    private ArrayList<Icon> mData2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelves);//上架
        mContext = ShelvesActivity.this;
        grid_shelves = (GridView) findViewById(R.id.grid_shelves);

        mData2 = new ArrayList<Icon>();
        mData2.add(new Icon(R.mipmap.shelves1, "上架"));//0
        mData2.add(new Icon(R.mipmap.shelves2, "上架准备"));
      //  mData2.add(new Icon(R.mipmap.receipt3, "快捷收货"));
      //  mData2.add(new Icon(R.mipmap.receipt4, "码盘收货"));
        mAdapter2 = new MyAdapter<Icon>(mData2, R.layout.item_grid_icon) {
            @Override
            public void bindView(MyAdapter.ViewHolder holder, Icon obj) {
                holder.setImageResource(R.id.img_icon, obj.getiId());
                holder.setText(R.id.txt_icon, obj.getiName());
            }
        };

        grid_shelves.setAdapter(mAdapter2);

    }
}
