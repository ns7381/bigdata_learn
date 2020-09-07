package com.nathan.bigdata.jes;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping(value = "/apis/servings/v1", method = {RequestMethod.POST, RequestMethod.GET})
public class LogController {
    @Autowired
    private LogRepository logRepository;

    @GetMapping("/logs")
    public Page<Log> search(@RequestParam List<String> podNames,
                            @RequestParam(required = false) String start,
                            @RequestParam(required = false) String end,
                            @RequestParam(required = false) String query,
                            @PageableDefault(page = 0, size = 30, sort = "@timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(QueryBuilders.matchAllQuery());
        qb.mustNot(QueryBuilders.matchPhraseQuery("kubernetes.container_name", "jsf-server"));
        if (null != query && "".equals(query)) {
            qb.must(QueryBuilders.matchPhraseQuery("log", query));
        }

        BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
        podNames.forEach(podName -> {
            orQuery.should(QueryBuilders.matchPhraseQuery("kubernetes.pod_name", podName));
        });
        orQuery.minimumShouldMatch(1);
        qb.must(orQuery);

        if (start != null && end != null) {
            qb.must(QueryBuilders.rangeQuery("@timestamp").from(start).to(end));
        }
        return logRepository.search(qb, pageable);
    }
}
