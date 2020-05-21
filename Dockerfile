FROM seddonm1/arc:arc_2.12.2_spark_2.4.5_scala_2.12_hadoop_2.9.2_1.0.0

COPY target/scala-2.12/nyc-trip-analysis-assembly-*.jar /opt/spark/jars/trip.jar
