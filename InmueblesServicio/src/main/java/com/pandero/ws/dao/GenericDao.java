package com.pandero.ws.dao;

import java.util.List;
import java.util.Map;

public interface GenericDao {
	public Map<String,Object> executeProcedure(Map<String,Object> parameters, String procedureName);
	public Map<String,Object> queryForMap(String sql);
	public List<Map<String,Object>> queryForList(String sql);
	
}
