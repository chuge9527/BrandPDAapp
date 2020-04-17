package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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


public class InstockNoOrderActivity extends AppCompatActivity {
    private Handler handler,handler2,handler3;
    private  String data1,data2;//查询货物的结果,提交的结果
    private String[] info;//查询的json数组
    private int lenCode;

    private EditText materialCode,barText,goodNUmET;
    private TextView materialTx,orderText;
    private Button btnSearch,btnCode,btnExit;
    private Spinner spinner1,spinner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instock_no_order);
        //
        btnExit = findViewById(R.id.btn_exit2);
        btnSearch = findViewById(R.id.btn_search2);

        materialCode = findViewById(R.id.mCode2);//查询的关键字
        barText = findViewById(R.id.barText2);//条码
        goodNUmET =findViewById(R.id.goodnumber2);//数量
        materialTx = findViewById(R.id.materialName2);//名称
        orderText = findViewById(R.id.orderText2);//任务单号
        spinner1 = findViewById(R.id.material2);
        spinner2 = findViewById(R.id.unit2);

        //退出
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //下拉框选择
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //当选中某一个数据项时触发该方法

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                //System.out.println(spinner==parent);//true
                //System.out.println(view);
                //String data = adapter.getItem(position);//从适配器中获取被选择的数据项
                //String data = list.get(position);//从集合中获取被选择的数据项
                String data = (String)spinner1.getItemAtPosition(position);//从spinner中获取被选择的数据
                String[] codename = data.split(":");
                //   materialCodetext.setText(codename[0]);
                materialTx.setText(codename[1]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        //显示数据到spinner上
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //  Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                //  Toast.makeText(Instore1Activity.this,info[1],Toast.LENGTH_SHORT).show();
                if(msg.what == 0x0002){
                    Toast.makeText(InstockNoOrderActivity.this,"json数据失败",Toast.LENGTH_SHORT).show();
                }else {
                    showinfo();//显示数据到spinner上了
                }

            }
        };
        //上架准备 显示返回的订单号
        handler2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //  Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                if(msg.what == 0x0001) {
                    Toast.makeText(InstockNoOrderActivity.this, "请扫托盘号", Toast.LENGTH_SHORT).show();
                    orderText.setText(data2);
                }
                else {
                    Toast.makeText(InstockNoOrderActivity.this, "请求托盘失败", Toast.LENGTH_SHORT).show();
                }

            }
        };
        //上交完成 清空表单
        handler3 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //  Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                if(msg.what == 0x0001) {
                    Toast.makeText(InstockNoOrderActivity.this, "补条码成功", Toast.LENGTH_SHORT).show();
                    barText.setText("");
                    goodNUmET.setText("");
                    materialTx.setText("");
                    orderText.setText("");
                }
                else {
                    Toast.makeText(InstockNoOrderActivity.this, "补条码失败", Toast.LENGTH_SHORT).show();
                }

            }
        };

    }
    //
    //获取货物编码和名称,给data1，handler显示到下拉框上
    public void getRequest2(View view) {

        String  mCodes = materialCode.getText().toString();
        String url = "http://192.168.1.109:8080/api/getMaterialsByMaterialCode?materialCode="+mCodes;
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
                    Toast.makeText(InstockNoOrderActivity.this, "Http失败", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(InstockNoOrderActivity.this, "Http链接错误", Toast.LENGTH_LONG).show();
        }
    }
    //
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
            //  String unit = spinner1.getSelectedItem().toString();//获取选中的值
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //

    //上架准备
    public void submitData2(View view) {
        String url2 = "http://192.168.1.109:8080/api/realTimeStockIn/initEmptyStockInOrderWithParams";

        String good = spinner1.getSelectedItem().toString();//获取选中的值
        String[] goodSP = good.split(":");//编码和名称
        String countNum = goodNUmET.getText().toString();//数量
        String unitGoods = spinner2.getSelectedItem().toString();//单位
        String barcode = barText.getText().toString(); //托盘条码
     //   String orderMsg2 = "?goodCode=" + goodSP[0] + "&goodName=" + goodSP[1] + "&count=" + countNum + "&unit=" + unitGoods + "&barCode=" + barcode + "&needTray=true";
    //    String url = url2 + orderMsg2;
   //     Log.d("s", countNum);
    //    Log.d("c", barcode);
        if (countNum.equals("") || goodSP[1].equals("")) {
            Toast.makeText(InstockNoOrderActivity.this, "数据不完整！！！", Toast.LENGTH_LONG).show();
        } else {

            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                JSONObject json = new JSONObject();
                json.put("materialName", goodSP[1]);
                json.put("materialCode", goodSP[0]);
                json.put("materialAmount", countNum );
                json.put("unit", unitGoods);
                json.put("isNeedTray",false);
                Log.d("wy", String.valueOf(json));
                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                final Request request = new Request.Builder()
                        .url(url2)
                        .post(requestBody)//post
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Log.d("wy", "onFailure: ");
                        Toast.makeText(InstockNoOrderActivity.this, "Http失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String json = response.body().string();//!!!只能调用一次!!!
                      //  Log.d("wy", "onResponse: " + json);
                        try {
                            //Json数据的处理

                            String js1 = new JSONObject(json).getString("data");
                            data2 = new JSONObject(js1).getString("stockInOrderCode");

                         //   Log.d("data:", data2);

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
                Toast.makeText(InstockNoOrderActivity.this, "Http链接错误", Toast.LENGTH_LONG).show();
            }
        }
    }
    //
    //提交入库任务号
    public void submitOrder(View view) {
        String url2 = "http://192.168.1.109:8080/api/realTimeStockIn/appendTrayCode";


        String orderNum = orderText.getText().toString();//入库任务号
        String barcode = barText.getText().toString(); //托盘条码

        if (orderNum.equals("") || barcode.equals("")) {
            Toast.makeText(InstockNoOrderActivity.this, "数据不完整！！！", Toast.LENGTH_LONG).show();
        } else {

            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                JSONObject json = new JSONObject();
                json.put("stockInOrderCode",orderNum);
                json.put("trayCode",barcode);

              //  Log.d("wy", String.valueOf(json));
                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                final Request request = new Request.Builder()
                        .url(url2)
                        .post(requestBody)//post
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Log.d("wy", "onFailure: ");
                   //     Toast.makeText(InstockNoOrderActivity.this, "补托盘号失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String json = response.body().string();//!!!只能调用一次!!!
                        //  Log.d("wy", "onResponse: " + json);


                        try {
                            String js1 = new JSONObject(json).getString("state");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    handler3.sendEmptyMessage(0x0001);
                                }
                            }).start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    handler3.sendEmptyMessage(0x0002);
                                }
                            }).start();
                        }


                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(InstockNoOrderActivity.this, "Http链接错误", Toast.LENGTH_LONG).show();
            }
        }
    }
    //
}
