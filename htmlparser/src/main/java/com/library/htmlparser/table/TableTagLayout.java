package com.library.htmlparser.table;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

public class TableTagLayout extends TableLayout {
    public TableTagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //this.setShrinkAllColumns(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int numberOfRows = this.getChildCount();
        for (int i = 0; i < numberOfRows; i++) {
            int actualChildCount = ((TableRow) getChildAt(i)).getChildCount();
            int weightSum = 0;
            for (int j = 0; j < actualChildCount; j++) {
                View view = ((TableRow) getChildAt(i)).getChildAt(j);
                int width = view.getMeasuredWidth();
                ((TableRow.LayoutParams) view.getLayoutParams()).weight = width;
                weightSum = weightSum + width;
            }
            ((TableRow) getChildAt(i)).setWeightSum(weightSum);
            getChildAt(i).requestLayout();
            System.out.println("");
        }
        /* Second call for super.onMeasure() is important here and is a work-around solution
         * for showing table properly. Same can be achieved by setShrinkAllColumns() method of
         * TableLayout, but if content of a column is relatively tiny, then it gives problem.
         * In this work-around solution, first call measures all the width and second call
         * helps in distributing the weights properly.
         */
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
