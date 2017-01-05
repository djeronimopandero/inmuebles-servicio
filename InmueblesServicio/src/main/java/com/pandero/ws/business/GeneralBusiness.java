package com.pandero.ws.business;

public interface GeneralBusiness {

	public Double convertirSoles(Double montoDolares,String fechaEmision) throws Exception;
	public Double convertirDolares(Double montoSoles,String fechaEmision) throws Exception;
	
}
