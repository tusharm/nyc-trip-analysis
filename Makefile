ARC_JUPYTER_VERSION := 2.3.0
ARC_VERSION := 2.10.2
HADOOP_VERSION := 2.9.2
IMAGE_VERSION := 1.0.0
PWD := $(shell pwd)
SECRET := $(shell openssl rand -hex 64)

.PHONY: run assembly submit

run:
	sbt 'run data/green_tripdata_2013-08.csv.gz'

assembly:
	sbt clean assembly

submit:
	spark-submit --master "local[*]" --class au.com.eliiza.nyctrip.TripDataApp  target/scala-2.12/nyc-trip-analysis-assembly-0.0.1.jar data/green_tripdata_2013-08.csv.gz

arc-dev:
	@docker run \
	--name arc-jupyter \
	--rm \
	--volume ${PWD}:/home/jovyan/examples \
	--entrypoint='' \
	-p 4040:4040 \
	-p 8888:8888 \
	triplai/arc-jupyter:arc-jupyter_${ARC_JUPYTER_VERSION}_scala_2.12_hadoop_${HADOOP_VERSION}_${IMAGE_VERSION} \
	jupyter notebook --ip=0.0.0.0 --no-browser --NotebookApp.password='' --NotebookApp.token='' | jq . 

arc-run:
	@docker run \
	--rm \
	--volume ${PWD}:/app \
	--env "ETL_CONF_ENV=production" \
	--env "ETL_BASE=/app" \
	--entrypoint='' \
	--publish 4040:4040 \
	triplai/arc:arc_${ARC_VERSION}_spark_2.4.5_scala_2.12_hadoop_2.9.2_1.0.0 \
	bin/spark-submit \
	--master local[*] \
	--driver-memory 4g \
	--driver-java-options "-XX:+UseG1GC -XX:-UseGCOverheadLimit -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap" \
	--conf spark.authenticate=true \
	--conf spark.authenticate.secret=${SECRET} \
	--conf spark.io.encryption.enabled=true \
	--conf spark.network.crypto.enabled=true \
	--class ai.tripl.arc.ARC \
	/opt/spark/jars/arc.jar \
	--etl.config.uri=file:///app/arc/nyctaxi.ipynb | jq . 

