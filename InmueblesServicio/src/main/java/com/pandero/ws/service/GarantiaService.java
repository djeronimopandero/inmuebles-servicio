package com.pandero.ws.service;

import java.util.List;

import com.pandero.ws.bean.Garantia;

public interface GarantiaService {

	public void setTokenCaspio(String token);
	
	public Garantia obtenerGarantiaPorId(String garantiaId) throws Exception;
	
	public List<Garantia> obtenerGarantiasPorPedido(String pedidoId) throws Exception;
}
