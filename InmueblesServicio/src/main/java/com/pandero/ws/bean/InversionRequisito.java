package com.pandero.ws.bean;

public class InversionRequisito {

	private Integer InversionId;
	private Integer RequisitoId;
	private String EstadoRequisito;
	private String Observacion;
	
	public Integer getInversionId() {
		return InversionId;
	}
	public void setInversionId(Integer inversionId) {
		InversionId = inversionId;
	}
	public Integer getRequisitoId() {
		return RequisitoId;
	}
	public void setRequisitoId(Integer requisitoId) {
		RequisitoId = requisitoId;
	}
	public String getEstadoRequisito() {
		return EstadoRequisito;
	}
	public void setEstadoRequisito(String estadoRequisito) {
		EstadoRequisito = estadoRequisito;
	}
	public String getObservacion() {
		return Observacion;
	}
	public void setObservacion(String observacion) {
		Observacion = observacion;
	}
		
}
