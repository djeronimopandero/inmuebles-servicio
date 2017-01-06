package com.pandero.ws.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constantes {
	
	public static final Map<Integer,String> ARMADAS_DOC_DESEMBOLSO;
	static {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(1, "primera armada por el 100%");
        map.put(2, "primera armada por el 50%");
        map.put(3, "segunda armada por el 40%");
        map.put(4, "tercera armada por el 10%");
        ARMADAS_DOC_DESEMBOLSO = Collections.unmodifiableMap(map);
    }

	public class Service {
		public final static String URL_WHERE = "?q={serviceWhere}";
		public final static String RESULTADO_ERROR_INESPERADO = "Ocurrio un error inesperado";
		public final static String RESULTADO_EXITOSO = "OPERACION_EXITOSA";		
		public final static String RESULTADO_INVERSIONES_CONFIRMADAS = "INVERSIONES_CONFIRMADAS";
		public final static String RESULTADO_PENDIENTE_DOCUMENTOS = "PENDIENTE_DOCUMENTOS";
		public final static String RESULTADO_DATOS_PENDIENTES = "DATOS_PENDIENTES";
		public final static String RESULTADO_NO_INVERSIONES_CONFIRMADAS = "NO_INVERSIONES_CONFIRMADAS";
		public final static String RESULTADO_PENDIENTE_REQUISITOS = "PENDIENTE_REQUISITOS";
		public final static String RESULTADO_TIENE_REQUISITOS = "TIENE_REQUISITOS";
		public final static String NO_MONTO_DISPONIBLE_LIQUIDAR = "NO_MONTO_DISPONIBLE_LIQUIDAR";
		public final static String RESULTADO_EXISTE_LIQUIDACION = "EXISTE_LIQUIDACION";
		public final static String RESULTADO_NO_EXISTE_LIQUIDACION = "NO_EXISTE_LIQUIDACION";
		public final static String RESULTADO_INVERSION_LIQUIDADA = "INVERSION_LIQUIDADA";
		public final static String RESULTADO_INVERSION_VB_CONTABLE = "INVERSION_VB_CONTABLE";
		public final static String RESULTADO_INVERSION_DESEMBOLSADA = "INVERSION_DESEMBOLSADA";
		//public final static String RESULTADO_PASO_LIQUIDACION = "PASO_LIQUIDACION";
		public final static String RESULTADO_NO_GARANTIAS = "NO_GARANTIAS";
		public final static String RESULTADO_SI_LIQUIDACION_AUTO = "SI_LIQUIDACION_AUTO";
		public final static String RESULTADO_NO_LIQUIDACION_AUTO = "NO_LIQUIDACION_AUTO";
		public final static String RESULTADO_OPERACION_CANCELADA = "OPERACION_CANCELADA";
		public final static String RESULTADO_PENDIENTE_COBROS = "PENDIENTE_COBROS";
		public final static String RESULTADO_INVERSION_NO_CONFIRMADA = "INVERSION_NO_CONFIRMADA";
		public final static String RESULTADO_SIN_COMPROBANTES= "SIN_COMPROBANTES";
		public final static String RESULTADO_SIN_ACTUALZ_SALDO_DEUDA= "SIN_ACTUALZ_SALDO_DEUDA";
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
		public final static String ADQUISICION_ID="1";
		public final static String CANCELACION_ID="2";
		public final static String CONSTRUCCION_ID="3";
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
		public final static String ESTADO_EN_PROCESO="EN-PROCESO";
		public final static String ESTADO_LIQUIDADO="LIQUIDADO";
		public final static String ESTADO_VB_CONTABLE="VB-CONT";
		public final static String ESTADO_DESEMBOLSADO="DESEMBOLSADO";
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
	
	public class Proveedor{
		public final static String TIPO_ENTIDAD_FINANCIERA="ENTIDAD_FINANCIERA";
		public final static String TIPO_CONSTRUCTORA="CONTRUCTORA";
		public final static String TIPO_PERSONA="PERSONA";
		public final static String TIPO_ENTIDAD_FINANCIERA_COD="9";
		public final static String TIPO_CONSTRUCTORA_COD="10";
		public final static String TIPO_PERSONA_COD="11";
	}
	
	public class Liquidacion{
		public final static String LIQUI_TIPO_COMPRAS="1";
		public final static String TIPO_DOCU_CONTRATO="1";
		public final static String TIPO_DOCU_DIF_PRECIO="2";
		public final static String MONEDA_DORALES_ID="2";
		public final static String LIQUI_ORIGEN_SISTEMA="S"; // DE CONTRATO
		public final static String LIQUI_ORIGEN_EMPRESA="E"; // DE DIF. PRECIO
		public final static String LIQUI_DESTINO_PROVEEDOR="1";
		public final static String LIQUI_DESTINO_PANDERO="2";
		public final static String LIQUI_DESTINO_FONDO_COLECTVO="3";
		public final static String LIQUI_ESTADO_CREADO="1";
		public final static String LIQUI_ESTADO_VB_CONTB="2";
		public final static String LIQUI_ESTADO_DESEMBOLSADO="3";
		public final static double PORCENTAJE_MIN_DESEMBOLSO=0.97;
		public final static String CONCEPTO_SITUACION_PENDIENTE="01";
	}
	
	public class GenLista{
		public final static String TIPO_ARMADAS_DESEMBOLSO="INMUEBLES_ARMADAS_DESEMBOLSO";
	}
		
	public static final String FORMATO_DATE_NORMAL = "dd/MM/yyyy";
	public static final String FORMATO_DATE_YMD = "yyyy-MM-dd";
	public static final String FORMATO_DATE_HH_MIN_SS = "hh:mm:ss";
	public static final String FORMATO_CARTA_VALIDACION_INVERSION = "dd 'de' MMMM 'del' yyyy";
	
}
