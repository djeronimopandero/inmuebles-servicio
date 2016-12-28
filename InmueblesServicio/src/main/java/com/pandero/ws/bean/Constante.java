package com.pandero.ws.bean;

public class Constante implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	

	private Integer ConstanteId;
	private String TipoConstante;
	private String NombreConstante;
	private String ListaId;
	private String Descripcion;
	
	public Integer getConstanteId() {
		return ConstanteId;
	}
	public void setConstanteId(Integer constanteId) {
		ConstanteId = constanteId;
	}
	public String getTipoConstante() {
		return TipoConstante;
	}
	public void setTipoConstante(String tipoConstante) {
		TipoConstante = tipoConstante;
	}
	public String getNombreConstante() {
		return NombreConstante;
	}
	public void setNombreConstante(String nombreConstante) {
		NombreConstante = nombreConstante;
	}
	public String getListaId() {
		return ListaId;
	}
	public void setListaId(String listaId) {
		ListaId = listaId;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
		
}
