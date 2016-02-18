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

import java.security.interfaces.RSAPrivateKey;

import net.markenwerk.utils.mail.dkim.Canonicalization;
import net.markenwerk.utils.mail.dkim.SigningAlgorithm;

/**
 * Contains configuration information to DKIM signing.
 */
public class MessageDKIMSignerConfig {

  /**
   * The canonicalization to be used for the body. More information in RFC4871.
   */
  public Canonicalization bodyCanonicalization;

  /**
   * The canonicalization to be used for the header. More information in RFC4871.
   */
  public Canonicalization headerCanonicalization;

  /**
   * Identity of the user or agent (e.g., a mailing list manager) on behalf of which this message is
   * signed. More information in RFC4871.
   */
  public String identity;

  /**
   * Use length parameter to signature or not.
   */
  public boolean lengthParam = false;

  /**
   * The RSA Private Key to be used to sign.
   */
  public RSAPrivateKey privateKey;

  /**
   * The selector subdividing the namespace for the domain tag. More information in RFC4871.
   */
  public String selector;

  /**
   * The algorithm that used to generate the signature. More information in RFC4871.
   */
  public SigningAlgorithm signingAlgorithm;

  /**
   * The domain of the signing entity. More information in RFC4871.
   */
  public String signingDomain;

  /**
   * Use z parameter to signature or not. More information in RFC4871.
   */
  public boolean zParam = false;

  public MessageDKIMSignerConfig bodyCanonicalization(
      final Canonicalization bodyCanonicalization) {
    this.bodyCanonicalization = bodyCanonicalization;
    return this;
  }

  public MessageDKIMSignerConfig headerCanonicalization(
      final Canonicalization headerCanonicalization) {
    this.headerCanonicalization = headerCanonicalization;
    return this;
  }

  public MessageDKIMSignerConfig identity(final String identity) {
    this.identity = identity;
    return this;
  }

  public MessageDKIMSignerConfig lengthParam(final Boolean lengthParam) {
    this.lengthParam = lengthParam;
    return this;
  }

  public MessageDKIMSignerConfig privateKey(final RSAPrivateKey privateKey) {
    this.privateKey = privateKey;
    return this;
  }

  public MessageDKIMSignerConfig selector(final String selector) {
    this.selector = selector;
    return this;
  }

  public MessageDKIMSignerConfig signingAlgorithm(final SigningAlgorithm signingAlgorithm) {
    this.signingAlgorithm = signingAlgorithm;
    return this;
  }

  public MessageDKIMSignerConfig signingDomain(final String signingDomain) {
    this.signingDomain = signingDomain;
    return this;
  }

  public MessageDKIMSignerConfig zParam(final Boolean zParam) {
    this.zParam = zParam;
    return this;
  }

}
