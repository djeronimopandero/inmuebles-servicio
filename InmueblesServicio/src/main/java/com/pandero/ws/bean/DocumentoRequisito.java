package com.pandero.ws.bean;

public class DocumentoRequisito implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer RequisitoId;
	private String Descripcion;
	private String TipoInversion;
	private String TipoPersona;
	
	public Integer getRequisitoId() {
		return RequisitoId;
	}
	public void setRequisitoId(Integer requisitoId) {
		RequisitoId = requisitoId;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	public String getTipoInversion() {
		return TipoInversion;
	}
	public void setTipoInversion(String tipoInversion) {
		TipoInversion = tipoInversion;
	}
	public String getTipoPersona() {
		return TipoPersona;
	}
	public void setTipoPersona(String tipoPersona) {
		TipoPersona = tipoPersona;
	}	
	
}
