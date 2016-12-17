package com.pandero.ws.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.pandero.ws.bean.Constante;
import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.ObservacionInversion;
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
				}
				sb.append(contrato.getNroContrato()).append(",");
			}
			strContratos=sb.toString();
		}
		return strContratos;
	}
	
	 /**
	  * @param contratos
	  * @param asociado
	  * @param tipoInmueble
	  * @param libreGravamen
	  * @param partidaRegistrar
	  * @param importeInversion
	  * @param areaTotal
	  * @param tipoInversion
	  * @param nroInversion
	  * @return	: String
	  * @date	: 15 de dic. de 2016
	  * @time	: 10:26:25 a. m.
	  * @author	: Arly Fernandez.
	  * @descripcion : Generar el html para ser enviado al generar una carta de validación cuando existen requisitos como NO CONFORME
	 */
	public static String getHtmlCartaValidacionNoConforme(String contratos,String asociado,String tipoInmueble,String libreGravamen,String partidaRegistrar,String importeInversion,String areaTotal,String tipoInversion,String nroInversion,List<ObservacionInversion> listObs){
		StringBuilder strHtml= new StringBuilder("");
		
		strHtml.append("<head>");
		strHtml.append("</head>");
		strHtml.append("<body>");
		strHtml.append("<table>");
			strHtml.append("<tr>");
				strHtml.append("<td>CC</td>");
				strHtml.append("<td>:"+contratos+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Asociado</td>");
				strHtml.append("<td>:"+asociado+"</td>");
			strHtml.append("</tr>");
		strHtml.append("</table>");
		strHtml.append("</br>");
		
		strHtml.append("<table>");
			strHtml.append("<tr>");
				strHtml.append("<td>Se ha finalizado la verificación de la inversión inmobiliaria Nro. "+nroInversion+" quedando algunas revisiones en estado NO CONFORME, se solicita emitir la carta de validación de datos para ser remitido al Asociado.</td>");
			strHtml.append("</tr>");
		strHtml.append("</table>");
		
		strHtml.append("</br>");
		
		if(null!=listObs){
			strHtml.append("<table>");
			for(int i=0; i<listObs.size();i++){
				strHtml.append("<tr>");
					strHtml.append("<td>Observación Nro. "+(i+1)+"</td>");
					strHtml.append("<td>:"+listObs.get(i).getObservacion()+"</td>");
				strHtml.append("</tr>");
			}
			strHtml.append("</table>");
		}
		
		strHtml.append("</body>");
		strHtml.append("</html>");
		
		return strHtml.toString();
	}
	
	 /**
	  * @param contratos
	  * @param asociado
	  * @param tipoInmueble
	  * @param libreGravamen
	  * @param partidaRegistrar
	  * @param importeInversion
	  * @param areaTotal
	  * @param tipoInversion
	  * @param nroInversion
	  * @return	: String
	  * @date	: 15 de dic. de 2016
	  * @time	: 11:05:22 a. m.
	  * @author	: Arly Fernandez.
	  * @descripcion : 	Generar el html para ser enviado al generar una carta de validación cuando el estado es CONFORME
	 */
	public static String getHtmlCartaValidacionConforme(String contratos,String asociado,String tipoInmueble,String libreGravamen,String partidaRegistrar,String importeInversion,String areaTotal,String tipoInversion,String nroInversion){
		StringBuilder strHtml= new StringBuilder("");
		
		strHtml.append("<head>");
		strHtml.append("</head>");
		strHtml.append("<body>");
		strHtml.append("<table>");
			strHtml.append("<tr>");
				strHtml.append("<td>CC</td>");
				strHtml.append("<td>:"+contratos+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Asociado</td>");
				strHtml.append("<td>:"+asociado+"</td>");
			strHtml.append("</tr>");
		strHtml.append("</table>");
		strHtml.append("</br>");
		strHtml.append("<table>");
			strHtml.append("<tr>");
				strHtml.append("<td>Se ha finalizado la verificación de la inversión inmobiliaria Nro. "+nroInversion+" quedando todas las revisiones en estado CONFORME, se solicita continuar con: el registro de comprobante(s) de pago emitidos por el proveedor/vendedor del inmueble; la actualización del saldo de la deuda; la liquidación de los fondos disponibles.</td>");
			strHtml.append("</tr>");
		strHtml.append("</table>");
		strHtml.append("</br>");
		strHtml.append("<table>");
			strHtml.append("<tr>");
				strHtml.append("<td>Tipo de inversión</td>");
				strHtml.append("<td>:"+tipoInversion+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Tipo de inmueble</td>");
				strHtml.append("<td>:"+tipoInmueble+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Libre de gravamen</td>");
				strHtml.append("<td>:"+libreGravamen+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Área total (m2)</td>");
				strHtml.append("<td>:"+areaTotal+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Partida registral</td>");
				strHtml.append("<td>:"+partidaRegistrar+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Importe de inversión (US$)</td>");
				strHtml.append("<td>:"+importeInversion+"</td>");
			strHtml.append("</tr>");
		strHtml.append("</table>");
		strHtml.append("</body>");
		strHtml.append("</html>");
		
		return strHtml.toString();
	}
	
}
