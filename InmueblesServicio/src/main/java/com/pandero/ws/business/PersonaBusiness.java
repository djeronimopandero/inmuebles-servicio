package com.pandero.ws.business;

import java.util.List;
import java.util.Map;

import com.pandero.ws.bean.PersonaSAF;

public interface PersonaBusiness {

	public PersonaSAF obtenerPersonaSAF(String pedidoId) throws Exception;
	public List<Map<String,Object>> obtenerDatosCorrespondencia(String garantiaId) throws Exception;
	
}
