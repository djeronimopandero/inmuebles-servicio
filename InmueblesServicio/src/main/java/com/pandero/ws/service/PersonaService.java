package com.pandero.ws.service;

import com.pandero.ws.bean.PersonaCaspio;
import com.pandero.ws.bean.PersonaSAF;

public interface PersonaService {

	public void setTokenCaspio(String token);
	
	public PersonaCaspio obtenerPersonaCaspio(String tipoDocumento, String nroDocumento) throws Exception;
	public String crearPersonaCaspio(PersonaSAF persona)throws Exception;

}
