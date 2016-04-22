package com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.Fragments;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.bhaimadadchahiye.club.ActualMatter.Answers.QuestionAdapter;

public class QuestionTouchHelper extends ItemTouchHelper.SimpleCallback {
    private QuestionAdapter questionAdapter;
    private RecyclerView recyclerView;

    public QuestionTouchHelper(QuestionAdapter movieAdapter, RecyclerView recyclerView) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.questionAdapter = movieAdapter;
        this.recyclerView = recyclerView;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //TODO: Not implemented here
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //Remove item
        Log.d("TAG", "onSwiped: " + viewHolder.getAdapterPosition());
        questionAdapter.onItemRemove(viewHolder, recyclerView);
//        questionAdapter.remove(viewHolder.getAdapterPosition());
    }
}