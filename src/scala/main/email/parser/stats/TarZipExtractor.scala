package email.parser.stats

import org.apache.spark.input.PortableDataStream
import java.nio.charset.StandardCharsets
import java.nio.charset.Charset
import org.apache.spark.rdd.RDD
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import scala.util.Try
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import java.io.InputStream

class TarZipExtractor extends Serializable {
  def extractFiles(ps: PortableDataStream, n: Int = 1024) = Try {
    processZipFile(ps.open)
  }

  def processZipFile(inputStream: InputStream) = {
    val zip = new ZipArchiveInputStream(inputStream)
    Stream.continually(Option(zip.getNextZipEntry))
      // Read until next entry is null
      .takeWhile(_.isDefined)
      // flatten
      .flatMap(x => x)
      // process only eml files
      .filter(entry => entry.getName.takeRight(3).toLowerCase() == "eml")
      .map(e => {
        Stream.continually {
          // Read n bytes
          val buffer = Array.fill[Byte](1024)(-1)
          val i = zip.read(buffer, 0, 1024)
          (i, buffer.take(i))
        }
          // Take as long as we've read something
          .takeWhile(_._1 > 0)
          .map(_._2)
          .flatten
          .toArray
      })
      .toArray
  }

  def decode(charset: Charset = StandardCharsets.UTF_8)(bytes: Array[Byte]) =
    new String(bytes, StandardCharsets.UTF_8)

  def extractAndDecode(rdd: RDD[(String, PortableDataStream)], charser: Charset) = {
    rdd.flatMapValues(x => extractFiles(x).toOption)
      .flatMapValues(_.map(decode()))
  }
}