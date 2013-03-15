/**
 * 
 */
package util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/* Copyright [2013] [Netherlands eScience Center]
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

/**
 * @author Maarten van Meersbergen <m.vanmeersbergen@esciencecenter.nl>
 */
public class StartStopDeamon implements Runnable {
    private final TouchToMouseApp app;
    private final int myPort;
    private boolean running = true;

    /**
     * Basic constructor for StartStopDeamon
     */
    public StartStopDeamon(TouchToMouseApp app, int port) {
        this.app = app;
        this.myPort = port;
    }

    @Override
    public void run() {
        ServerSocket ssock;
        try {
            ssock = new ServerSocket(myPort);
            // ssock.setSoTimeout(10000);

            while (running) {
                try {
                    Socket server = ssock.accept();

                    DataOutputStream os = new DataOutputStream(server.getOutputStream());
                    if (os != null) {
                        if (app.isListeningToEvents()) {
                            os.writeUTF("touch events currently on\n");
                        } else {
                            os.writeUTF("touch events currently off\n");
                        }
                    }

                    DataInputStream in = new DataInputStream(server.getInputStream());
                    String s = in.readUTF();
                    while (s != null) {
                        if (s.compareTo("turn on touch events\n") == 0) {
                            app.setListeningToEvents(true);
                        } else if (s.compareTo("turn off touch events\n") == 0) {
                            app.setListeningToEvents(false);
                        } else {
                            System.err.println("incoming message: " + s);
                        }

                        try {
                            s = in.readUTF();
                        } catch (EOFException e) {
                            break;
                        }
                    }

                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
