package com.wayzim.wayzimpda.stockdetail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;


import com.wayzim.wayzimpda.Detail2Fragment;
import com.wayzim.wayzimpda.Detail3Fragment;
import com.wayzim.wayzimpda.R;

import com.wayzim.wayzimpda.tools.ItemInventory;
import com.wayzim.wayzimpda.tools.ListviewHeight;
import com.wayzim.wayzimpda.tools.MyAdapter;
import com.wayzim.wayzimpda.tools.MyAdapterInventory;
import com.wayzim.wayzimpda.tools.MyListView;
import com.wayzim.wayzimpda.tools.SharedHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InventoryProductActivity extends AppCompatActivity {
    private Button inventorySearch;
    private EditText inventoryNum;
    private MyListView list_inventory;
    private Detail3Fragment detail3Fra;
    private ScrollView sv;

    private Handler handler_inventory;
    private String data_inventory;
    private MyAdapterInventory<ItemInventory> myAdapter1 = null;
    private List<ItemInventory> mData1 = null;
    private JSONObject data_detail;

    private SharedHelper sh;
    private Context aContext;
    private String wms_URl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_product);

        inventorySearch = (Button) findViewById(R.id.btn_searchInventory);
        inventoryNum = (EditText)findViewById(R.id.inventoryNum);
        list_inventory = findViewById(R.id.exlist_inventory);
        //listview嵌套
        sv = (ScrollView) findViewById(R.id.invent_scroll);
        sv.smoothScrollTo(0, 0);
        //查询
        inventorySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inventory_request();
            }
        });
        //读取WMS的IP
        aContext = getApplicationContext();
        sh = new SharedHelper(aContext);
        Map<String,String> dataMap = sh.readURL();
        wms_URl ="http://"+dataMap.get("urlWMS");

        detail3Fra = new Detail3Fragment();//frag加载
        getSupportFragmentManager().beginTransaction().add(R.id.frame3, detail3Fra).commit();

        //显示listview数据
        handler_inventory = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x0002){
                    Toast.makeText(InventoryProductActivity.this,"json:data数据失败",Toast.LENGTH_SHORT).show();
                }else {
                   showInventoryinfo();//显示数据到listview上了
                }

            }
        };

    }//


    //获取盘点任务信息，给data_order
    public void inventory_request() {
        String url =wms_URl+ ":8088/api/getInventoryCheckPlansByPage";
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            String  pageSize = inventoryNum .getText().toString();//每页显示的条数
            if(pageSize.equals("")){
                pageSize = "6";
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
                    Log.d("有源入库 请求入库单",json);
                    try {
                        //Json数据的处理
                        JSONObject jsonObject = new JSONObject(json);
                        data_inventory = jsonObject.getString("data");//data数据
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler_inventory.sendEmptyMessage(0x0001);
                            }
                        }).start();
                    } catch (JSONException e) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler_inventory.sendEmptyMessage(0x0002);
                            }
                        }).start();
                    }
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();

        }
        //
    }
    //显示list数据到listview
    private void showInventoryinfo(){
        try {
            //下拉菜单的数据源list，如何填充arrayAdapter
            JSONObject jO = new JSONObject(data_inventory);//data数据
            JSONArray jsonArray = new JSONArray(jO.getString("list"));//list数据
            //数据初始化
            int length = jsonArray.length();//入库单 的数量
            Log.d("有源入库，入库单数量", String.valueOf(length));//ok入库单的数量 2
            mData1 = new ArrayList<ItemInventory>();

            for (int i = 0; i < length; i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);//i从零开始
                ItemInventory inventory = new ItemInventory();
                inventory.setiTaskId(jsonObject1.getString("id"));//任务号
                inventory.setIbinId(jsonObject1.getString("bin_code"));//托盘条码
                inventory.setiTrayCode(jsonObject1.getString("tray_code"));
                ;//库位条码
                inventory.setiName(jsonObject1.getString("material_name"));//物料名称
                inventory.setiMCode(jsonObject1.getString("material_code"));
                inventory.setiCount(jsonObject1.getString("record_amount"));
                mData1.add(inventory);
            }
            Log.d("有源入库，盘点信息",mData1.toString());
            myAdapter1 = new MyAdapterInventory<ItemInventory>((ArrayList)mData1,R.layout.item_inventory_item) {
                @Override
                public void bindView(ViewHolder holder, ItemInventory obj) {
                    holder.setText(R.id.inventory_binId,obj.getIbinId());
                    holder.setText(R.id.inventory_materialName,obj.getiName());
                }
            };

            list_inventory.setAdapter(myAdapter1);
            //listview嵌套
         //   ListviewHeight.setListViewHight(list_inventory);

            //选择
            list_inventory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  //  String result = parent.getItemAtPosition(position).toString();//获取选择项的值
               //     Toast.makeText(InventoryProductActivity.this, "您点击了" + result, Toast.LENGTH_SHORT).show();

               //     Toast.makeText(InventoryProductActivity.this, "你点击了：" + mData1.get(position).getiName(), Toast.LENGTH_SHORT).show();
                    data_detail = new JSONObject();
                    try {
                        data_detail.put("task_id",mData1.get(position).getiTaskId());//任务号
                        data_detail.put("bin_code",mData1.get(position).getIbinId());//库位
                        data_detail.put("tray_code",mData1.get(position).getiTrayCode());//托盘
                        data_detail.put("material_name",mData1.get(position).getiName());//物料名称
                        data_detail.put("material_code",mData1.get(position).getiMCode());//物料编码
                        data_detail.put("record_amount",mData1.get(position).getiCount());//物料数量
                        //物料编码
                        //数量
                        showinfo3();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //
    //传送数据
    public void  showinfo3(){
        Bundle bundle = new Bundle();
        bundle.putString("data",data_detail.toString());
        Detail3Fragment detail3Fra = new Detail3Fragment();
        detail3Fra.setArguments(bundle);//数据传递到fragment中

        getSupportFragmentManager().beginTransaction().replace(R.id.frame3,detail3Fra).commit();


    }
}
