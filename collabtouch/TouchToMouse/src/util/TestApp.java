/**
 * 
 */
package util;

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
public class TestApp {
    public static void main(String[] args) {
        try {

            Socket s = new Socket("127.0.0.1", 12346);

            DataOutputStream os = new DataOutputStream(s.getOutputStream());
            if (os != null) {
                os.writeUTF("turn on touch events\n");
                os.writeUTF("turn off touch events\n");
            }
            s.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
