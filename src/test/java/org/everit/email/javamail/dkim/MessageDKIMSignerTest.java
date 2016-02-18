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
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.everit.email.Attachment;
import org.everit.email.Email;
import org.everit.email.EmailAddress;
import org.everit.email.HtmlContent;
import org.everit.email.InputStreamSupplier;
import org.everit.email.Recipients;
import org.everit.email.javamail.sender.JavaMailEmailSender;
import org.everit.email.javamail.sender.JavaMailMessageEnhancer;
import org.everit.email.sender.EmailSender;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.markenwerk.utils.data.fetcher.BufferedFetcher;
import net.markenwerk.utils.mail.dkim.Canonicalization;
import net.markenwerk.utils.mail.dkim.SigningAlgorithm;

public class MessageDKIMSignerTest {

  private static final class EmailConfiguration {

    public static final String COFFE_JPG_NAME = "ineedcoffee.jpg";

    public static final String JPG_CONTENT_TYPE = "image/jpg";

    public static final String SENDER_EMAIL_ADDRESS = "sender@sender.org";

    public static final String SENDER_NAME = "Foo Sender";

    public static final String TO_EMAIL_ADDRESS = "sample@sample.org";

    private EmailConfiguration() {
    }
  }

  private static final class SessionConfiguration {

    public static final String SENDER_PASSWORD = "123456";

    public static final String SENDER_USERNAME = "foo@absdem.org";

    public static final String SMTP_AUTH = "true";

    public static final String SMTP_HOST = "localhost";

    public static final String SMTP_STARTTLS_ENABLE = "true";

    private SessionConfiguration() {
    }
  }

  private GreenmailService greenmailService;

  @After
  public void after() {
    if (greenmailService != null) {
      greenmailService.stopGreenmail();
    }
  }

  @Before
  public void before() {
    greenmailService = new GreenmailServiceImpl();
  }

  private EmailAddress createFrom(final String address, final String personal) {
    return new EmailAddress()
        .withAddress(address)
        .withPersonal(personal);
  }

  private void createMailContent(final Email email) {
    Map<String, Attachment> inlineImages = new HashMap<>();
    final String picture = "/coffee.jpg";
    InputStreamSupplier coffePictureSupplier = new InputStreamSupplier() {
      @Override
      public InputStream getStream() {
        return MessageDKIMSignerTest.class.getResourceAsStream(picture);
      }
    };
    inlineImages.put("ineedcoffee", new Attachment()
        .withContentType(EmailConfiguration.JPG_CONTENT_TYPE)
        .withName(EmailConfiguration.COFFE_JPG_NAME)
        .withInputStreamSupplier(coffePictureSupplier));
    email.withHtmlContent(new HtmlContent()
        .withHtml("<h1>I really need coffee</h1>"
            + "<img src='cid:ineedcoffee' />")
        .withInlineImageByCidMap(inlineImages));
    email.withAttachments(Arrays.asList(
        new Attachment()
            .withContentType(EmailConfiguration.JPG_CONTENT_TYPE)
            .withName(EmailConfiguration.COFFE_JPG_NAME)
            .withInputStreamSupplier(coffePictureSupplier)));
  }

  private Recipients createRecipients(final String toAddress) {
    return new Recipients()
        .withTo(Arrays.asList(
            new EmailAddress[] {
                new EmailAddress()
                    .withAddress(toAddress)
            }));
  }

  private Session createSession() {
    Properties props = new Properties();
    props.put("mail.smtp.auth", SessionConfiguration.SMTP_AUTH);
    props.put("mail.smtp.starttls.enable", SessionConfiguration.SMTP_STARTTLS_ENABLE);
    props.put("mail.smtp.host", SessionConfiguration.SMTP_HOST);
    props.put("mail.smtp.port", greenmailService.getPort());
    Session session = Session.getInstance(props,
        new javax.mail.Authenticator() {
          @Override
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(SessionConfiguration.SENDER_USERNAME,
                SessionConfiguration.SENDER_PASSWORD);
          }
        });
    return session;
  }

  private Email getEmail() {
    Email email = new Email()
        .withSubject("Test DKIM mail with HTML and attachment")
        .withFrom(createFrom(EmailConfiguration.SENDER_EMAIL_ADDRESS,
            EmailConfiguration.SENDER_NAME))
        .withRecipients(createRecipients(EmailConfiguration.TO_EMAIL_ADDRESS));
    createMailContent(email);
    return email;
  }

  private MimeMessage getLastMimeMessage() {
    MimeMessage[] receivedMessages = getReceivedMessages();
    return (receivedMessages == null) || (receivedMessages.length == 0)
        ? null : receivedMessages[receivedMessages.length - 1];
  }

  private MessageDKIMSignerConfig getMessageDKIMSignerConfig() {
    return new MessageDKIMSignerConfig()
        .bodyCanonicalization(Canonicalization.RELAXED)
        .headerCanonicalization(Canonicalization.SIMPLE)
        .identity("foo@absdem.org")
        .lengthParam(true)
        .zParam(true)
        .signingAlgorithm(SigningAlgorithm.SHA256_WITH_RSA)
        .signingDomain("absdem.org")
        .selector("test")
        .privateKey(loadRSAPrivateKey());
  }

  private MimeMessage[] getReceivedMessages() {
    return greenmailService.getGreenMail().getReceivedMessages();
  }

  private RSAPrivateKey loadRSAPrivateKey() {
    try (InputStream is = MessageDKIMSignerTest.class.getResourceAsStream("/dkim.der")) {
      byte[] privKeyBytes = new BufferedFetcher().fetch(is);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privKeyBytes);
      return (RSAPrivateKey) keyFactory.generatePrivate(privSpec);
    } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testMessageDKIMSigner() throws MessagingException {
    List<JavaMailMessageEnhancer> enhancers = new ArrayList<>();
    JavaMailMessageEnhancer javaMailMessageEnhancer =
        new MessageDKIMSigner(getMessageDKIMSignerConfig());
    enhancers.add(javaMailMessageEnhancer);
    EmailSender emailSender = new JavaMailEmailSender(createSession(), enhancers);

    emailSender.sendEmail(getEmail());

    MimeMessage[] receivedMessages = greenmailService.getGreenMail().getReceivedMessages();
    Assert.assertEquals(1, receivedMessages.length);

    MimeMessage msg = getLastMimeMessage();
    String[] header = msg.getHeader("DKIM-Signature");
    Assert.assertNotNull(header[0]);
  }

}
