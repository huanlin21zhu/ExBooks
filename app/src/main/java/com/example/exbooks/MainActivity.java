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
public class MainActivity extends AppCompatActivity {
    private Button loginbutton,zhucebutton;
    private TextView tv;   //提示语句
    private EditText yourname,yourpd; //账号和密码编辑
    private String uname="",pd="";
    private Connection con = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //指向提示语句
        tv=(TextView)findViewById(R.id.hint1);
        zhucebutton=(Button)findViewById(R.id.zhuce);//注册按钮

        //登录按钮
        loginbutton=(Button)findViewById(R.id.denglu1);

        zhucebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RecActivity.class);
                startActivity(intent);
            }
        });

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取账号和密码
                yourname=(EditText)findViewById(R.id.username1);
                yourpd=(EditText)findViewById(R.id.password1);
                uname=yourname.getText().toString();
                pd=yourpd.getText().toString();
                //不能有空
                if(uname.equals("")||pd.equals("")){
                    tv.setText("账号或密码不能为空");
                }
                else{
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ResultSet rs;
                            int re=0;
                            try
                            {   //连接数据库
                                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                                Log.d("加载驱动", "成功");
                                con=(Connection)DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.197.1:1433;DatabaseName=Fwbook", "sa", "fwmota1998");
                                if(con!=null){
                                    Log.d("sqlserver", "数据库连接成功");
                                }
                                PreparedStatement stmt=null;
                                stmt=con.prepareStatement("select * from Stus where Sno=?",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY) ;
                                stmt.setString(1,uname);
                                rs=stmt.executeQuery();

                                Message msg = new Message();
                                Bundle data = new Bundle();

                                while(rs.next()){  //先查询
                                    Log.d("sqlserver", rs.getString("Sno"));
                                    if(pd.equals(rs.getString("Spwd"))){ //密码正确时
                                        if(rs.getString("Slogo").equals("是")){
                                            data.putString("result", "该用户已登录");
                                        }
                                        else{
                                            stmt=con.prepareStatement("update Stus set Slogo='是' where Sno='"+uname+"'");
                                            re=stmt.executeUpdate();
                                            if(re>0){ //更改登录状态
                                                data.putString("result", "登录成功！");
                                                Intent intent = new Intent(MainActivity.this,UserActivity.class);
                                                intent.putExtra("username",uname);//给intent添加额外数据
                                                startActivity(intent);
                                            }
                                            else{
                                                data.putString("result", "登录失败！");
                                                //tv.setText("登录失败！");
                                            }
                                        }
                                    }
                                    else{
                                        data.putString("result", "账号或密码错误");
                                        //tv.setText("账号或密码错误！");
                                    }

                                }
                                msg.setData(data);
                                handler.sendMessage(msg);
                                rs.close();
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
            }
        });
    }

    private Handler handler =  new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = msg.getData().getString("result");
            tv.setText(message);
        }
    };
}
