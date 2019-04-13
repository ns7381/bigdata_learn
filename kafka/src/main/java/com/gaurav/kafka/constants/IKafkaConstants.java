package com.gaurav.kafka.constants;

public interface IKafkaConstants {
	String KAFKA_BROKERS = "hdinsight-20190325094135-20-master-4:6667";
	
	Integer MESSAGE_COUNT=1000;
	
	String CLIENT_ID="client1";
	
	String TOPIC_NAME="demo";
	
	String GROUP_ID_CONFIG="consumerGroup10";
	
	Integer MAX_NO_MESSAGE_FOUND_COUNT=100;
	
	String OFFSET_RESET_LATEST="latest";
	
	String OFFSET_RESET_EARLIER="earliest";
	
	Integer MAX_POLL_RECORDS=1;
}
