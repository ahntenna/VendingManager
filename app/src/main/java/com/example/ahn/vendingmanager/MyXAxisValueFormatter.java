package com.example.ahn.vendingmanager;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/*
 막대 그래프의 x축 설정
 Main2Activity 클래스의 Xvalues 배열의 크기만큼 x축 개수를 할당하고 설정
 */
public class MyXAxisValueFormatter implements IAxisValueFormatter{
    private String[] mValues;
    public MyXAxisValueFormatter(String[] values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        if((int)value < mValues.length) // 설정하고자 하는 인덱스가 Main2Activity 클래스의 Xvalues 의 크기보다 작을경우만 설정
            return mValues[(int) value];
        return null;
    }

}