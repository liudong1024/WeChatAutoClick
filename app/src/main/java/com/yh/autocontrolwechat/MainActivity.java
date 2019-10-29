package com.yh.autocontrolwechat;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yh.autocontrolwechat.bean.CarInfoBean;
import com.yh.autocontrolwechat.bean.WechatGroupInfoBean;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity {

    private Button button, btn_cancel,btn_select_p;
    private EditText et_name, et_content, et_start_time, et_end_time;
    private Context mContext;
    private TextView tv_wechat_group;
    private static final int TIMER = 999;
    private static final int OPEN = 666;
    private static final int GROUP = 333;
    private MyTimeTask task;
    private AlertDialog alertDialog;
    private List<WechatGroupInfoBean.GroupInfoBean> grouplist ;
    public String[] singleName, groupName, selectedGroups;
    List<String> list;
    boolean[] checkedList;
//    List<WechatGroupInfoBean.GroupInfoBean> list;
    public String selectWechatGroup;
    public int group_index;
//680000/list.size()

    private void setTimer() {
        task = new MyTimeTask(2400000/list.size(), new TimerTask() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences("sp_name", Context.MODE_PRIVATE);
                String group_name_index = sp.getString("start_group_index", "0");
                group_index = Integer.parseInt(group_name_index);
                SharedPreferences.Editor editor = sp.edit();
//                editor.putString("group_name", et_name.getText().toString());
//                editor.putString("start_group_index", 0+"");
                editor.apply();
                editor.commit();
                int index = Integer.parseInt(sp.getString("start_group_index", "0"));
                Log.d("AAAAAA","group_index123:"+list.size());
                if(index == list.size()-1){
                    Log.d("AAAAAA","group_index123:"+list.size());
                    editor.putString("start_group_index", 0+"");
                    editor.apply();
                    editor.commit();
                    group_index = Integer.parseInt(group_name_index);
                    Log.d("AAAAAA","group_indexaaa:"+group_index);
                }else{
                    index = index + 1;
                    editor.putString("start_group_index", index+"");
                    editor.apply();
                    editor.commit();
                    Log.d("AAAAAA","group_index111:"+index);
                }
                mHandler.sendEmptyMessage(TIMER);
                //或者发广播，启动服务都是可以的
            }
        });
        task.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIMER:
                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    String start_time = et_start_time.getText().toString();
                    String end_time = et_end_time.getText().toString();
                    int s = Integer.parseInt(start_time);
                    int e = Integer.parseInt(end_time);


                    Log.d("aaa", hour + "");
                    if (hour >= s && hour <= e) {
                        getCarInfo();

                    }

                    break;
                case GROUP:
//                    et_name.setText(selectWechatGroup);
                    tv_wechat_group.setText(selectWechatGroup);
                    break;
                case OPEN:
                    Toast.makeText(MainActivity.this, "请先打开无障碍", Toast.LENGTH_SHORT).show();
                    Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(accessibleIntent);
                    break;
                default:
                    break;
            }
        }
    };


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, "FIND_CONTANCT_RESULT")) {
                Toast.makeText(MainActivity.this, "没有找到联系人", Toast.LENGTH_LONG).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        SharedPreferences sp = getSharedPreferences("sp_name", Context.MODE_PRIVATE);
        String group_name = sp.getString("group_name", "");
        String start_time = sp.getString("start_time", "6");
        String end_time = sp.getString("end_time", "23");

        registerReceiver(broadcastReceiver, new IntentFilter("FIND_CONTANCT_RESULT"));

        btn_select_p = (Button) findViewById(R.id.btn_select_p);
        button = (Button) findViewById(R.id.button);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
//        et_name = (EditText) findViewById(R.id.et_name);
        tv_wechat_group = (TextView) findViewById(R.id.tv_wechat_group);
//        et_content = (EditText) findViewById(R.id.et_content);
        et_start_time = (EditText) findViewById(R.id.et_start_time);
        et_end_time = (EditText) findViewById(R.id.et_end_time);
//        et_name.setText(group_name);
        et_start_time.setText(start_time);
        et_end_time.setText(end_time);
        getWechatGroupInfo();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.stop();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获得SharedPreferences的实例 sp_name是文件名
                SharedPreferences sp = getSharedPreferences("sp_name", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
//                editor.putString("group_name", et_name.getText().toString());
                editor.putString("start_time", et_start_time.getText().toString());
                editor.putString("end_time", et_end_time.getText().toString());
                editor.putString("start_group_index", 0+"");
                editor.apply();
                editor.commit();
                if(list==null || list.size()==0){
                    Toast.makeText(MainActivity.this, "请先选择群聊",Toast.LENGTH_SHORT).show();
                }else{
                    setTimer();
                }


//                if (isAccessibilitySettingsOn(mContext)) {
//                    String name = et_name.getText().toString().trim();
//                    String content = et_content.getText().toString().trim();
//                    ControlService.isSendSuccess = false;
//                    WechatUtils.NAME = name;
//                    WechatUtils.CONTENT = content;
//                    openWChart();
//                } else {
//                    Toast.makeText(MainActivity.this, "请先打开无障碍",Toast.LENGTH_SHORT).show();
//                    Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//                    startActivity(accessibleIntent);
//                }
            }
        });

    }


    /**
     * 打开微信界面
     */
    private void openWChart() {

        Intent intent = new Intent();
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
        startActivity(intent);

    }

    private void getCarInfo() {
        SharedPreferences sp = getSharedPreferences("sp_name", Context.MODE_PRIVATE);
        group_index = Integer.parseInt(sp.getString("start_group_index", "0"));

        String url = "https://www.meigym.com/api/getcarpushsmallgroup";
        OkHttpClient client = new OkHttpClient();
        String urlString = URLEncoder.encode(list.get(group_index));
        Log.d("============group_name：", urlString);
        FormBody formBody = new FormBody.Builder().add("group_name", urlString).build();
        final Request request = new Request.Builder().url(url).post(formBody).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("=======", "失败");
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                if(res.indexOf("nginx")!= -1){
                    return;
                }else{
                    Log.d("============", res);
                    Gson gson = new Gson();
                    CarInfoBean carInfo = gson.fromJson(res, CarInfoBean.class);
                    if(carInfo.displacement=="" || carInfo.displacement=="undefined"){
                        carInfo.displacement = "--";
                    }
                    if(carInfo.diplacement_standard=="" || carInfo.diplacement_standard=="undefined" || carInfo.diplacement_standard==null){
                        carInfo.diplacement_standard = "--";
                    }
                    if(carInfo.color=="" || carInfo.color=="undefined"){
                        carInfo.color = "--";
                    }
                    String content = "";
                    if(carInfo.source!=null && carInfo.source.equals("华夏二手车")){
                        content = carInfo.title + "\n\n【批发报价】 " + carInfo.price + " (可议价)\n【上牌日期】 " + carInfo.buy_date + "\n【马力排量】 " + carInfo.displacement + "\n【排放标准】 " + carInfo.diplacement_standard + "\n【真实公里】 " + carInfo.mile_age + " 万\n【车辆颜色】 " + carInfo.color + "\n【过户次数】 " + carInfo.transfer_time + "\n【所在城市】 " + carInfo.city + "\n【联系电话】 " + carInfo.tellphone + "\n【详细车况】 --\n";
                    }else{
                        if(carInfo.images!=null && carInfo.images.length()>0){
                            carInfo.images = carInfo.images.substring(1,carInfo.images.length()-1);
                            carInfo.images = carInfo.images.replace("\"","");
                            Log.d("=========",carInfo.images);
                            content = carInfo.title + "\n\n【批发报价】 " + carInfo.price + "万元 (可议价)\n【上牌日期】 " + carInfo.buy_date + "\n【排放标准】 " + carInfo.diplacement_standard + "\n【真实公里】 " + carInfo.mile_age + " 万\n【车辆颜色】 " + carInfo.color + "\n【过户次数】 " + carInfo.transfer_time + "\n【所在城市】 " + carInfo.city + "\n【联系电话】 " + carInfo.tellphone + "\n【车辆照片】 ↓  ↓  ↓";
                            List<String> result = Arrays.asList(carInfo.images.split(","));
                            for (int i=0; i<result.size(); i++){
                                content = content + "\n"+ result.get(i);
                            }
                        }else{
                            content = carInfo.title + "\n\n【批发报价】 " + carInfo.price + "万元 (可议价)\n【上牌日期】 " + carInfo.buy_date +  "\n【排放标准】 " + carInfo.diplacement_standard +"\n【真实公里】 " + carInfo.mile_age + " 万\n【车辆颜色】 " + carInfo.color + "\n【过户次数】 " + carInfo.transfer_time + "\n【所在城市】 " + carInfo.city + "\n【联系电话】 " + carInfo.tellphone + "\n【车辆照片】 ↓  ↓  ↓\n";
                        }
                    }

                    if (isAccessibilitySettingsOn(mContext)) {
//                    Toast.makeText(MainActivity.this, WechatUtils.CONTENT,Toast.LENGTH_SHORT).show();
//                    String name = et_name.getText().toString().trim();
                        ControlService.isSendSuccess = false;
                        WechatUtils.NAME = list.get(group_index);
                        Log.d("AAAAAA","group_index111:"+WechatUtils.NAME);
                        WechatUtils.CONTENT = content;
                        Log.d("AAAAAA","group_index111:"+content);


                        if(carInfo.title == null || carInfo.title .equals("null") || carInfo.title.equals("")){
                            Log.d("AAAAAA========","车辆信息为空");
                            return;
                        } else{
                            openWChart();
                        }
                    } else {
                        mHandler.sendEmptyMessage(OPEN);
                    }
                }


            }
        });

    }

    private void getWechatGroupInfo() {
        String url = "https://www.meigym.com/api/getwechatgroupnew";
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("=======", "失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d("============", res);
                if(res.indexOf("nginx")!= -1){
                    return;
                }
                Gson gson = new Gson();
                WechatGroupInfoBean wechatGroupInfoBean = gson.fromJson(res, WechatGroupInfoBean.class);
                Log.d("=========",wechatGroupInfoBean.toString());
                grouplist = null;
                grouplist = wechatGroupInfoBean.groupInfo;
                Log.d("=========",grouplist.get(1).group_name);
                singleName = new String[grouplist.size()];
                groupName = new String[grouplist.size()];
                checkedList =new boolean[grouplist.size()];
                Log.d("=========",grouplist.size()+"====="+grouplist);
                for(int i=0; i<grouplist.size(); i++){
                    singleName[i] = grouplist.get(i).simple_name;
                    groupName[i] = grouplist.get(i).group_name;
                    checkedList[i] = false;
                }

            }
        });

    }


    public void showMutilAlertDialog(View view) {
        final String[] items = {"北京①群", "山东①群", "山东②群", "海南①群", "广西①群", "云南①群", "广东①群", "广东②群", "四川①群", "贵州①群", "湖南①群", "江西①群", "福建①群", "安徽①群", "浙江①群", "湖北①群", "江苏①群", "河南①群", "宁夏①群", "甘肃①群", "青海①群", "西藏①群", "新疆①群", "陕西①群", "山西①群", "河北①群", "内蒙古①群", "辽宁①群", "吉林①群", "黑龙江①群", "重庆①群", "天津①群", "上海①群"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("群聊选择");
        /**
         *第一个参数:弹出框的消息集合，一般为字符串集合
         * 第二个参数：默认被选中的，布尔类数组
         * 第三个参数：勾选事件监听
         */
        alertBuilder.setMultiChoiceItems(singleName, checkedList, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                if (isChecked) {
//                    Toast.makeText(MainActivity.this, "选择" + singleName[i], Toast.LENGTH_SHORT).show();
                    checkedList[i] = isChecked;
                } else {
//                    Toast.makeText(MainActivity.this, "取消选择" + singleName[i], Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                selectedGroups = null;
                list = new LinkedList<>();
                selectWechatGroup = "";
                WechatGroupInfoBean.GroupInfoBean groupInfoBean = new WechatGroupInfoBean.GroupInfoBean();
                for(int j=0; j<checkedList.length; j++){
                    if(checkedList[j] == true){
                        selectWechatGroup = selectWechatGroup + "\n"+groupName[j];
                        groupInfoBean.setGroup_name(groupName[j]);
                        groupInfoBean.setSimple_name(singleName[j]);
                        Log.d("========", groupName[j]);
                        list.add(groupName[j]);
                    }
                }
                mHandler.sendEmptyMessage(GROUP);
            }
        });

        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });


        alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    /**
     * 判断微信助手是否开启
     *
     * @param context
     * @return
     */
    public boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.i("URL", "错误信息为：" + e.getMessage());
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("关闭应用").setMessage("是否确定退出？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("取消",null);
        builder.create().show();
    }
}
