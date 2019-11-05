package com.au569987.assignment2;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/*
* item decoration inspireret herfra
* https://blog.fossasia.org/set-spacing-in-recyclerview-items-by-custom-item-decorator-in-phimpme-android-app/
* Laver afstand imellem hver recycleview item.
* */

public class Decoration extends RecyclerView.ItemDecoration {

    private int mItemOffset_L;
    private int mItemOffset_R;
    private int mItemOffset_T;
    private int mItemOffset_B;

    public Decoration(int itemOffset_r,int itemOffset_t,int itemOffset_l,int itemOffset_b) {

        mItemOffset_R = itemOffset_r;
        mItemOffset_T = itemOffset_t;
        mItemOffset_L = itemOffset_l;
        mItemOffset_B = itemOffset_b;

    }

    public Decoration(@NonNull Context context, @DimenRes int itemOffset_r,@DimenRes int itemOffset_t,@DimenRes int itemOffset_l,@DimenRes int itemOffset_b) {

        this(   context.getResources().getDimensionPixelSize(itemOffset_r),
                context.getResources().getDimensionPixelSize(itemOffset_t),
                context.getResources().getDimensionPixelSize(itemOffset_l),
                context.getResources().getDimensionPixelSize(itemOffset_b));

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,

                               RecyclerView.State state) {

        super.getItemOffsets(outRect, view, parent, state);

        // set offset between cards. LEFT, TOP, RIGHT, BOTTOM
        outRect.set(mItemOffset_R, mItemOffset_T, mItemOffset_L, mItemOffset_B);

    }

}