package com.wayzim.wayzimpda;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class Detail2Fragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView  instockTX2,materialNametext,materialCodetext,task_id,count;
    private EditText counttext,trayCodeTX2;
    private Button btnSubmit3,btnSendcode2,btnExit;
    private CheckBox check_traycode2;

    private Handler handler,handler2,handler3;
    private  String data1,data2;//查询货物的结果,提交的结果
    private Spinner spinner1,spinner2;
    private String[] info;//查询的json数组
    private String unit;//单位

    private SharedHelper sh;
    private Context aContext;
    private String wms_URl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_detail2,container,false);

        instockTX2= view.findViewById(R.id.instockTX2);//入库单号
        task_id = view.findViewById(R.id.task_id);//任务号
        materialCodetext=  view.findViewById(R.id.materialCodeTX2);//物料编号
         materialNametext=  view.findViewById(R.id.materialNameTX2);//物料名称
        count = view.findViewById(R.id.countTX2);//数量
        counttext=  view.findViewById(R.id.countSure);//核对数量
        spinner1 = view.findViewById(R.id.unit22); //单位
        trayCodeTX2 =view.findViewById(R.id.trayCodeTX2);//托盘号
        check_traycode2 =view.findViewById(R.id.check_traycode2);//是否需要托盘

        btnSubmit3= view.findViewById(R.id.submit3);//上架准备
        btnSendcode2 =view.findViewById(R.id.btn_sendcode2);//补托盘码
        btnExit =view.findViewById(R.id.btn_exit3);//退出

        //读取WMS的IP
        aContext = getActivity().getApplicationContext();
        sh = new SharedHelper(aContext);
        Map<String,String> dataMap = sh.readURL();
        wms_URl ="http://"+dataMap.get("urlWMS");
        //后退
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        //提交
        btnSubmit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitOrder2();
            }
        });
        btnSendcode2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //补码
            }
        });

       //下拉框选择 货物单位
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //当选中某一个数据项时触发该方法

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {

                String unit_data = (String)spinner1.getItemAtPosition(position);//从spinner中获取被选择的数据
                unit = unit_data;//单位

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        //提交数据完成
        handler2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //  Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                if(msg.what == 0x0001) {
                    boolean needtray2 = check_traycode2.isChecked();
                    if(needtray2){//需要托盘码
                        Toast.makeText(getActivity(), "补上托盘码", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(), "提交成功", Toast.LENGTH_SHORT).show();
                        instockTX2.setText("");
                        task_id.setText("");
                        materialNametext.setText("");
                        count.setText("");
                        materialCodetext.setText("");
                        trayCodeTX2.setText("");
                        counttext.setText("");
                    }

                }
                else {
                    Toast.makeText(getActivity(), "提交失败", Toast.LENGTH_SHORT).show();
                }

            }
        };

        //补码完成 清空表单
        handler3 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //  Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                if(msg.what == 0x0001) {
                    Toast.makeText(getActivity(), "补条码成功", Toast.LENGTH_SHORT).show();
                    instockTX2.setText("");
                    task_id.setText("");
                    materialNametext.setText("");
                    count.setText("");
                    materialCodetext.setText("");
                    trayCodeTX2.setText("");
                    counttext.setText("");
                    btnSendcode2 .setBackgroundColor(Color.parseColor("#00BCD4"));
                }
                else {
                    Toast.makeText(getActivity(), "补条码失败", Toast.LENGTH_SHORT).show();
                }

            }
        };

        //得到从Activity传来的数据
        Bundle bundle =this.getArguments();
        String mess = null;
        if(bundle!=null){
            mess = bundle.getString("data");
            Log.d("wy", mess);
            try {
                JSONObject jsonObject = new JSONObject(mess);
                String instock_code= jsonObject.getString("instock");//入库单
                String taskid_code= jsonObject.getString("task_id");//任务号
                String material_name= jsonObject.getString("materialName");//物料名称
                String material_Code = jsonObject.getString("materialCode");
                String material_Count = jsonObject.getString("materialCount");
              //  String binCode= jsonObject.getString("binCode");

                instockTX2.setText(instock_code);
                task_id.setText(taskid_code);
                materialNametext.setText(material_name);
                materialCodetext.setText(material_Code);
                count.setText(material_Count);
              //  binCodetext.setText(binCode);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return view;
    }


    //上架准备

    private void submitOrder2(){
        String instockCode = instockTX2.getText().toString();//入库单
        String task_code = task_id .getText().toString();//任务号

        String materialCode =materialCodetext.getText().toString();//物料编码
        String materialName = materialNametext.getText().toString();//物料名称
        String count1 = count.getText().toString();//数量
        String countSure =  counttext.getText().toString();//核对数量
        String traycode1= trayCodeTX2.getText().toString();//托盘条码
        String unitGoods2 = spinner1.getSelectedItem().toString();//单位

        boolean needtray = check_traycode2.isChecked(); //是否需要托盘

        String url2 = wms_URl+":8080/api/pdaQueryStockIn/ensureToStockInPerTray";

        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject json = new JSONObject();
            json.put("stockInOrderCode",instockCode);
            json.put("taskNo",task_code);
            json.put("materialName", materialName );
            json.put("materialCode", materialCode);
            json.put("materialAmount", count1 );
            json.put("materialAmountReceived",countSure);
            json.put("unit", unitGoods2);
            json.put("isNeedTray",needtray);

            RequestBody requestBody;
            if(needtray){
                requestBody = RequestBody.create(JSON, String.valueOf(json));
                //需要补托盘码,按钮颜色改变
                btnSendcode2.setBackgroundColor(Color.parseColor("#AA6600"));
            }else{
                json.put("trayCode",traycode1);//没请求托盘，输入托盘码
                requestBody = RequestBody.create(JSON, String.valueOf(json));
            }
            final Request request = new Request.Builder()
                    .url(url2)
                    .post(requestBody)//默认就是GET请求，可以不写
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("wy", "onFailure: ");
                    Toast.makeText(getActivity(), "httpURL失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String json = response.body().string();//!!!只能调用一次!!!
                    Log.d("wy", "onResponse: " + json);
                    try {
                        //Json数据的处理
                        JSONObject jsonObject = new JSONObject(json);
                        data2 = jsonObject.getString("state");
                        //  Log.d("data:", data1);

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
            Toast.makeText(getActivity(), "Http链接错误", Toast.LENGTH_LONG).show();
        }
    }

    //补上托盘码，提交入库任务号
    public void submitOrder3(View view) {
        String url2 = wms_URl+":8080/api/pdaQueryStockIn/appendTrayCode";

        String orderNum = instockTX2.getText().toString();//入库单号
        String taskNum =  task_id.getText().toString(); //任务号
        String barcode = trayCodeTX2.getText().toString(); //托盘条码

        if (orderNum.equals("") || barcode.equals("")) {
            Toast.makeText(getActivity(), "数据不完整！！！", Toast.LENGTH_LONG).show();
        } else {

            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                JSONObject json = new JSONObject();
                json.put("stockInOrderCode",orderNum);
                json.put("taskNo",taskNum );
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
                        Toast.makeText(getActivity(), "okhttp 响应失败！", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();//!!!只能调用一次!!!
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
                Toast.makeText(getActivity(), "Http链接错误", Toast.LENGTH_LONG).show();
            }
        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
