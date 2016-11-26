package com.pandero.ws.bean;

public class Contrato{
	private Integer ContratoId;
	private Integer PedidoId;
	private String NroContrato;
	private Double MontoCertificado;
	private String FechaAdjudicacion;
	private Integer AsociadoId;
	private String SituacionContrato;
	
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
	public Integer getAsociadoId() {
		return AsociadoId;
	}
	public void setAsociadoId(Integer asociadoId) {
		AsociadoId = asociadoId;
	}
	public String getSituacionContrato() {
		return SituacionContrato;
	}
	public void setSituacionContrato(String situacionContrato) {
		SituacionContrato = situacionContrato;
	}
	public Integer getContratoId() {
		return ContratoId;
	}
	public void setContratoId(Integer contratoId) {
		ContratoId = contratoId;
	}	
	
}
