package com.example.lfasd.fengkuang;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SDCard extends Activity {

    ListView mListView;
    TextView mTextView;
    //记录当前的父文件夹
    File currentParent;
    //记录当前路径下所有文件的文件数组
    File[] currentFiles;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        //获取列出全部文件的ListView
        mListView = (ListView) findViewById(R.id.list);
        mTextView = (TextView) findViewById(R.id.path);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //获取系统的SD卡的目录
            currentParent = Environment.getExternalStorageDirectory();
            currentFiles = currentParent.listFiles();
            //使用当前目录下的全部文件，文件夹来填充ListView
            inflateListView(currentFiles);
        }

        //为ListView的列表项的单击事件绑定监听器
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //用户单击了文件，直接返回，不做任何处理
                if (currentFiles[position].isFile()) {
                    return;
                }
                //获取用户单击的文件夹下的所有文件
                File[] tmp = currentFiles[position].listFiles();
                if (tmp == null || tmp.length == 0) {
                    Toast.makeText(SDCard.this, "当前路径不可访问或该路径下没有文件", Toast.LENGTH_SHORT).show();
                } else {
                    //获取用户单击的列表对应的文件夹，设为当前的父文件夹
                    currentParent = currentFiles[position];
                    //保存当前父文件夹内的全部文件和文件夹
                    currentFiles = tmp;
                    //再次更新ListView
                    inflateListView(currentFiles);
                }
            }
        });

        //获取上一级目录的按钮
        Button parent = (Button) findViewById(R.id.parent);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!currentParent.getCanonicalPath().equals(Environment.getExternalStorageDirectory().getCanonicalPath())) {
                        //获取上一级目录
                        currentParent = currentParent.getParentFile();
                        //列出当前目录下的所有文件
                        currentFiles = currentParent.listFiles();
                        //再次更新ListView
                        inflateListView(currentFiles);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void inflateListView(File[] files) {
        //创建一个List集合，List集合的元素是Map
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < files.length; ++i) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            //如果当前File是文件夹，使用folder图标；否则使用file图标
            if (files[i].isDirectory()) {
                listItem.put("icon", R.mipmap.ic_launcher);
            } else {
                listItem.put("icon", R.mipmap.ic_launcher);
            }
            listItem.put("fileName", files[i].getName());
            //添加List项
            listItems.add(listItem);
        }
        //创建一个SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.line, new String[]{"icon", "fileName"}, new int[]{R.id.icon, R.id.file_name});
        //为ListView设置Adapter
        mListView.setAdapter(simpleAdapter);
        try {
            mTextView.setText("当前路径为：" + currentParent.getCanonicalPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}