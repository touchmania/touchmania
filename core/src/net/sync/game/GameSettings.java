/*
 * Copyright 2020 Vincenzo Fortunato
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sync.game;

import net.sync.game.song.sim.SimFormat;
import net.sync.game.ui.ScreenCachePolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Vincenzo Fortunato
 */
public class GameSettings {
    /** Contains supported sim formats ordered by their priority in ascending order (formats with
     * higher priority have higher indexes).**/
    public List<net.sync.game.song.sim.SimFormat> simFormatPriorityList = new ArrayList<>();

    private net.sync.game.ui.ScreenCachePolicy screenCachePolicy = net.sync.game.ui.ScreenCachePolicy.DISPOSE_ON_HIDE;

    public GameSettings() {
        simFormatPriorityList.add(net.sync.game.song.sim.SimFormat.DWI);
        simFormatPriorityList.add(net.sync.game.song.sim.SimFormat.SSC);
        simFormatPriorityList.add(net.sync.game.song.sim.SimFormat.SM);
    }

    /**
     * Get sim format priority. Higher values are associated with formats with higher priority.
     * This will be used to pick the preferred sim format from a directory containing more then
     * one sim file.
     * @param format the sim format
     * @return the priority of the sim format.
     */
    public int getSimFormatPriority(SimFormat format) {
        return simFormatPriorityList.indexOf(format);
    }

    public ScreenCachePolicy getScreenCachePolicy() {
        return screenCachePolicy;
    }

    public GameMode getGameMode() {
        return GameMode.DANCE;
    }

    public Locale getLanguage() {
        return Locale.getDefault(); //TODO
    }
}