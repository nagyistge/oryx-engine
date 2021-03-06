package org.jodaengine.navigator;

/**
 * Represents execution states a navigator may be in.
 */
public enum NavigatorState {

    INIT,

    RUNNING,

    STOPPED,

    /** All process instances are finished at this moment (no running Instances). */
    IDLE
}
