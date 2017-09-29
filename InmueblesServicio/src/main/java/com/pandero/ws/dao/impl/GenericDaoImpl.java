package com.pandero.ws.dao.impl;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.pandero.ws.dao.GenericDao;

@Repository
public class GenericDaoImpl implements GenericDao{
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("dataSource")
	public void setDataSource(DataSource dataSource)	{
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
    public Map<String,Object> executeProcedure(Map<String,Object> parameters, String procedureName){
    	SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
    	call.withProcedureName(procedureName);
    	MapSqlParameterSource in = new MapSqlParameterSource();
    	for(Map.Entry<String, Object> parameter:parameters.entrySet()){
    		in.addValue(parameter.getKey(), parameter.getValue());
    	}
    	Map<String,Object> procedureResult = call.execute(in);
		return procedureResult;
    }

	@Override
	public Map<String, Object> queryForMap(String sql) {
		return jdbcTemplate.queryForMap(sql);
	}

	@Override
	public List<Map<String, Object>> queryForList(String sql) {
		// TODO Auto-generated method stub
		return jdbcTemplate.queryForList(sql);
	}
	
	
}
