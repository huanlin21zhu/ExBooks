package com.example.exbooks;

import java.sql.Connection;
import java.sql.DriverManager;
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
import android.widget.TextView;
import android.widget.Toast;

public class ChangeActivity extends AppCompatActivity {
    private Button xiugai;
    private TextView number;
    private EditText name,cge,dept;
    private Connection con = null;
    String you="",youname="",youcge="",youdept="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        you=bundle.getString("username");
        youname=bundle.getString("stuname");
        youcge=bundle.getString("stucge");
        youdept=bundle.getString("studept");
        number=(TextView)findViewById(R.id.younumber);
        number.setText(you);
        name=(EditText)findViewById(R.id.youstu);
        cge=(EditText)findViewById(R.id.cge);
        dept=(EditText)findViewById(R.id.dept);
        name.setText(youname);
        cge.setText(youcge);
        dept.setText(youdept);
        xiugai=(Button)findViewById(R.id.change_button);
        xiugai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youname=name.getText().toString();
                youcge=cge.getText().toString();
                youdept=dept.getText().toString();
                if(youname.equals("")||youcge.equals("")||youdept.equals("")){
                    Toast.makeText(ChangeActivity.this,"不能有空", Toast.LENGTH_LONG).show();
                }
                else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int re=0;
                            try {
                                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                                con = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.197.1:1433;DatabaseName=Fwbook", "sa", "fwmota1998");
                                Statement stmt = con.createStatement();//创建一个 Statement对象来将 SQL语句发送到数据库
                                Message msg = new Message();
                                Bundle data = new Bundle();
                                re=stmt.executeUpdate("update Stus set Sname='"+youname+"', Scge='"+youcge+"', Sdept='"+youdept+"' where Sno='"+you+"'");
                                if(re>0){
                                    data.putString("result", "修改成功！");
                                    Intent intent = new Intent(ChangeActivity.this,UserActivity.class);
                                    intent.putExtra("username",you);
                                    startActivity(intent);
                                }
                                else{
                                    data.putString("result", "修改失败！");
                                    Log.i("TAG","更新失败");
                                }
                                msg.setData(data);
                                handler.sendMessage(msg);
                                stmt.close();
                                con.close();
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
            }
        });
    }

    private Handler handler =  new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = msg.getData().getString("result");
            Toast.makeText(ChangeActivity.this, message, Toast.LENGTH_LONG).show();
        }
    };
}
