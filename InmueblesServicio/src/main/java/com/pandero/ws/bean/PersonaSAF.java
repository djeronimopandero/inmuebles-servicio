package com.pandero.ws.bean;

public class PersonaSAF {

	private String personaID;
	private String tipoDocumentoID;
	private String personaCodigoDocumento;
	private String nombre;
	private String apellidoPaterno;
	private String apellidoMaterno;
	private String razonSocial;
	private String tipoPersona;
	private String nombreCompleto;
	
	public PersonaSAF() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public String getPersonaID() {
		return personaID;
	}



	public void setPersonaID(String personaID) {
		this.personaID = personaID;
	}



	public String getTipoDocumentoID() {
		return tipoDocumentoID;
	}

	public void setTipoDocumentoID(String tipoDocumentoID) {
		this.tipoDocumentoID = tipoDocumentoID;
	}

	public String getPersonaCodigoDocumento() {
		return personaCodigoDocumento;
	}

	public void setPersonaCodigoDocumento(String personaCodigoDocumento) {
		this.personaCodigoDocumento = personaCodigoDocumento;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidoPaterno() {
		return apellidoPaterno;
	}

	public void setApellidoPaterno(String apellidoPaterno) {
		this.apellidoPaterno = apellidoPaterno;
	}

	public String getApellidoMaterno() {
		return apellidoMaterno;
	}

	public void setApellidoMaterno(String apellidoMaterno) {
		this.apellidoMaterno = apellidoMaterno;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getTipoPersona() {
		return tipoPersona;
	}

	public void setTipoPersona(String tipoPersona) {
		this.tipoPersona = tipoPersona;
	}

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public PersonaSAF(String tipoDocumentoID, String personaCodigoDocumento) {
		super();
		this.tipoDocumentoID = tipoDocumentoID;
		this.personaCodigoDocumento = personaCodigoDocumento;
	}
	
	
	
}
