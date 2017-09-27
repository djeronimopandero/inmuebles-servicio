package com.pandero.ws.service;

import java.util.List;

import com.pandero.ws.bean.Constante;
import com.pandero.ws.bean.DocumentoRequisito;

public interface ConstanteService {
	
	public void setTokenCaspio(String token);
	
	public List<Constante> obtenerListaDocumentosIdentidad() throws Exception;
	public List<Constante> obtenerListaArmadasDesembolso() throws Exception;
	
	public List<DocumentoRequisito> obtenerListaDocumentosPorTipoInversion(String tipoInversion) throws Exception;
	public List<DocumentoRequisito> obtenerListaRequisitosPorTipoInversion(String tipoInversion) throws Exception;
}
