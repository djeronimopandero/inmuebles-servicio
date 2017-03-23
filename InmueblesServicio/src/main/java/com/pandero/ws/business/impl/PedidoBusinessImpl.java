package com.pandero.ws.business.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pandero.ws.bean.Asociado;
import com.pandero.ws.bean.Constante;
import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.ContratoSAF;
import com.pandero.ws.bean.DocumentoRequisito;
import com.pandero.ws.bean.EmailBean;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.Parametro;
import com.pandero.ws.bean.Pedido;
import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.bean.Usuario;
import com.pandero.ws.business.ContratoBusiness;
import com.pandero.ws.business.PedidoBusiness;
import com.pandero.ws.dao.ContratoDao;
import com.pandero.ws.dao.GenericDao;
import com.pandero.ws.dao.PedidoDao;
import com.pandero.ws.dao.UsuarioDao;
import com.pandero.ws.service.ConstanteService;
import com.pandero.ws.service.ContratoService;
import com.pandero.ws.service.GenericService;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.service.MailService;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.DocumentoUtil;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.Util;

@Component
public class PedidoBusinessImpl implements PedidoBusiness{

	private static final Logger LOG = LoggerFactory.getLogger(PedidoBusinessImpl.class);
	
	@Value("${ruta.documentos.templates}")
	private String rutaDocumentosTemplates;
	@Value("${ruta.documentos.generados}")
	private String rutaDocumentosGenerados;
	@Value("${documento.email.to}")
	private String documentoEmailTo;
	
	@Autowired
	PedidoDao pedidoDao;
	@Autowired
	ContratoDao contratoDao;
	@Autowired
	UsuarioDao usuarioDao;
	@Autowired
	PedidoService pedidoService;
	@Autowired
	ContratoService contratoService;
	@Autowired
	ConstanteService constanteService;
	@Autowired
	InversionService inversionService;
	@Autowired
	MailService mailService;
	@Autowired
	GenericService genericService;
	
	@Autowired
	ContratoBusiness contratoBusiness;
	
	@Autowired
	GenericDao genericDao;

	@Override
	public ResultadoBean registrarNuevoPedido(String nroContrato, String usuarioSAFId) throws Exception{
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		contratoService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		
		ResultadoBean resultado = new ResultadoBean();
		// Obtener la situacion del contrato
		ContratoSAF contratoSAF = contratoDao.obtenerContratoSAF(nroContrato);
		
		// Si no esta adjudicado
		if(!Util.esSituacionAdjudicado(contratoSAF.getSituacionContrato())){
			// Actualizar estado del contrato a no adjudicado en Caspio
			contratoService.actualizarSituacionContratoCaspio(nroContrato, contratoSAF.getSituacionContrato(), null, null);
			
			// Enviar mensaje contrato no adjudicado
			resultado.setMensajeError("El contrato no se encuentra adjudicado");
		}else{		
			// Crear pedido y contrato-pedido en SAF
			resultado = pedidoDao.crearPedidoSAF(nroContrato, usuarioSAFId);

			if(resultado.getMensajeError().equals("")){
				String nroPedido = String.valueOf(resultado.getResultado());
				// Obtener ContratoCaspio
				Contrato contratoCaspio = contratoService.obtenerContratoCaspio(nroContrato);
				String asociadoId = String.valueOf(contratoCaspio.getAsociadoId().intValue());
				String contratoId = String.valueOf(contratoCaspio.getContratoId().intValue());
				
				// Crear pedido en Caspio
				pedidoService.crearPedidoCaspio(nroPedido, asociadoId);
				
				// Obtener PedidoCaspio
				Pedido pedidoCaspio = pedidoService.obtenerPedidoCaspio(nroPedido);
				String pedidoId = String.valueOf(pedidoCaspio.getPedidoId().intValue());
				
				// Crear contrato-pedido en Caspio
				pedidoService.agregarContratoPedidoCaspio(pedidoId, contratoId);
				
				// Actualizar estado asociacion del contrato 
				contratoService.actualizarAsociacionContrato(nroContrato, Constantes.Contrato.ESTADO_ASOCIADO);
				
				Map<String,Object> params = new HashMap<String, Object>();
				params.put("numeroContrato", nroContrato);
				Map<String,Object> result = contratoDao.obtenerContratosEvaluacionCrediticia(params);
				List<Map<String,Object>> contratosEvaluacionCrediticia = (List<Map<String,Object>>)result.get("#result-set-1");
				if(contratosEvaluacionCrediticia!=null && contratosEvaluacionCrediticia.size()>0){
					contratoBusiness.sincronizarContratosyAsociadosSafACaspio();
					for(Map<String,Object> contrato:contratosEvaluacionCrediticia){
						String nroCon = contrato.get("ContratoNumero").toString();
						agregarContratoPedido(pedidoId, nroCon, usuarioSAFId);
					}
					
				}
				resultado.setResultado(pedidoId);
			}
		}
		
		return resultado;
	}
	
	public ResultadoBean eliminarPedido(String pedidoCaspioId, String nroPedido, String usuarioSAFId) throws Exception{
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		contratoService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		inversionService.setTokenCaspio(tokenCaspio);
		
		ResultadoBean resultado = new ResultadoBean();
		// Caspio - buscar inversiones del pedido
		boolean inversionesConfirmadas = false;
		List<Inversion> listaInversiones = pedidoService.obtenerInversionesxPedidoCaspio(pedidoCaspioId);
		if(listaInversiones!=null && listaInversiones.size()>0){
			for(Inversion inversion : listaInversiones){
				if(inversion.getConfirmado()!=null && inversion.getConfirmado().equals(Constantes.Inversion.SITUACION_CONFIRMADO)){
					inversionesConfirmadas = true;
					break;
				}
			}
		}
		
		if(inversionesConfirmadas==true){
			resultado.setMensajeError(Constantes.Service.RESULTADO_INVERSIONES_CONFIRMADAS);
		}else{
			// SAF - eliminar el pedido y desasociarlo
			resultado = pedidoDao.eliminarPedidoSAF(nroPedido, usuarioSAFId);
			
			if(resultado.getMensajeError().equals("")){
				// Caspio - cambiar estado pedido a anulado
				pedidoService.actualizarEstadoPedidoCaspio(nroPedido, Constantes.Pedido.ESTADO_ANULADO);
							
				// Caspio - desasociar los contratos del pedido
				List<Contrato> listaContratos = pedidoService.obtenerContratosxPedidoCaspio(pedidoCaspioId);
				if(listaContratos!=null && listaContratos.size()>0){
					for(Contrato contrato : listaContratos){
						contratoService.actualizarAsociacionContrato(contrato.getNroContrato(), Constantes.Contrato.ESTADO_NO_ASOCIADO);
					}
				}
				
				// Anular las inversiones
				if(listaInversiones!=null && listaInversiones.size()>0){
					for(Inversion inversion : listaInversiones){
						inversionService.actualizarEstadoInversionCaspio(String.valueOf(inversion.getInversionId().intValue()), Constantes.Inversion.ESTADO_ANULADO);
					}
				}
			}
		}
				
		return resultado;
	}
	
	public String agregarContratoPedido(String pedidoCaspioId, String nroContrato, String usuarioSAFId) throws Exception{
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		contratoService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		
		String resultado = "";
		// Caspio - obtener datos pedido
		Pedido pedido = pedidoService.obtenerPedidoCaspioPorId(pedidoCaspioId);
		String nroPedido = pedido.getNroPedido();
		
		// SAF - agregar contrato pedido
		pedidoDao.agregarContratoPedidoSAF(nroPedido, nroContrato, usuarioSAFId);
		
		// Caspio - obtener datos contrato
		Contrato contrato = contratoService.obtenerContratoCaspio(nroContrato);
				
		// Caspio - agregar contrato pedido
		String contratoCaspioId = String.valueOf(contrato.getContratoId().intValue());
		pedidoService.agregarContratoPedidoCaspio(pedidoCaspioId, contratoCaspioId);
		
		// Caspio: Asociar contrato a pedido
		contratoService.actualizarAsociacionContrato(nroContrato, Constantes.Contrato.ESTADO_ASOCIADO);
		
		return resultado;
	}
	
	public String eliminarContratoPedido(String pedidoCaspioId, String nroContrato, String usuarioSAFId) throws Exception{
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		contratoService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		
		String resultado = "";
		boolean permiteEliminar = true;
		
		// Obtener las inversiones del pedido
		List<Inversion> listaInversiones = pedidoService.obtenerInversionesxPedidoCaspio(pedidoCaspioId);
				
		// Verificar si existe alguna inversion confirmada
		if(listaInversiones!=null && listaInversiones.size()>0){
			for(Inversion inversion : listaInversiones){
				if(inversion.getConfirmado().equals(Constantes.Inversion.SITUACION_CONFIRMADO)){
					permiteEliminar = false;
					break;
				}
			}
		}
		
		if(permiteEliminar==false){
			resultado=Constantes.Service.RESULTADO_INVERSIONES_CONFIRMADAS;
		}
		
		if(permiteEliminar){
			// Caspio - obtener datos pedido
			Pedido pedido = pedidoService.obtenerPedidoCaspioPorId(pedidoCaspioId);
			String nroPedido = pedido.getNroPedido();
			
			// SAF - eliminar contrato pedido
			pedidoDao.eliminarContratoPedidoSAF(nroPedido, nroContrato, usuarioSAFId);
			
			// Caspio - Obtener datos contrato
			Contrato contrato = contratoService.obtenerContratoCaspio(nroContrato);
					
			// Caspio - eliminar contrato pedido
			String contratoCaspioId = String.valueOf(contrato.getContratoId().intValue());
			pedidoService.eliminarContratoPedidoCaspio(pedidoCaspioId, contratoCaspioId);
			
			// Caspio: Desasociar contrato a pedido
			contratoService.actualizarAsociacionContrato(nroContrato, Constantes.Contrato.ESTADO_NO_ASOCIADO);
		}
		
		return resultado;
	}
	
	
	@Override
	public String generarOrdenIrrevocablePedido(String pedidoId, String usuarioSAFId, String pedidoNumero) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		constanteService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);		
		
		String resultado = "";
		
		LOG.info("generarOrdenIrrevocablePedido");
		String nombreDocumento="Orden-irrevocable-inversion-inmobiliaria-Generado-"+pedidoId+".docx";	
		String gestionInmobiliaria="gestion_inversion_inmobiliaria_desembolso";
		 
		DocumentoUtil documentoUtil=new DocumentoUtil();	
		XWPFDocument doc = documentoUtil.openDocument(rutaDocumentosTemplates+"/"+gestionInmobiliaria+"/"+"Orden-irrevocable-de-uso-de-certificado-para-inversion-inmobiliaria.docx");
				 
	     if (doc != null) {
	    	// Obtener inversiones confirmadas del pedido
			 List<Inversion> listaInversionesCaspio = pedidoService.obtenerInversionesxPedidoCaspio(pedidoId);
			 List<Inversion> listaInversiones = new ArrayList<Inversion>();
			 if(listaInversionesCaspio!=null && listaInversionesCaspio.size()>0){
				 for(Inversion inversion : listaInversionesCaspio){
					 if(!Constantes.Inversion.SITUACION_CONFIRMADO.equals(inversion.getConfirmado())){
						 listaInversiones.add(inversion);
					 }
				 }
			 }
			 
			 boolean datosCompletos = true;
			 if(listaInversiones!=null && listaInversiones.size()>0){
				 for(Inversion inversion : listaInversiones){
					// Obtener lista de documentos
					List<DocumentoRequisito> listaDocumentos = obtenerDocumentosTipoInversion(inversion.getTipoInversion(), inversion.getPropietarioTipoDocId());
					// Validar datos antes de confirmar
					resultado = DocumentoUtil.validarConfirmarInversion(listaDocumentos, inversion);
					if(!resultado.equals("")){
						datosCompletos = false;
						break;
					}					
				 }
			 }else{
				 datosCompletos=false;
				 resultado=Constantes.Service.RESULTADO_NO_INVERSIONES_CONFIRMADAS;
			 }
			 System.out.println("VALIDACION DATOS COMPLETOS::: "+datosCompletos+" - "+resultado);
			 
			 if(datosCompletos){
		    	 // Obtener contratos del pedido
				 List<Contrato> listaContratos= pedidoService.obtenerContratosxPedidoCaspio(pedidoId);
				 
				 // Obtener nro contrato del asociado
				 String nroContrato = listaContratos.get(0).getNroContrato();
				 System.out.println("nroContrato: "+nroContrato);
				 				 
				 // Obtener datos del o los asociados
				 List<Asociado> listaAsociados=contratoDao.obtenerAsociadosxContratoSAF(nroContrato);
				 			 
				 // Obtener inversiones del pedido
				 List<Constante> listaDocuIdentidad = constanteService.obtenerListaDocumentosIdentidad();
		    	 
				 // Obtener sumatorias totales
				 double sumaContratos = Util.getSumaContratosxPedido(listaContratos);
				 double sumaInversiones = Util.getSumaInversionesxPedido(listaInversiones);
				 double diferenciaPrecio = getSumaDiferenciaPrecioxPedido(listaContratos);
				 double saldo = sumaInversiones+diferenciaPrecio-sumaContratos;				 
				 System.out.println("diferenciaPrecio2: "+diferenciaPrecio);
				 
				 // Obtener los parametros a enviar al documento
		    	 List<Parametro> listaParametros=DocumentoUtil.getParametrosOrdenIrrevocable(sumaContratos,sumaInversiones,saldo,pedidoId,pedidoNumero,diferenciaPrecio);
		    	 
		    	 // Reemplazar los datos en la plantilla
		         doc = DocumentoUtil.replaceTextOrdenIrrevocable(doc,listaParametros,listaDocuIdentidad, listaAsociados,listaContratos,listaInversiones);
		         StringBuilder sb=new StringBuilder();
		         
		         // Grabar el documento generado
		         documentoUtil.saveDocument(doc, sb.append(rutaDocumentosGenerados).append("/").append(nombreDocumento).toString());		         
		         System.out.println("SE GENERO EL DOCUMENTO");
		         
		         // Obtener correo para enviar documento
		         String emailTo = documentoEmailTo;
		         Usuario usuario = usuarioDao.obtenerCorreoUsuarioCelula(usuarioSAFId);
		         
		         LOG.info("Enviar a:"+usuario.getCelulaCorreo());
		         
		         if(!Util.esVacio(usuario.getCelulaCorreo())){
		        	 emailTo = usuario.getCelulaCorreo();
		         }else if(!Util.esVacio(usuario.getEmpleadoCorreo())){
		        	 emailTo = usuario.getEmpleadoCorreo();
		         }	 
		         LOG.info("emailTo:"+emailTo);
		         // Enviar orden irrevocable a correo
		         String asunto = "Orden Irrevocable - "+listaAsociados.get(0).getNombreCompleto();
		         String textoEmail = "Se adjunta la orden irrevocable correspondiente";
		         
		         
		         EmailBean emailBean=new EmailBean();
		         emailBean.setEmailFrom("saf@pandero.com.pe");
		         emailBean.setEmailTo(emailTo);
		         emailBean.setSubject(asunto);
		         emailBean.setDocumento(nombreDocumento);
		         emailBean.setTextoEmail(textoEmail);
		         emailBean.setFormatHtml(false);
		         emailBean.setEnviarArchivo(true);
		         mailService.sendMail(emailBean);
//		         mailService.sendMail("desarrollo@pandero.com.pe", emailTo, asunto, nombreDocumento, textoEmail);

			 }
	     }
		
	     return resultado;
	}
		
	private List<DocumentoRequisito> obtenerDocumentosTipoInversion(String tipoInversion, String tipoDocId) throws Exception{
		List<DocumentoRequisito> listaDocumentos = new ArrayList<DocumentoRequisito>();
		// Obtener lista de documentos
		List<DocumentoRequisito> listaDocumentosTotal = constanteService.obtenerListaDocumentosPorTipoInversion(tipoInversion);		
		
		// Obtener la lista de documentos por tipo persona
		if(Constantes.TipoInversion.ADQUISICION_COD.equals(tipoInversion)){
			for(DocumentoRequisito documentoRequisito : listaDocumentosTotal){
				String propietarioTipoPersona = Util.getTipoPersonaPorDocIden(tipoDocId);
				System.out.println("propietarioTipoPersona:: "+propietarioTipoPersona);
				if(documentoRequisito.getTipoPersona().equals(propietarioTipoPersona)){
					listaDocumentos.add(documentoRequisito);
				}
			}
		}else{
			listaDocumentos = listaDocumentosTotal;
		}
		System.out.println("listaDocumentos:: "+listaDocumentos.size());
		return listaDocumentos;
	}
	
	private double getSumaDiferenciaPrecioxPedido(List<Contrato> listaContratos){
		double sumaDiferenciaPrecio=0.00;
		for(Contrato contrato : listaContratos){
			Double dblDifPrecioSaf=0.00;
			try {
				dblDifPrecioSaf = contratoDao.obtenerDiferenciaPrecioPorContrato(contrato.getNroContrato());
			} catch (Exception e) {
				LOG.error("###Error al obtener la diferencia de precio en al suma:",e);
			}
			sumaDiferenciaPrecio = sumaDiferenciaPrecio + dblDifPrecioSaf;		
		}
		return sumaDiferenciaPrecio;
	}
	
	@Override
	public Map<String,Object> contratoPedidoEnEvaluacionCrediticia(Map<String,Object> params) throws Exception{
		Map<String,Object> result = new HashMap<String,Object>();
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		genericService.setTokenCaspio(tokenCaspio);
		List<Map<String,Object>> pedidoContratos = genericService.obtenerTablaCaspio(genericService.tablepedidoContrato, JsonUtil.toJson(params));
		if(pedidoContratos!=null && pedidoContratos.size()>0){
			params = new HashMap<String,Object>();
			params.put("idContrato", pedidoContratos.get(0).get("ContratoId"));
			params = genericDao.executeProcedure(params, "USP_FOC_VerificarContratoEvaluacionCrediticia");
			result.put("resultado", params.get("resultado"));
			return result;
		}
		result.put("resultado", "0");
		return result;
	}
	
}
