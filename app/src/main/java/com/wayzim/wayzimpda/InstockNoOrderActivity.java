package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Color;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wayzim.wayzimpda.tools.SharedHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class InstockNoOrderActivity extends AppCompatActivity {
    private Handler handler,handler2,handler3;
    private  String data_spinner1 ,data_order,httpMessage;//查询货物的结果,提交的结果
    private String[] spinner_info;//查询的json数组

    private EditText materialCode,barText,goodNUmET;
    private TextView materialTx,orderText,needTary;
    private Button btnSearch,btnCode,btnExit;
    private Spinner spinner1,spinner2;
    private CheckBox check_traycode;
    private Button  sendTrayCode;

    private SharedHelper sh;
    private Context aContext;
    private Context mContext;
    private String wms_URl;
    //对话框
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private View view_custom;
    private Handler handlerDiag;
    private String diagAlertInfo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instock_no_order);

        //读取WMS的IP
        aContext = getApplicationContext();
        mContext = InstockNoOrderActivity.this;
        sh = new SharedHelper(aContext);
        Map<String,String> dataMap = sh.readURL();
        wms_URl ="http://"+dataMap.get("urlWMS");

        btnExit = findViewById(R.id.btn_exit2);
        btnSearch = findViewById(R.id.btn_search2);
        check_traycode =findViewById(R.id.check_traycode2);//是否需要托盘
        needTary = findViewById(R.id.needtray);//是否需要托盘
        materialCode = findViewById(R.id.mCode2);//查询的关键字
        barText = findViewById(R.id.trayBarCode2);//托盘条码
        goodNUmET =findViewById(R.id.goodnumber2);//数量
        materialTx = findViewById(R.id.materialName2);//名称
        orderText = findViewById(R.id.orderText2);//任务单号
        sendTrayCode =findViewById(R.id.btn_sendcode);//补托盘码按钮
        spinner1 = findViewById(R.id.material2);
        spinner2 = findViewById(R.id.unit2);

        //是否需要托盘
     check_traycode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             if(isChecked){
                 needTary.setText("请求托盘，须补发托盘条码");
             }else{
              //   needTary.setText("");
                 needTary.setText("请输入托盘条码");
             }
         }
     });
     // enter键 查询货物?????
     materialCode.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            /**
             * 参数说明
             * @param v 被监听的对象
             * @param actionId  动作标识符,如果值等于EditorInfo.IME_NULL，则回车键被按下。
             * @param event    如果由输入键触发，这是事件；否则，这是空的(比如非输入键触发是空的)。
             * @return 返回你的动作
             */
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    Log.i("---","搜索操作执行");
                    getMaterialInfo2(v);
                }
                return false;
            }
        });
        // 条码限制扫一次???????
        barText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            /**
             * 参数说明
             * @param v 被监听的对象
             * @param actionId  动作标识符,如果值等于EditorInfo.IME_NULL，则回车键被按下。
             * @param event    如果由输入键触发，这是事件；否则，这是空的(比如非输入键触发是空的)。
             * @return 返回你的动作
             */
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    Log.i("---","搜索操作执行");
                   // barText.setCursorVisible(false);//隐藏光标,还可输入的
                    barText.clearFocus();//失去焦点
                }
                return false;
            }
        });

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

        //显示物料信息到spinner1上
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x0002){
                    Toast.makeText(InstockNoOrderActivity.this,"查询物料信息失败，获取不到data",Toast.LENGTH_LONG).show();
                }else if(msg.what == 0x0003){
                    showDialogInfo("URL错误,无法请求HTTP");
                } else {
                    showSpinnerInfo();//显示数据到spinner上了
                }

            }
        };
        //上架准备 显示返回的入库单号
        handler2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //  Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                if(msg.what == 0x0001) {
                    boolean needtray2 = check_traycode.isChecked(); //是否需要托盘
                    if(needtray2) {
                        //需要补码
                        orderText.setText(data_order);
                        materialTx.setText("");//防止上架准备重复提交
                        //需要补托盘码,按钮颜色改变
                        sendTrayCode.setBackgroundColor(Color.parseColor("#AA6600"));
                        Toast.makeText(InstockNoOrderActivity.this, "请扫托盘号", Toast.LENGTH_LONG).show();
                    }else {
                        //不需要补码，完成上架
                        barText.setText("");//托盘条码
                        goodNUmET.setText("");
                        materialTx.setText("");
                        orderText.setText("");
                        Toast.makeText(InstockNoOrderActivity.this, "上架成功", Toast.LENGTH_LONG).show();

                    }

                }else if(msg.what == 0x0003){
                    showDialogInfo("无源入库，发送上架物料信息,http Failure");
                }
                else {
                   // Toast.makeText(InstockNoOrderActivity.this, "请求托盘失败", Toast.LENGTH_SHORT).show();
                    showDialogInfo(httpMessage);
                }

            }
        };
        //补码完成 清空表单
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
                    sendTrayCode.setBackgroundColor(Color.parseColor("#00BCD4"));
                }
                else if(msg.what == 0x0003){
                    showDialogInfo("无源入库，补发托盘码,http Failure");
                }
                else {
                    Toast.makeText(InstockNoOrderActivity.this, "补条码失败", Toast.LENGTH_SHORT).show();
                }

            }
        };
       //对话框
        handlerDiag = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                int what = msg.what;
                if (what == 0) {	//update
                    TextView tv = (TextView) alert.findViewById(R.id.dialog_info);
                    tv.setText(diagAlertInfo);

                }else {
                    alert.cancel();
                }
            }
        };

    }
    //
    //获取货物编码和名称,给data1，handler处理
    public void getMaterialInfo2(View view) {

        String  mCodes = materialCode.getText().toString();
        String url = wms_URl+":8080/api/getMaterialsByMaterialCode?materialCode="+mCodes;
     //   String url = "http://120.27.143.134:8080/api/getMaterialsByMaterialCode?materialCode=2";
        try {
            OkHttpClient okHttpClient = new OkHttpClient();//.addHeader("Connection", "close")
            final Request request = new Request.Builder()
                    .url(url)
                    .get()//默认就是GET请求，可以不写
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("无源入库，物料编码查询名称", "onFailure: URL失败");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                           handler.sendEmptyMessage(0x0003);//  showDialogInfo必须在handler里
                        }
                    }).start();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();//!!!只能调用一次!!!
             //       Log.d("无源入库，物料编码查询名称", "onResponse: " + json);
                    try {
                        //Json数据的处理
                        JSONObject jsonObject = new JSONObject(json);
                        data_spinner1 = jsonObject.getString("data");
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
  //          showDialogInfo("无源入库，物料编码查询名称 URL错误");
        }
    }
    //
    //handler显示下拉框数据
    private void showSpinnerInfo(){
        try {
            JSONArray jsonArray = new JSONArray(data_spinner1);
            int length = jsonArray.length();
            spinner_info = new String[length];
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);//i从零开始
                spinner_info[i] = jsonObject1.getString("materialCode") + ":" + jsonObject1.getString("materialName");
            }
            //下拉菜单
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinner_info);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner1.setAdapter(adapter);
            //  String unit = spinner1.getSelectedItem().toString();//获取选中的值
        }catch (Exception e){
            e.printStackTrace();
            Log.d("spinner1填充物料编码物料名称","Failure");
        }
    }
    //
    //上架准备
    public void submitData2(View view) {
        String url2 = wms_URl+":8080/api/realTimeStockIn/initEmptyStockInOrderWithParams";
        String materialName = materialTx.getText().toString();//名称
        String countNum = goodNUmET.getText().toString();//数量
        String unitGoods = spinner2.getSelectedItem().toString();//单位
        String trayCode = barText.getText().toString(); //托盘条码
        boolean needtray = check_traycode.isChecked(); //是否需要托盘
        //okhttp
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try{
            String good = spinner1.getSelectedItem().toString();//获取sipnner的值
            String[] goodSP = good.split(":");//编码和名称
            if (countNum.equals("") || materialName.equals("")) {
                Toast.makeText(InstockNoOrderActivity.this, "数据不完整！！！", Toast.LENGTH_LONG).show();
            } else {
                json.put("materialName", goodSP[1]);
                json.put("materialCode", goodSP[0]);
                json.put("materialAmount", countNum );
                json.put("unit", unitGoods);
                json.put("isNeedTray",needtray);
                Log.d("wyyy",goodSP[0]);
                if(goodSP[0].equals("10000000001")){
                    json.put("skuType",2);
                }
                RequestBody requestBody;
                if(needtray){
                    requestBody = RequestBody.create(JSON, String.valueOf(json));//请求托盘
                }else{
                    json.put("trayCode",trayCode);//没请求托盘，输入托盘码
                    requestBody = RequestBody.create(JSON, String.valueOf(json));
                }
                final Request request = new Request.Builder()
                        .url(url2)
                        .post(requestBody)//post
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("无源入库，发送上架物料信息", "onFailure:无返回？ ");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler2.sendEmptyMessage(0x0003);
                            }
                        }).start();

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String json = response.body().string();//!!!只能调用一次!!!
                        Log.d("无源入库，上架准备结果", "onResponse: " + json);
                        try {
                            String js_state1 = new JSONObject(json).getString("state");
                            httpMessage = new JSONObject(json).getString("message");
                            String js_data1 = new JSONObject(json).getString("data");//data为空,走catch，处理message
                            if(js_state1.equals("success")){
                                data_order = new JSONObject(js_data1).getString("stockInOrderCode");
                                new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    handler2.sendEmptyMessage(0x0001);
                                }
                            }).start();
                            }
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

            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(InstockNoOrderActivity.this, "数据不完整！！！", Toast.LENGTH_LONG).show();
        }

    }
    //
    //补上托盘码，提交入库任务号
    public void submitOrder(View view) {
        String url2 = wms_URl+":8080/api/realTimeStockIn/appendTrayCode";

        String instockOrderNum = orderText.getText().toString();//入库单号
        String trayBarcode = barText.getText().toString(); //托盘条码

        if (trayBarcode.equals("")) {
            //条码的长度判断？？
            Toast.makeText(InstockNoOrderActivity.this, "补上托盘码", Toast.LENGTH_LONG).show();
        } else {
            OkHttpClient okHttpClient = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject json = new JSONObject();
            try {
                json.put("stockInOrderCode",instockOrderNum);
                json.put("trayCode",trayBarcode);
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
                        Log.d("无源入库 补托盘码", "onFailure");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler3.sendEmptyMessage(0x0003);
                            }
                        }).start();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();//!!!只能调用一次!!!
                        try {
                            //这边怎么处理更好
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

            }
        }
    }
    //设置dialog
    public void showDialogInfo(String info){
        //初始化Builder
        builder = new AlertDialog.Builder(mContext);

        //加载自定义的那个View,同时设置下
        final LayoutInflater inflater = InstockNoOrderActivity.this.getLayoutInflater();
        view_custom = inflater.inflate(R.layout.view_dialog_custom, null,false);
     //    view_custom.findViewById(R.id.dialog_info).setText();
        builder.setView(view_custom);

        builder.setCancelable(false);
        alert = builder.create();
        diagAlertInfo =info;
        handlerDiag.sendEmptyMessage(0);
        alert.show();

        view_custom.findViewById(R.id.btn_dialogcancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        view_custom.findViewById(R.id.btn_blog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //     Toast.makeText(getApplicationContext(),"询问WMS", Toast.LENGTH_SHORT).show();
                alert.dismiss();
            }
        });

        view_custom.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //     Toast.makeText(getApplicationContext(), "对话框已关闭~", Toast.LENGTH_SHORT).show();
                alert.dismiss();
            }
        });
      /*  //点击弹出起对话框
        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.show();
            }
        });*/

    }
    //
}
