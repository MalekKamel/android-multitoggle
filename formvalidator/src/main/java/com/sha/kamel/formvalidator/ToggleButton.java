package com.sha.kamel.formvalidator;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

public abstract class ToggleButton extends LinearLayout
        implements Defaults{

    private static final String KEY_BUTTON_STATES  = "button_states";
    private static final String KEY_INSTANCE_STATE = "instance_state";

    /**
     * A list of rendered items. Used to get state, among others
     */
    protected List<TextView> items;

    /**
     * The specified labels
     */
    protected CharSequence[] labels;
    /**
     * If true, multiple items can be pressed at the same time
     */
    protected boolean multipleChoice = false;

    /**
     * The layout containing all items
     */
    protected ViewGroup rootView;

    public interface OnItemSelectedListener {
        void onSelected(View item, int value, String label, boolean selected);
    }

    private OnItemSelectedListener listener;

    protected float cornerRadius;
    protected int rootViewBackgroundColor;
    protected boolean isRounded;
    protected boolean scrollable;
    protected boolean selectFirstItem;

    @ColorInt
    int colorPressed;

    @ColorInt
    int colorUnpressed;

    @ColorInt
    int colorPressedText;

    @ColorInt
    int colorUnpressedText;

    public ToggleButton(Context context) {
        super(context, null);
    }

    public ToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        resolveAttrs(attrs);
    }

    private void resolveAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MultiToggleButton, 0, 0);
        try {
            rootViewBackgroundColor = color(a, R.styleable.MultiToggleButton_rootViewBackgroundColor, R.color.default_root_view_color);

            colorPressed = color(a, R.styleable.MultiToggleButton_mtbPressedColor, R.color.blue);
            colorUnpressed = color(a, R.styleable.MultiToggleButton_mtbUnpressedColor, R.color.white_unpressed);

            colorPressedText = color(a, R.styleable.MultiToggleButton_mtbColorPressedText);
            colorUnpressedText = color(a, R.styleable.MultiToggleButton_mtbColorUnpressedText);

            isRounded = a.getBoolean(R.styleable.MultiToggleButton_mtbRoundedCorners, false);
            multipleChoice = a.getBoolean(R.styleable.MultiToggleButton_mtbMultipleChoice, false);
            scrollable = a.getBoolean(R.styleable.MultiToggleButton_mtbScrollable, false);
            selectFirstItem = a.getBoolean(R.styleable.MultiToggleButton_mtbSelectFirstItem, true);
            cornerRadius = a.getDimension(R.styleable.MultiToggleButton_mtbCornerRadius, 0f);

            labels = a.getTextArray(R.styleable.MultiToggleButton_labels);

        } finally {
            a.recycle();
        }
    }

    protected void prepare() {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = layoutInflater();
        if (rootView == null) {
            int res;
            if (scrollable)
                res = !isRounded ? R.layout.view_root_srcollable : R.layout.view_root_rounded_scrollable;
            else
                res = !isRounded ? R.layout.view_root : R.layout.view_root_rounded;

            ViewGroup v = (ViewGroup) inflater.inflate(res, this, true);
            rootView = scrollable ? v.findViewById(R.id.rootView) : v;

            if (isRounded){
                rootView.setBackgroundResource(R.drawable.root_view_rounded);
                radius(rootView, cornerRadius);
            }
            setBackground(rootView, colorUnpressed);
        }
    }

    protected TextView createTextView(int i, int itemsCount, LayoutInflater inflater) {
        TextView tv;

        if (i == 0 && itemsCount != 2) {
            // Add a special view when there's only one element
            if (itemsCount == 1)
                tv = inflateTextView(
                        inflater,
                        !isRounded ? R.layout.view_single_toggle_button : R.layout.view_single_toggle_button_rounded
                );
            else {
                tv = inflateTextView(
                        inflater,
                        !isRounded ? R.layout.view_left_toggle_button : R.layout.view_left_toggle_button_rounded
                );
                if (isRounded) leftRadius(tv, cornerRadius);
            }
        }

        else if (itemsCount == 2)
            tv = inflateTextView(
                    inflater,
                    !isRounded ? R.layout.view_center_toggle_button : R.layout.view_center_toggle_button_rounded
            );
        else if (i == itemsCount - 1) {
            tv = inflateTextView(
                    inflater,
                    !isRounded ? R.layout.view_right_toggle_button : R.layout.view_right_toggle_button_rounded
            );
            if (isRounded) rightRadius(tv, cornerRadius);
        }
        else
            tv = inflateTextView(
                    inflater,
                    R.layout.view_center_toggle_button
            );
        return tv;
    }

    protected TextView inflateTextView(LayoutInflater inflater, int res) {
        return (TextView) inflater.inflate(res, rootView, false);
    }

    public ToggleButton setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putBooleanArray(KEY_BUTTON_STATES, getSelectionArray());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            setSelectionStates(bundle.getBooleanArray(KEY_BUTTON_STATES));
            state = bundle.getParcelable(KEY_INSTANCE_STATE);
        }
        super.onRestoreInstanceState(state);
    }

    public boolean[] getSelectionArray() {
        boolean[] list = new boolean[items.size()];
        for (int i = 0 ; i < items.size() ; i++)
            list[i] = items.get(i).isSelected();
        return list;
    }

    public List<Boolean> getSelectionList() {
        if (isEmpty(items)) return new ArrayList<>();
        return Stream.of(items).map(View::isSelected).toList();
    }

    public ToggleButton setSelectionStates(boolean[] selected) {
        if (items == null || selected == null || items.size() != selected.length) return this;
        Stream.of(items).forEachIndexed((i, v) -> setSelected(v, selected[i]));
        return this;
    }

    public ToggleButton setSelected(View v, boolean selected) {
        if (v == null) return this;

        v.setSelected(selected);

        setBackground(v, selected ? colorPressed : colorUnpressed);

        textStyle(v, selected);
        return this;
    }

    private void textStyle(View v, boolean selected) {

        int style = selected ? R.style.WhiteBoldText : R.style.PrimaryNormalText;
        TextView tv = (TextView) v;
        tv.setTextAppearance(getContext(), style);


        if (isValidColor(colorPressedText) || isValidColor(colorUnpressedText))
            tv.setTextColor(selected ? colorPressedText : colorUnpressedText);
        else
            tv.setTextColor(!selected ? colorPressed : colorUnpressed);
    }

    public Selected getSelected() {
        List<TextView> selected = Stream.of(items)
                .filterIndexed((i, item) -> item.isSelected())
                .toList();

        List<Integer> selectedPositions = Stream.of(items)
                .filterIndexed((i, item) -> item.isSelected())
                .mapIndexed((i, item) -> i)
                .toList();
        return new Selected(selected, selectedPositions);
    }

    public ToggleButton toggleItemSelection(int position) {
        TextView item = items.get(position);
        boolean currentState = item.isSelected();
        Stream.of(items)
                .forEachIndexed((i, v) -> {
                    // Update selected item only in multiple choice
                    if (multipleChoice) {
                        if (i == position && v != null)
                            setSelected(v, !v.isSelected());
                        return;
                    }
                    //
                    setSelected(items.get(i), i == position);
                });

        String label = item.getText().toString();

        if (listener != null && currentState != item.isSelected())
            listener.onSelected(item, position, label, item.isSelected());

        return this;
    }

    protected void refresh() {
        Stream.of(getSelectionList())
                .forEachIndexed((i, selected) -> setSelected(items.get(i), selected));
    }

    /**
     * The desired color resource identifier generated by the aapt tool
     *
     * @param colorPressed    color resource ID for the pressed createTextView(s)
     * @param colorUnpressed color resource ID for the released createTextView(s)
     */
    public ToggleButton setColorRes(@ColorRes int colorPressed, @ColorRes int colorUnpressed) {
        setColors(
                color(colorPressed),
                color(colorUnpressed)
        );
        return this;
    }

    /**
     * Color values are in the form 0xAARRGGBB
     *
     * @param colorPressed    resolved color for the pressed createTextView(s)
     * @param colorUnpressed resolved color for the released createTextView(s)
     */
    public ToggleButton setColors(@ColorInt int colorPressed, @ColorInt int colorUnpressed) {
        try {
            this.colorPressed = colorPressed;
            this.colorUnpressed = colorUnpressed;
            // set root color like unpressed to handle effect of round selected item
            if (items.size() == 2) rootViewBackgroundColor = colorUnpressed;
            return this;
        }finally {
            refresh();
        }
    }

    /**
     * The desired color resource identifier generated by the aapt tool
     *
     * @param colorPressedText  color resource ID for the pressed createTextView's text
     * @param colorPressedBackground  color resource ID for the pressed createTextView's background
     */
    public ToggleButton setPressedColorsRes(@ColorRes int colorPressedText, @ColorRes int colorPressedBackground) {
        setPressedColors(
                color(colorPressedText),
                color(colorPressedBackground)
        );
        return this;
    }

    public ToggleButton setPressedColorTextRes(@ColorRes int color) {
        this.colorPressedText = color(color);
        return this;
    }

      public ToggleButton setPressedColorText(@ColorInt int color) {
        this.colorPressedText = color;
          refresh();
          return this;
    }

    public ToggleButton setUnpressedColorTextRes(@ColorRes int color) {
        this.colorUnpressedText = color(color);
        refresh();
        return this;
    }

    public ToggleButton setUnpressedColorText(@ColorInt int color) {
        this.colorUnpressedText = color;
        refresh();
        return this;
    }

    /**
     * Color values are in the form 0xAARRGGBB
     *
     * @param colorPressedText  resolved color for the pressed createTextView's text
     * @param colorPressedBackground  resolved color for the pressed createTextView's background
     */
    public ToggleButton setPressedColors(@ColorInt int colorPressedText, @ColorInt int colorPressedBackground) {
        this.colorPressedText = colorPressedText;
        this.colorPressed = colorPressedBackground;
        refresh();
        return this;
    }

    /**
     * The desired color resource identifier generated by the aapt tool
     *
     * @param colorUnpressedText  color resource ID for the released createTextView's text
     * @param colorUnpressedBackground  color resource ID for the released createTextView's background
     */
    public ToggleButton setUnpressedColorRes(@ColorRes int colorUnpressedText, @ColorRes int colorUnpressedBackground) {
        setUnpressedColors(
                color(colorUnpressedText),
                color(colorUnpressedBackground)
        );
        return this;
    }

    /**
     * Color values are in the form 0xAARRGGBB
     *
     * @param colorNotPressedText  resolved color for the released createTextView's text
     * @param colorUnpressedBackground  resolved color for the released createTextView's background
     */
    public ToggleButton setUnpressedColors(@ColorInt int colorNotPressedText, @ColorInt int colorUnpressedBackground) {
        this.colorUnpressedText = colorNotPressedText;
        this.colorUnpressed = colorUnpressedBackground;
        refresh();
        return this;
    }

    /**
     * The desired color resource identifier generated by the aapt tool
     *
     * @param colorPressedText     drawable resource ID for the pressed createTextView's background
     * @param colorUnpressedText  drawable resource ID for the released createTextView's background
     */
    public ToggleButton setForegroundColorsRes(@ColorRes int colorPressedText, @ColorRes int colorUnpressedText) {
        setForegroundColors(
                color(colorPressedText),
                color(colorUnpressedText)
        );
        return this;
    }

    /**
     * Color values are in the form 0xAARRGGBB
     *
     * @param colorUnpressedText  resolved color for the pressed createTextView's text
     * @param colorUnpressedText  resolved color for the released createTextView's text
     */
    public ToggleButton setForegroundColors(@ColorInt int colorPressedText, @ColorInt int colorUnpressedText) {
        this.colorPressedText = colorPressedText;
        this.colorUnpressedText = colorUnpressedText;
        refresh();
        return this;
    }

    /**
     * If multiple choice is enabled, the user can select multiple
     * values simultaneously.
     *
     * @param enable
     */
    public ToggleButton multipleChoice(boolean enable) {
        this.multipleChoice = enable;
        return this;
    }

    public ToggleButton selectFirstItem(boolean selectFirstItem) {
        this.selectFirstItem = selectFirstItem;

        if (isEmpty(items)) return this;

        List<Boolean> states = getSelectionList();
        states.set(0, true);
        Stream.of(states).forEachIndexed((i, selected) -> setSelected(items.get(i), selected));
        return this;
    }

    /**
     * @return An array of the items' labels
     */
    public CharSequence[] getLabels() {
        return this.labels;
    }

    public ToggleButton setLabel(CharSequence label, int position){
        TextView item = items.get(position);
        item.setText(label);
        return this;
    }

    public ToggleButton setLabels(List<CharSequence> labels){
        if (isEmpty(items)) return this;
        if (count(labels) != count(items)) throw new IllegalArgumentException("Labels size may not equal to items size.");

        Stream.of(items).forEachIndexed((i, item) -> item.setText(labels.get(i)));
        return this;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }
}