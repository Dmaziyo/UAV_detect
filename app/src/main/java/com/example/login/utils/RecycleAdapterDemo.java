package com.example.login.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.Data.UAV_Data;
import com.example.login.Data.User;
import com.example.login.R;

import java.util.List;

public class RecycleAdapterDemo extends RecyclerView.Adapter<RecycleAdapterDemo.MyViewHolder> {
    private Context context;
    private List<UAV_Data> list;
    private View inflater;

    class MyViewHolder extends RecyclerView.ViewHolder{
       private TextView tv_user_id;
       private TextView tv_UAV_ID;
       private TextView tv_Time_event;
       private ImageView imageView;
       private Bitmap  bitmap;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_user_id = (TextView) itemView.findViewById(R.id.user_id);
            tv_UAV_ID=itemView.findViewById(R.id.UAV_ID);
            tv_Time_event=itemView.findViewById(R.id.Time_event);
            imageView=itemView.findViewById(R.id.Event_pic);
        }
    }

    //构造方法，传入数据
    public RecycleAdapterDemo(Context context, List<UAV_Data> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecycleAdapterDemo.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建ViewHolder，返回每一项的布局
        inflater = LayoutInflater.from(context).inflate(R.layout.list_layout,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(inflater);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleAdapterDemo.MyViewHolder holder, int position) {
        //将数据和控件绑定
        holder.tv_user_id.setText(User.myUser.getName());
        holder.tv_UAV_ID.setText("上传无人机编号:"+list.get(position).getUAV_id());
        holder.tv_Time_event.setText("时间"+list.get(position).getDate()+"\n"+"坐标:"+list.get(position).getLocation()+"\n"+"事件:"+list.get(position).getEvent_type()+"\n"+list.get(position).getText());
        holder.bitmap=GetImg.getImage(list.get(position).getImg_url());
        holder.imageView.setImageBitmap(holder.bitmap);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}

