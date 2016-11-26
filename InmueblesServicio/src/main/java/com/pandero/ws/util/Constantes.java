package com.pandero.ws.util;

public class Constantes {

	public class Service {
		public final static String URL_WHERE = "?q={serviceWhere}";
		public final static String REQUEST_VACIO = "{}";
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
	
	public static final String FORMATO_DATE_NORMAL = "dd/MM/yyyy";
	public static final String FORMATO_DATE_HH_MIN_SS = "hh:mm:ss";
	
}
