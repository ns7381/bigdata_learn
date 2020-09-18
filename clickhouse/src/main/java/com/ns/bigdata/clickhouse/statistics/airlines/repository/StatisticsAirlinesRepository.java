package com.ns.bigdata.clickhouse.statistics.airlines.repository;

import java.util.List;

import com.ns.bigdata.clickhouse.statistics.airlines.model.AirlineFlightsNumber;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StatisticsAirlinesRepository {

	private JdbcTemplate jdbcTemplate;

	public StatisticsAirlinesRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;

	}

	public List<AirlineFlightsNumber> getFlightsByYear(int year) {
		return jdbcTemplate.query(
				" select Carrier, sum(AirlineID) as flights from ontime where Year = ? group by Carrier order by flights desc",
				new Object[] { year }, (rs, rowNum) -> {
					return new AirlineFlightsNumber(rs.getString("Carrier"), rs.getLong("flights"));
				});
	}

}
