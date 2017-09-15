package com.pandero.ws.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

public abstract class GenericService {
	@Value("${url.service.table.pedido}")
	public String tablepedido;
	@Value("${url.service.table.pedidoContrato}")
	public String tablepedidoContrato;
	@Value("${url.service.table.pedidoInversion}")
	public String tablepedidoInversion;
	@Value("${url.service.table.contrato}")
	public String tablecontrato;
	@Value("${url.service.table.documentoRequisito}")
	public String tabledocumentoRequisito;
	@Value("${url.service.table.persona}")
	public String tablepersona;
	@Value("${url.service.table.inversionRequisito}")
	public String tableinversionRequisito;
	@Value("${url.service.table.constantes}")
	public String tableconstantes;
	@Value("${url.service.table.comprobante}")
	public String tablecomprobante;
	@Value("${url.service.table.garantia}")
	public String tablegarantia;
	@Value("${url.service.table.liquidacionDesembolso}")
	public String tableliquidacionDesembolso;
	@Value("${url.service.table.seguro}")
	public String tableseguro;
	@Value("${url.service.view.tablaPedidoInversion}")
	public String viewPedidoInversion;

	public abstract void actualizarTablaCaspio(Map<String, Object> body,
			String tableURL, String where) throws Exception;

	public abstract List<Map<String, Object>> obtenerTablaCaspio(
			String tableURL, String where) throws Exception;
}
