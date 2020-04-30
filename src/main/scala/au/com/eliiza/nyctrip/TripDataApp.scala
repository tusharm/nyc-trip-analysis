package au.com.eliiza.nyctrip

import org.apache.spark.sql.SaveMode.Overwrite
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object TripDataApp {

  def main(args: Array[String]): Unit = {

    // create Spark session
    val spark = SparkSession.builder
      .master("local")
      .appName("NYC Trip Data")
      .getOrCreate


    // extract trip data from CSV
    val rawTripDF = spark.read.option("header", true).csv(args(0))
    rawTripDF.persist
    rawTripDF.printSchema
    rawTripDF.show(10, 40, false)


    // rename columns, assign types etc.
    val tripDF = rawTripDF.select(
      col("VendorID").cast(IntegerType).as("vendor_id"),
      from_utc_timestamp(col("lpep_pickup_datetime"), "America/New_York").as("lpep_pickup_datetime"),
      from_utc_timestamp(col("Lpep_dropoff_datetime"), "America/New_York").as("lpep_dropoff_datetime"),
      when(col("Store_and_fwd_flag") === "N", true).otherwise(false).as("store_and_fwd_flag"),
      col("RateCodeID").as("rate_code_id"),
      col("Pickup_longitude").as("pickup_longitude"),
      col("Pickup_latitude").as("pickup_latitude"),
      col("Dropoff_longitude").as("dropoff_longitude"),
      col("Dropoff_latitude").as("dropoff_latitude"),
      col("Passenger_count").as("passenger_count"),
      col("Trip_distance").as("trip_distance"),
      col("Fare_amount").as("fare_amount"),
      col("Extra").as("extra"),
      col("MTA_tax").as("mta_tax"),
      col("Tip_amount").as("tip_amount"),
      col("Tolls_amount").as("tolls_amount"),
      col("Ehail_fee").as("ehail_fee"),
      col("Total_amount").as("total_amount"),
      col("Payment_type").as("payment_type"),
      col("Trip_type ").as("trip_type"),
    )
    tripDF.printSchema
    tripDF.show(10, 10, false)


    // partition and save as parquet
    tripDF.write
      .mode(Overwrite)
      .partitionBy("vendor_id")
      .parquet("./output")
  }
}
