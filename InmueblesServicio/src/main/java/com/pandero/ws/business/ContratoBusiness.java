package com.pandero.ws.business;

import com.pandero.ws.bean.ResultadoBean;

public interface ContratoBusiness {

	public ResultadoBean sincronizarContratosyAsociadosSafACaspio() throws Exception;
	
	public ResultadoBean actualizarDiferenciaPrecioContratos(Integer pedidoId)throws Exception;
	
	public ResultadoBean getDetalleDiferenciaPrecio(Integer pedidoId)throws Exception;
}
