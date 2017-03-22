package com.pandero.ws.service;

import java.util.Map;

import com.pandero.ws.bean.Desembolso;

public interface LiquidDesembService {

	public String registrarLiquidacionInversion(String inversionId, String nroArmada, String nroLiquidacion) throws Exception;	
	public String registrarDesembolsoInversion(String inversionId, String nroArmada, String nroDesembolso) throws Exception;
	
	public Desembolso obtenerLiquidacionDesembolsoPorInversionArmada(String inversionId, String nroArmada) throws Exception;
	
	public String eliminarLiquidacionInversion(String inversionId, String nroArmada) throws Exception;
	
	public String actualizarEstadoLiquDesembInversion(String inversionId, String nroArmada, String estado) throws Exception;
	

	public String setGenerarConstanciaInversioPedido(Map<String,Object> params) throws Exception;
	
	public void setTokenCaspio(String token);
	
	public Map<String,Object> getLiquidacionDesembolso(Map<String,Object> params)throws Exception;
}
