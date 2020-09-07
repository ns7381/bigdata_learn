package com.nathan.bigdata.jes;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "logstash-*", type = "flb_type")
public class Log {
    @Id
    private String id;
    private String log;
}
