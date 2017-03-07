package com.pandero.ws.bean;

public class PedidoContrato {

	private Integer PedidoId;
	private Integer ContratoId;
	private String NroPedido;
	private String NroContrato;
	private String PedidoContratoEstado;
	
	public Integer getPedidoId() {
		return PedidoId;
	}
	public void setPedidoId(Integer pedidoId) {
		PedidoId = pedidoId;
	}
	public Integer getContratoId() {
		return ContratoId;
	}
	public void setContratoId(Integer contratoId) {
		ContratoId = contratoId;
	}
	public String getNroPedido() {
		return NroPedido;
	}
	public void setNroPedido(String nroPedido) {
		NroPedido = nroPedido;
	}
	public String getNroContrato() {
		return NroContrato;
	}
	public void setNroContrato(String nroContrato) {
		NroContrato = nroContrato;
	}
	public String getPedidoContratoEstado() {
		return PedidoContratoEstado;
	}
	public void setPedidoContratoEstado(String pedidoContratoEstado) {
		PedidoContratoEstado = pedidoContratoEstado;
	}
		
}
