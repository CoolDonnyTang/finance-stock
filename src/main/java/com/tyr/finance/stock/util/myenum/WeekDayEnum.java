package com.tyr.finance.stock.util.myenum;

import java.util.HashMap;
import java.util.Map;

public enum WeekDayEnum {
    MON(1), TUE(2), WED(3), THU(4), FRI(5), SAT(6), SUN(7);

    private int value;
    private static Map<Integer, WeekDayEnum> map = new HashMap<>();
    static {
        for(WeekDayEnum day : WeekDayEnum.values()) {
            map.put(day.value, day);
        }
    }

    WeekDayEnum(int value) {
        this.value = value;
    }

    public static WeekDayEnum getDataByValue(int value) {
        return map.get(value);
    }

    public int getValue() {
        return value;
    }
}
