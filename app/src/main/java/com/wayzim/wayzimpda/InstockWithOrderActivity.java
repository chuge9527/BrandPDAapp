package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
    private String data_order;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instock_with_order);

        orderSearch = (Button) findViewById(R.id.btn_search2);
        orderNum = (EditText)findViewById(R.id.orderNum);


        //
    }

    //获取入库任务信息，给data_order
    public void getRequest2(View view) {
        String url = "http://localhost:8080/api/pdaQueryStockIn/getStockInOrderWithDetailPerTray";
        try {
            String  pageSize = orderNum .getText().toString();//每页显示的条数
            OkHttpClient okHttpClient = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject json = new JSONObject();
            json.put("pageSize", pageSize);
            json.put("currentPage", 1);
            Log.d("wy", String.valueOf(json));
            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
            final Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)//默认就是GET请求，可以不写
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("wy", "onFailure: ");
                    Toast.makeText(InstockWithOrderActivity.this, "Http失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String json = response.body().string();//!!!只能调用一次!!!
                    Log.d("wy", "onResponse: " + json);
                    try {
                        //Json数据的处理
                        JSONObject jsonObject = new JSONObject(json);
                        data_order = jsonObject.getString("list");

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
            Toast.makeText(InstockWithOrderActivity.this, "Http链接错误", Toast.LENGTH_LONG).show();
        }
    }

}
