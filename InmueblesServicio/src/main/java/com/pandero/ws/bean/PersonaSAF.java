package com.pandero.ws.bean;

public class PersonaSAF implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer personaID;
	private String tipoDocumentoID;
	private String personaCodigoDocumento;
	private String nombre;
	private String apellidoPaterno;
	private String apellidoMaterno;
	private String razonSocial;
	private String tipoPersona;
	private String nombreCompleto;
	private Integer personaRelacionadaID;
	private Integer proveedorID;
	private String tipoProveedor;
	private String telefono;
	
	public PersonaSAF(){
		
	}
	
	public Integer getPersonaID() {
		return personaID;
	}

	public void setPersonaID(Integer personaID) {
		this.personaID = personaID;
	}


	public Integer getPersonaRelacionadaID() {
		return personaRelacionadaID;
	}

	public void setPersonaRelacionadaID(Integer personaRelacionadaID) {
		this.personaRelacionadaID = personaRelacionadaID;
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

	public Integer getProveedorID() {
		return proveedorID;
	}

	public void setProveedorID(Integer proveedorID) {
		this.proveedorID = proveedorID;
	}
	
	public String getTipoProveedor() {
		return tipoProveedor;
	}

	public void setTipoProveedor(String tipoProveedor) {
		this.tipoProveedor = tipoProveedor;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
}
