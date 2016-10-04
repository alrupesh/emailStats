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
class TarZipExtractorSpec extends FlatSpec with BeforeAndAfterAll with ShouldMatchers with MockFactory {

  it should "successfully extract the content from zip file" in {
    val tarZipExtractor = new TarZipExtractor
    val zipFileContent = tarZipExtractor.processZipFile(getClass.getResourceAsStream("/Archive.zip"))
    val output = zipFileContent.map { bytes => new String(bytes, StandardCharsets.UTF_8) }
    output should not equal (null)
  }
}