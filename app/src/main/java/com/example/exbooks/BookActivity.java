package com.example.exbooks;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;
public class BookActivity extends ListActivity implements Runnable{
    private  static  String TAG ="BOOK";
    private static int yeshu=2; //要提取的页数
    String s,stu="";
    ArrayList<HashMap<String,String>> listItems = new ArrayList<HashMap<String, String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_book);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        stu=bundle.getString("username");
        Thread t = new Thread(this);
        t.start();
        while (s == null) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }//等待子线程完成

        //自定义法↓
        MyAdapter myAdapter = new MyAdapter(this, R.layout.activity_book, listItems);
        this.setListAdapter(myAdapter);
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView title = (TextView) view.findViewById(R.id.itemTitle);
                String title2 = String.valueOf(title.getText());
                Log.i(TAG, "title2=" + title2);
                //把网址读取出来
                String[] line = title2.split("\\r?\\n"); //按行分割字符串

                Log.i(TAG, "onItemClick: title2=" + line[0]+"|"+line[1]);
                Intent intent = new Intent(BookActivity.this, NovelActivity.class);
                Bundle bdl = new Bundle();
                bdl.putString("username", stu);//用户名
                bdl.putString("bookname", line[0]);//书名
                bdl.putString("url", line[1]);//网址
                intent.putExtras(bdl);
                startActivity(intent);
            }
        });
    }

    public void run() {
        Log.i(TAG, "run: run()......");
        try {
            for (int j=1; j<=yeshu; j++){
                String url = "http://www.huanyue123.com/book/quanbu/default-0-0-0-0-2-0-"+String.valueOf(j)+".html";
                Document document = Jsoup.connect(url).get();;
                document.getElementsByAttribute("width:47%");
                Elements trs = document.select("dd");
                String[] text= new String[trs.size()];
                String mn="";
                for(int i = 0;i<trs.size();i++){//每一项有5个dd，原网页一页有30项，所以这里长为150
                    HashMap<String, String> map = new HashMap<String, String>();

                    if((i+1)%5==1){
                        Elements link = trs.get(i).select("a[href]");
                        String relHref = link.attr("href"); //每本书的网址
                        //删除前缀的更新时间，书名的前头一开始是带了更新时间的
                        text[i] = trs.get(i).text().substring(17,trs.get(i).text().length());
                        mn = mn+text[i]+"\r\n"+relHref+"\r\n";

                    }
                    else if((i+1)%5==0){
                        text[i] = "开始阅读";
                        map.put("ItemTitle", mn);
                        map.put("ItemDetail", text[i]);
                        mn="";
                        listItems.add(map);
                    }
                    else{
                        text[i] = trs.get(i).text();
                        mn = mn+text[i]+"\r\n";
                    }
                }
            }
            s="23366";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
