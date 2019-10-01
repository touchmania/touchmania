/*
 * Copyright 2018 Vincenzo Fortunato
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

package net.touchmania.game.ui.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import net.touchmania.game.Game;
import net.touchmania.game.song.*;
import net.touchmania.game.song.sim.SimParseException;
import net.touchmania.game.song.sim.SimParser;
import net.touchmania.game.util.concurrent.DoneListener;
import net.touchmania.game.util.ui.Screen;
import net.touchmania.game.util.ui.Theme;

import java.io.IOException;
import java.util.logging.FileHandler;

/**
 * @author flood2d
 */
public class GameScreen implements Screen {
    private static GameScreen instance;
    private boolean prepared = false;

    /* Widgets */
    private BeatmapView beatmapView;

    private Music music;

    private GameScreen() {
        Game.instance().getDisposer().manage(this);
    }

    @Override
    public void prepare() {
        /* TODO TEST START */
        FileHandle fh = Gdx.files.external("touchmania/Songs/ITG Rodeo Tournament 8/012 - Into Dust");
        SongLoader sl = new SongLoader(fh);
        try {
            Song song = sl.call();
            Chart chart = null;
            for(Chart c : song.charts) {
                if(c.type == ChartType.DANCE_SINGLE) {
                    chart = c;
                }
            }

            SimParser parser = song.simFormat.newParser();
            parser.init(Files.toString(song.simFile.file(), Charsets.UTF_8));
            chart.beatmap = parser.parseBeatmap(chart);
            beatmapView = new BeatmapView();
            beatmapView.setBeatmap(chart.beatmap);
            music = Gdx.audio.newMusic(Gdx.files.external(song.musicPath));
        } catch (Exception e) {
            System.err.println("Cannot read song!");
            e.printStackTrace();
        }
        /* TODO TEST END */

        prepared = true;
    }

    @Override
    public void show(Stage stage) {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        table.setDebug(true); //TODO

        beatmapView = new BeatmapView();

        table.add(beatmapView)
                .padLeft(0)
                .padTop(0)
                .width(1080)
                .height(1920);

        table.left().top();

        music.play();
    }

    @Override
    public void hide(DoneListener listener) {

    }

    @Override
    public void dispose() {
        if(music != null) {
            music.dispose();
        }
        prepared = false;
    }

    @Override
    public boolean isPrepared() {
        return prepared;
    }

    @Override
    public Theme getTheme() {
        return Game.instance().getThemes().getActiveTheme();
    }

    public static GameScreen instance() {
        return instance == null ? instance = new GameScreen() : instance;
    }
}
