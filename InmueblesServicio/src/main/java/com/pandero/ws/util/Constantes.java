package com.pandero.ws.util;

public class Constantes {

	public class Service {
		public final static String URL_WHERE = "?q={serviceWhere}";
		public final static String RESULTADO_EXITOSO = "OPERACION_EXITOSA";
		public final static String RESULTADO_ERROR_INESPERADO = "Ocurrio un error inesperado";
		public final static String RESULTADO_INVERSIONES_CONFIRMADAS = "INVERSIONES_CONFIRMADAS";
		public final static String RESULTADO_PENDIENTE_DOCUMENTOS = "PENDIENTE_DOCUMENTOS";
		public final static String RESULTADO_DATOS_PENDIENTES = "DATOS_PENDIENTES";
	}
	
	public class Pedido {
		public final static String ESTADO_EMITIDO = "EMITIDO";
		public final static String ESTADO_ANULADO = "ANULADO";
	}
	
	public class Producto {
		public final static String PRODUCTO_INMUEBLES = "C5 - INMUEBLES";
	}
	
	public class DocumentoIdentidad{
		public final static String RUC_ID = "60";
	}
	
	public class Ubigeo{
		public final static String DEPT_COD_LIMA = "15";
		public final static String DEPT_COD_CALLAO = "07";
		public final static String DEPT_NOM_LIMA = "LIMA";
		public final static String DEPT_NOM_CALLAO = "CALLAO";
		public final static String PROV_COD_LIMA = "1501";
		public final static String PROV_COD_CALLAO = "0701";
		public final static String PROV_NOM_LIMA = "LIMA";
		public final static String PROV_NOM_CALLAO = "CALLAO";
	}
	
	public class TipoInversion{
		public final static String ADQUISICION_COD="ADQUISICION";
		public final static String CANCELACION_COD="CANCELACION";
		public final static String CONSTRUCCION_COD="CONSTRUCCION";
		public final static String ADQUISICION_NOM="ADQUISICIÓN DE UN INMUEBLE (3)";
		public final static String CANCELACION_NOM="CANCELACIÓN DE CRÉDITO HIPOTECARIO (2)";
		public final static String CONSTRUCCION_NOM="CONSTRUCCIÓN, AMPLIACIÓN Y/O REFACCIÓN DE UN BIEN INMUEBLE (1)";
	}
	
	public class Contrato{
		public final static String SITUACION_ADJUDICADO="ADJUDICADO";
		public final static String SITUACION_NO_ADJUDICADO="NO ADJUDICADO";
		public final static String ESTADO_ASOCIADO="ASOCIADO";
		public final static String ESTADO_NO_ASOCIADO="";
	}
	
	public class Inversion{
		public final static String SITUACION_CONFIRMADO="SI";
		public final static String SITUACION_NO_CONFIRMADO="NO";
		public final static String ESTADO_ANULADO="ANULADO";
		public final static String DIFERENCIA_PRECIO="DIFERENCIA_PRECIO";
		public final static String EXCEDENTE_CERTIFICADO="EXCEDENTE_CERTIFICADO";
	}
	
	public class InversionRequisito{
		public final static String PENDIENTE="PENDIENTE";
		public final static String CONFORME="CONFORME";
	}
	
	public class Persona{
		public final static String PERSONA_NATURAL_COD="N";
		public final static String PERSONA_JURIDICA_COD="J";
		public final static String TIPO_DOCUMENTO_RUC_ID="60";
	}
	
	public class Celula{
		public final static String SECTORIZACION_CELULA_COD="00004";
	}
	
	public class DocumentoRequisito{
		public final static String TIPO_DOCUMENTO="DOCUMENTO";
		public final static String TIPO_REQUISITO="REQUISITO";
		public final static String ESTADO_REQUISITO_PENDIENTE="PENDIENTE";
		public final static String ESTADO_REQUISITO_CONFORME="CONFORME";
		public final static String ESTADO_REQUISITO_NO_CONFORME="NO CONFORME";
	}
	
	public static final String FORMATO_DATE_NORMAL = "dd/MM/yyyy";
	public static final String FORMATO_DATE_HH_MIN_SS = "hh:mm:ss";
	
}
