package com.wayzim.wayzimpda.tools;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListviewHeight {
    //动态设置Listview高度,不然就一行
    public static void setListViewHight(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if(listAdapter == null){
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            // listAdapter.getCount()返回数据项的数目
            View itemView = listAdapter.getView(i,null,listView);
            // 计算子项View 的宽高
            itemView.measure(0,0);
            // 统计所有子项的总高度
            totalHeight += itemView.getMeasuredHeight();
        }
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight +(listView.getDividerHeight()*(listAdapter.getCount()-1));
        listView.setLayoutParams(params);
    }
}
