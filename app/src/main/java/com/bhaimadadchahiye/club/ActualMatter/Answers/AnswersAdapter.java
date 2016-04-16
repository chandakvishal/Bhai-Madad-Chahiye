package com.bhaimadadchahiye.club.ActualMatter.Answers;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bhaimadadchahiye.club.R;

import java.util.List;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.MyViewHolder> {

    private List<Answer> questionList;
    private String[] bgColors;
    private static final int VIEW_TYPE_FILLED = 0;
    private static final int VIEW_TYPE_EMPTY = 1;


    public AnswersAdapter(Activity activity, List<Answer> questionList) {
        this.questionList = questionList;
        bgColors = activity.getApplicationContext().getResources().getStringArray(R.array.movie_serial_bg);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;
        if (viewType == VIEW_TYPE_FILLED) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.question_list_row, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.empty_layout, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_FILLED) {
            holder.title.setText(questionList.get(position).title);
            holder.serial.setText(String.valueOf(questionList.get(position).id));

            String color = bgColors[position % bgColors.length];
            holder.serial.setBackgroundColor(Color.parseColor(color));
        }
    }

    @Override
    public int getItemCount() {
        if (questionList.size() == 0) {
            return 1;
        } else {
            return questionList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (questionList.size() == 0) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_FILLED;
        }
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