package com.pandero.ws.bean;

public class DocumentoRequisito {

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
