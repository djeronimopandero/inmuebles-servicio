package com.pandero.ws.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.pandero.ws.bean.Constante;
import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.LiquidacionSAF;
import com.pandero.ws.util.Constantes.Persona;

public class Util {

	public static Date getFechaActual(){
		Date today = new Date();
		return today;
	}
	
	public static String getFechaFormateada(Date date, String format){
		StringBuilder sb=new StringBuilder();
		sb.append("Lima, ");
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		String fecha=sdf.format(date);
		sb.append(fecha);
		return sb.toString();
	}
	
	public static String getMontoFormateado(Double monto){
		String format ="0.00";
		if(null!=monto){
			if(monto<0){
				monto = monto * -1;
			}
			
			Locale locale  = new Locale("en", "UK");
			String pattern = "####,###,###.00";
			DecimalFormat decimalFormat = (DecimalFormat)
			NumberFormat.getNumberInstance(locale);
			decimalFormat.applyPattern(pattern);
			format = decimalFormat.format(monto);
		}
		return format;
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
	
	public static Date convertirFechaStrADate(String fecha, String formato){
		SimpleDateFormat sdf=new SimpleDateFormat(formato);
		Date fechaDate = null;
		try {
			fechaDate = sdf.parse(fecha);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return fechaDate;
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
	
	public static boolean esVacio(String cadena){
		boolean resultado = true;
		if(cadena!=null && !cadena.trim().equals("")){
			resultado = false;
		}
		return resultado;
	}
	
	public static String obtenerTipoInversionID(String tipoInversionCod){
		String tipoInversionId = "";
		if(Constantes.TipoInversion.ADQUISICION_COD.equals(tipoInversionCod)){
			tipoInversionId = Constantes.TipoInversion.ADQUISICION_ID;
		}else if(Constantes.TipoInversion.CANCELACION_COD.equals(tipoInversionCod)){
			tipoInversionId = Constantes.TipoInversion.CANCELACION_ID;
		}else if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(tipoInversionCod)){
			tipoInversionId = Constantes.TipoInversion.CONSTRUCCION_ID;
		}
		return tipoInversionId;
	}
	
	public static String obtenerTipoDocuIdenSAFPorCaspioId(List<Constante> listaTiposDocuIden, String tipoDocuCaspioId){
		String resultado = "";
		if(listaTiposDocuIden!=null && listaTiposDocuIden.size()>0){
			for(Constante tipoDocu : listaTiposDocuIden){
				if(tipoDocuCaspioId.equals(String.valueOf(tipoDocu.getConstanteId().intValue()))){
					resultado = tipoDocu.getListaId();
				}
			}
		}
		return resultado;
	}
	
	public static String obtenerTipoDocuIdenCaspioPorSAFId(List<Constante> listaTiposDocuIden, String tipoDocuSAFId){
		String resultado = "";
		if(listaTiposDocuIden!=null && listaTiposDocuIden.size()>0){
			for(Constante tipoDocu : listaTiposDocuIden){
				if(tipoDocuSAFId.equals(tipoDocu.getListaId())){
					resultado = String.valueOf(tipoDocu.getConstanteId().intValue());
				}
			}
		}
		return resultado;
	}
	
	 /**
	  * @param listContratos
	  * @return	: String
	  * @date	: 15 de dic. de 2016
	  * @time	: 10:35:53 a. m.
	  * @author	: Arly Fernandez.
	  * @descripcion : Concatenar los numeros de contrato de una lista
	 */
	public static String getContratosFromList(List<Contrato> listContratos){
		String strContratos="";
		if(null!=listContratos){
			StringBuilder sb=new StringBuilder();
			for(int i=0; i<listContratos.size();i++){
				Contrato contrato=listContratos.get(i);
				if(i==listContratos.size()-1){
					sb.append(contrato.getNroContrato());
				}else{
					sb.append(contrato.getNroContrato()).append("/");
				}
			}
			strContratos=sb.toString();
		}
		return strContratos;
	}
	
	public static double obtenerPorcentajeArmada(List<Constante> listaArmadas, String nroArmada){
		double resultado = 0.00;
		if(listaArmadas!=null && listaArmadas.size()>0){
			for(Constante armada : listaArmadas){
				if(armada.getListaId().equals(nroArmada)){
					resultado = Double.parseDouble(armada.getDescripcion());
					break;
				}
			}
		}
		return resultado;
	}
	
	
	public static String obtenerEstadoLiquidacionPorNroArmada(List<LiquidacionSAF> liquidacionInversion, String nroArmada){
		String estadoLiquidacion = "";
		if(liquidacionInversion!=null && liquidacionInversion.size()>0){
			for(LiquidacionSAF liquidacion : liquidacionInversion){
				System.out.println("liquidacion.getNroArmada(): "+liquidacion.getNroArmada()+" - nroArmada: "+nroArmada);
				if(String.valueOf(liquidacion.getNroArmada()).equals(nroArmada)){
					estadoLiquidacion = liquidacion.getLiquidacionEstado();
					break;
				}
			}
		}		
		return estadoLiquidacion;
	}

	public static String obtenerBooleanString(Boolean valor){
		String resultado = "0";
		if(valor!=null){
			if(valor){
				resultado="1";
			}
		}
		return resultado;
	}
}
