package com.ns.bigdata.clickhouse.statistics.airlines.service;

import java.util.List;

import com.ns.bigdata.clickhouse.statistics.airlines.model.AirlineFlightsNumber;
import org.springframework.stereotype.Service;

import com.ns.bigdata.clickhouse.statistics.airlines.repository.StatisticsAirlinesRepository;

@Service
public class StatisticsAirlinesService {

	private StatisticsAirlinesRepository statisticsRepository;

	public StatisticsAirlinesService (StatisticsAirlinesRepository statisticsRepository) {
		this.statisticsRepository = statisticsRepository;

	}

	public List<AirlineFlightsNumber> getFlightsByYear(int year) {
		return statisticsRepository.getFlightsByYear(year);
	}

}
