package com.medmobile.pid.medassist.app.InputFilters;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by Pavle on 2.6.2014..
 */
public class InputFilterMinMax implements InputFilter
{
    private int min;
    private int max;

    public InputFilterMinMax(int min, int max)
    {
        this.min = min;
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
    {
        try {
            String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length());
            // Add the new string in
            newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length());
            int input = Integer.parseInt(newVal);
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nf) { }
            return "";

    }

    private boolean isInRange(int min, int max, int n)
    {
        return min < max ? n >= min && n <= max : n >= max && n <= min;
    }
}
