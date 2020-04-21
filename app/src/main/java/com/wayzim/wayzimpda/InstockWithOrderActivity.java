package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.wayzim.wayzimpda.tools.Group;
import com.wayzim.wayzimpda.tools.Item;
import com.wayzim.wayzimpda.tools.MyBaseExpandableListAdapter;
import com.wayzim.wayzimpda.tools.SharedHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InstockWithOrderActivity extends AppCompatActivity {
    private Button orderSearch;
    private EditText orderNum;
    private Handler handler;
    private Context mContext;

    private String data_order;
    private String[] list_OrderCode;//查询的json数组
    private String[] list_taskCode;
    private String[] list_materialName;
    private String[] list_materialCode;
    private String[] list_mcount;
    private ArrayList<Group> gData = null;
    private ArrayList<ArrayList<Item>> iData = null;

    private ExpandableListView exlist_lol;
    private MyBaseExpandableListAdapter myAdapter = null;
    private Detail2Fragment detail2Fra;
    private JSONObject data_detail;

    private SharedHelper sh;
    private Context aContext;
    private String wms_URl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instock_with_order);

        mContext = InstockWithOrderActivity.this;
        exlist_lol = (ExpandableListView) findViewById(R.id.exlist_lol);
        orderSearch = (Button) findViewById(R.id.btn_search2);
        orderNum = (EditText)findViewById(R.id.orderNum);

        //读取WMS的IP
        aContext = getApplicationContext();
        sh = new SharedHelper(aContext);
        Map<String,String> dataMap = sh.readURL();
        wms_URl ="http://"+dataMap.get("urlWMS");

        detail2Fra = new Detail2Fragment();//frag加载
        getSupportFragmentManager().beginTransaction().add(R.id.frame2, detail2Fra).commit();

        //显示listview数据
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x0002){
                    Toast.makeText(InstockWithOrderActivity.this,"json:data数据失败",Toast.LENGTH_SHORT).show();
                }else {
                    showOrderinfo();//显示数据到listview上了
                }

            }
        };

    }

    //获取入库任务信息，给data_order
    public void order_request(View view) {
        String url =wms_URl+ ":8080/api/pdaQueryStockIn/getStockInOrderWithDetailPerTray";
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            String  pageSize = orderNum .getText().toString();//每页显示的条数
            if(pageSize.equals("")){
                pageSize = "10";
            }
            //传入post的body数据
            json.put("pageSize", pageSize);
            json.put("currentPage", 1);//默认显示第一页
            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
            final Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)//默认就是GET请求，可以不写
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                   Log.d("有源入库 请求入库单","onFailure");
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();//!!!只能调用一次!!!
                    try {
                        //Json数据的处理
                        JSONObject jsonObject = new JSONObject(json);
                        data_order = jsonObject.getString("data");//data数据
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(0x0001);
                            }
                        }).start();
                    } catch (JSONException e) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(0x0002);
                            }
                        }).start();
                    }
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(InstockWithOrderActivity.this, "Http连接失败", Toast.LENGTH_LONG).show();
        }
        //
    }

    //显示list数据到listview
    private void showOrderinfo(){
        try {
            //下拉菜单的数据源list，如何填充arrayAdapter
            JSONObject jO = new JSONObject(data_order);//data数据
            JSONArray jsonArray = new JSONArray(jO.getString("list"));//list数据

            int length = jsonArray.length();//入库单 的数量
            Log.d("有源入库，入库单数量", String.valueOf(length));//ok入库单的数量 2
            list_OrderCode = new String[length];//存 入库单号
            gData = new ArrayList<Group>();
            iData = new ArrayList<ArrayList<Item>>();

            for (int i = 0; i < length; i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);//i从零开始

                list_OrderCode[i] =  jsonObject1.getString("stockInOrderCode");
                gData.add(new Group(list_OrderCode[i]));
                Log.d("有源入库，入库号",list_OrderCode[i]);//
                JSONArray jsa = new JSONArray(jsonObject1.getString("stockInDetailsPerTray"));
                int task_len = jsa.length();//任务号的数量
                Log.d("有源入库，任务的数量",String.valueOf(task_len));//任务号的数量
                //arrylist？？
                list_taskCode = new String[task_len];
                list_materialName = new String[task_len];
                list_materialCode = new String[task_len];
                list_mcount = new String[task_len];
                ArrayList<Item> lData =  new ArrayList<Item>();
                for (int j = 0; j < task_len; j++) {
                    JSONObject jsonObject2 = jsa.getJSONObject(j);//i从零开始
                    list_taskCode[j] = jsonObject2.getString("taskNo");//任务号
                    list_materialName[j] =jsonObject2.getString("materialName");//物料名称
                    list_materialCode[j] = jsonObject2.getString("materialCode");
                    list_mcount[j] = jsonObject2.getString("materialAmount");
                    lData.add(new Item(list_taskCode[j],list_materialName[j],list_materialCode[j],list_mcount[j]));
                }
                iData.add(lData);
            }
            //
            //数据准备
            myAdapter = new MyBaseExpandableListAdapter(gData,iData,mContext);
            exlist_lol.setAdapter(myAdapter);
            //为列表设置点击事件
            exlist_lol.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    Toast.makeText(mContext, "你点击了：" + iData.get(groupPosition).get(childPosition).getiName(), Toast.LENGTH_SHORT).show();
                   //数据传送json到data_detail
                    data_detail = new JSONObject();
                    try {
                        data_detail.put("instock",gData.get(groupPosition).getgName());//入库单
                        data_detail.put("task_id",iData.get(groupPosition).get(childPosition).getiId());//任务号
                        data_detail.put("materialName",iData.get(groupPosition).get(childPosition).getiName());//物料名称
                        data_detail.put("materialCode",iData.get(groupPosition).get(childPosition).getiMCode());//物料编码
                        data_detail.put("materialCount",iData.get(groupPosition).get(childPosition).getiCount());//物料数量
                        //物料编码
                        //数量
                        showinfo2();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //
    //显示物料详情
    private void showListSelect(int groupPosition, int childPosition){

    }

    //传送数据
    public void  showinfo2(){
        Bundle bundle = new Bundle();
        bundle.putString("data",data_detail.toString());
        Detail2Fragment detail2Fra2 = new Detail2Fragment();
        detail2Fra2.setArguments(bundle);//数据传递到fragment中

        getSupportFragmentManager().beginTransaction().replace(R.id.frame2,detail2Fra2).commit();


    }
}
