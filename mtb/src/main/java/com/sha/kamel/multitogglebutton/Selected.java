package com.sha.kamel.multitogglebutton;

import android.view.View;
import android.widget.TextView;

import java.util.List;

public class Selected {
    private List<TextView> items;
    private List<Integer> positions;

    Selected(List<TextView> items, List<Integer> positions) {
        this.items = items;
        this.positions = positions;
    }

    public boolean isSingleItem() {
        return items.size() == 1;
    }

    public View getSingleItem() {
        return items.get(0);
    }

    public int getSinglePosition() {
        return positions.get(0);
    }

    public List<TextView> getItems() {
        return items;
    }

    public List<Integer> getPositions() {
        return positions;
    }

    public boolean isAnySelected() {
        return !items.isEmpty();
    }

}
