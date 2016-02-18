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

import com.icegreen.greenmail.util.GreenMail;

/**
 * Interface to manage {@link GreenMail}.
 */
public interface GreenmailService {

  /**
   * Gets greenmail service.
   *
   * @return {@link GreenMail} instance.
   */
  GreenMail getGreenMail();

  /**
   * Gets port number.
   */
  int getPort();

  /**
   * Start greenmail service.
   */
  void startGreenmail();

  /**
   * Stop green mail service.
   */
  void stopGreenmail();
}
