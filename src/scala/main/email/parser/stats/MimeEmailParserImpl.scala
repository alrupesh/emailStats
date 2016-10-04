package email.parser.stats

import java.util.Properties
import java.util._
import java.io._
import javax.mail._
import javax.mail.internet._
import javax.mail.Message.RecipientType
import java.nio.charset.StandardCharsets

class MimeEmailParserImpl() extends MimeEmailParser {

  val props = System.getProperties()
  props.put("mail.host", "smtp.dummydomain.com")
  props.put("mail.transport.protocol", "smtp")
  val mailSession = Session.getDefaultInstance(props, null)
  System.setProperty("mail.mime.address.strict", "false")

  def getMailWordCount(content: String): Int = {
    val source = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))
    val message = new MimeMessage(mailSession, source)
    val msgContent = message.getContent
    val contentStr = message.getContent().toString()
    val wordsCount = contentStr.split(" ").size
    wordsCount
  }

  def getToAndCcRecipients(content: String): Array[(String, Double)] = {
    val source = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))
    val message = new MimeMessage(mailSession, source)
    val toRecipients = message.getRecipients(RecipientType.TO).map { address => (address.toString(), 1.0) }
    val ccRecipients = message.getRecipients(RecipientType.CC).map { address => (address.toString(), 0.5) }
    toRecipients ++ ccRecipients
  }
}