package com.sha.kamel.multitogglebutton;

import android.view.View;
import android.widget.TextView;

import java.util.List;

public class Selected {
    private List<TextView> items;
    private List<Integer> positions;
    private int allItemsize;

    Selected(List<TextView> items, List<Integer> positions, int allItemsize) {
        this.items = items;
        this.positions = positions;
        this.allItemsize = allItemsize;
    }

    public boolean isSingleItem() {
        return items.size() == 1;
    }

    public View getSingleItem() {
        return items.get(0);
    }

    public int getSingleItemPosition() {
        return positions.get(0);
    }

    public List<TextView> getSelectedItems() {
        return items;
    }

    public List<Integer> getSelectedPositions() {
        return positions;
    }

    public boolean isAnySelected() {
        return !items.isEmpty();
    }

    public boolean isAllSelected() {
        return items.size() == allItemsize;
    }

}
