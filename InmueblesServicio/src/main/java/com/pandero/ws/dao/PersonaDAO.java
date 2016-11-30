package com.pandero.ws.dao;

import com.pandero.ws.bean.PersonaSAF;

public interface PersonaDAO {

	public PersonaSAF obtenerPersonaSAF(String personaID) throws Exception;
	
}
