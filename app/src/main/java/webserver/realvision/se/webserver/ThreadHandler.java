/*
 * Copyright 2016 Jan Igerud
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

package webserver.realvision.se.webserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import fi.iki.elonen.NanoHTTPD;

/**
 * Thread handler for the Webserver
 */
public class ThreadHandler implements NanoHTTPD.AsyncRunner {
    private ExecutorService executorService;
    private final List<NanoHTTPD.ClientHandler> running =
            Collections.synchronizedList(new ArrayList<NanoHTTPD.ClientHandler>());

    public ThreadHandler(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void closeAll() {
        // copy of the list for concurrency
        for (NanoHTTPD.ClientHandler clientHandler : new ArrayList<>(this.running)) {
            clientHandler.close();
        }
    }

    @Override
    public void closed(NanoHTTPD.ClientHandler clientHandler) {
        this.running.remove(clientHandler);
    }

    @Override
    public void exec(NanoHTTPD.ClientHandler clientHandler) {
        executorService.submit(clientHandler);
        this.running.add(clientHandler);
    }
}