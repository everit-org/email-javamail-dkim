/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.email.javamail.dkim;

import java.io.IOException;
import java.net.ServerSocket;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

/**
 * Implementation of {@link GreenmailService}.
 */
public class GreenmailServiceImpl implements GreenmailService {

  private GreenMail greenMail = null;

  private int port;

  public GreenmailServiceImpl() {
    port = findRandomOpenPort();
    startGreenmail();
  }

  private int findRandomOpenPort() {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public GreenMail getGreenMail() {
    return greenMail;
  }

  @Override
  public int getPort() {
    return port;
  }

  @Override
  public void startGreenmail() {
    ServerSetup serverSetup = new ServerSetup(port, null, "smtp");
    greenMail = new GreenMail(serverSetup);
    greenMail.start();
  }

  @Override
  public void stopGreenmail() {
    if (greenMail != null) {
      greenMail.stop();
    }
  }

}
