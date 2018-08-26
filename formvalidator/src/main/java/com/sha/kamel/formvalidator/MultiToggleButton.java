package com.sha.kamel.formvalidator;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.annimon.stream.Stream;

import java.util.ArrayList;

public class MultiToggleButton extends ToggleButton {

    public MultiToggleButton(Context context) {
        super(context, null);
    }

    public MultiToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initItems();
    }

    private void initItems() {

        setItems(labels);
    }

    /**
     * Set the enabled state of this MultiToggleButton, including all of its child items.
     *
     * @param enabled True if this view is enabled, false otherwise.
     */
    @Override
    public void setEnabled(boolean enabled) {
        Stream.of(items).forEach(item -> item.setEnabled(enabled));
    }

    public ToggleButton setItems(@Nullable CharSequence[] labels) {
        return setItems(labels, null, null);
    }

    public ToggleButton setItems(@Nullable CharSequence[] labels, @Nullable boolean[] selected) {
        return setItems(labels, null, selected);
    }

    /**
     * Set multiple items with the specified labels and default
     * initial values. Initial states are allowed, but both
     * arrays must be of the same size.
     *
     * @param labels            An array of CharSequences for the items
     * @param imageResourceIds an optional icon to show, either text, icon or both needs to be set.
     * @param selected         The default value for the items
     */

    public ToggleButton setItems(@Nullable CharSequence[] labels, @Nullable int[] imageResourceIds, @Nullable boolean[] selected) {
        boolean[] selection = selected == null ? new boolean[count(labels)] : selected;
        if (selectFirstItem && selection.length > 0) selection[0] = true;

        this.labels = labels;
        final int itemsCount = Math.max(count(labels), count(imageResourceIds));

        if (itemsCount == 0) return this;

        prepare();

        addItem(itemsCount, labels, imageResourceIds, selected);
        return this;
    }

    private void addItem(
            int itemsCount,
            CharSequence[] labels,
            int[] imageResourceIds,
            boolean[] selected
    ) {
        rootView.removeAllViews();
        items = new ArrayList<>(itemsCount);

        if (labels == null) return;

        LayoutInflater inflater = layoutInflater();

        Stream.of(labels)
                .forEachIndexed((i, label) -> {
                    TextView tv = createTextView(i, itemsCount, inflater);

                    tv.setText(label);
                    if (imageResourceIds != null && imageResourceIds[i] != 0)
                        tv.setCompoundDrawablesWithIntrinsicBounds(imageResourceIds[i], 0, 0, 0);

                    tv.setOnClickListener(v -> toggleItemSelection(i));
                    rootView.addView(tv);

                    boolean defaultSelected = true;
                    if (selected == null || itemsCount != selected.length)
                        defaultSelected = false;
                    if (defaultSelected) setSelected(tv, selected[i]);

                    this.items.add(tv);
                });
    }



}
