package com.zigapk.gimvic.suplence;

import java.lang.reflect.Field;

/**
 * Created by ziga on 12/5/14.
 */
public class Other {

    public static boolean layoutComponentsReady(){
        boolean temp = true;
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 8; j++){
                if(Main.textViews[i][j][2] == null) temp = false;
            }
        }

        if(temp){
            System.out.print("");
        }

        return temp;
    }
}
