package com.zigapk.gimvic.suplence;

import java.lang.reflect.Field;

/**
 * Created by ziga on 12/5/14.
 */
public class Other {

    public static int getResId(String variableName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
