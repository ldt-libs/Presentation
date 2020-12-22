package com.dtrung98.presentation.util;

public class FlagUtils {

// ------------------------------------------------------------------------
// TYPES
// ------------------------------------------------------------------------

// ------------------------------------------------------------------------
// STATIC FIELDS
// ------------------------------------------------------------------------

// ------------------------------------------------------------------------
// STATIC METHODS
// ------------------------------------------------------------------------

/**
 * Sets the specified flags on the source int
 *
 * @param source the source int
 * @param flag   the flags which should be set
 *
 * @return the set int
 */
public static int setFlag(int source, int flag) {
    return source | flag;
}

/**
 * Un-sets the specified flags on the source int
 *
 * @param source the source int
 * @param flag   the flags which should be set
 *
 * @return the set int
 */
public static int unsetFlag(int source, int flag) {
    return source & ~flag;
}


/**
 * Check if the flags are set on the source ints
 *
 * @param source the source int
 * @param flag   the flags which should be set
 *
 * @return the set int
 */
public static boolean isFlagSet(int source, int flag) {
    return (source & flag) == flag;
}

/**
 * Flibs teh specified bit on the source
 *
 * @param source the source int
 * @param flag   the flags which should be set
 *
 * @return the set int
 */
public static int flip(int source, int flag) {
    return source & ~flag;
}

/**
 * Returns the masked int
 *
 * @param source the source int
 * @param mask
 *
 * @return the set int
 */
public static int mask(int source, int mask) {
    return source & mask;
}


// ------------------------------------------------------------------------
// FIELDS
// ------------------------------------------------------------------------

// ------------------------------------------------------------------------
// CONSTRUCTORS
// ------------------------------------------------------------------------

// ------------------------------------------------------------------------
// METHODS
// ------------------------------------------------------------------------

// ------------------------------------------------------------------------
// GETTERS / SETTTERS
// ------------------------------------------------------------------------
}