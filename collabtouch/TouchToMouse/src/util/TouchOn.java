/**
 * 
 */
package util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

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
public class TouchOn {
    public static void main(String[] args) {
        try {
            if (args.length != 2) {
                System.out.println("useage: java -jar TouchOn.jar server_ip_address server_port");
                System.exit(0);
            }
            Socket sock = new Socket(args[0], Integer.parseInt(args[1]));
            DataInputStream in = new DataInputStream(sock.getInputStream());

            String s = in.readUTF();

            DataOutputStream os = new DataOutputStream(sock.getOutputStream());
            if (os != null) {
                os.writeUTF("turn on touch events\n");
            }
            sock.close();

        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + args[0]);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
