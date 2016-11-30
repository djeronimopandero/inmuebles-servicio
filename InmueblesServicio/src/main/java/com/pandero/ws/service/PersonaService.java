package com.pandero.ws.service;

import com.pandero.ws.bean.PersonaCaspio;
import com.pandero.ws.bean.PersonaSAF;

public interface PersonaService {

	public PersonaCaspio obtenerPersonaCaspio(PersonaSAF persona) throws Exception;
	public String crearPersonaCaspio(PersonaSAF persona)throws Exception;

}
