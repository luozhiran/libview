// IPersistWakeupServiceCallback.aidl
package com.yqtec.toy.service;

// Declare any non-default types here with import statements

interface IPersistWakeupServiceCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    boolean onPersistWakeup(String keyword, int id);
}
