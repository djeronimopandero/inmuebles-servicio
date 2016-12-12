package com.pandero.ws.dao;

import com.pandero.ws.bean.PersonaSAF;

public interface PersonaDao {

	public PersonaSAF obtenerPersonaSAF(String personaID) throws Exception;
	
	public PersonaSAF obtenerProveedorSAF(String tipoDocumento, String nroDocumento, Integer personaID) throws Exception; // nuevo sp
	public PersonaSAF registrarProveedorSAF(PersonaSAF personaSAF) throws Exception; // nuevo sp
	
}
