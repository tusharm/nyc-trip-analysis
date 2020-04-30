.PHONY: run assembly submit

run:
	sbt 'run data/green_tripdata_2013-08.csv.gz'

assembly:
	sbt clean assembly

submit:
	spark-submit --master "local[*]" --class au.com.eliiza.nyctrip.TripDataApp  target/scala-2.12/nyc-trip-analysis-assembly-0.0.1.jar data/green_tripdata_2013-08.csv.gz
