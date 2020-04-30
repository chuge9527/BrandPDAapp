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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Detail3Fragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView  invent_binCode,inventory_materialName,invent_materialCode,invent_trayCode,invent_count,inventory_TaskId;
    private EditText inventory_countSure;
    private Button inventory_submit,inventory_request,btnExit;


    private Handler handler1,handler2,handler3;
    private  String data_request,data2;//查询货物的结果,提交的结果
    private Spinner spinner1,spinner2;
    private String[] info;//查询的json数组
    private String unit;//单位

    private SharedHelper sh;
    private Context aContext;
    private String wms_URl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_detail3,container,false);

        inventory_TaskId =view.findViewById(R.id.inventory_TaskId);
        invent_binCode= view.findViewById(R.id.inventory_binCode);//库位号
        invent_trayCode = view.findViewById(R.id.inventory_trayId);//托盘码
        invent_materialCode =  view.findViewById(R.id.inventory_materialCode2);//物料编号
        inventory_materialName=  view.findViewById(R.id.inventory_materialName2);//物料名称
        invent_count = view.findViewById(R.id.inventory_countTX2);//数量
        inventory_countSure=  view.findViewById(R.id.inventory_countSure);//核对数量
        spinner1 = view.findViewById(R.id.unit22); //单位
        inventory_request = view.findViewById(R.id.inventory_request);//请求
        inventory_submit= view.findViewById(R.id.inventory_submit);//提交
        btnExit =view.findViewById(R.id.btn_exit4);//退出

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
        //请求
        inventory_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestInvent();
            }
        });
        //提交
        inventory_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitInvent();
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
        //请求盘点
        handler1 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //  Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                if(msg.what == 0x0001) {
                    Toast.makeText(getActivity(), "请求成功", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "请求失败", Toast.LENGTH_SHORT).show();
                }

            }
        };

        //提交数据完成
        handler2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //  Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                if(msg.what == 0x0001) {

                    Toast.makeText(getActivity(), "提交成功", Toast.LENGTH_SHORT).show();
                    inventory_TaskId.setText("");
                    invent_binCode.setText("");
                    invent_trayCode.setText("");
                    invent_materialCode.setText("");
                    inventory_materialName.setText("");
                    invent_count.setText("");
                    inventory_countSure.setText("");
                } else {
                    Toast.makeText(getActivity(), "提交失败", Toast.LENGTH_SHORT).show();
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
                //
                //物料编码
                //
                String task_id =jsonObject.getString("task_id");//任务号
                String bin_code1= jsonObject.getString("bin_code");//库位
                String tray_code1= jsonObject.getString("tray_code");//托盘码
                String material_Name= jsonObject.getString("material_name");//物料名称
                String material_Code = jsonObject.getString("material_code");
                String material_Count = jsonObject.getString("record_amount");
              //  String binCode= jsonObject.getString("binCode");
                inventory_TaskId.setText(task_id);
                invent_binCode.setText(bin_code1);
                invent_trayCode.setText(tray_code1);
                invent_materialCode.setText(material_Code);
                inventory_materialName.setText(material_Name);
                invent_count.setText(material_Count);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //
        }

        return view;
    }
    //请求
    private void requestInvent(){
        String task_id = inventory_TaskId.getText().toString();//任务号


        String url2 = wms_URl+":8088/api/executeInventoryCheckPlan";
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            FormBody formBody = new FormBody.Builder()
                    .add( "inventoryCheckPlanId",task_id)
                    .build();
               //     .add("bin_code",bin_Code)
               //     .add("tray_code",tray_code)

            final Request request = new Request.Builder()
                    .url(url2)
                    .post(formBody)//默认就是GET请求，可以不写
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("wy", "onFailure: ");

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();//!!!只能调用一次!!!
                    Log.d("wy", "onResponse: " + json);
                    try {
                        //Json数据的处理
                        JSONObject jsonObject = new JSONObject(json);
                        data_request = jsonObject.getString("state");
                        //  Log.d("data:", data1);
                       if(data_request.equals("success")) {
                           new Thread(new Runnable() {
                               @Override
                               public void run() {
                                   handler1.sendEmptyMessage(0x0001);
                               }
                           }).start();
                       }else{
                           new Thread(new Runnable() {
                               @Override
                               public void run() {
                                   handler1.sendEmptyMessage(0x0002);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //提交

    private void submitInvent(){
        String bin_Code = invent_binCode.getText().toString();//库位
        String tray_code = invent_trayCode .getText().toString();//托盘码
        String material_Code =invent_materialCode.getText().toString();//物料编码
        String material_Name = inventory_materialName.getText().toString();//物料名称
        String count = invent_count.getText().toString();//数量
        String countSure =  inventory_countSure.getText().toString();//核对数量
        String unitGoods = spinner1.getSelectedItem().toString();//单位

        String url2 = wms_URl+":8088/api/updateBin2MaterialByTrayCode";
        try {
            OkHttpClient okHttpClient = new OkHttpClient();

            FormBody formBody = new FormBody.Builder()
                    .add( "id","107")
                    .add("bin_code",bin_Code)
                    .add("tray_code",tray_code)
                    .add("material_code", material_Code)
                    .add("material_name", material_Name)
                    .add("record_amount", count)
                    .add("tray_code",tray_code)
                    .add("real_amount",countSure)
                    .add("unit_name",unitGoods)
                    .build();


            final Request request = new Request.Builder()
                    .url(url2)
                    .post(formBody)//默认就是GET请求，可以不写
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("wy", "onFailure: ");

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
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
