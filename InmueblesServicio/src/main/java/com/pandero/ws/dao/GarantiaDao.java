package com.pandero.ws.dao;

import java.util.List;

import com.pandero.ws.bean.Garantia;

public interface GarantiaDao {

	public String crearGarantiaSAF(Garantia garantia, String usuarioId) throws Exception;
	public String editarGarantiaSAF(Garantia garantia, String usuarioId) throws Exception;
	public String eliminarGarantiaSAF(String garantiaSAFId, String usuarioId) throws Exception;
	
	public List<Garantia> obtenerGarantiasPorInversion(String nroInversion) throws Exception;
	
	public Integer crearCreditoGarantiaEvaluacionCrediticia(String nroInversion,String usuarioId) throws Exception;
	public Integer eliminarCreditoGarantiaEvaluacionCrediticia(String nroInversion,String usuarioId) throws Exception;
}
