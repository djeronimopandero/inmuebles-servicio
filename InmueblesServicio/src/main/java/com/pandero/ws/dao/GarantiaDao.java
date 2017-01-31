package com.pandero.ws.dao;

import java.util.Map;

import com.pandero.ws.bean.Garantia;

public interface GarantiaDao {

	public String crearGarantiaSAF(Garantia garantia, String usuarioId) throws Exception;
	public String editarGarantiaSAF(Garantia garantia, String usuarioId) throws Exception;
	public String eliminarGarantiaSAF(String garantiaSAFId, String usuarioId) throws Exception;
	
}
