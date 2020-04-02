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

package net.sync.game.song;

import com.badlogic.gdx.files.FileHandle;
import net.sync.game.Game;
import org.jooq.DSLContext;

import java.io.File;

public class SongManager {
    //Start indexing the given folder (the songs folder)
    public void index(FileHandle dir) {
        //TODO Temp test
        long millis = System.currentTimeMillis();
        for(FileHandle f : dir.list(File::isDirectory)) {
            String pack = f.name();
            try(DSLContext database = Game.instance().getDatabase().getDSL()) {
                database.transaction(configuration -> {
                    for(FileHandle songDir : f.list(File::isDirectory)) {
                        SongIndexer indexer = new SongIndexer(pack, songDir, configuration);
                        try {
                            indexer.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        System.out.println(System.currentTimeMillis() - millis);
    }

    //Find a set of song matching the given params
    public void find(SongSearchParams params) {

    }

    //From preview to view state
    public void load(Song song) {

    }

    //From view to preview state
    public void unload(Song song) {

    }


}