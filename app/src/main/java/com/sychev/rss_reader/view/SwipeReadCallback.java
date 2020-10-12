package com.sychev.rss_reader.view;

import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.sychev.rss_reader.R;

public class SwipeReadCallback extends ItemTouchHelper.SimpleCallback {

    private NewsListAdapter adapter;
    private final int LIMIT_SWIPE = 100;
    private int positiveLimit;
    private int negativeLimit;

    private Drawable icon;
    private final ColorDrawable background;

    private SwipeActionCallback actor;

    public interface SwipeActionCallback {
        void processSwipe(int position);
    }

    public SwipeReadCallback(NewsListAdapter adapter, SwipeActionCallback actor) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_check_24);
        if (icon != null) {
            icon.setTint(adapter.getContext().getColor(R.color.colorBackground));
            positiveLimit = icon.getIntrinsicWidth() * 2;
            negativeLimit = -1 * positiveLimit;
        }
        background = new ColorDrawable(Color.GRAY);
        this.actor = actor;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        actor.processSwipe(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int newDx = (int) dX;

        if (newDx >= positiveLimit) newDx = positiveLimit;
        if (newDx <= negativeLimit) newDx = negativeLimit;
        super.onChildDraw(c, recyclerView, viewHolder, newDx, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 0; //so background is behind the rounded corners of itemView

        int iconMargin = icon.getIntrinsicHeight() / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (newDx > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            int iconRight = itemView.getLeft() + iconMargin;
            icon.setBounds(iconRight, iconTop, iconLeft, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) newDx) + backgroundCornerOffset, itemView.getBottom());
            Log.d("SWIPE", "Bounds " + iconLeft + " " + iconRight);
        } else if (newDx < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            Log.d("SWIPE", "Bounds " + iconLeft + " " + iconRight);
            background.setBounds(itemView.getRight() + ((int) newDx) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);
    }
}
