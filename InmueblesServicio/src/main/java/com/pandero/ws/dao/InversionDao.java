package com.pandero.ws.dao;

import java.util.List;
import java.util.Map;

public interface InversionDao {
	public List<Map<String,Object>> getListaEventoInversionPorNumeroInversion(String nroInversion) throws Exception;
}
