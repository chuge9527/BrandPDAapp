package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Instore1Activity extends AppCompatActivity {
    private Handler handler,handler2;
    private  String data1,data2;//查询货物的结果,提交的结果
    private String[] info;//查询的json数组
    private int lenCode;

    private EditText materialCode,barText,goodNUmET;
    private Button btnSearch,btnCode;
    private Spinner spinner1,spinner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instore1);
        //
        btnSearch = findViewById(R.id.btn_search);
        btnCode = findViewById(R.id.btn_code);
        materialCode = findViewById(R.id.mCode);//查询的关键字

        barText = findViewById(R.id.barText);//条码
        goodNUmET =findViewById(R.id.goodnumber);//数量
        spinner1 = findViewById(R.id.material);
        spinner2 = findViewById(R.id.unit);


        btnCode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //跳转扫码
                Toast.makeText(Instore1Activity.this,"条码",Toast.LENGTH_SHORT).show();
             //   Intent intent = new Intent(Instore1Activity.this,CodeActivity.class);
            //    startActivityForResult(intent,0x11);
                //   startActivity(intent);
            }
        });


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //  Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                //  Toast.makeText(Instore1Activity.this,info[1],Toast.LENGTH_SHORT).show();
                if(msg.what == 0x0002){
                    Toast.makeText(Instore1Activity.this,"json数据失败",Toast.LENGTH_SHORT).show();
                }else {
                    showinfo();
                }

            }
        };
        handler2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //  Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                if(msg.what == 0x0001) {
                    Toast.makeText(Instore1Activity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    barText.setText("");
                    goodNUmET.setText("");
                }
                else {
                    Toast.makeText(Instore1Activity.this, "提交失败", Toast.LENGTH_SHORT).show();
                }

            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //requestCode == 0x11  返回时为 requestCode 没有赋值
        if ( resultCode == 0x11) {

            String codenum = data.getStringExtra("barcode");
            barText.setText(codenum);
        }

    }

    //获取货物编码和名称
    public void getRequest(View view) {

        String  mCodes = materialCode.getText().toString();
        String url = "http://192.168.1.110:8080/api/getMaterialsByMaterialCode?materialCode="+mCodes;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .get()//默认就是GET请求，可以不写
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("wy", "onFailure: ");
                    Toast.makeText(Instore1Activity.this, "Http失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String json = response.body().string();//!!!只能调用一次!!!
                    Log.d("wy", "onResponse: " + json);
                    try {
                        //Json数据的处理
                        JSONObject jsonObject = new JSONObject(json);
                        data1 = jsonObject.getString("data");
                        //  Log.d("data:", data1);

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
            Toast.makeText(Instore1Activity.this, "Http链接错误", Toast.LENGTH_LONG).show();
        }
    }
    //显示下拉框数据
    private void showinfo(){
        try {
            //下拉菜单的数据源list，如何填充arrayAdapter

            JSONArray jsonArray = new JSONArray(data1);
            int length = jsonArray.length();
            lenCode = length;
            info = new String[length];
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);//i从零开始

                info[i] = jsonObject1.getString("materialCode") + ":" + jsonObject1.getString("materialName");
            }
            //下拉菜单
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, info);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner1.setAdapter(adapter);
            String unit = spinner1.getSelectedItem().toString();//获取选中的值
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //提交
    public void submitData(View view) {
        String url2 = "http://192.168.1.110:8080/api/pdaStockInWithOrderCode";

        String good = spinner1.getSelectedItem().toString();//获取选中的值
        String[] goodSP = good.split(":");//编码和名称
        String countNum = goodNUmET.getText().toString();//数量
        String unitGoods = spinner2.getSelectedItem().toString();//单位
        String barcode = barText.getText().toString(); //托盘条码
        String orderMsg2 = "?goodCode=" + goodSP[0] + "&goodName=" + goodSP[1] + "&count=" + countNum + "&unit=" + unitGoods + "&barCode=" + barcode + "&needTray=true";
        String url = url2 + orderMsg2;
        Log.d("s", countNum);
        Log.d("c", barcode);
        if (countNum.equals("") || barcode.equals("")) {
            Toast.makeText(Instore1Activity.this, "数据不完整！！！", Toast.LENGTH_LONG).show();
        } else {

            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                final Request request = new Request.Builder()
                        .url(url)
                        .get()//默认就是GET请求，可以不写
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("wy", "onFailure: ");
                        Toast.makeText(Instore1Activity.this, "Http失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String json = response.body().string();//!!!只能调用一次!!!
                        Log.d("wy", "onResponse: " + json);
                        try {
                            //Json数据的处理
                            JSONObject jsonObject = new JSONObject(json);
                            data2 = jsonObject.getString("state");
                            Log.d("data:", data2);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    handler2.sendEmptyMessage(0x0001);
                                }
                            }).start();
                        } catch (JSONException e) {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    handler2.sendEmptyMessage(0x0002);
                                }
                            }).start();


                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(Instore1Activity.this, "Http链接错误", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
