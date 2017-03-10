package com.pandero.ws.bean;

public class Contrato implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer ContratoId;
	private String NroContrato;
	private Integer PedidoId;
	private String NroPedido;
	private Double MontoCertificado;
	private Double MontoDisponible;
	private String FechaAdjudicacion;
	private Integer AsociadoId;
	private String SituacionContrato;
	private Double DiferenciaPrecio;
	private Double DiferenciaPrecioDisponible;
	private Double TotalDisponible;
	private Double MontoLiquidacionContrato;
	private Double MontoLiquidacionDifPrecio;
	//private Integer PedidoContrato_ContratoId;
	
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
	public Double getMontoDisponible() {
		return MontoDisponible;
	}
	public void setMontoDisponible(Double montoDisponible) {
		MontoDisponible = montoDisponible;
	}
	public Double getDiferenciaPrecio() {
		return DiferenciaPrecio;
	}
	public void setDiferenciaPrecio(Double diferenciaPrecio) {
		DiferenciaPrecio = diferenciaPrecio;
	}
	public Double getDiferenciaPrecioDisponible() {
		return DiferenciaPrecioDisponible;
	}
	public void setDiferenciaPrecioDisponible(Double diferenciaPrecioDisponible) {
		DiferenciaPrecioDisponible = diferenciaPrecioDisponible;
	}
	public Double getTotalDisponible() {
		return TotalDisponible;
	}
	public void setTotalDisponible(Double totalDisponible) {
		TotalDisponible = totalDisponible;
	}
	public Double getMontoLiquidacionContrato() {
		return MontoLiquidacionContrato;
	}
	public void setMontoLiquidacionContrato(Double montoLiquidacionContrato) {
		MontoLiquidacionContrato = montoLiquidacionContrato;
	}
	public Double getMontoLiquidacionDifPrecio() {
		return MontoLiquidacionDifPrecio;
	}
	public void setMontoLiquidacionDifPrecio(Double montoLiquidacionDifPrecio) {
		MontoLiquidacionDifPrecio = montoLiquidacionDifPrecio;
	}
	public String getNroPedido() {
		return NroPedido;
	}
	public void setNroPedido(String nroPedido) {
		NroPedido = nroPedido;
	}
//	public Integer getPedidoContrato_ContratoId() {
//		return PedidoContrato_ContratoId;
//	}
//	public void setPedidoContrato_ContratoId(Integer pedidoContrato_ContratoId) {
//		PedidoContrato_ContratoId = pedidoContrato_ContratoId;
//	}	
	
}
