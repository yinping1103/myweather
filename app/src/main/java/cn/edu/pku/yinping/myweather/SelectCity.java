package cn.edu.pku.yinping.myweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city);

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        InitCityList();


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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1, cityNameList);
        cityList.setAdapter(adapter);
        //click
        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener(){ @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(SelectCity.this, "Clicked"+ i, Toast.LENGTH_SHORT).show();
            selectedCityCode = cityNumberList[i];
        }
        });
    }


    public void onClick(View v){
        switch (v.getId()){
            case R.id.title_back:
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("cityCode", "selectedCityCode");
                setResult(RESULT_OK, i);

                finish();
                break;
            default:
                break;
        }

    }

}
