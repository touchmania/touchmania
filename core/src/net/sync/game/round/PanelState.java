/*
 * Copyright (c) 2020 Vincenzo Fortunato.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.sync.game.round;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import java.util.Map;
import java.util.TreeMap;

/**
 * Contains panels states over time. Released is the default initial state.
 */
public class PanelState {
    private IntMap<TreeMap<Double, Boolean>> panelStates = new IntMap<>();
    private Array<PanelStateListener> listeners = new Array<>();

    /**
     * Set the panel state at the given time. If the panel
     * state at the given time is the same as the one that is being set
     * it will be ignored. Listeners will be notified only
     * if the state actually change.
     * @param panel the panel
     * @param time the time in seconds
     * @param pressed the state to set
     */
    public void setState(int panel, double time, boolean pressed) {
        //Get the map containing the states at each time or create it if necessary
        TreeMap<Double, Boolean> states = panelStates.get(panel);
        if(states == null) {
            states = new TreeMap<>();
            panelStates.put(panel, states);
        }

        if(states.isEmpty() && !pressed) {
            //By default the initial state is released.
            //Ignore setting release state when panel has never been pressed.
            return;
        }

        //Check if the state actually change
        Map.Entry<Double, Boolean> floorEntry = states.floorEntry(time);
        if(floorEntry == null || floorEntry.getValue() != pressed) {
            //State changes
            states.put(time, pressed);

            //Notify listeners
            for(PanelStateListener listener : listeners) {
                listener.onPanelStateChange(panel, time, pressed);
            }
        }
    }

    /**
     * Convenience method for {@link #setState(int, double, boolean)}.
     * Set the panel pressed at the given time.
     * @param panel the panel
     * @param time the time in seconds
     */
    public void setPressed(int panel, double time) {
        setState(panel, time, true);
    }

    /**
     * Convenience method for {@link #setState(int, double, boolean)}.
     * Set the panel released at the given time.
     * @param panel the panel
     * @param time the time in seconds
     */
    public void setReleased(int panel, double time) {
        setState(panel, time, false);
    }

    /**
     * Checks if the given panel is pressed at the given time.
     * @param panel the panel
     * @param time the time in seconds
     * @return true if the panel is pressed at the given time
     */
    public boolean isPressedAt(int panel, double time) {
        TreeMap<Double, Boolean> states = panelStates.get(panel);
        if(states != null) {
            Map.Entry<Double, Boolean> entry = states.floorEntry(time);
            return entry != null && entry.getValue();
        }
        return false;
    }

    /**
     * Checks if the given panel is released at the given time.
     * @param panel the panel
     * @param time the time in seconds
     * @return true if the panel is released at the given time
     */
    public boolean isReleasedAt(int panel, double time) {
        return !isPressedAt(panel, time);
    }

    /**
     * Gets the time in seconds strictly less to the given time when a state change occurred.
     * @param panel the panel
     * @param time the time in seconds.
     * @return <li> the time in seconds strictly less to the given time when a state change occurred </li>
     * <li> {@link Double#MIN_VALUE} if a pressed state never occurred </li>
     */
    public double getLowerTimeState(int panel, double time) {
        TreeMap<Double, Boolean> states = panelStates.get(panel);
        if(states != null && !states.isEmpty()) {
            Map.Entry<Double, Boolean> state = states.lowerEntry(time);
            if(state != null) {
                return state.getKey();
            }
        }
        return Double.MIN_VALUE;
    }

    /**
     * Gets the time in seconds strictly less to the given time when the given state occurred.
     * @param panel the panel
     * @param time the time in seconds.
     * @param pressed the state, true for pressed, false for released.
     * @return <li> the time in seconds strictly less to the given time when the given state occurred </li>
     * <li> {@link Double#MAX_VALUE} if checking for pressed state and a pressed state never occurred </li>
     * <li> {@link Double#MIN_VALUE} if checking for released state and a pressed state never occurred </li>
     */
    public double getLowerTimeState(int panel, double time, boolean pressed) {
        TreeMap<Double, Boolean> states = panelStates.get(panel);
            if(states != null && !states.isEmpty()) {
                Map.Entry<Double, Boolean> state = states.lowerEntry(time);
                if(state != null) {
                    if(state.getValue() == pressed) {
                        return state.getKey();
                    } else {
                        state = states.lowerEntry(state.getKey());
                        if(state != null) {
                            return state.getKey();
                        }
                    }
                }
        }
        return pressed ? Double.MAX_VALUE : Double.MIN_VALUE;
    }

    /**
     * Gets the time in seconds strictly less to the given time when a pressed state occurred.
     * @param panel the panel
     * @param time the time in seconds.
     * @return the the time in seconds strictly less to the given time when a pressed state occurred,
     * or {@link Double#MAX_VALUE} if a pressed state never occurred.
     */
    public double getLowerTimePressed(int panel, double time) {
        return getLowerTimeState(panel, time, true);
    }

    /**
     * Gets the time in seconds strictly less to the given time when a released state occurred.
     * @param panel the panel
     * @param time the time in seconds.
     * @return the the time in seconds strictly less to the given time when a released state occurred,
     * or {@link Double#MIN_VALUE} if the panel has never been pressed.
     */
    public double getLowerTimeReleased(int panel, double time) {
        return getLowerTimeState(panel, time, false);
    }

    /**
     * Gets the time in seconds strictly less or equal to the given time when a state change occurred.
     * @param panel the panel
     * @param time the time in seconds.
     * @return <li> the time in seconds strictly less or equal to the given time when a state change occurred </li>
     * <li> {@link Double#MIN_VALUE} if a pressed state never occurred </li>
     */
    public double getFloorTimeState(int panel, double time) {
        TreeMap<Double, Boolean> states = panelStates.get(panel);
        if(states != null && !states.isEmpty()) {
            Map.Entry<Double, Boolean> state = states.lowerEntry(time);
            if(state != null) {
                return state.getKey();
            }
        }
        return Double.MIN_VALUE;
    }

    /**
     * Gets the time in seconds strictly less or equal to the given time when the given state occurred.
     * @param panel the panel
     * @param time the time in seconds.
     * @param pressed the state, true for pressed, false for released.
     * @return <li> the time in seconds strictly less or equal to the given time when the given state occurred </li>
     * <li> {@link Double#MAX_VALUE} if checking for pressed state and a pressed state never occurred </li>
     * <li> {@link Double#MIN_VALUE} if checking for released state and a pressed state never occurred </li>
     */
    public double getFloorTimeState(int panel, double time, boolean pressed) {
        TreeMap<Double, Boolean> states = panelStates.get(panel);
        if(states != null && !states.isEmpty()) {
            Map.Entry<Double, Boolean> state = states.floorEntry(time);
            if(state != null) {
                if(state.getValue() == pressed) {
                    return state.getKey();
                } else {
                    state = states.lowerEntry(state.getKey());
                    if(state != null) {
                        return state.getKey();
                    }
                }
            }
        }
        return pressed ? Double.MAX_VALUE : Double.MIN_VALUE;
    }

    /**
     * Gets the time in seconds strictly less or equal to the given time when a pressed state occurred.
     * @param panel the panel
     * @param time the time in seconds.
     * @return the the time in seconds strictly less or equal to the given time when a pressed state occurred,
     * or {@link Double#MAX_VALUE} if a pressed state never occurred.
     */
    public double getFloorTimePressed(int panel, double time) {
        return getFloorTimeState(panel, time, true);
    }

    /**
     * Gets the time in seconds strictly less or equal to the given time when a released state occurred.
     * @param panel the panel
     * @param time the time in seconds.
     * @return the the time in seconds strictly less or equal to the given time when a released state occurred,
     * or {@link Double#MIN_VALUE} if the panel has never been pressed.
     */
    public double getFloorTimeReleased(int panel, double time) {
        return getFloorTimeState(panel, time, false);
    }

    /**
     * Adds the given listener.
     * @param listener the listener
     * @return true if the listener has been added, false otherwise (listener already registered)
     */
    public boolean addListener(PanelStateListener listener) {
        if(!listeners.contains(listener, true)) {
            listeners.add(listener);
            return true;
        }
        return false;
    }

    /**
     * Removes the given listener.
     * @param listener the listener
     * @return true if the listener has been removed, false otherwise (listener not registered)
     */
    public boolean removeListener(PanelStateListener listener) {
        return listeners.removeValue(listener, true);
    }

    /**
     * Removes all listeners
     */
    public void clearListeners() {
        listeners.clear();
    }

    public interface PanelStateListener {
        /**
         * Called when the given panel change state at the given time.
         * @param panel the panel
         * @param time the time in seconds when the change occurs
         * @param pressed the updated state, true if pressed, false if released
         */
        void onPanelStateChange(int panel, double time, boolean pressed);
    }
}
