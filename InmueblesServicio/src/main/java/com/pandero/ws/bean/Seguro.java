package com.pandero.ws.bean;

public class Seguro {

	private Integer idSeguro;
	private Double sumaAsegurada;
	private String codigoTipoPlan;
	private Double prima;
	private String modalidad;
	private Integer idGarantia;
	private String nroPoliza;
	
	public Integer getIdSeguro() {
		return idSeguro;
	}
	public void setIdSeguro(Integer idSeguro) {
		this.idSeguro = idSeguro;
	}
	public Double getSumaAsegurada() {
		return sumaAsegurada;
	}
	public void setSumaAsegurada(Double sumaAsegurada) {
		this.sumaAsegurada = sumaAsegurada;
	}
	public String getCodigoTipoPlan() {
		return codigoTipoPlan;
	}
	public void setCodigoTipoPlan(String codigoTipoPlan) {
		this.codigoTipoPlan = codigoTipoPlan;
	}
	public Double getPrima() {
		return prima;
	}
	public void setPrima(Double prima) {
		this.prima = prima;
	}
	public String getModalidad() {
		return modalidad;
	}
	public void setModalidad(String modalidad) {
		this.modalidad = modalidad;
	}
	public Integer getIdGarantia() {
		return idGarantia;
	}
	public void setIdGarantia(Integer idGarantia) {
		this.idGarantia = idGarantia;
	}
	public String getNroPoliza() {
		return nroPoliza;
	}
	public void setNroPoliza(String nroPoliza) {
		this.nroPoliza = nroPoliza;
	}
	
}
