package cn.edu.pku.yinping.myweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.yinping.app.MyApplication;
import cn.edu.pku.yinping.bean.City;

/**
 * Created by ag on 10/18/16.
 */

public class SelectCity extends Activity implements View.OnClickListener{

    private ImageView mBackBtn;
    private ListView cityListview;
    private ArrayList<String> cityNameList;
    private ArrayList<String> cityNumberList;
    private ArrayList<String> show_List;
    private ArrayList<String> new_List;
    private String selectedCityCode;
    private String selectedCity;
//    new
    private EditText sEdit;
    private ImageView sDelete;
    private ArrayAdapter<String> adapter; List<City> cityList;
    private Handler myhandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        sDelete = (ImageView) findViewById(R.id.search_delete);
        sDelete.setOnClickListener(this);
        cityNameList = new ArrayList<String>();
        cityNumberList = new ArrayList<String>();
        show_List = new ArrayList<String>();
        new_List = new ArrayList<String>();
        set_sEdit_TextChanged();
        InitCityList();
    }

    private void InitCityList(){
        MyApplication myApp = (MyApplication)this.getApplication();
        cityList = myApp.getCityList();
        for (int i = 0; i < cityList.size(); i++){
            cityNameList.add(cityList.get(i).getCity().toString());
            cityNumberList.add(cityList.get(i).getNumber().toString());
            String s = cityList.get(i).getNumber().toString()+"-"+cityList.get(i).getCity().toString();
            show_List.add(s);
            new_List.add(s);
        }

        cityListview = (ListView)findViewById(R.id.selection_list);
        adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1, new_List);
        cityListview.setAdapter(adapter);
        cityListview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCityCode = new_List.get(i).substring(0,9);
                selectedCity = new_List.get(i).substring(10);

                Toast.makeText(SelectCity.this, selectedCity +"天气", Toast.LENGTH_SHORT).show();

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

        SharedPreferences mySharedPreferences = getSharedPreferences("config", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("main_city_code",selectedCityCode );
        editor.commit();

        finish();

    }


    private void set_sEdit_TextChanged() {
        sEdit = (EditText) findViewById(R.id.search_edit);
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
                myhandler.post(eChanged);

                if (s.length() == 0) {
                    sDelete.setVisibility(View.GONE);
                } else {
                    sDelete.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    Runnable eChanged = new Runnable() {
        @Override
        public void run() {
            String s_data = sEdit.getText().toString();
            new_List.clear();
            getnew_ListSub(new_List, s_data);
            adapter.notifyDataSetChanged();
        }
    };

    private void getnew_ListSub(ArrayList<String> new_ListSubs, String s_data){
        int l = show_List.size();
        for (int i = 0; i < l; ++i){
            if (show_List.get(i).contains(s_data)){
                String item = show_List.get(i);
                new_ListSubs.add(item);
            }
        }

    }


}