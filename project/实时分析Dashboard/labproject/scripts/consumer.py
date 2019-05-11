from kafka import KafkaConsumer

consumer = KafkaConsumer('result', bootstrap_servers='hdinsight-20190409175205-62-master-3.novalocal:6667')
for msg in consumer:
    print((msg.value).decode('utf8'))

