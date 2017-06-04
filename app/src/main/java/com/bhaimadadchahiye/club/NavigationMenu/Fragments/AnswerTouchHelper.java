package com.bhaimadadchahiye.club.NavigationMenu.Fragments;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.bhaimadadchahiye.club.Answers.AnswersAdapter;

public class AnswerTouchHelper extends ItemTouchHelper.SimpleCallback {
    private AnswersAdapter answerAdapter;
    private RecyclerView recyclerView;

    public AnswerTouchHelper(AnswersAdapter movieAdapter, RecyclerView recyclerView) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.answerAdapter = movieAdapter;
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
        answerAdapter.onItemRemove(viewHolder, recyclerView);
    }
}