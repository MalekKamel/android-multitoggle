package com.sha.kamel.multitogglebutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.annimon.stream.function.IntConsumer;

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

    protected IntConsumer maxCallback;
    protected int maxItemsToSelect;

    public interface OnItemSelectedListener {
        void onSelected(ToggleButton toggleButton, View item, int value, String label, boolean selected);
    }

    private OnItemSelectedListener listener;

    protected float cornerRadius;
    protected int rootViewBackgroundColor;
    protected boolean isRounded;
    protected boolean scrollable;
    protected boolean selectFirstItem;
    protected boolean textAllCaps;

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
            colorPressed = color(a, R.styleable.MultiToggleButton_mtbPressedColor, R.color.blue);
            colorUnpressed = color(a, R.styleable.MultiToggleButton_mtbUnpressedColor, R.color.white_unpressed);
            rootViewBackgroundColor = colorUnpressed;

            colorPressedText = color(a, R.styleable.MultiToggleButton_mtbColorPressedText);
            colorUnpressedText = color(a, R.styleable.MultiToggleButton_mtbColorUnpressedText);

            isRounded = a.getBoolean(R.styleable.MultiToggleButton_mtbRoundedCorners, false);
            multipleChoice = a.getBoolean(R.styleable.MultiToggleButton_mtbMultipleChoice, false);
            scrollable = a.getBoolean(R.styleable.MultiToggleButton_mtbScrollable, false);
            textAllCaps = a.getBoolean(R.styleable.MultiToggleButton_mtbTextAllCaps, true);
            selectFirstItem = a.getBoolean(R.styleable.MultiToggleButton_mtbSelectFirstItem, true);
            cornerRadius = a.getDimension(R.styleable.MultiToggleButton_mtbCornerRadius, -1);

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
                res = !hasRoundedCorners() ? R.layout.view_root_srcollable : R.layout.view_root_rounded_scrollable;
            else
                res = !hasRoundedCorners() ? R.layout.view_root : R.layout.view_root_rounded;

            ViewGroup v = (ViewGroup) inflater.inflate(res, this, true);
            rootView = scrollable ? v.findViewById(R.id.rootView) : v;

            if (hasRoundedCorners()){
                rootView.setBackgroundResource(R.drawable.root_view_rounded);
                radius(rootView, cornerRadius);
            }
            setBackground(rootView, colorUnpressed);
        }
    }

    protected TextView createTextView(int i, int itemsCount) {
        TextView tv = new Button(getContext());
        tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f));
        tv.setMinHeight(getResources().getDimensionPixelSize(R.dimen.button_min_height));
        tv.setAllCaps(textAllCaps);

        if (i == 0 && itemsCount != 2) {
            if (itemsCount == 1) {
                if (hasRoundedCorners()) radius(tv, cornerRadius);
            }
            else {
                if (hasRoundedCorners()) leftRadius(tv, cornerRadius);
            }
        }

        else if (itemsCount == 2) {
            if (hasRoundedCorners()) radius(tv, cornerRadius);
        }
        else if (i == itemsCount - 1) {
            if (hasRoundedCorners()) rightRadius(tv, cornerRadius);
        }

        return tv;
    }

    /**
     * Listen to selection states of items
     * @param listener called if state changed
     * @return this
     */
    public ToggleButton setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INSTANCE_STATE, super.onSaveInstanceState());
        boolean[] selection = getSelectionArray();
        if (!isEmpty(selection))
            bundle.putBooleanArray(KEY_BUTTON_STATES, selection);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            setItemsSelection(bundle.getBooleanArray(KEY_BUTTON_STATES));
            state = bundle.getParcelable(KEY_INSTANCE_STATE);
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * @return an array of items selection
     */
    public boolean[] getSelectionArray() {
        if (isEmpty(items)) return new boolean[]{};
        boolean[] list = new boolean[items.size()];
        for (int i = 0 ; i < items.size() ; i++)
            list[i] = items.get(i).isSelected();
        return list;
    }

    /**
     * @return list of items selection
     */
    public List<Boolean> getSelectionList() {
        if (isEmpty(items)) return new ArrayList<>();
        return Stream.of(items).map(View::isSelected).toList();
    }

      /**
     * @return list of items selection
     */
    public int getSelectedItemsSize() {
        return Stream.of(items).filter(View::isSelected).toList().size();
    }

    /**
     * Set items selection. selection array must be equal to items
     * size
     * @param selected selection array
     * @return this
     */
    public ToggleButton setItemsSelection(boolean[] selected) {
        if (isEmpty(items) || isEmpty(selected) || items.size() != selected.length) return this;
        Stream.of(items).forEachIndexed((i, v) -> setItemSelected(v, selected[i]));
        return this;
    }

    /**
     * Set an item selected
     * @param v item view
     * @param selected true for selected
     * @return this
     */
    public ToggleButton setItemSelected(View v, boolean selected) {
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

    /**
     * @return a {@link Selected} object with zero or
     * more selected items
     */
    public Selected getSelected() {
        List<TextView> selected = Stream.of(items)
                .filterIndexed((i, item) -> item.isSelected())
                .toList();

        List<Integer> selectedPositions = Stream.of(items)
                .filterIndexed((i, item) -> item.isSelected())
                .map(items::indexOf)
                .toList();
        return new Selected(selected, selectedPositions, items.size());
    }

    /**
     * Reverse selection of an item
     * @param position index of item
     * @return this
     */
    public ToggleButton toggleItemSelection(int position) {
        TextView item = items.get(position);
        boolean currentState = item.isSelected();
        Stream.of(items)
                .forEachIndexed((i, v) -> {
                    // Update selected item only in multiple choice
                    if (multipleChoice) {
                        if (i == position && v != null)
                            setItemSelected(v, !v.isSelected());
                        return;
                    }
                    //
                    setItemSelected(items.get(i), i == position);
                });

        String label = item.getText().toString();

        if (listener != null && currentState != item.isSelected())
            listener.onSelected(this, item, position, label, item.isSelected());

        return this;
    }

    /**
     * update items
     */
    public void refresh() {
        Stream.of(getSelectionList())
                .forEachIndexed((i, selected) -> setItemSelected(items.get(i), selected));
        setBackground(rootView, colorUnpressed);
    }

    /**
     * The desired color resource identifier generated by the aapt tool
     *
     * @param colorPressed    color resource ID for the pressed item
     * @param colorUnpressed color resource ID for the released item
     * @return this
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
     * @param colorPressed    resolved color for the pressed item
     * @param colorUnpressed resolved color for the released item
     * @return this
     */
    public ToggleButton setColors(@ColorInt int colorPressed, @ColorInt int colorUnpressed) {
        try {
            this.colorPressed = colorPressed;
            this.colorUnpressed = colorUnpressed;
            rootViewBackgroundColor = colorUnpressed;
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
     * @return this
     */
    public ToggleButton setPressedColorsRes(@ColorRes int colorPressedText, @ColorRes int colorPressedBackground) {
        setPressedColors(
                color(colorPressedText),
                color(colorPressedBackground)
        );
        return this;
    }

    /**
     * The desired color resource for pressed color text
     * @param color resource
     * @return this
     */
    public ToggleButton setPressedColorTextRes(@ColorRes int color) {
        this.colorPressedText = color(color);
        return this;
    }

    /**
     * The desired color for pressed color text
     * @param color resource
     * @return this
     */
    public ToggleButton setPressedColorText(@ColorInt int color) {
        this.colorPressedText = color;
        refresh();
        return this;
    }

    /**
     * The desired color for unpressed color text
     * @param color resource
     * @return this
     */
    public ToggleButton setUnpressedColorTextRes(@ColorRes int color) {
        this.colorUnpressedText = color(color);
        refresh();
        return this;
    }

    /**
     * The desired color for pressed color text
     * @param color resource
     * @return this
     */
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
     * @return this
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
     * @return this
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
     * @return this
     */
    public ToggleButton setUnpressedColors(@ColorInt int colorNotPressedText, @ColorInt int colorUnpressedBackground) {
        this.colorUnpressedText = colorNotPressedText;
        this.colorUnpressed = colorUnpressedBackground;
        rootViewBackgroundColor = colorUnpressed;
        refresh();
        return this;
    }

    /**
     * The desired color resource identifier generated by the aapt tool
     *
     * @param colorPressedText     drawable resource ID for the pressed createTextView's background
     * @param colorUnpressedText  drawable resource ID for the released createTextView's background
     * @return this
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
     * @param colorPressedText  resolved color for the pressed createTextView's text
     * @param colorUnpressedText  resolved color for the released createTextView's text
     * @return this
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
     * @param enable true to be multiple selected
     * @return this
     */
    public ToggleButton multipleChoice(boolean enable) {
        this.multipleChoice = enable;
        return this;
    }

    /**
     * Select first item. The first item is selected by default
     * @param selected false to deselect
     * @return this
     */
    public ToggleButton selectFirstItem(boolean selected) {
        this.selectFirstItem = selected;

        if (isEmpty(items)) return this;

        List<Boolean> states = getSelectionList();
        states.set(0, true);
        Stream.of(states).forEachIndexed((i, s) -> setItemSelected(items.get(i), s));
        return this;
    }

    /**
     * @return An array of the items' labels
     */
    public CharSequence[] getLabels() {
        return this.labels;
    }

    /**
     * Set item label for item by position
     * @param label text
     * @param position index of item
     * @return this
     */
    public ToggleButton setLabel(CharSequence label, int position){
        TextView item = items.get(position);
        item.setText(label);
        return this;
    }

    /**
     * Set item label for item by position
     * @param label text resource
     * @param position index of item
     * @return this
     */
     public ToggleButton setLabel(@StringRes int label, int position){
        TextView item = items.get(position);
        item.setText(label);
        return setLabel(getContext().getString(label), position);
    }

    /**
     * Set item label for item by position
     * @param labels texts resources
     * @return this
     */
    public ToggleButton setLabelsRes(List<Integer> labels){
        List<String> l = Stream.of(labels).map(label -> getContext().getString(label)).toList();
        return setLabels(l);
    }

    /**
     * Set item label for item by position
     * @param labels texts strings
     * @return this
     */
    public ToggleButton setLabels(List<String> labels){
        if (isEmpty(items)) return this;
        if (count(labels) != count(items)) throw new IllegalArgumentException("Labels size may not equal to items size.");

        Stream.of(items).forEachIndexed((i, item) -> item.setText(labels.get(i)));
        return this;
    }

    /**
     * Specify the radius of corners
     * @param cornerRadius radius size
     */
    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    /**
     * @return true if mtbRoundedCorners is set to true
     * or mtbCornerRadius value is greater than zero
     */
    public boolean hasRoundedCorners(){
        return cornerRadius != -1 || isRounded;
    }

    /**
     * Return item by position
     * @param position index of item
     * @return item with the specified position
     */
    public TextView itemAt(int position){
        return items.get(position);
    }

    /**
     * @return list of all items. If no items, an empty array is returned.
     * Never return null
     */
    public List<TextView> items() {
        return items == null ? new ArrayList<>() : items;
    }

    /**
     * Specify maximum number of items to be selected
     * @param max number of items
     * @param callbackIfExceeded will be called if the selected items exceeds max
     * @return this
     */
    public ToggleButton maxSelectedItems(int max, IntConsumer callbackIfExceeded){
        if (max > items.size())
            throw new IllegalArgumentException("max may not be greater than added items");
        maxItemsToSelect = max;
        maxCallback = callbackIfExceeded;
        multipleChoice = true;
        return this;
    }

    /**
     * @return true if multiple choice enabled
     */
    public boolean isMultipleChoice() {
        return multipleChoice;
    }

    /**
     * @return root view of all items
     */
    @Override
    public ViewGroup getRootView() {
        return rootView;
    }

    /**
     * @return max items to select
     */
    public int getMaxItemsToSelect() {
        return maxItemsToSelect;
    }

    public float getCornerRadius() {
        return cornerRadius;
    }

    /**
     * @return true if rounded cornders
     */
    public boolean isRoundedCorners() {
        return isRounded;
    }

    /**
     * @return true if can scroll
     */
    public boolean isScrollable() {
        return scrollable;
    }

    /**
     * @return true if first item is selected by default
     */
    public boolean isSelectFirstItem() {
        return selectFirstItem;
    }

    /**
     * @return true if all caps enabled
     */
    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    /**
     * @return color of pressed item
     */
    public int getColorPressed() {
        return colorPressed;
    }

    /**
     * @return color of unpressed item
     */
    public int getColorUnpressed() {
        return colorUnpressed;
    }

    /**
     * @return text color of pressed text
     */
    public int getColorPressedText() {
        return colorPressedText;
    }

    /**
     * @return text color of unpressed text
     */
    public int getColorUnpressedText() {
        return colorUnpressedText;
    }
}