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
import java.util.zip.Inflater;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Detail1Fragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView  traytext,binCodetext,materialNametext;
    private EditText materialCodetext,counttext;
    private Button btnCheck,btnSubmit,btnExit;
    private Handler handler,handler2;
    private  String data1,data2;//查询货物的结果,提交的结果
    private Spinner spinner1,spinner2;
    private String[] info;//查询的json数组
    private int lenCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_detail1,container,false);
        traytext= view.findViewById(R.id.trayTX);//托盘
        materialCodetext=  view.findViewById(R.id.materialCodeTX);//物料编号
         materialNametext=  view.findViewById(R.id.materialNameTX);//物料名称
        counttext=  view.findViewById(R.id.countTX);//数量
        binCodetext=  view.findViewById(R.id.binCodeTX);//货位

        btnCheck = view.findViewById(R.id.check1);
        btnSubmit= view.findViewById(R.id.btn_submit1);//提交
        btnExit =view.findViewById(R.id.btn_exit);
        spinner1 = view.findViewById(R.id.spinner1);
        //后退
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
       //查询物料
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code1 = materialCodetext.getText().toString();
                getMaterial2(code1);
            }
        });
        //提交
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              submit();
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
                materialCodetext.setText(codename[0]);
                materialNametext.setText(codename[1]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //  Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                //  Toast.makeText(Instore1Activity.this,info[1],Toast.LENGTH_SHORT).show();
                if(msg.what == 0x0002){
                    Toast.makeText(getActivity(),"json数据失败",Toast.LENGTH_SHORT).show();
                }else {
                    showinfo2();

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
                    traytext.setText("");
                    materialCodetext.setText("");
                    materialNametext.setText("");
                    counttext.setText("");
                    binCodetext.setText("");

                    //清空下拉菜单

                }
                else {
                    Toast.makeText(getActivity(), "提交失败", Toast.LENGTH_SHORT).show();
                }

            }
        };

        Bundle bundle =this.getArguments();//得到从Activity传来的数据
        String mess = null;
        if(bundle!=null){
            mess = bundle.getString("data");
            Log.d("wy", mess);
            try {
                JSONObject jsonObject = new JSONObject(mess);
                String tray_code= jsonObject.getString("tray_code");
                String materialCode= jsonObject.getString("materialCode");
                String amount= jsonObject.getString("amount");
                String binCode= jsonObject.getString("binCode");

                traytext.setText(tray_code);
                materialCodetext.setText(materialCode);
                counttext.setText(amount);
                binCodetext.setText(binCode);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return view;
    }

    private void showinfo2(){
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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, info);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner1.setAdapter(adapter);
            String unit = spinner1.getSelectedItem().toString();//获取选中的值
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //提交
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

    //获取货物编码和名称
    public void getMaterial2(String code) {

      //  String  mCodes = materialCode.getText().toString();
        String url = "http://192.168.1.103:8080/api/getMaterialsByMaterialCode?materialCode="+code;
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
                 //   Toast.makeText(Instore1Activity.this, "Http失败", Toast.LENGTH_SHORT).show();
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
         //   Toast.makeText(Instore1Activity.this, "Http链接错误", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
