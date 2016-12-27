package cn.edu.pku.yinping.myweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

import cn.edu.pku.yinping.app.MyApplication;
import cn.edu.pku.yinping.bean.City;
import cn.edu.pku.yinping.bean.TodayWeather;
import cn.edu.pku.yinping.util.NetUtil;

import static cn.edu.pku.yinping.myweather.R.id.title_update_btn;
import static cn.edu.pku.yinping.myweather.R.id.title_update_progress;
import static cn.edu.pku.yinping.myweather.R.id.tittle_location;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;


/**
 * Created by ag on 9/20/16.
 */

public class MainActivity extends Activity implements View.OnClickListener {

    private static final int UPDATE_TODAY_WEATHER = 1;

    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private ProgressBar mtitle_update_progress;

    private static final String TAG = "dzt";
    private ImageView mstartLocation;
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();

    List<City> cityList;
    private String location_result;

    private TextView cityTv, timeTv, humidityTv, temperature_nowTv, weekTv, pmDataTv,
            pmQualityTv, temperatureTv, climateTv, windTv, city_name_Tv,
            weekTv1, climateTv1, temperatureTv1,windTv1,
            weekTv2, climateTv2, temperatureTv2,windTv2,
            weekTv3, climateTv3, temperatureTv3,windTv3;
    private ImageView weatherImg, pmImg,weatherImg1,weatherImg2,weatherImg3;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wether_info);

        mUpdateBtn = (ImageView) findViewById(title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        mtitle_update_progress = (ProgressBar) findViewById(title_update_progress);

        //定位
        mstartLocation = (ImageView) findViewById(tittle_location) ;
        mstartLocation.setOnClickListener(this);

        mLocationClient = new LocationClient(getApplicationContext()); //声明类
        mLocationClient.registerLocationListener(myListener); //注册监听
        initLocationOption();

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myweather", "网络OK");
            Toast.makeText(MainActivity.this, "网络OK", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myweather", "Wrong Network");
            Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
        }

        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        initView();
        initView_week();
    }

    void initView() {
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        temperature_nowTv = (TextView) findViewById(R.id.temperature_now);

        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm25_quality);
        pmImg = (ImageView) findViewById(R.id.pm25_img);

        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);

        city_name_Tv.setText("北京天气");
        cityTv.setText("北京");
        timeTv.setText("16：00发布");
        humidityTv.setText("湿度：74%");
        temperature_nowTv.setText("当前温度 1°");
        pmDataTv.setText("121");
        pmQualityTv.setText("轻度污染");
        pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
        weekTv.setText("星期二");
        temperatureTv.setText("-4° ~ 5°");
        climateTv.setText("晴");
        windTv.setText("东风 1级");
    }
    void initView_week() {
        weekTv1 = (TextView) findViewById(R.id.week_today1);
        weatherImg1 = (ImageView) findViewById(R.id.weather_img1);
        temperatureTv1 = (TextView) findViewById(R.id.temperature1);
        climateTv1 = (TextView) findViewById(R.id.climate1);
        windTv1 = (TextView) findViewById(R.id.wind1);
        weekTv2 = (TextView) findViewById(R.id.week_today2);
        weatherImg2 = (ImageView) findViewById(R.id.weather_img2);
        temperatureTv2 = (TextView) findViewById(R.id.temperature2);
        climateTv2 = (TextView) findViewById(R.id.climate2);
        windTv2 = (TextView) findViewById(R.id.wind2);
        weekTv3 = (TextView) findViewById(R.id.week_today3);
        weatherImg3 = (ImageView) findViewById(R.id.weather_img3);
        temperatureTv3 = (TextView) findViewById(R.id.temperature3);
        climateTv3 = (TextView) findViewById(R.id.climate3);
        windTv3 = (TextView) findViewById(R.id.wind3);


        weekTv1.setText("星期四");
        weatherImg1.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        temperatureTv1.setText("-2° ~ 6°");
        climateTv1.setText("多云");
        windTv1.setText("南风 1级");
        weekTv2.setText("星期五");
        weatherImg2.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        temperatureTv2.setText("-2° ~  -4°");
        climateTv2.setText("阴");
        windTv2.setText("北风 2级");
        weekTv3.setText("星期六");
        weatherImg3.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        temperatureTv3.setText("-4° ~ 3°");
        climateTv3.setText("多云转晴");
        windTv3.setText("东南风 1级");


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tittle_location){
            mLocationClient.start();// 开始定位

        }

        if (view.getId() == R.id.title_city_manager){
            Intent i = new Intent(this, SelectCity.class);
            startActivityForResult(i,1);
        }

        if (view.getId() == title_update_btn) {

            mtitle_update_progress.setVisibility(View.VISIBLE);
            mUpdateBtn.setVisibility(View.INVISIBLE);

            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("update_myweather", cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myweather", "网络OK");
                queryWeatherCode(cityCode);
            } else {
                Log.d("myweather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
            }
        }


    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        mLocationClient.stop();// 停止定位
    }


    private void initLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        option.setAddrType("all");// 返回的定位结果包含地址信息
        option.setScanSpan(0);// 设置发起定位请求的间隔时间为？ms
        mLocationClient.setLocOption(option);
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            StringBuffer sb = new StringBuffer(256);
            sb.append(location.getCity());
            sb.append(location.getDistrict());

            int l = location.getDistrict().length() - 1;
            location_result = location.getDistrict().substring(0,l);
            locationResult();

            Toast.makeText(MainActivity.this, "您所在的城市是"+sb.toString(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onReceiveLocation " + sb.toString());
        }
    }

    private void locationResult() {
        MyApplication myApp = (MyApplication) this.getApplication();
        cityList = myApp.getCityList();
        for (int i = 0; i < cityList.size(); i++) {
            if (cityList.get(i).getCity().toString().equals(location_result)){
                Log.i("location","match");
                city_name_Tv.setText(location_result + "天气");
                cityTv.setText(location_result);
                String location_code = cityList.get(i).getNumber().toString();
                queryWeatherCode(location_code);

                SharedPreferences mySharedPreferences = getSharedPreferences("config", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putString("main_city_code",location_code );
                editor.commit();
                Log.d("location_code","定位的城市：" + location_code);

            } ;

        }
    }

//  接收返回的数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode= data.getStringExtra("cityCode");
            Log.d("myWeather", "选择的城市代码为"+newCityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(newCityCode);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

//parm.citycode

    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myweather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null)
                    {
                        response.append(str);
                        Log.d("myweather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myweather", responseStr);

                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null) {
                        Log.d("myWeather", todayWeather.toString());

                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();
    }


    private TodayWeather parseXML(String xmldata) {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    // 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // 判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {

                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
//                                Log.d("myWeather", "city: " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
//                                Log.d("myWeather", "updatetime: " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
//                                Log.d("myWeather", "shidu: " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
//                                Log.d("myWeather", "wendu: " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
//                                Log.d("myWeather", "pm25: " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
//                                Log.d("myWeather", "quality: " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
//                                Log.d("myWeather", "fengxiang: " + xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
//                                Log.d("myWeather", "fengli: " + xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
//                                Log.d("myWeather", "date: " + xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText());
//                                Log.d("myWeather", "high: " + xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText());
//                                Log.d("myWeather", "low: " + xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
//                                Log.d("myWeather", "type: " + xmlPullParser.getText());
                                typeCount++;
                            }
                            else if (xmlPullParser.getName().equals("type") && typeCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType1(xmlPullParser.getText());
//                                Log.d("myWeather", "type: " + xmlPullParser.getText());
                                typeCount++;
                            }
                            else if (xmlPullParser.getName().equals("type") && typeCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType2(xmlPullParser.getText());
//                                Log.d("myWeather", "type: " + xmlPullParser.getText());
                                typeCount++;
                            }
                            else if (xmlPullParser.getName().equals("type") && typeCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType3(xmlPullParser.getText());
//                                Log.d("myWeather", "type: " + xmlPullParser.getText());
                                typeCount++;
                            }
                            else if (xmlPullParser.getName().equals("date") && dateCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate1(xmlPullParser.getText());
//                                Log.d("myWeather", "date: " + xmlPullParser.getText());
                                dateCount++;
                            }
                            else if (xmlPullParser.getName().equals("date") && dateCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate2(xmlPullParser.getText());
//                                Log.d("myWeather", "date: " + xmlPullParser.getText());
                                dateCount++;
                            }
                            else if (xmlPullParser.getName().equals("date") && dateCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate3(xmlPullParser.getText());
//                                Log.d("myWeather", "date: " + xmlPullParser.getText());
                                dateCount++;
                            }
                            else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText());
//                                Log.d("myWeather", "low: " + xmlPullParser.getText());
                                lowCount++;
                            }else if (xmlPullParser.getName().equals("low") && lowCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow1(xmlPullParser.getText());
//                                Log.d("myWeather", "low: " + xmlPullParser.getText());
                                lowCount++;
                            }
                            else if (xmlPullParser.getName().equals("low") && lowCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow2(xmlPullParser.getText());
//                                Log.d("myWeather", "low: " + xmlPullParser.getText());
                                lowCount++;
                            }
                            else if (xmlPullParser.getName().equals("low") && lowCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow3(xmlPullParser.getText());
//                                Log.d("myWeather", "low: " + xmlPullParser.getText());
                                lowCount++;
                            }
                            else if (xmlPullParser.getName().equals("high") && highCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh1(xmlPullParser.getText());
//                                Log.d("myWeather", "high: " + xmlPullParser.getText());
                                highCount++;
                            }
                            else if (xmlPullParser.getName().equals("high") && highCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh2(xmlPullParser.getText());
//                                Log.d("myWeather", "high: " + xmlPullParser.getText());
                                highCount++;
                            }
                            else if (xmlPullParser.getName().equals("high") && highCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh3(xmlPullParser.getText());
//                                Log.d("myWeather", "high: " + xmlPullParser.getText());
                                highCount++;
                            }
                            else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang1(xmlPullParser.getText());
//                                Log.d("myWeather", "fengxiang: " + xmlPullParser.getText());
                                fengxiangCount++;
                            }
                            else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang2(xmlPullParser.getText());
//                                Log.d("myWeather", "fengxiang: " + xmlPullParser.getText());
                                fengxiangCount++;
                            }
                            else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang3(xmlPullParser.getText());
//                                Log.d("myWeather", "fengxiang: " + xmlPullParser.getText());
                                fengxiangCount++;
                            }

                            else if (xmlPullParser.getName().equals("fengli") && fengliCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli1(xmlPullParser.getText());
//                                Log.d("myWeather", "fengli: " + xmlPullParser.getText());
                                fengliCount++;
                            }
                            else if (xmlPullParser.getName().equals("fengli") && fengliCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli2(xmlPullParser.getText());
//                                Log.d("myWeather", "fengli: " + xmlPullParser.getText());
                                fengliCount++;
                            }
                            else if (xmlPullParser.getName().equals("fengli") && fengliCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli3(xmlPullParser.getText());
//                                Log.d("myWeather", "fengli: " + xmlPullParser.getText());
                                fengliCount++;
                            }


                        }

                        break;

                    // 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                // 进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("myWeather",todayWeather.toString());
        return todayWeather;
    }


//    加载页面信息，数据可视化
    public void updateTodayWeather(TodayWeather todayWeather) {
        city_name_Tv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        humidityTv.setText("湿度：" + todayWeather.getShidu());
        temperature_nowTv.setText("当前温度：" +todayWeather.getWendu()+"°");
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());

        if(todayWeather.getPm25() == null){
            pmDataTv.setText("N/A");
            pmQualityTv.setText("A/N");
            pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
        }
        if(todayWeather.getPm25() != null){
            int pm25 = Integer.parseInt(todayWeather.getPm25());
            if (pm25 <=50) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            }else if (pm25>50 && pm25<=100){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            }else if (pm25>100 && pm25<=150){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
            }else if (pm25>150 && pm25<=200) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
            }else if (pm25>200 && pm25<=300) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
            }
            else pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
        }

        int l = todayWeather.getDate().length() - 3;
        weekTv.setText("今天 "+todayWeather.getDate().substring(l));
        climateTv.setText(todayWeather.getType());
        temperatureTv.setText(todayWeather.getLow().substring(2)+ " ~"+todayWeather.getHigh().substring(2));
        if (todayWeather.getFengxiang().equals("无持续风向") ){
            windTv.setText(todayWeather.getFengli());
        }else windTv.setText(todayWeather.getFengxiang()+" "+todayWeather.getFengli());
        Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
        switch (todayWeather.getType())
        {
            case "晴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "阴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雾":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "多云":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "小雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "中雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            case "大雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "雷阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨加暴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "特大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "阵雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "暴雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "大雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "小雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "雨夹雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "中雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "沙尘暴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            default:
                break;
        }

        int l1 = todayWeather.getDate1().length()- 3;
        weekTv1.setText(todayWeather.getDate1().substring(l1));
        temperatureTv1.setText(todayWeather.getLow1().substring(2)+" ~"+ todayWeather.getHigh1().substring(2));
        climateTv1.setText(todayWeather.getType1());
        if (todayWeather.getFengxiang1().equals( "无持续风向")){
            windTv1.setText(todayWeather.getFengli1());
            Log.i("无持续风向","yes");
        }else windTv1.setText(todayWeather.getFengxiang1()+" "+todayWeather.getFengli1());

        switch (todayWeather.getType1())
        {
            case "晴":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "阴":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雾":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "多云":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "小雨":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "中雨":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            case "大雨":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "阵雨":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "雷阵雨":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨加暴":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "暴雨":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "特大暴雨":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "阵雪":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "暴雪":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "大雪":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "小雪":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "雨夹雪":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "中雪":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "沙尘暴":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            default:
                break;
        }
        int l2 = todayWeather.getDate2().length()- 3;
        weekTv2.setText(todayWeather.getDate2().substring(l2));
        temperatureTv2.setText(todayWeather.getLow2().substring(2)+" ~"+ todayWeather.getHigh2().substring(2));
        climateTv2.setText(todayWeather.getType2());
        if (todayWeather.getFengxiang2().equals( "无持续风向")){
            windTv2.setText(todayWeather.getFengli2());
            Log.i("无持续风向","yes");
        }else windTv2.setText(todayWeather.getFengxiang2()+" "+todayWeather.getFengli2());
        switch (todayWeather.getType2())
        {
            case "晴":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "阴":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雾":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "多云":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "小雨":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "中雨":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            case "大雨":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "阵雨":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "雷阵雨":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨加暴":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "暴雨":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "特大暴雨":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "阵雪":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "暴雪":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "大雪":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "小雪":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "雨夹雪":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "中雪":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "沙尘暴":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            default:
                break;
        }

        int l3 = todayWeather.getDate3().length()- 3;
        weekTv3.setText(todayWeather.getDate3().substring(l3));
        temperatureTv3.setText(todayWeather.getLow3().substring(2)+" ~"+ todayWeather.getHigh3().substring(2));
        climateTv3.setText(todayWeather.getType3());
        if (todayWeather.getFengxiang3().equals( "无持续风向")){
            windTv3.setText(todayWeather.getFengli3());
            Log.i("无持续风向","yes");
        }else windTv3.setText(todayWeather.getFengxiang3()+" "+todayWeather.getFengli3());
        switch (todayWeather.getType3())
        {
            case "晴":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "阴":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雾":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "多云":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "小雨":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "中雨":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            case "大雨":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "阵雨":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "雷阵雨":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨加暴":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "暴雨":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "特大暴雨":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "阵雪":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "暴雪":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "大雪":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "小雪":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "雨夹雪":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "中雪":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "沙尘暴":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            default:
                break;
        }

        mtitle_update_progress.setVisibility(View.GONE);
        mUpdateBtn.setVisibility(View.VISIBLE);
    }

}




