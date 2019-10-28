package com.example.exbooks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import static android.os.SystemClock.sleep;

public class NovelActivity extends AppCompatActivity implements Runnable{
    String p,url="",bookname="",usernom="";
    Elements trs;
    private  static  String TAG ="Novel";
    private Connection con=null;
    private ListView listview;
    ArrayList<HashMap<String,String>> listItems = new ArrayList<HashMap<String, String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel);
        Bundle bundle = getIntent().getExtras();
        usernom = bundle.getString("username","");
        url = bundle.getString("url","");
        bookname = bundle.getString("bookname","");
        listview = (ListView) findViewById(R.id.verylist);
        Thread t = new Thread(this);
        t.start();
        while (p == null) {
            sleep(100);
        }//等待子线程完成
        for(int i = 31;i<trs.size()-10;i++){//去除其他不需要的
            HashMap<String, String> map = new HashMap<String, String>();
            String text = trs.get(i).text();
            Elements link = trs.get(i).select("a[href]");
            String relHref = link.attr("href"); //每本书的网址
            map.put("ItemTitle2", text);
            map.put("ItemDetail2", relHref);
            listItems.add(map);
        }

        //自定义法↓
        MyAdapter2 myAdapter2 = new MyAdapter2(this, R.layout.activity_novel, listItems);
        listview.setAdapter(myAdapter2);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView bknm = (TextView) view.findViewById(R.id.itemTitle2);
                TextView bkul = (TextView) view.findViewById(R.id.itemDetail2);
                String title1 = String.valueOf(bknm.getText());
                String title2 = String.valueOf(bkul.getText());
                Intent intent = new Intent(NovelActivity.this, ReadActivity.class);
                Bundle bdl = new Bundle();
                bdl.putString("bookname2", title1);//书名
                bdl.putString("url2", title2);//网址
                intent.putExtras(bdl);
                startActivity(intent);
            }
        });

    }
    public void run() {
        Log.i(TAG, "run: run()......");
        try {
            Document document = Jsoup.connect(url).get();
            document.getElementsByClass("book_list");
            trs = document.select("a");
            p ="223";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.library,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.shu1){
            dingyue();
        }
        if(item.getItemId()==R.id.shu2){
            qingkong();
        }

        return super.onOptionsItemSelected(item);
    }

    private void dingyue(){//订阅图书
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int re1=0;
                try {
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    con= DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.197.1:1433;DatabaseName=Fwbook", "sa", "fwmota1998");
                    if (con != null) {
                        Log.d("sqlserver", "数据库连接成功");
                    }
                    Statement stmt=con.createStatement();//创建一个 Statement对象来将 SQL语句发送到数据库
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    re1=stmt.executeUpdate("insert into Stu"+usernom+"(Bno,Bname) values('"+url+"','"+bookname+"');");
                    if(re1>0){
                        data.putString("result", "订阅成功");
                        Intent intent = new Intent(NovelActivity.this,UserActivity.class);
                        intent.putExtra("username",usernom);//给intent添加额外数据
                        startActivity(intent);
                    }
                    else{
                        data.putString("result", "已经订阅了本书");
                    }
                    msg.setData(data);
                    handler.sendMessage(msg);
                    stmt.close();//关闭原来的对象
                    con.close();//关闭原来的连接
                } catch (ClassNotFoundException e)
                {
                    Log.i("TAG","加载驱动程序出错  "+e.getMessage());
                } catch (SQLException e)
                {
                    Log.i("TAG",e.getMessage());
                }
            }
        }).start();
    }

    private void qingkong() {//清空书架
        new Thread(new Runnable() {
            @Override
            public void run() {
                int re2=0;
                try {
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    con = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.197.1:1433;DatabaseName=Fwbook", "sa", "fwmota1998");
                    if (con != null) {
                        Log.d("sqlserver", "数据库连接成功");
                    }
                    Statement stmt=con.createStatement();//创建一个 Statement对象来将 SQL语句发送到数据库
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    re2=stmt.executeUpdate("delete from Stu"+usernom+" where Bno='"+url+"'");
                    if(re2>0){
                        data.putString("result", "删除成功");
                        Intent intent = new Intent(NovelActivity.this,UserActivity.class);
                        intent.putExtra("username",usernom);//给intent添加额外数据
                        startActivity(intent);
                    }
                    else{
                        data.putString("result", "书架里没有本书");
                    }
                    msg.setData(data);
                    handler.sendMessage(msg);
                    stmt.close();//关闭原来的对象
                    con.close();//关闭原来的连接
                }
                catch (ClassNotFoundException e)
                {
                    Log.i("TAG","加载驱动程序出错  "+e.getMessage());
                } catch (SQLException e)
                {
                    Log.i("TAG",e.getMessage());
                }
            }
        }).start();
    }

    private Handler handler =  new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = msg.getData().getString("result");
            Toast.makeText(NovelActivity.this, message, Toast.LENGTH_LONG).show();

        }

    };

}
