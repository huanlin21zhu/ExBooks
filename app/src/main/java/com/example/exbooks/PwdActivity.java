package com.example.exbooks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PwdActivity extends AppCompatActivity {
    private Button udpd;
    private Connection con=null;
    private EditText pwd1,pwd2,pwd3;
    private String username="",pd1="",pd2="",pd3="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        username=bundle.getString("username");
        pwd1=(EditText)findViewById(R.id.yspwd);
        pwd2=(EditText)findViewById(R.id.uppwd);
        pwd3=(EditText)findViewById(R.id.uppwd2);
        udpd=(Button)findViewById(R.id.uppassword);
        udpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd1=pwd1.getText().toString();
                pd2=pwd2.getText().toString();
                pd3=pwd3.getText().toString();
                if(pd1.equals("")||pd2.equals("")||pd3.equals("")){
                    Toast.makeText(PwdActivity.this,"不能有空", Toast.LENGTH_LONG).show();
                }
                else{
                    if(pd2.equals(pd3)){
                        new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                int re=0;
                                ResultSet rs;
                                try {
                                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                                    con=DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.197.1:1433;DatabaseName=Fwbook", "sa", "fwmota1998");
                                    Statement stmt=con.createStatement();//创建一个 Statement对象来将 SQL语句发送到数据库
                                    rs=stmt.executeQuery("select * from Stus where Sno='"+username+"'");
                                    Message msg = new Message();
                                    Bundle data = new Bundle();
                                    while(rs.next()){
                                        if(pd1.equals(rs.getString("Spwd"))){
                                            Statement stmt1=con.createStatement();
                                            re=stmt1.executeUpdate("update Stus set Spwd='"+pd2+"' where Sno='"+username+"'");
                                            if(re>0){
                                                data.putString("result", "修改密码成功！");
                                                Intent intent = new Intent(PwdActivity.this,UserActivity.class);
                                                intent.putExtra("username",username);
                                                startActivity(intent);
                                            }
                                            else{
                                                Log.i("TAG","更新失败");
                                            }
                                            stmt1.close();
                                        }
                                        else{
                                            data.putString("result", "原密码输入错误");
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
                    else{
                        Toast.makeText(PwdActivity.this,"两次新密码输入不一致", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private Handler handler =  new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = msg.getData().getString("result");
            pwd1.setText("");
            pwd2.setText("");
            pwd3.setText("");
            Toast.makeText(PwdActivity.this, message, Toast.LENGTH_LONG).show();
        }
    };
}
