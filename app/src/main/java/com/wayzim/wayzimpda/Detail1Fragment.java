package com.wayzim.wayzimpda;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.zip.Inflater;

public class Detail1Fragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView  traytext,binCodetext;
    private EditText materialCodetext,counttext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_detail1,container,false);
        traytext= view.findViewById(R.id.trayTX);//托盘
        materialCodetext=  view.findViewById(R.id.materialCodeTX);
        counttext=  view.findViewById(R.id.countTX);
        binCodetext=  view.findViewById(R.id.binCodeTX);//货位

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





    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    //

    }
}
