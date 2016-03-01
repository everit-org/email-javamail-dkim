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

import java.util.Objects;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.everit.email.javamail.sender.JavaMailMessageEnhancer;

import net.markenwerk.utils.mail.dkim.DkimMessage;
import net.markenwerk.utils.mail.dkim.DkimSigner;

/**
 * Implementation of {@link JavaMailMessageEnhancer} that enhance {@link MimeMessage} with DKIM
 * signature.
 */
public class DKIMJavaMailMessageEnancer implements JavaMailMessageEnhancer {

  private final DkimSigner dkimSigner;

  /**
   * Constructor.
   *
   * @param config
   *          the {@link DKIMJavaMailMessageEnhancerConfig}.
   * @throws NullPointerException
   *           if one of required configuration field is <code>null</code>. Required fields:
   *           {@link DKIMJavaMailMessageEnhancerConfig#signingDomain},
   *           {@link DKIMJavaMailMessageEnhancerConfig#selector}, {@link DKIMJavaMailMessageEnhancerConfig#privateKey},
   *           {@link DKIMJavaMailMessageEnhancerConfig#additionalHeadersToSign},
   *           {@link DKIMJavaMailMessageEnhancerConfig#excludedHeadersFromSign},
   *           {@link DKIMJavaMailMessageEnhancerConfig#signingAlgorithm}.
   */
  public DKIMJavaMailMessageEnancer(final DKIMJavaMailMessageEnhancerConfig config) {
    Objects.requireNonNull(config, "config cannot be null");
    Objects.requireNonNull(config.signingDomain, "signingDomain cannot be null");
    Objects.requireNonNull(config.selector, "selector cannot be null");
    Objects.requireNonNull(config.privateKey, "privateKey cannot be null");
    Objects.requireNonNull(config.additionalHeadersToSign, "Additional headers set cannot be null");
    Objects.requireNonNull(config.excludedHeadersFromSign, "Excluded headers set cannot be null");

    dkimSigner = new DkimSigner(config.signingDomain, config.selector, config.privateKey);

    if (config.identity != null) {
      dkimSigner.setIdentity(config.identity);
    }

    if (config.headerCanonicalization != null) {
      dkimSigner.setHeaderCanonicalization(config.headerCanonicalization);
    }

    if (config.bodyCanonicalization != null) {
      dkimSigner.setBodyCanonicalization(config.bodyCanonicalization);
    }

    if (config.signingAlgorithm != null) {
      dkimSigner.setSigningAlgorithm(config.signingAlgorithm);
    }

    dkimSigner.setLengthParam(config.lengthParam);
    dkimSigner.setZParam(config.zParam);

    for (String header : config.additionalHeadersToSign) {
      dkimSigner.addHeaderToSign(header);
    }

    for (String header : config.excludedHeadersFromSign) {
      dkimSigner.removeHeaderToSign(header);
    }
  }

  @Override
  public MimeMessage enhance(final MimeMessage message) {
    try {
      return new DkimMessage(message, dkimSigner);
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

}
