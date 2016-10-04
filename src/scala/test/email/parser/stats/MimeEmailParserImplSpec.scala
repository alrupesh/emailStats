package email.parser.stats

import java.nio.charset.StandardCharsets

import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.ShouldMatchers
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MimeEmailParserImplSpec extends FlatSpec with BeforeAndAfterAll with ShouldMatchers with MockFactory {

  val emailContent = """Date: Thu, 8 Mar 2001 00:08:00 -0800 (PST)
Message-ID: <00000000B56E2B9F5DC95E4CB45F7C869628C9D184432000@PMZL03>
MIME-Version: 1.0
Content-Type: text/plain; charset=us-ascii
Content-Transfer-Encoding: 7bit
From:  Danielle Stephens
To:  Jason Wolfe
Cc: "Mike  Advert" <Mike.Advert@ENRON.com>
X-Filename:  jwolfe.nsf
X-Folder:  \Saved-03
X-SDOC:  721207
X-ZLID:  zl-edrm-enron-v2-wolfe-j-703.eml

I tried calling you, but your voice mail wasn't working.  Please give me a 
call back when you have a chance.  3-5536 and ask for me.
Thanks,

Danielle Stephens
Information Risk Management
Enron Net Works
713-345-3238


***********
EDRM Enron Email Data Set has been produced in EML, PST and NSF format by ZL Technologies, Inc. This Data Set is licensed under a Creative Commons Attribution 3.0 United States License <http://creativecommons.org/licenses/by/3.0/us/> . To provide attribution, please cite to "ZL Technologies, Inc. (http://www.zlti.com)."
***********

"""
  behavior of "MimeEmailParserImpl"

  it should "sucessfully get the content word count" in {
    val mimeEmailParserImpl = new MimeEmailParserImpl
    val wordCount = mimeEmailParserImpl.getMailWordCount(emailContent)
    (wordCount > 50) should equal(true)
  }

  it should "sucessfully get the to and cc email address" in {
    val mimeEmailParserImpl = new MimeEmailParserImpl
    val toAndCcAddresses = mimeEmailParserImpl.getToAndCcRecipients(emailContent)
    val jsonAddress = toAndCcAddresses.filter(address => address._1.contains("Jason"))
    val mikeAddress = toAndCcAddresses.filter(address => address._1.contains("Mike"))
    jsonAddress(0)._2 should equal(1)
    mikeAddress(0)._2 should equal(0.5)
    jsonAddress(0)._1 should equal("Jason Wolfe")
    mikeAddress(0)._1 should equal("Mike  Advert <Mike.Advert@ENRON.com>")
  }

}