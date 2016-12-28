package com.pandero.ws.bean;

 /**
  * Proyecto: InmueblesServicio - Tabla Comprobante de Caspio usada para registrar comprobantes usados para cubrir una inversion
  * @date	: 22 de dic. de 2016
  * @time	: 11:06:55 a. m.
  * @author	: Arly Fernandez.
 */
public class ComprobanteCaspio implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String Documento;
	private String FechaEmision;
	private String Serie;
	private String Numero;
	private String NumeroDocumento;
	private String TipoDocumento;
	private String TipoMoneda;
	private String Importe;
	private String FechaEnvio;
	private String Proveedor;
	private String InversionId;
	private String NroArmada;
	
	public String getDocumento() {
		return Documento;
	}
	public void setDocumento(String documento) {
		Documento = documento;
	}
	public String getFechaEmision() {
		return FechaEmision;
	}
	public void setFechaEmision(String fechaEmision) {
		FechaEmision = fechaEmision;
	}
	public String getSerie() {
		return Serie;
	}
	public void setSerie(String serie) {
		Serie = serie;
	}
	public String getNumero() {
		return Numero;
	}
	public void setNumero(String numero) {
		Numero = numero;
	}
	public String getNumeroDocumento() {
		return NumeroDocumento;
	}
	public void setNumeroDocumento(String numeroDocumento) {
		NumeroDocumento = numeroDocumento;
	}
	public String getTipoDocumento() {
		return TipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento) {
		TipoDocumento = tipoDocumento;
	}
	public String getTipoMoneda() {
		return TipoMoneda;
	}
	public void setTipoMoneda(String tipoMoneda) {
		TipoMoneda = tipoMoneda;
	}
	public String getImporte() {
		return Importe;
	}
	public void setImporte(String importe) {
		Importe = importe;
	}
	public String getFechaEnvio() {
		return FechaEnvio;
	}
	public void setFechaEnvio(String fechaEnvio) {
		FechaEnvio = fechaEnvio;
	}
	public String getProveedor() {
		return Proveedor;
	}
	public void setProveedor(String proveedor) {
		Proveedor = proveedor;
	}
	public String getInversionId() {
		return InversionId;
	}
	public void setInversionId(String inversionId) {
		InversionId = inversionId;
	}
	 /**
	  * @return	: String
	  * @date	: 22 de dic. de 2016
	  * @time	: 11:09:26 a. m.
	  * @author	: Arly Fernandez.
	  * @descripcion : 
	  * Nro Armada 1 : Se realizo el desembolso del 100%
  	  * Nro Armada 2 : Se realizo el desembolso del 50%
  	  * Nro Armada 3 : Se realizo el desembolso del 40%
  	  * Nro Armada 4 : Se realizo el desembolso del 10%
	 */
	public String getNroArmada() {
		return NroArmada;
	}
	public void setNroArmada(String nroArmada) {
		NroArmada = nroArmada;
	}
	

}
