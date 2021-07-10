package com.task.newapp.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

public final class RandomColors {
    private Stack<Integer> recycle;
    private Stack<Integer> colors;

    public RandomColors(int colorModel) {
        this.recycle = new Stack();
        this.colors = new Stack();
        if (colorModel == 700) {
            this.recycle.addAll(Arrays.asList(new Integer[]{-13840175, -12720219, -8069026, -5320104,
                    -3162015, -1668818, -166097, -38823, -31083, -3706428, -6856504, -11514923,
                    -16498733, -16752640, -16088064, -15092249, -6111287, -6381921, -4545124}));
        }

        if (colorModel == 400) {
            this.recycle.addAll(Arrays.asList(new Integer[]{-15684432, -15483002, -11225020, -8280002,
                    -6056896, -4367861, -2733814, -2541274, -2533018, -6732650, -10275941, -13951319,
                    -16772696, -16763432, -16754470, -16740419, -9268835, -12434877, -7901340}));
        }

        if (colorModel == 900) {
            this.recycle.addAll(Arrays.asList(new Integer[]{-12000284, -8916559, -4854924, -3218322,
                    -1713022, -870305, -87963, -24676, -19776, -1793568, -3369246, -8550167, -16088855,
                    -16740096, -15094016, -12531212, -4073251, -2171169, -2503224}));
        }

    }

    public int getColor() {
        if (colors.size() == 0) {
            while (!recycle.isEmpty()) colors.push(recycle.pop());
            Collections.shuffle(colors);
        }
        int c = colors.pop();
        recycle.push(c);
        return c;
    }


}
