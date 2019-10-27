/* This file is part of PolyChat.
 *
 * Copyright © 2018 john01dav
 *
 * PolyChat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PolyChat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Polychat. If not, see <https://www.gnu.org/licenses/>.
 */
package club.moddedminecraft.polychat.networking.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ThreadedQueue<T> {
    private ExecutorService executorService;

    public final void start() {
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try{
                init();
            }catch(Throwable t){
                t.printStackTrace();
            }
        });
    }

    public final void stop() {
        executorService.shutdown();
    }

    public final synchronized void enqueue(final T obj) {
        executorService.submit(() -> {
            try{
                handle(obj);
            }catch(Throwable t){
                t.printStackTrace();
            }
        });
    }

    protected abstract void init() throws Throwable;

    protected abstract void handle(T obj) throws Throwable;

}
