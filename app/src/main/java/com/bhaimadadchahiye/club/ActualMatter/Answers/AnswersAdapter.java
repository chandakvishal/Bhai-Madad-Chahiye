package com.bhaimadadchahiye.club.ActualMatter.Answers;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bhaimadadchahiye.club.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.MyViewHolder> {

    private String TAG = AnswersAdapter.class.getSimpleName();
    private List<Answer> questionList;
    private Activity activity;
    private Context ctx;
    private static final int VIEW_TYPE_FILLED = 0;
    private static final int VIEW_TYPE_EMPTY = 1;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<Answer> answerListToDelete = new ArrayList<>();

    AnswersAdapter(Activity activity, List<Answer> questionList) {
        this.questionList = questionList;
        ctx = activity.getApplicationContext();
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;
        if (viewType == VIEW_TYPE_FILLED) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.answer_list_row, parent, false);
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
            Log.d("TAG", "onBindViewHolder: " + questionList.size());
            holder.title.setText(questionList.get(position).title);
            String email = "Answered By: " + String.valueOf(questionList.get(position).email);
            holder.email.setText(email);
            final TypedArray imgs = ctx.getResources().obtainTypedArray(R.array.userArray);
            final Random rand = new Random();
            final int rndInt = rand.nextInt(imgs.length());
            final int resID = imgs.getResourceId(rndInt, 0);

            holder.image.setImageResource(resID);

        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (questionList.size() == 0) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_FILLED;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView title, email;
        public ImageView image;
        private FrameLayout frameLayout;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.questionBodyAnswer);
            image = (ImageView) view.findViewById(R.id.userImageAnswer);
            email = (TextView) view.findViewById(R.id.emailAnswer);
            frameLayout = (FrameLayout) activity.findViewById(R.id.main_fragment);

            view.setOnLongClickListener(this);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean onLongClick(final View view) {

            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.menu_question);

            // Force icons to show
            Object menuHelper;
            Class[] argTypes;
            try {
                Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                fMenuHelper.setAccessible(true);
                menuHelper = fMenuHelper.get(popupMenu);
                argTypes = new Class[]{boolean.class};
                menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
            } catch (Exception e) {
                Log.w("TAG", "error forcing menu icons to show", e);
                popupMenu.show();
                return false;
            }

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.album_overflow_delete:
                            Toast.makeText(view.getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                            return true;

                        case R.id.album_overflow_share:
                            Toast.makeText(view.getContext(), "Shared on Facebook", Toast.LENGTH_SHORT).show();
                            return true;

                        case R.id.album_overflow_fav:
                            Toast.makeText(view.getContext(), "Stored as Favourite", Toast.LENGTH_SHORT).show();
                            return true;

                        default:
                            return true;
                    }
                }
            });

            popupMenu.show();

            // Try to force some horizontal offset
            try {
                Field fListPopup = menuHelper.getClass().getDeclaredField("mPopup");
                fListPopup.setAccessible(true);
                Object listPopup = fListPopup.get(menuHelper);
                argTypes = new Class[]{int.class};
                Class listPopupClass = listPopup.getClass();

                //Blur background
                frameLayout.getForeground().setAlpha(100);

                // Invoke setHorizontalOffset() with the negative width to move left by that distance
                listPopupClass.getDeclaredMethod("setHorizontalOffset", argTypes).invoke(listPopup, 17);

                // Invoke show() to update the window's position
                listPopupClass.getDeclaredMethod("show").invoke(listPopup);
            } catch (Exception e) {
                // Again, an exception here indicates a programming error rather than an exceptional condition
                // at runtime
                Log.w(TAG, "Unable to force offset", e);
            }

            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu popupMenu) {
                    frameLayout.getForeground().setAlpha(0);
                }
            });

            return true;
        }
    }

    public void onItemRemove(final RecyclerView.ViewHolder viewHolder, final RecyclerView recyclerView) {
        final int adapterPosition = viewHolder.getAdapterPosition();
        Log.d(TAG, "onItemRemove: " + adapterPosition);
        // Doing -1 as adapterPosition gives count from 1 and list uses from 0
        final Answer answer = questionList.get(adapterPosition - 1);
        Snackbar snackbar = Snackbar
                .make(recyclerView, "Question Removed", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        questionList.add(adapterPosition - 1, answer);
                        notifyItemInserted(adapterPosition);
                        recyclerView.scrollToPosition(adapterPosition);
                        answerListToDelete.remove(answer);
                    }
                });
        snackbar.show();
        questionList.remove(adapterPosition - 1);
        notifyItemRemoved(adapterPosition);
//        notifyItemRangeChanged(adapterPosition,questionList.size());
        answerListToDelete.add(answer);
    }
}