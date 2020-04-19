package com.wayzim.wayzimpda;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Detail2Fragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView  instockTX2,materialNametext,materialCodetext,task_id,count;
    private EditText counttext,trayCodeTX2;
    private Button btnSubmit,btnExit;

    private Handler handler,handler2;
    private  String data1,data2;//查询货物的结果,提交的结果
    private Spinner spinner1,spinner2;
    private String[] info;//查询的json数组
    private String unit;//单位

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

        btnSubmit= view.findViewById(R.id.btn_submit2);//提交
        btnExit =view.findViewById(R.id.btn_exit22);//退出

        //后退
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        //提交
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //  submit();
            }
        });

       //下拉框选择
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
                    Toast.makeText(getActivity(), "提交成功", Toast.LENGTH_SHORT).show();
                    instockTX2.setText("");
                    task_id.setText("");
                    materialNametext.setText("");
                  //  counttext.setText("");
                  //  binCodetext.setText("");

                    //清空下拉菜单

                }
                else {
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
                String instock_code= jsonObject.getString("instock");//入库单
                String taskid_code= jsonObject.getString("task_id");//任务号
                String material_name= jsonObject.getString("materialName");//物料名称
              //  String binCode= jsonObject.getString("binCode");

                instockTX2.setText(instock_code);
                task_id.setText(taskid_code);
                materialNametext.setText(material_name);
              //  binCodetext.setText(binCode);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return view;
    }


    //提交
    /*
    private void submit(){
        String tray = traytext.getText().toString();
        String mcode =materialCodetext.getText().toString();
        String mname = materialNametext.getText().toString();
        String count =  counttext.getText().toString();
        String bincode= binCodetext.getText().toString();
        String url2 = "http://192.168.1.103:8080/api/updateBin2MaterialByTrayCode";
        // traycode=tp_1001&materialCode=01.01.212-0104&amount=10&binCode=01A0010102
        String message1 = "?traycode="+tray+"&materialCode="+mcode+"&amount="+count+"&binode="+bincode;
        String url= url2+ message1;
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
  */


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
