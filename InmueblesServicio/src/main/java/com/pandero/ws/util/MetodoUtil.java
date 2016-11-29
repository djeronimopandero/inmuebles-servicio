package com.pandero.ws.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.pandero.ws.bean.Constante;
import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.util.Constantes.Persona;

public class MetodoUtil {

	public static Date getFechaActual(){
		Date today = new Date();
		return today;
	}
	
	public static String formatearFechaYYYYMMDD(Date fecha){
		String fechaText = null;
		if(fecha!=null){
			fechaText = new SimpleDateFormat("yyyy-MM-dd").format(fecha);
		}
		return fechaText;
	}

	public static String getFechaActualYYYYMMDD(){
		String fecha = null;
		Date today = new Date();
		fecha = new SimpleDateFormat("yyyy-MM-dd").format(today);
		return fecha;
	}
	
	public static String getDateFormat(Date date,String format){
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		String fecha=sdf.format(date);
		return fecha;
	}
	
	public static String getDocuIdentidadNombre(List<Constante> listaDocuIdentidad, String docuIdentidadId){
		String docuIdentidadNombre = "";
		for(Constante constante : listaDocuIdentidad){
			if(String.valueOf(constante.getConstanteId().intValue()).equals(docuIdentidadId)){
				docuIdentidadNombre = constante.getNombreConstante();
				break;
			}
		}
		return docuIdentidadNombre;
	}
	
	public static boolean esPersonaJuridica(String docuIdentidadId){
		boolean esJuridico = false;
		if(Constantes.DocumentoIdentidad.RUC_ID.equals(docuIdentidadId)){
			esJuridico = true;
		}
		return esJuridico;
	}
	
	public static String getDepartamentoNombre(String departamentoId){
		String nombre = "";
		if(Constantes.Ubigeo.DEPT_COD_CALLAO.equals(departamentoId)){
			nombre = Constantes.Ubigeo.DEPT_NOM_CALLAO;
		}else{
			nombre = Constantes.Ubigeo.DEPT_NOM_LIMA;			
		}
		return nombre;
	}
	
	public static String getProvinciaNombre(String provinciaId){
		String nombre = "";
		if(Constantes.Ubigeo.PROV_COD_CALLAO.equals(provinciaId)){
			nombre = Constantes.Ubigeo.PROV_NOM_CALLAO;
		}else{
			nombre = Constantes.Ubigeo.PROV_NOM_LIMA;			
		}
		return nombre;
	}
	
	public static String getTipoInversionNombre(String tipoInversionId){
		String nombre = "";
		if(Constantes.TipoInversion.ADQUISICION_COD.equals(tipoInversionId)){
			nombre = Constantes.TipoInversion.ADQUISICION_NOM;
		}else if(Constantes.TipoInversion.CANCELACION_COD.equals(tipoInversionId)){
			nombre = Constantes.TipoInversion.CANCELACION_NOM;			
		}else{
			nombre = Constantes.TipoInversion.CONSTRUCCION_NOM;	
		}
		return nombre;
	}
	
	public static double getSumaContratosxPedido(List<Contrato> listaContratos){
		double sumaContratos = 0;
		for(Contrato contrato : listaContratos){
			sumaContratos = sumaContratos + contrato.getMontoCertificado().doubleValue();	
		}
		return sumaContratos;
	}
	
	public static double getSumaInversionesxPedido(List<Inversion> listaInversiones){
		double sumaInversiones = 0;
		for(Inversion inversion : listaInversiones){
			sumaInversiones = sumaInversiones+ inversion.getImporteInversion().doubleValue();		
		}
		return sumaInversiones;
	}
	
	public static boolean esSituacionAdjudicado(String situacionContrato){
		boolean resultado = false;
		int situacionContratoId = Integer.parseInt(situacionContrato);
		if(situacionContratoId>9 && situacionContratoId<100){
			resultado = true;
		}
		return resultado;
	}
	
	public static String getTipoPersonaPorDocIden(String docuIdent){
		String tipoPersona=Constantes.Persona.PERSONA_NATURAL_COD;
		if(Persona.TIPO_DOCUMENTO_RUC_ID.equals(docuIdent)){
			tipoPersona=Constantes.Persona.PERSONA_JURIDICA_COD;
		}
		return tipoPersona;
	}
	
}
