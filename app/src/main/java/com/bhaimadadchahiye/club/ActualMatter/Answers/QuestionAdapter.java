package com.bhaimadadchahiye.club.ActualMatter.Answers;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bhaimadadchahiye.club.R;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyViewHolder> {

    private List<Question> questionList;
    private String[] bgColors;


    public QuestionAdapter(Activity activity, List<Question> questionList) {
        this.questionList = questionList;
        bgColors = activity.getApplicationContext().getResources().getStringArray(R.array.movie_serial_bg);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setText(questionList.get(position).title);
        holder.serial.setText(String.valueOf(questionList.get(position).id));

        String color = bgColors[position % bgColors.length];
        holder.serial.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, serial;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            serial = (TextView) view.findViewById(R.id.serial);
        }
    }
}