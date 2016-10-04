This project built with scala 2.10 version and jvm 1.7 and spark 1.6 version. This code process eml format emails.

EmailParserSparkMain class is main class for this spark application.
this main class takes 2 arguments which need to be passed as arguments to spark-submit while executing this program.
after doing research and understanding and looking at the enrol email folder structure in AWS it is clear that emails are compressed to 
zip and stored in two folders which each contains zip files. based on that following folder structure is proposed for argument1. 
argument 1 : hdfs path from where email files to read. here path is mentioned as to read sub-directories as well.	
			so if you have sub-directory in mentioned path as basePath/sub-directory1/a.zip
															  basePath/sub-directory1/b.zip .. ....
															  basePath/sub-directory2/1.zip
															  basePath/sub-directory2/2.zip
			you can mention argument 1 as "basePath" here. zip file contains the actual email files whether eml, xml, pst files.
argument 2 : hdfs path where output data to be stored, you need to provide directory where output file need to be stored, 
			it will store the output with file name "mailOutput.txt" in this specified directory.
			
this project processes zip files in sub-directory and processing eml format emails and calculates average words and first 100 most recipients.

Tests been written for correct extract of eml file content and recipients.
test been written for correct extract of zip file content and test zip file used is from resources of this project.
libs folder contain all the dependencies required for this project.
 
based on more business requirement and better understanding of business requirements, data formats, how it is structured and having much control
on data storage in hadoop much better optimization can be made to the code and project.    