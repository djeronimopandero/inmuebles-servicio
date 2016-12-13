package com.pandero.ws.dao;

import com.pandero.ws.bean.PersonaSAF;

public interface PersonaDao {

	public PersonaSAF obtenerPersonaSAF(String personaID) throws Exception;
	
	public PersonaSAF obtenerProveedorSAF(Integer proveedorId, String tipoProveedor, Integer personaID, 
			String tipoDocumento, String nroDocumento) throws Exception; // nuevo sp
	public PersonaSAF registrarProveedorSAF(PersonaSAF personaSAF) throws Exception; // nuevo sp
	
}
