package com.ns.bigdata.clickhouse.statistics.airlines.rest;

import java.util.List;

import javax.validation.constraints.Size;

import com.ns.bigdata.clickhouse.statistics.airlines.model.AirlineFlightsNumber;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ns.bigdata.clickhouse.statistics.airlines.service.StatisticsAirlinesService;

@RestController
@RequestMapping("/statistics/airlines")
public class StatisticsAirlinesController {

	private StatisticsAirlinesService statisticsService;

	public StatisticsAirlinesController(StatisticsAirlinesService statisticsService) {
		this.statisticsService = statisticsService;

	}

	@GetMapping("/flights")
	public List<AirlineFlightsNumber> getFlightsByYear(@Size(min=4, max= 4) @RequestParam("year") int year) {
		return statisticsService.getFlightsByYear(year);
	}

}
