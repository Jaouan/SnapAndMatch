package com.jaouan.snapandmatch.components.utils;

/**
 * Procedure with 1 paramter.
 *
 * @param <Parameter1> Type of parameter 1.
 * @author Maxence Jaouan
 */
public interface Procedure1<Parameter1> {

    /**
     * Proceed action.
     *
     * @param parameter1 Parameter 1.
     */
    void proceed(Parameter1 parameter1);

}
