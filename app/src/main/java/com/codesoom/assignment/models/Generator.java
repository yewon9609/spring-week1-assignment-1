package com.codesoom.assignment.models;

import java.util.concurrent.atomic.AtomicLong;

public class Generator {
    private static AtomicLong sequence = new AtomicLong();

    public static Long incrementId(){
        return sequence.addAndGet(1);
    }
}
