package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.wayzim.wayzimpda.tools.Icon;
import com.wayzim.wayzimpda.tools.MyAdapter;

import java.util.ArrayList;

public class TaskManageActivity extends AppCompatActivity {
    private Context mContext;
    private GridView grid_task;
    private BaseAdapter mAdapter6 = null;
    private ArrayList<Icon> mData6 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manage);//任务管理

        mContext = TaskManageActivity.this;
        grid_task = (GridView) findViewById(R.id.grid_task);//

        mData6 = new ArrayList<Icon>();
        mData6.add(new Icon(R.mipmap.picking1, "订单拣货"));//0
        mData6.add(new Icon(R.mipmap.picking2, "标签拣货"));
        mData6.add(new Icon(R.mipmap.picking3, "人工拣货"));
        mData6.add(new Icon(R.mipmap.picking4, "波次拣货"));
        mData6.add(new Icon(R.mipmap.picking5, "仓库直拣"));

        mAdapter6 = new MyAdapter<Icon>(mData6, R.layout.item_grid_icon) {
            @Override
            public void bindView(MyAdapter.ViewHolder holder, Icon obj) {
                holder.setImageResource(R.id.img_icon, obj.getiId());
                holder.setText(R.id.txt_icon, obj.getiName());
            }
        };
        grid_task.setAdapter(mAdapter6);
    }
}
