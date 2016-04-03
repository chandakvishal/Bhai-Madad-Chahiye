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

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private List<Movie> moviesList;
    private String[] bgColors;


    public MoviesAdapter(Activity activity, List<Movie> moviesList) {
        this.moviesList = moviesList;
        bgColors = activity.getApplicationContext().getResources().getStringArray(R.array.movie_serial_bg);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movie movie = moviesList.get(position);
        holder.title.setText(moviesList.get(position).title);
        holder.serial.setText(String.valueOf(moviesList.get(position).id));

        String color = bgColors[position % bgColors.length];
        holder.serial.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, serial;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            serial = (TextView) view.findViewById(R.id.serial);
        }
    }

//    // Clean all elements of the recycler
//    public void clear() {
//        moviesList.clear();
//        notifyDataSetChanged();
//    }
//
//    // Add a list of items
//    public void addAll(List<Movie> list) {
//        moviesList.addAll(list);
//        notifyDataSetChanged();
//    }
}