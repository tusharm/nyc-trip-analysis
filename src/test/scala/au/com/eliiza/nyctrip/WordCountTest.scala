package au.com.eliiza.nyctrip

/**
 * A simple test for everyone's favourite wordcount example.
 */

import org.scalatest.FunSuite
import org.apache.spark.sql._

class WordCountTest extends FunSuite {
  test("word count with Stop Words Removed"){
    val session = SparkSession.builder.master("local[*]").getOrCreate()

    val linesRDD = session.sparkContext.parallelize(Seq(
      "How happy was the panda? You ask.",
      "Panda is the most happy panda in all the#!?ing land!"))

    val stopWords: Set[String] = Set("a", "the", "in", "was", "there", "she", "he")
    val splitTokens: Array[Char] = "#%?!. ".toCharArray

    val wordCounts = WordCount.withStopWordsFiltered(
      linesRDD, splitTokens, stopWords)
    val wordCountsAsMap = wordCounts.collectAsMap()
    assert(!wordCountsAsMap.contains("the"))
    assert(!wordCountsAsMap.contains("?"))
    assert(!wordCountsAsMap.contains("#!?ing"))
    assert(wordCountsAsMap.contains("ing"))
    assert(wordCountsAsMap.get("panda").get.equals(3))

    session.close
  }
}
