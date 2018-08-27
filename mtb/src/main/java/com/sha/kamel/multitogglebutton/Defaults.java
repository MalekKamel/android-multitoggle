package com.sha.kamel.multitogglebutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public interface Defaults {
    float DEFAULT_CORNER_RADIUS = 18;

    default int color(TypedArray a, int res) {
        return a.getColor(res, 0);
    }

    default int color(TypedArray a, int res, int defValue) {
        return a.getColor(res, color(defValue));
    }

    default int color(int res) {
        return ContextCompat.getColor(getContext(), res);
    }

    default Context getContext(){
        throw new UnsupportedOperationException();
    }

    default LayoutInflater layoutInflater(){
        return  (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    default boolean isValidColor(int color){
        return color != 0;
    }

    default void setBackground(View v, @ColorInt int color) {
        if (!(v.getBackground() instanceof GradientDrawable)){
            v.setBackgroundColor(color);
            return;
        }
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        drawable.setColor(color);
    }

    default void radius(View v, float radius) {
        if (radius == -1) radius = DEFAULT_CORNER_RADIUS;
        if (radius == 0) return;
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(0, Color.TRANSPARENT);
        drawable.setCornerRadius(radius);
        v.setBackground(drawable);
    }

    default void radius(View v, float[] radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadii(radius);
        drawable.setStroke(0, Color.TRANSPARENT);
        v.setBackground(drawable);
    }

    default void leftRadius(View v, float radius) {
        if (radius == -1) radius = DEFAULT_CORNER_RADIUS;
        if (radius == 0) return;
        radius(v, new float[] { radius, radius, 0, 0, 0, 0, radius, radius });
    }

    default void rightRadius(View v, float radius) {
        if (radius == -1) radius = DEFAULT_CORNER_RADIUS;
        if (radius == 0) return;
        radius(v, new float[] { 0, 0, radius, radius, radius, radius, 0, 0 });
    }


    default <T> int count(T[] array){
        return array == null ? 0 : array.length;
    }

    default int count(int[] array){
        return array == null ? 0 : array.length;
    }

    default boolean isEmpty(int[] array){
        return array == null || array.length == 0;
    }

    default boolean isNotEmpty(int[] array){
        return array != null && array.length > 0;
    }

    default boolean isEmpty(boolean[] array){
        return array == null || array.length == 0;
    }

    default boolean isNotEmpty(boolean[] array){
        return array != null && array.length > 0;
    }

    default <T> boolean isEmpty(List<T> array){
        return array == null || array.isEmpty();
    }

    default <T> boolean isNotEmpty(List<T> array){
        return array != null && !array.isEmpty();
    }

    default <T> boolean isEmpty(T[] array){
        return array == null || array.length == 0;
    }

    default <T> boolean isNotEmpty(T[] array){
        return array != null && array.length > 0;
    }

    default <T> int count(List<T> list){
        return list == null ? 0 : list.size();
    }

    default String text(TextView tv){
        return tv.getText().toString();
    }


}
