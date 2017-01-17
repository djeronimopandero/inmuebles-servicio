package com.pandero.ws.bean;

public class SolicitudAutorizacion {

	private int SolicitudID;
	private String ProcesoID;
	private int PedidoInversionID;
	private String NroArmada;
	private String NroInversion;
	private String SolicitudEstadoID;
	
	public int getSolicitudID() {
		return SolicitudID;
	}
	public void setSolicitudID(int solicitudID) {
		SolicitudID = solicitudID;
	}
	public String getProcesoID() {
		return ProcesoID;
	}
	public void setProcesoID(String procesoID) {
		ProcesoID = procesoID;
	}
	public int getPedidoInversionID() {
		return PedidoInversionID;
	}
	public void setPedidoInversionID(int pedidoInversionID) {
		PedidoInversionID = pedidoInversionID;
	}
	public String getNroArmada() {
		return NroArmada;
	}
	public void setNroArmada(String nroArmada) {
		NroArmada = nroArmada;
	}
	public String getNroInversion() {
		return NroInversion;
	}
	public void setNroInversion(String nroInversion) {
		NroInversion = nroInversion;
	}
	public String getSolicitudEstadoID() {
		return SolicitudEstadoID;
	}
	public void setSolicitudEstadoID(String solicitudEstadoID) {
		SolicitudEstadoID = solicitudEstadoID;
	}
	
}
