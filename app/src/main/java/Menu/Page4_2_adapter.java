package Menu;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;

import Page1_schedule.Page1_Main;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page4_2_adapter extends RecyclerView.Adapter<Page4_2_adapter.ViewHolder> {

    private Context context;
    private String[] stay = new String[5];  // 하트의 클릭 여부
    private ArrayList<Train_Data_forRecyclerview> train_data;
    private Page4_sendData sendData;


    //메인에서 불러올 때, 이 함수를 씀
    public Page4_2_adapter(Context context, ArrayList<Train_Data_forRecyclerview> train_data, Page4_sendData sendData) {
        this.context = context;
        this.train_data = train_data;
        this.sendData = sendData;
    }

    @Override
    public Page4_2_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.page4_2_item, null);
        return new Page4_2_adapter.ViewHolder(v);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final Page4_2_adapter.ViewHolder holder, final int position) {
        final Train_Data_forRecyclerview item = train_data.get(position);

        //짧게 누르면 페이지 이동
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Page1_Main.class);
                intent.putExtra("key", item.keyDate);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
            }
        });


        //길게누르면 삭제
        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                sendData.onDelete(item.keyDate ,position);
                return false;
            }
        });

        //날짜
        holder.date.setText(item.startdate.substring(0, 4)+"."+item.startdate.substring(4, 6) +"." + item.startdate.substring(6) + " ~ "
                + item.enddate.substring(0, 4) + "." + item.enddate.substring(4, 6) + "." + item.enddate.substring(6));

        //첫번째 여행
        if(position ==0) holder.title.setText("첫번째 여행");
        else if(position ==1) holder.title.setText("두번째 여행");
        else if(position ==2) holder.title.setText("세번째 여행");
        else if(position ==3) holder.title.setText("네번째 여행");
        else if(position ==4) holder.title.setText("다섯번째 여행");
        else if(position ==5) holder.title.setText("여섯번째 여행");
        else if(position ==6) holder.title.setText("일곱번째 여행");
        else if(position ==7) holder.title.setText("여덟번째 여행");
        else  holder.title.setText("아홉번째 여행");

    }

    @Override
    public int getItemCount() {
        return train_data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, date;

        public ViewHolder(View itemView) {
            super(itemView);
            image =  itemView.findViewById(R.id.page4_2_image);
            title = itemView.findViewById(R.id.page4_2_title);
            date =  itemView.findViewById(R.id.page4_2_date);
        }
    }
}
