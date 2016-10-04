package email.parser.stats

import java.io.PrintWriter
import java.net.URI

import org.apache.commons.compress.utils.Charsets
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD.numericRDDToDoubleRDDFunctions
import org.apache.spark.rdd.RDD.rddToOrderedRDDFunctions
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions

object EmailParserSparkMain extends App {
  val fileInputPath = args(0)
  val hdfsPath = args(1)
  val conf = new SparkConf().setAppName("SparkJoins").setMaster("local")
  val sparkContext = new SparkContext(conf)
  sparkContext.hadoopConfiguration.set("mapreduce.input.fileinputformat.input.dir.recursive", "true")
  val fileDetails = sparkContext.binaryFiles(fileInputPath + "/*/*")
  val extracted = new TarZipExtractor().extractAndDecode(fileDetails, Charsets.UTF_8);
  val mimeEmailParser = new MimeEmailParserImpl
  val mailAverageWordsMap = extracted.map(mailContent => (mimeEmailParser.getMailWordCount(mailContent._2)))
  val toAndCcMails = extracted.flatMap(mailContent => (mimeEmailParser.getToAndCcRecipients(mailContent._2)))

  val toAndCcCount = toAndCcMails.reduceByKey((a, b) => a + b).map(tuple => tuple.swap).sortByKey(false, 2).take(100)
  val mailAverageWords = mailAverageWordsMap.mean()

  val hadoopConf = new Configuration()
  System.out.println("Connecting to -- " + hadoopConf.get("fs.defaultFS"))
  val fs = FileSystem.get(URI.create(destinationHdfsPath), hadoopConf)
  val destinationHdfsPath = hdfsPath + "/mailOutput.txt"
  val outputFile = fs.create(new Path(destinationHdfsPath))
  val writer = new PrintWriter(outputFile)
  try {
    writer.write("average words count in emails are : " + mailAverageWords + "\n")
    writer.write("first 100 most email recipients are :")
    toAndCcCount.foreach(recipients => writer.write(recipients._2 + " ->" + recipients._1))
  } finally {
    writer.close()
    sparkContext.stop()
  }
}