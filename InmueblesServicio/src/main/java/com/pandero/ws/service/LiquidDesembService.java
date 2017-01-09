package com.pandero.ws.service;

import com.pandero.ws.bean.Desembolso;

public interface LiquidDesembService {

	public String registrarLiquidacionInversion(String inversionId, String nroArmada, String nroLiquidacion) throws Exception;	
	public String registrarDesembolsoInversion(String inversionId, String nroArmada, String nroDesembolso) throws Exception;
	
	public Desembolso obtenerLiquidacionDesembolsoPorInversionArmada(String inversionId, String nroArmada) throws Exception;
	
	public String eliminarLiquidacionInversion(String inversionId, String nroArmada) throws Exception;
	
	public void setTokenCaspio(String token);
}
