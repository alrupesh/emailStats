package email.parser.stats

trait MimeEmailParser {
  def getMailWordCount(content: String): Int
  def getToAndCcRecipients(content: String) : Array[(String, Double)]
}