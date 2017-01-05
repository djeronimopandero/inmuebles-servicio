package com.pandero.ws.bean;

public class ConceptoLiquidacion {

	private String ConceptoID;
	private String ConceptoNombre;
	private Double Importe;
	private Double ImportePagado;
	private Double TotalPendiente;
	private String SituacionID;
	private String Situacion;
	private Integer ContratoID;
	private String ContratoNumero;
	private Integer PedidoID;
	private Integer PedidoInversionID;
	
	public String getConceptoID() {
		return ConceptoID;
	}
	public void setConceptoID(String conceptoID) {
		ConceptoID = conceptoID;
	}
	public String getConceptoNombre() {
		return ConceptoNombre;
	}
	public void setConceptoNombre(String conceptoNombre) {
		ConceptoNombre = conceptoNombre;
	}
	public Double getImporte() {
		return Importe;
	}
	public void setImporte(Double importe) {
		Importe = importe;
	}
	public Double getImportePagado() {
		return ImportePagado;
	}
	public void setImportePagado(Double importePagado) {
		ImportePagado = importePagado;
	}
	public Double getTotalPendiente() {
		return TotalPendiente;
	}
	public void setTotalPendiente(Double totalPendiente) {
		TotalPendiente = totalPendiente;
	}
	public String getSituacionID() {
		return SituacionID;
	}
	public void setSituacionID(String situacionID) {
		SituacionID = situacionID;
	}
	public String getSituacion() {
		return Situacion;
	}
	public void setSituacion(String situacion) {
		Situacion = situacion;
	}
	public Integer getContratoID() {
		return ContratoID;
	}
	public void setContratoID(Integer contratoID) {
		ContratoID = contratoID;
	}
	public String getContratoNumero() {
		return ContratoNumero;
	}
	public void setContratoNumero(String contratoNumero) {
		ContratoNumero = contratoNumero;
	}
	public Integer getPedidoID() {
		return PedidoID;
	}
	public void setPedidoID(Integer pedidoID) {
		PedidoID = pedidoID;
	}
	public Integer getPedidoInversionID() {
		return PedidoInversionID;
	}
	public void setPedidoInversionID(Integer pedidoInversionID) {
		PedidoInversionID = pedidoInversionID;
	}
	
}
