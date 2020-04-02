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

package net.sync.game.util;

import com.badlogic.gdx.utils.Disposable;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class Disposer implements Disposable {
    private List<Disposable> disposables;

    public void manage(Disposable disposable) {
        Preconditions.checkNotNull(disposable, "Cannot manage null disposable!");
        Preconditions.checkArgument(disposable != this, "Cannot manage itself!");
        if(disposables == null) {
            disposables = new ArrayList<>();
        }
        disposables.add(disposable);
    }

    @Override
    public void dispose() {
        for(Disposable disposable : disposables) {
            disposable.dispose();
        }
    }
}