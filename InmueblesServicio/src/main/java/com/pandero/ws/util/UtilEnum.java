package com.pandero.ws.util;

public class UtilEnum {

	public static enum TIPO_DOCUMENTO {
		
		DNI(4, "DNI", 59),
		CE(4, "C.E.", 57),
		PSP(4, "PSP", 58),
		RUC (8, "RUC", 60);
		
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
			  return TIPO_DOCUMENTO.DNI;//si es nulo
		}
		
		public static TIPO_DOCUMENTO obtenerTipoDocumentoByCodigoCaspio(Integer codigoCaspio){
			TIPO_DOCUMENTO[] valores = TIPO_DOCUMENTO.values();
			for(int i=0; i< valores.length; i++){
				if(valores[i].getCodigoCaspio().equals(codigoCaspio)){
					return  valores[i];
				}
			}
			return TIPO_DOCUMENTO.DNI;//si es nulo
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
	
	public static enum ESTADO_OPERACION {
		
		EXITO(0, "EXITO"),
		ERROR(1, "ERROR"),
		EXCEPTION(2, "EXCEPTION");
		
		private final Integer codigo;
		private final String texto;

		private ESTADO_OPERACION(Integer codigo, String texto) {
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
	
	public static enum ESTADO_COMPROBANTE {
		
		ENVIADO(1, "ENVIADO"),
		GUARDADO(2, "GUARDADO"),
		RECIBIDO(3, "RECIBIDO");
		
		private final Integer codigo;
		private final String texto;

		private ESTADO_COMPROBANTE(Integer codigo, String texto) {
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
