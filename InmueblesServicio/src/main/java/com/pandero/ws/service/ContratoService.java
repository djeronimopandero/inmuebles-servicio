package com.pandero.ws.service;

import java.util.Date;

import com.pandero.ws.bean.Contrato;

public interface ContratoService {

	public Contrato obtenerContratoCaspio(String nroContrato) throws Exception;
	public Contrato actualizarEstadoContratoCaspio(String nroContrato, String situacionId, String situacionNom, Date fechaSituacion) throws Exception;
}
