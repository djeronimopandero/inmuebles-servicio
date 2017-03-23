package com.pandero.ws.dao;

import java.util.Map;

public interface GenericDao {
	public Map<String,Object> executeProcedure(Map<String,Object> parameters, String procedureName);
}
