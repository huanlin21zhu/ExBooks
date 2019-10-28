package com.example.exbooks;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class UserActivity extends AppCompatActivity implements Runnable{
    List<String> data;
    private ListView listView2;
    private Button jinshu;
    private TextView uo,nam,cge,dep;
    private String userno="";
    private  static  String TAG ="User";
    private Connection con=null;
    String j;
    String[] xinxi = new String[4];
    ArrayList arr = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        userno=bundle.getString("username");
        jinshu=(Button)findViewById(R.id.shuku);
        uo=(TextView)findViewById(R.id.stunum);
        nam=(TextView)findViewById(R.id.stuname);
        cge=(TextView)findViewById(R.id.stucge);
        dep=(TextView)findViewById(R.id.studept);
        Thread t = new Thread(this);
        t.start();
        while (j == null) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }//等待子线程完成

        uo.setText("学号："+xinxi[0]);
        nam.setText("姓名："+xinxi[1]);
        cge.setText("学院："+xinxi[2]);
        dep.setText("专业："+xinxi[3]);

        listView2 = (ListView) findViewById(R.id.mylist);
        data = new ArrayList<String>();
        for(int k=0;k<arr.size();k++){
            data.add(arr.get(k).toString());
            Log.i(TAG, "onItemClick: title3=" + arr.get(k).toString());
        }
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,data);
        listView2.setAdapter(adapter);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text= listView2.getItemAtPosition(position).toString();
                String[] line = text.split("\\r?\\n");
                Log.i(TAG, "onItemClick: title2=" + line[0]);
                Log.i(TAG, "onItemClick: title2=" + line[1]);
                Intent intent = new Intent(UserActivity.this, NovelActivity.class);
                Bundle bdl = new Bundle();
                bdl.putString("username", userno);//用户名
                bdl.putString("bookname", line[0]);//书名
                bdl.putString("url", line[1]);//网址
                intent.putExtras(bdl);
                startActivity(intent);
            }
        });

        jinshu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this,BookActivity.class);
                intent.putExtra("username",userno);
                startActivity(intent);
            }
        });

    }

    public void run() {
        Log.i(TAG, "run: run()......");
        ResultSet rs,rs1;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            con=DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.197.1:1433;DatabaseName=Fwbook", "sa", "fwmota1998");
            Statement stmt=con.createStatement();//创建一个 Statement对象来将 SQL语句发送到数据库
            Statement stmt2=con.createStatement();//创建一个 Statement对象来将 SQL语句发送到数据库
            rs=stmt.executeQuery("select * from Stus where Sno='"+userno+"'");
            while(rs.next()){
                xinxi[0] = rs.getString("Sno");
                xinxi[1] = rs.getString("Sname");
                xinxi[2] = rs.getString("Scge");
                xinxi[3] = rs.getString("Sdept");
                rs1=stmt2.executeQuery("select * from Stu"+userno);
                while(rs1.next()){
                    arr.add(rs1.getString("Bname")+"\r\n"+rs1.getString("Bno"));
                    Log.i(TAG, "onItemClick: title2=" + rs1.getString("Bname"));
                }
                rs1.close();
            }
            Log.i(TAG, "onItemClick: title2=" + arr.size());
            rs.close();
            stmt.close();//关闭原来的对象
            stmt2.close();//关闭原来的对象
            con.close();//关闭原来的连接
            j="233";
        } catch (ClassNotFoundException e)
        {
            Log.i("TAG","加载驱动程序出错  "+e.getMessage());
        } catch (SQLException e)
        {
            Log.i("TAG",e.getMessage());
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bian,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.gaixin){
            //修改密码
            Intent intent = new Intent(UserActivity.this,ChangeActivity.class);
            Bundle bdl = new Bundle();
            bdl.putString("username", userno);
            bdl.putString("stuname", xinxi[1]);
            bdl.putString("stucge", xinxi[2]);
            bdl.putString("studept", xinxi[3]);
            intent.putExtras(bdl);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.gaimi){
                //修改密码
                Intent intent = new Intent(UserActivity.this,PwdActivity.class);
                intent.putExtra("username",userno);
                startActivity(intent);
        }
        if(item.getItemId()==R.id.tuichu){
            //退出登录
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    int re=0;
                    try {
                        Class.forName("net.sourceforge.jtds.jdbc.Driver");
                        con= DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.197.1:1433;DatabaseName=Fwbook", "sa", "fwmota1998");
                        Statement stmt=con.createStatement();//创建一个 Statement对象来将 SQL语句发送到数据库
                        re=stmt.executeUpdate("update Stus set Slogo='否' where Sno='"+userno+"'");
                        if(re>0){ //更改登录状态
                            Log.i("TAG","退出成功");
                            Intent intent = new Intent(UserActivity.this,MainActivity.class);
                            //发送意图.将意图发送给android系统，系统根据意图来激活组件
                            startActivity(intent);
                        }
                        else{
                            Log.i("TAG","退出失败");
                        }
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
        return super.onOptionsItemSelected(item);
    }

    //禁用返回键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return false;
    }
}
