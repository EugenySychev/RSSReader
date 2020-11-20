package com.sychev.rss_reader.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    private final NewsListAdapter adapter;
    private final Paint fontPaint;
    private final int textWidth;
    private final String markReadText;
    private final Drawable icon;
    private final ColorDrawable background;
    private final SwipeActionCallback actor;
    private boolean swipeProcessed;

    public SwipeReadCallback(NewsListAdapter adapter, SwipeActionCallback actor) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_check_24);
        if (icon != null) {
            icon.setTint(adapter.getContext().getColor(R.color.colorBackground));
        }
        background = new ColorDrawable(Color.GRAY);
        this.actor = actor;
        int color = adapter.getContext().getResources().getColor(R.color.colorBackground, adapter.getContext().getTheme());
        fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fontPaint.setTextSize(adapter.getContext().getResources().getDimension(R.dimen.nav_source_item_icon));
        fontPaint.setStyle(Paint.Style.FILL);
        fontPaint.setColor(color);
        markReadText = adapter.getContext().getString(R.string.set_read);
        textWidth = (int) fontPaint.measureText(markReadText);

    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        float newDx = dX;
        float positiveLimit = icon.getIntrinsicWidth() * 2 + textWidth;
        float negativeLimit = -1 * positiveLimit;
        if (newDx >= positiveLimit) newDx = positiveLimit;
        if (newDx <= negativeLimit) newDx = negativeLimit;
        Log.d("SWIPE", "Dx is " + dX + " action state " + actionState + " is active " + isCurrentlyActive);
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

            background.draw(c);
            icon.draw(c);
            c.drawText(markReadText, iconLeft + 10, iconBottom - 10, fontPaint);
        } else if (newDx < 0) {
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) newDx) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());

            background.draw(c);
            icon.draw(c);
            c.drawText(markReadText, iconLeft - textWidth - 10, iconBottom - 10, fontPaint);
        } else {
            background.setBounds(0, 0, 0, 0);
        }

        if (!swipeProcessed && isCurrentlyActive) {
            if ((newDx < 0 && newDx < negativeLimit / 3) ||
                    (newDx > 0 && newDx > positiveLimit / 3)) {
                swipeProcessed = true;
                android.os.Handler handler = new android.os.Handler();
                final int position = viewHolder.getAdapterPosition();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        actor.processSwipe(position);
                    }
                });
            }
        }

        if (swipeProcessed && !isCurrentlyActive)
            swipeProcessed = false;

        if (newDx == 0 && !isCurrentlyActive) {
            adapter.notifyDataSetChanged();
        }
    }

    public interface SwipeActionCallback {
        void processSwipe(int position);
    }
}
