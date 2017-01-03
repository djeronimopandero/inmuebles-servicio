package com.pandero.ws.bean;

public class Desembolso {

	private Integer DesembolsoId;
	private Integer InversionId;
	private String NroArmada;
	private String NroDesembolso;
	private String FechaDesembolso;
	
	public Integer getDesembolsoId() {
		return DesembolsoId;
	}
	public void setDesembolsoId(Integer desembolsoId) {
		DesembolsoId = desembolsoId;
	}
	public Integer getInversionId() {
		return InversionId;
	}
	public void setInversionId(Integer inversionId) {
		InversionId = inversionId;
	}
	public String getNroArmada() {
		return NroArmada;
	}
	public void setNroArmada(String nroArmada) {
		NroArmada = nroArmada;
	}
	public String getNroDesembolso() {
		return NroDesembolso;
	}
	public void setNroDesembolso(String nroDesembolso) {
		NroDesembolso = nroDesembolso;
	}
	public String getFechaDesembolso() {
		return FechaDesembolso;
	}
	public void setFechaDesembolso(String fechaDesembolso) {
		FechaDesembolso = fechaDesembolso;
	}
		
}
