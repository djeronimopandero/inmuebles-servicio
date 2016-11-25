package com.pandero.ws.bean;

import java.util.Date;

public class Contrato{
	private Integer PedidoId;
	private String NroContrato;
	private Double MontoCertificado;
	private String FechaAdjudicacion;
	public String getNroContrato() {
		return NroContrato;
	}
	public void setNroContrato(String nroContrato) {
		NroContrato = nroContrato;
	}
	public Double getMontoCertificado() {
		return MontoCertificado;
	}
	public void setMontoCertificado(Double montoCertificado) {
		MontoCertificado = montoCertificado;
	}
	public String getFechaAdjudicacion() {
		return FechaAdjudicacion;
	}
	public void setFechaAdjudicacion(String fechaAdjudicacion) {
		FechaAdjudicacion = fechaAdjudicacion;
	}
	public Integer getPedidoId() {
		return PedidoId;
	}
	public void setPedidoId(Integer pedidoId) {
		PedidoId = pedidoId;
	}	
	
}
