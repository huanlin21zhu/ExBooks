package com.example.exbooks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import static android.os.SystemClock.sleep;

public class ReadActivity extends AppCompatActivity implements Runnable{
    private TextView zhengwen;
    String s,novelname="",nelurl="",zne="";
    Elements trs;
    private  static  String TAG ="zhangjie";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        Bundle bundle = getIntent().getExtras();
        nelurl = bundle.getString("url2","");
        novelname = bundle.getString("bookname2","");
        Log.i(TAG, "name：" + novelname);
        Log.i(TAG, "url：" + nelurl);
        zhengwen=(TextView)findViewById(R.id.neirong);
        Thread t = new Thread(this);
        t.start();
        while (s == null) {
            sleep(100);
        }//等待子线程完成
        String[] line = zne.split(" ");
        zhengwen.setText(novelname+"\r\n");
        for(int j = 2;j<line.length;j++){
            zhengwen.setText(zhengwen.getText().toString()+"\r\n"+"\r\n"+line[j]);
        }

    }

    public void run() {
        Log.i(TAG, "run: run()......");
        try {
            Document document = Jsoup.connect(nelurl).get();
            zne = document.select("#htmlContent").text();
            s ="223";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
