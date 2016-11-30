package com.pandero.ws.dao;

import com.pandero.ws.bean.PersonaSAF;

public interface PersonaDAO {

	public PersonaSAF obtenerAsociadosxContratoSAF(String personaID) throws Exception;
	
}
