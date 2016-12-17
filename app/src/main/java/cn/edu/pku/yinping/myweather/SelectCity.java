package cn.edu.pku.yinping.myweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.yinping.app.MyApplication;

/**
 * Created by ag on 10/18/16.
 */

public class SelectCity extends Activity implements View.OnClickListener{

    private ImageView mBackBtn;
    private ListView cityList;
    private String[] cityNameList;
    private String[] cityNumberList;
    private String selectedCityCode;
    private String selectedCity;
//    new
    private EditText sEdit;
    private ImageView sDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        sEdit = (EditText) findViewById(R.id.search_edit);
        sDelete = (ImageView) findViewById(R.id.search_delete);
        mBackBtn.setOnClickListener(this);
        sDelete.setOnClickListener(this);
        InitCityList();
        set_sEdit_TextChanged();

    }

    private void InitCityList(){
        MyApplication myApp = (MyApplication)this.getApplication();
        int l = myApp.getCityList().size();
        List<String> cn = new ArrayList<String>();
        List<String> cn2 = new ArrayList<String>();
        for (int i = 0; i < l; ++i){
            cn.add(myApp.getCityList().get(i).getCity());
            cn2.add(myApp.getCityList().get(i).getNumber());
        }
        cityNameList = (String[])cn.toArray(new String[cn.size()]);
        cityNumberList = (String[])cn2.toArray(new String[cn2.size()]);

        cityList = (ListView)findViewById(R.id.selection_list);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1, cityNameList);
        cityList.setAdapter(adapter);
        //click
        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCityCode = cityNumberList[i];
                selectedCity = cityNameList[i];
                Toast.makeText(SelectCity.this, "您选择了"+ selectedCity, Toast.LENGTH_SHORT).show();

                back();
            }
        });
    }


    public void onClick(View view){
        if (view.getId() == R.id.search_delete){
            sEdit.setText("");
        }
        if (view.getId() == R.id.title_back){
            Intent i = new Intent(this, MainActivity.class);
            finish();
        }
    }

    public void back(){
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("cityCode", selectedCityCode);
        setResult(RESULT_OK, i);
        finish();
    }


    public void set_sEdit_TextChanged() {

        sEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

                if (s.length() == 0) {
                    sDelete.setVisibility(View.GONE);//当文本框为空时，则叉叉消失
                } else {
                    sDelete.setVisibility(View.VISIBLE);//当文本框不为空时，出现叉叉
                }
            }
        });

    }




}