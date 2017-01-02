package com.pandero.ws.bean;


public class Asociado implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String nombreCompleto;
	private String tipoDocumentoIdentidad;
	private String nroDocumentoIdentidad;
	private String direccion;
	public String getNombreCompleto() {
		return nombreCompleto;
	}
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}
	public String getTipoDocumentoIdentidad() {
		return tipoDocumentoIdentidad;
	}
	public void setTipoDocumentoIdentidad(String tipoDocumentoIdentidad) {
		this.tipoDocumentoIdentidad = tipoDocumentoIdentidad;
	}
	public String getNroDocumentoIdentidad() {
		return nroDocumentoIdentidad;
	}
	public void setNroDocumentoIdentidad(String nroDocumentoIdentidad) {
		this.nroDocumentoIdentidad = nroDocumentoIdentidad;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
		
}
