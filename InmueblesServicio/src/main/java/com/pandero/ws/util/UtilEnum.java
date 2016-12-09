package com.pandero.ws.util;

public class UtilEnum {

	public static enum TIPO_DOCUMENTO {
		
		DNI(4, "DNI", 59),
		PASAPORTE (8, "RUC", 60);
		
		private final Integer codigo;
		private final String texto;
		private final Integer codigoCaspio;

		private TIPO_DOCUMENTO(Integer codigo, String texto, Integer codigoCaspio) {
			this.codigo = codigo;
			this.texto = texto;
			this.codigoCaspio = codigoCaspio;
		}

		public Integer getCodigo() {
			return codigo;
		}

		public String getTexto() {
			return texto;
		}
		
		public Integer getCodigoCaspio() {
			return codigoCaspio;
		}

		public static TIPO_DOCUMENTO obtenerTipoDocumentoByCodigo(Integer codigo){
			  TIPO_DOCUMENTO[] valores = TIPO_DOCUMENTO.values();
			  for(int i=0; i< valores.length; i++){
				  if(valores[i].getCodigo().equals(codigo)){
						return  valores[i];
				  }
			  }
			  return null;
		}
		
		public static TIPO_DOCUMENTO obtenerTipoDocumentoByCodigoCaspio(Integer codigoCaspio){
			TIPO_DOCUMENTO[] valores = TIPO_DOCUMENTO.values();
			for(int i=0; i< valores.length; i++){
				if(valores[i].getCodigoCaspio().equals(codigoCaspio)){
					return  valores[i];
				}
			}
			return null;
		}
		
	}
	
	public static enum ADJUDICACION {
		
		SI(1, "ADJUDICADO"),
		NO(2, "NO ADJUDICADO");
		
		private final Integer codigo;
		private final String texto;

		private ADJUDICACION(Integer codigo, String texto) {
			this.codigo = codigo;
			this.texto = texto;
		}

		public Integer getCodigo() {
			return codigo;
		}

		public String getTexto() {
			return texto;
		}
	}
	
}
