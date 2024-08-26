package net.iamaprogrammer.reimaginedmenus.util;

import java.util.ArrayList;

public class ProportionManager {
    private final ArrayList<Float> rowProportions = new ArrayList<>();
    private final ArrayList<Float> columnProportions = new ArrayList<>();

    public ProportionManager() {}

    public void addRowProportion(float proportion) {
        this.rowProportions.add(proportion);
    }

    public void addColumnProportion(float proportion) {
        this.columnProportions.add(proportion);
    }

    public float getRowProportion(int pos) {
        return this.rowProportions.get(pos) / sumOf(this.rowProportions);
    }

    public float getColumnProportion(int pos) {
        return this.columnProportions.get(pos) / sumOf(this.columnProportions);
    }

    public float getRowProportion(float context, int pos) {
        return context * (this.rowProportions.get(pos) / sumOf(this.rowProportions));
    }

    public float getColumnProportion(float context, int pos) {
        return context * (this.columnProportions.get(pos) / sumOf(this.columnProportions));
    }

    private static float sumOf(ArrayList<Float> list) {
        float sum = 0;

        for (float i : list) {
            sum += i;
        }
        return sum;
    }

}
