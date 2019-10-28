package com.example.exbooks;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class MyAdapter2 extends ArrayAdapter{
    private static final String TAG = "MyAdapter2";
    public MyAdapter2(Context context, int resource, ArrayList<HashMap<String,String>> list) {
        super(context, resource, list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if(itemView == null){
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        Map<String,String> map = (Map<String, String>)getItem(position);
        TextView title = (TextView) itemView.findViewById(R.id.itemTitle2);
        TextView detail = (TextView) itemView.findViewById(R.id.itemDetail2);
        detail.setVisibility(View.GONE);
        title.setText(map.get("ItemTitle2"));
        detail.setText(map.get("ItemDetail2"));
        return itemView;
    }
}