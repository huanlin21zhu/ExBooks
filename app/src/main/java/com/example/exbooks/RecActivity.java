package com.example.exbooks;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import java.sql.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RecActivity extends AppCompatActivity {
    private EditText myname,mypassword,mypasswordch;
    private Button regisdbutton;      //确定按钮
    String stuzh="",stuma="",stumi="";
    private Connection con = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec);
        regisdbutton=(Button)findViewById(R.id.registered_button);
        regisdbutton.setOnClickListener(new View.OnClickListener() { //注册按钮
            @Override
            public void onClick(View v) {
                //获取账号
                myname = (EditText) findViewById(R.id.students);
                stuzh = myname.getText().toString();
                //获取密码
                mypassword = (EditText) findViewById(R.id.stupwds);
                stuma = mypassword.getText().toString();
                //获取二次输入的密码
                mypasswordch = (EditText) findViewById(R.id.jianpwds);
                stumi = mypasswordch.getText().toString();
                if(stuzh.equals("")||stuma.equals("")||stumi.equals("")){
                    Toast.makeText(RecActivity.this, "不能有空", Toast.LENGTH_LONG).show();
                }
                else{
                    if(stuma.equals(stumi)){
                        if(stuzh.length()==8&&stuma.length()>=6&&stuma.length()<=16){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    int re=0;
                                    try {   //连接数据库
                                        Class.forName("net.sourceforge.jtds.jdbc.Driver");
                                        Log.d("加载驱动", "成功");
                                        con = (Connection) DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.197.1:1433;DatabaseName=Fwbook", "sa", "fwmota1998");
                                        if (con != null) {
                                            Log.d("sqlserver", "数据库连接成功");
                                        }
                                        Message msg = new Message();
                                        Bundle data = new Bundle();
                                        PreparedStatement stmt=null;
                                        PreparedStatement stmt2=null;
                                        stmt=con.prepareStatement("insert into Stus(Sno,Sname,Scge,Sdept,Spwd,Slogo) values('"+stuzh+"','','','','"+stuma+"','否');");
                                        re=stmt.executeUpdate();
                                        if(re>0){
                                            String stu = "Stu"+stuzh;
                                            stmt2=con.prepareStatement("create table "+stu+"(Bno varchar(500) NOT NULL UNIQUE, Bname varchar(500), Primary key (Bno));");
                                            stmt2.executeUpdate();
                                            data.putString("result", "注册成功！");
                                            Intent intent = new Intent(RecActivity.this,MainActivity.class);
                                            startActivity(intent);

                                        }
                                        else{
                                            data.putString("result", "账号重复，注册失败！");
                                        }
                                        msg.setData(data);
                                        handler.sendMessage(msg);
                                        stmt2.close();//关闭原来的对象
                                        stmt.close();//关闭原来的对象
                                        con.close();//关闭原来的连接
                                    }catch (ClassNotFoundException e)
                                    {
                                        Log.i("TAG","加载驱动程序出错  "+e.getMessage());
                                    } catch (SQLException e)
                                    {
                                        Log.i("TAG",e.getMessage());
                                    }
                                }
                            }).start();
                        }
                        else{
                            Toast.makeText(RecActivity.this, "账号或密码输入不符", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(RecActivity.this, "两次密码输入不一致", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private Handler handler =  new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = msg.getData().getString("result");
            Toast.makeText(RecActivity.this, message, Toast.LENGTH_LONG).show();
        }
    };
}
