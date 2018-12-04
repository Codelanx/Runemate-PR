package com.codelanx.pr.runemate;

/**
 * Event listener priority. Priorities are called from first to last in order of declaration
 *
 * @author 1Rogue
 */
public enum Priority {
    /**
     * Lowest priority. Called first, and can be overwritten by higher priorities
     */
    LOWEST,
    /**
     * Called after {@link Priority#LOWEST}, can be overwritten by {@link Priority#NORMAL} or higher
     */
    LOW,
    /**
     * Called after {@link Priority#LOW}, can be overwritten by {@link Priority#HIGH} or higher
     */
    NORMAL,
    /**
     * Called after {@link Priority#NORMAL}, can be overwritten by {@link Priority#HIGHEST}
     */
    HIGH,
    /**
     * Called after {@link Priority#HIGH}, should not be overwritten
     */
    HIGHEST,
    /**
     * Called after all other priorities. Should not be used to modify an event outcome, but merely to listen to it
     */
    MONITOR,
    ;
    //used locally
    static Priority[] VALUES = values();
}
