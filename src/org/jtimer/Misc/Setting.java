package org.jtimer.Misc;

/**
 * This enum contain the various settings that you can use for the
 * {@link org.jtimer.Annotations.Settings @Settings} annotation.
 * <br>
 * {@link org.jtimer.Misc.Setting#BEST_FIT BEST_FIT} will creat a split-screen
 * graph for the runner after everything has been graphed so that a function of
 * best fit can be shown.
 * <br>
 * {@link org.jtimer.Misc.Setting#AVERAGE_TIME AVERAGE_TIME} currently does not
 * do anything until implemented...
 * 
 * @author MagneticZero
 *
 */
public enum Setting {
	BEST_FIT, AVERAGE_TIME
}
