package com.pandero.ws.bean;

public class Garantia implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer idGarantia;
	private Integer garantiaSAFId;
	private Integer pedidoID;
	private String partidaRegistral;
	private String fichaConstitucion;
	private String fechaConstitucion;
	private String montoPrima;
	private String estadoGarantia;
	private String pedidoNumero;
	private String modalidad;
	private String usoBien;
	private String nroContrato;
	
	
	public String getNroContrato() {
		return nroContrato;
	}
	public void setNroContrato(String nroContrato) {
		this.nroContrato = nroContrato;
	}
	public Integer getIdGarantia() {
		return idGarantia;
	}
	public void setIdGarantia(Integer idGarantia) {
		this.idGarantia = idGarantia;
	}
	public Integer getGarantiaSAFId() {
		return garantiaSAFId;
	}
	public void setGarantiaSAFId(Integer garantiaSAFId) {
		this.garantiaSAFId = garantiaSAFId;
	}
	public Integer getPedidoID() {
		return pedidoID;
	}
	public void setPedidoID(Integer pedidoID) {
		this.pedidoID = pedidoID;
	}
	public String getPartidaRegistral() {
		return partidaRegistral;
	}
	public void setPartidaRegistral(String partidaRegistral) {
		this.partidaRegistral = partidaRegistral;
	}
	public String getFichaConstitucion() {
		return fichaConstitucion;
	}
	public void setFichaConstitucion(String fichaConstitucion) {
		this.fichaConstitucion = fichaConstitucion;
	}
	public String getFechaConstitucion() {
		return fechaConstitucion;
	}
	public void setFechaConstitucion(String fechaConstitucion) {
		this.fechaConstitucion = fechaConstitucion;
	}
	public String getMontoPrima() {
		return montoPrima;
	}
	public void setMontoPrima(String montoPrima) {
		this.montoPrima = montoPrima;
	}
	public String getEstadoGarantia() {
		return estadoGarantia;
	}
	public void setEstadoGarantia(String estadoGarantia) {
		this.estadoGarantia = estadoGarantia;
	}
	public String getPedidoNumero() {
		return pedidoNumero;
	}
	public void setPedidoNumero(String pedidoNumero) {
		this.pedidoNumero = pedidoNumero;
	}
	public String getModalidad() {
		return modalidad;
	}
	public void setModalidad(String modalidad) {
		this.modalidad = modalidad;
	}
	public String getUsoBien() {
		return usoBien;
	}
	public void setUsoBien(String usoBien) {
		this.usoBien = usoBien;
	}
		
}
