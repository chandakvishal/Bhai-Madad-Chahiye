package com.bhaimadadchahiye.club.ActualMatter.Answers;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.Fragments.HomeFragment;
import com.bhaimadadchahiye.club.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyViewHolder> {

    private String TAG = QuestionAdapter.class.getSimpleName();
    private List<Question> questionList;
    private HomeFragment hm = new HomeFragment();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<Question> questionListToDelete = new ArrayList<>();
    private Activity activity;


    public QuestionAdapter(Activity activity, List<Question> questionList) {
        this.questionList = questionList;
        this.activity = activity;
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
        String email = "@" + String.valueOf(questionList.get(position).email).split("@")[0];
        holder.email.setText(email);
        holder.image.setImageResource(questionList.get(position).imageId);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView title;
        ImageView image;
        public TextView email;
        private FrameLayout frameLayout;
        private ShareActionProvider mShareActionProvider;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            image = (ImageView) view.findViewById(R.id.userImage);
            email = (TextView) view.findViewById(R.id.email);
            frameLayout = (FrameLayout) activity.findViewById(R.id.main_fragment);

            view.setOnLongClickListener(this);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean onLongClick(final View view) {

            final PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
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

            Menu menu = popupMenu.getMenu();
            final MenuItem menuItem = menu.findItem(R.id.album_overflow_share);

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.album_overflow_delete:
                            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Title", questionList.get(getAdapterPosition()).title);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(view.getContext(), "Text Successfully Copied", Toast.LENGTH_SHORT).show();
                            return true;

                        case R.id.album_overflow_share:
                            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

                            shareTextUrl();

                            Toast.makeText(view.getContext(), "Position is " + getAdapterPosition(), Toast.LENGTH_SHORT).show();

                            return true;

                        case R.id.album_overflow_fav:
                            Toast.makeText(view.getContext(), "Position is " + getAdapterPosition(), Toast.LENGTH_SHORT).show();

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
//                frameLayout.getForeground().setAlpha(100);

                frameLayout.setAlpha((float) 0.3);

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
                    frameLayout.setAlpha((float) 1);
                }
            });

            return true;
        }

        // Method to share either text or URL.
        private void shareTextUrl() {
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

            // Add data to the intent, the receiving app will decide
            // what to do with it.
            share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Question");
            share.putExtra(Intent.EXTRA_TEXT, questionList.get(getAdapterPosition()).title);
            share.putExtra(Intent.EXTRA_SUBJECT, "Body of the Question");
            share.putExtra(Intent.EXTRA_TEXT, questionList.get(getAdapterPosition()).body);

            activity.startActivity(Intent.createChooser(share, "Share link!"));
        }

        private void setShareIntent(Intent shareIntent) {
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(shareIntent);
            }
        }
    }

    public void onItemRemove(final RecyclerView.ViewHolder viewHolder, final RecyclerView recyclerView) {
        final int adapterPosition = viewHolder.getAdapterPosition();
        Log.d(TAG, "onItemRemove: " + adapterPosition);
        final Question question = questionList.get(adapterPosition);
        Snackbar snackbar = Snackbar
                .make(recyclerView, "Question Removed", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        questionList.add(adapterPosition, question);
                        notifyItemInserted(adapterPosition);
                        recyclerView.scrollToPosition(adapterPosition);
                        questionListToDelete.remove(question);
                        hm.setSavedJson(questionList);
                    }
                });
        snackbar.show();
        questionList.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        questionListToDelete.add(question);
        hm.setSavedJson(questionList);
    }
}