package com.pandero.ws.business.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pandero.ws.bean.ComprobanteCaspio;
import com.pandero.ws.bean.Constante;
import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.ContratoSAF;
import com.pandero.ws.bean.DocumentoRequisito;
import com.pandero.ws.bean.EmailBean;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.InversionRequisito;
import com.pandero.ws.bean.LiquidacionSAF;
import com.pandero.ws.bean.ObservacionInversion;
import com.pandero.ws.bean.Parametro;
import com.pandero.ws.bean.Pedido;
import com.pandero.ws.bean.PedidoInversionSAF;
import com.pandero.ws.bean.PersonaSAF;
import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.bean.Usuario;
import com.pandero.ws.business.InversionBusiness;
import com.pandero.ws.dao.ContratoDao;
import com.pandero.ws.dao.LiquidacionDao;
import com.pandero.ws.dao.PedidoDao;
import com.pandero.ws.dao.PersonaDao;
import com.pandero.ws.dao.UsuarioDao;
import com.pandero.ws.service.ConstanteService;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.service.MailService;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.DocumentoUtil;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.Util;
import com.pandero.ws.util.UtilEnum;

@Component
public class InversionBusinessImpl implements InversionBusiness{

	private static final Logger LOG = LoggerFactory.getLogger(PedidoBusinessImpl.class);

	@Autowired
	InversionService inversionService;
	@Autowired
	ConstanteService constanteService;
	@Autowired
	PedidoService pedidoService;
	@Autowired
	UsuarioDao usuarioDao;
	@Autowired
	PersonaDao personaDao;
	@Autowired
	ContratoDao contratoDao;
	@Autowired
	PedidoDao pedidoDao;
	@Autowired
	MailService mailService;
	@Autowired
	LiquidacionDao liquidacionDao;
	
	@Value("${ruta.documentos.templates}")
	private String rutaDocumentosTemplates;
	@Value("${ruta.documentos.generados}")
	private String rutaDocumentosGenerados;
	@Value("${desarrollo.pandero.email}")
	private String emailDesarrolloPandero;
	@Value("${documento.email.to}")
	private String documentoEmailTo;
	
	
	@Override
	public String confirmarInversion(String inversionId, String situacionConfirmado, String usuarioId) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		constanteService.setTokenCaspio(tokenCaspio);
		
		String resultado = "";
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
		
		// Confirmar Inversion
		if(Constantes.Inversion.SITUACION_CONFIRMADO.equals(situacionConfirmado)){
			System.out.println("inversion:: "+inversion);
			System.out.println("inversion.getTipoInversion(): "+inversion.getTipoInversion()+" - inversion.getPropietarioTipoDocId(): "+inversion.getPropietarioTipoDocId());
			// Obtener lista de documentos
			List<DocumentoRequisito> listaDocumentos = obtenerDocumentosTipoInversion(inversion.getTipoInversion(), inversion.getPropietarioTipoDocId());
			
			// Validar datos antes de confirmar
			String resultadoValidacion = DocumentoUtil.validarConfirmarInversion(listaDocumentos, inversion);
			if(!Util.esVacio(resultadoValidacion)){
				resultado=resultadoValidacion;
			}else{
				// Validar verificacion de requisitos
				boolean verificacionRequisitos = true;
				 List<InversionRequisito> listaRequisitos = inversionService.obtenerRequisitosPorInversion(inversionId);
				 if(listaRequisitos!=null && listaRequisitos.size()>0){
					for(InversionRequisito requisito : listaRequisitos){
						if(!Constantes.DocumentoRequisito.ESTADO_REQUISITO_CONFORME.equals(requisito.getEstadoRequisito())){
							verificacionRequisitos=false;
						}
					}
				 }else{
					 verificacionRequisitos=false;
				 }
				 if(verificacionRequisitos==false) resultado=Constantes.Service.RESULTADO_PENDIENTE_REQUISITOS;
			}
			
			// Continuar si paso las validaciones
			if(Util.esVacio(resultado)){
				// Obtener datos del pedido	
				Pedido pedido = pedidoService.obtenerPedidoCaspioPorId(String.valueOf(inversion.getPedidoId()));
				
				// Obtener proveedor SAF
				PersonaSAF proveedor = obtenerProveedor(inversion, pedido.getAsociadoId());
				
				// Registrar pedido-inversion SAF
				PedidoInversionSAF pedidoInversionSAF = new PedidoInversionSAF();
				pedidoInversionSAF.setNroPedido(pedido.getNroPedido());
				pedidoInversionSAF.setProveedorID(String.valueOf(proveedor.getProveedorID().intValue()));
				pedidoInversionSAF.setPedidoInversionNumero(inversion.getNroInversion());
				pedidoInversionSAF.setPedidoTipoInversionID(Util.obtenerTipoInversionID(inversion.getTipoInversion()));
				pedidoInversionSAF.setConfirmarID("1");
				pedidoInversionSAF.setUsuarioIDCreacion(usuarioId);				
				pedidoDao.agregarPedidoInversionSAF(pedidoInversionSAF);								
			}
		}
		
		// Desconfirmar Inversion
		if(Constantes.Inversion.SITUACION_NO_CONFIRMADO.equals(situacionConfirmado)){
			// Validar verificacion de requisitos
			boolean verificacionRequisitos = true;
			List<InversionRequisito> listaRequisitos = inversionService.obtenerRequisitosPorInversion(inversionId);
			if(listaRequisitos!=null && listaRequisitos.size()>0){
				for(InversionRequisito requisito : listaRequisitos){
					if(!Constantes.DocumentoRequisito.ESTADO_REQUISITO_PENDIENTE.equals(requisito.getEstadoRequisito())){
						verificacionRequisitos=false;
					}
				}
			}
			if(verificacionRequisitos==false) resultado=Constantes.Service.RESULTADO_TIENE_REQUISITOS;
			
			// Eliminar pedido-inversion en SAF
			if(Util.esVacio(resultado)){
				pedidoDao.eliminarPedidoInversionSAF(inversion.getNroInversion(), usuarioId);
			}
		}
		
		// Actualizar situacion confirmacion inversion en Caspio
		if(Util.esVacio(resultado)){		
			if(Constantes.Inversion.SITUACION_CONFIRMADO.equals(situacionConfirmado)){
				// Verificar si existe excedente certificado o diferencia de precio
				resultado=validarDiferenciaPrecioExcedenteEnInversion(inversionId, String.valueOf(inversion.getPedidoId()));
			}
			inversionService.actualizarSituacionConfirmadoInversionCaspio(inversionId, situacionConfirmado);
		}
							
		return resultado;
	}
	
	private PersonaSAF obtenerProveedor(Inversion inversion, Integer asociadoId) throws Exception{		
		Integer proveedorId = null, personaId = null;
		String tipoDocuProv="", nroDocuProv="", tipoProveedor="";
		// Obtener tipo de proveedor
		if(Constantes.TipoInversion.CANCELACION_COD.equals(inversion.getTipoInversion())){
			System.out.println("PROVEEDOR 1");
			tipoProveedor = Constantes.Proveedor.TIPO_ENTIDAD_FINANCIERA_COD;
			proveedorId = inversion.getEntidadFinancieraId();					
		}else if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())){
			if(inversion.getServicioConstructora()){
				System.out.println("PROVEEDOR 2");
				tipoProveedor = Constantes.Proveedor.TIPO_CONSTRUCTORA_COD;
				tipoDocuProv = inversion.getConstructoraTipoDoc();
				nroDocuProv = inversion.getConstructoraNroDoc();
			}else{
				System.out.println("PROVEEDOR 3");
				tipoProveedor = Constantes.Proveedor.TIPO_PERSONA_COD;
				personaId = asociadoId;
			}
		}else{
			System.out.println("PROVEEDOR 4");
			tipoProveedor = Constantes.Proveedor.TIPO_PERSONA_COD;
			tipoDocuProv = inversion.getPropietarioTipoDocId();
			nroDocuProv = inversion.getPropietarioNroDoc();
		}
		// Obtener tipos de documento
		List<Constante> listaTiposDocuIden = constanteService.obtenerListaDocumentosIdentidad();

		// Obtener proveedor SAF	
		String tipoDocuIdenSAF = Util.obtenerTipoDocuIdenSAFPorCaspioId(listaTiposDocuIden, tipoDocuProv);
		System.out.println("TIPO_DOCU_CASPIO:: "+tipoDocuProv);
		System.out.println("TIPO_DOCU_SAF:: "+tipoDocuIdenSAF);
		System.out.println("TIPO_PROVEEDOR: "+tipoProveedor);
		PersonaSAF proveedor = personaDao.obtenerProveedorSAF(proveedorId, tipoProveedor, personaId, tipoDocuIdenSAF, nroDocuProv);
		if(proveedor==null){
			PersonaSAF proveedorSAF = new PersonaSAF();
			proveedorSAF.setTipoProveedor(tipoProveedor);
			proveedorSAF.setPersonaID(personaId);
			proveedorSAF.setTipoDocumentoID(tipoDocuIdenSAF);
			proveedorSAF.setPersonaCodigoDocumento(nroDocuProv);
			if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())
					&& inversion.getServicioConstructora()){
				proveedorSAF.setNombre(inversion.getConstructoraNombres());
				proveedorSAF.setApellidoPaterno(inversion.getConstructoraApePaterno());
				proveedorSAF.setApellidoMaterno(inversion.getConstructoraApeMaterno());
				proveedorSAF.setRazonSocial(inversion.getConstructoraRazonSocial());
			}else if(Constantes.TipoInversion.ADQUISICION_COD.equals(inversion.getTipoInversion())){
				proveedorSAF.setNombre(inversion.getPropietarioNombres());
				proveedorSAF.setApellidoPaterno(inversion.getPropietarioApePaterno());
				proveedorSAF.setApellidoMaterno(inversion.getPropietarioApeMaterno());
				proveedorSAF.setRazonSocial(inversion.getPropietarioRazonSocial());
			}					
			proveedor = personaDao.registrarProveedorSAF(proveedorSAF);
		}
		
		return proveedor;
	}
	
	private String validarDiferenciaPrecioExcedenteEnInversion(String inversionId, String pedidoId) throws Exception{
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		pedidoService.setTokenCaspio(tokenCaspio);
		
		String resultado = "";
		// Obtener inversiones del pedido
		List<Inversion> listaInversiones = pedidoService.obtenerInversionesxPedidoCaspio(pedidoId);
		double montoTotalCertificados = 0, montoCertificadoUsado = 0, montoInversion = 0;		
		if(listaInversiones!=null && listaInversiones.size()>0){
			for(Inversion inversion : listaInversiones){
				System.out.println("inversion.getConfirmado():: "+inversion.getConfirmado());
				if(Constantes.Inversion.SITUACION_CONFIRMADO.equals(inversion.getConfirmado())){
					montoCertificadoUsado += inversion.getImporteInversion();
				}
				if(String.valueOf(inversion.getInversionId().intValue()).equals(inversionId)){
					montoInversion = inversion.getImporteInversion();
				}
			}
		}		
		
		// Obtener contratos del pedido
		List<Contrato> listaContratos= pedidoService.obtenerContratosxPedidoCaspio(pedidoId);
		if(listaContratos!=null && listaContratos.size()>0){
			for(Contrato contrato : listaContratos){
				montoTotalCertificados += contrato.getMontoCertificado();
			}
		}
		
		double montoCertificadoDisponible = montoTotalCertificados-montoCertificadoUsado;		
		double montoInversionRequerido = montoCertificadoDisponible-montoInversion;
		System.out.println("montoTotalCertificados:: "+montoTotalCertificados+" - montoCertificadoUsado:: "+montoCertificadoUsado);
		System.out.println("montoInversionRequerido:: "+montoInversionRequerido+" - montoInversion:: "+montoInversion);
		if(montoInversionRequerido>0){
			resultado=Constantes.Inversion.EXCEDENTE_CERTIFICADO;
		}else if(montoInversionRequerido<0){
			resultado=Constantes.Inversion.DIFERENCIA_PRECIO;
		}
		return resultado;
	}
	
	@Override
	public String eliminarInversion(String inversionId) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		inversionService.actualizarEstadoInversionCaspio(inversionId, Constantes.Inversion.ESTADO_ANULADO);
		return null;
	}

	@Override
	public String registrarInversionRequisitos(String inversionId) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		constanteService.setTokenCaspio(tokenCaspio);
		inversionService.setTokenCaspio(tokenCaspio);
		
		// Obtener requisitos de la inversion
		List<InversionRequisito> listaInversionRequisitos = inversionService.obtenerRequisitosPorInversion(inversionId);
		
		if(listaInversionRequisitos==null){
			// Obtener datos de la inversion
			Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
			// Obtener lista requisitos
			List<DocumentoRequisito> listaRequisitos = obtenerRequisitosTipoInversion(inversion.getTipoInversion(), inversion.getPropietarioTipoDocId());
			if(listaRequisitos!=null && listaRequisitos.size()>0){
				for(DocumentoRequisito requisito : listaRequisitos){
					// Crear el requisito de la inversion
					inversionService.crearRequisitoInversion(inversionId, String.valueOf(requisito.getRequisitoId().intValue()));
				}
			}
		}		
		return null;
	}
	
	private List<DocumentoRequisito> obtenerRequisitosTipoInversion(String tipoInversion, String tipoDocId) throws Exception{
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		constanteService.setTokenCaspio(tokenCaspio);
		
		List<DocumentoRequisito> listaRequisitos = new ArrayList<DocumentoRequisito>();
		// Obtener lista de documentos
		List<DocumentoRequisito> listaRequisitosTotal = constanteService.obtenerListaRequisitosPorTipoInversion(tipoInversion);		
		// Obtener la lista de documentos por tipo persona
		if(Constantes.TipoInversion.ADQUISICION_COD.equals(tipoInversion)){
			for(DocumentoRequisito documentoRequisito : listaRequisitosTotal){
				String propietarioTipoPersona = Util.getTipoPersonaPorDocIden(tipoDocId);
				if(documentoRequisito.getTipoPersona().equals(propietarioTipoPersona)){
					listaRequisitos.add(documentoRequisito);
				}
			}
		}else{
			listaRequisitos = listaRequisitosTotal;
		}
		return listaRequisitos;
	}
	
	private List<DocumentoRequisito> obtenerDocumentosTipoInversion(String tipoInversion, String tipoDocId) throws Exception{
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		constanteService.setTokenCaspio(tokenCaspio);
		
		List<DocumentoRequisito> listaDocumentos = new ArrayList<DocumentoRequisito>();
		// Obtener lista de documentos
		List<DocumentoRequisito> listaDocumentosTotal = constanteService.obtenerListaDocumentosPorTipoInversion(tipoInversion);		
		// Obtener la lista de documentos por tipo persona
		if(Constantes.TipoInversion.ADQUISICION_COD.equals(tipoInversion)){
			for(DocumentoRequisito documentoRequisito : listaDocumentosTotal){
				String propietarioTipoPersona = Util.getTipoPersonaPorDocIden(tipoDocId);
				if(documentoRequisito.getTipoPersona().equals(propietarioTipoPersona)){
					listaDocumentos.add(documentoRequisito);
				}
			}
		}else{
			listaDocumentos = listaDocumentosTotal;
		}
		return listaDocumentos;
	}	
	
	@Override
	public String anularVerificacion(String inversionId) throws Exception {
		String resultado = "";
		LOG.info("###anularVerificacion inversionId:"+inversionId);
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
				
		// Validar si existe liquidacion
		List<LiquidacionSAF> liquidacionInversion = liquidacionDao.obtenerLiquidacionPorInversionSAF(inversion.getNroInversion());
		
		if(liquidacionInversion!=null && liquidacionInversion.size()>0){
			resultado = Constantes.Service.RESULTADO_EXISTE_LIQUIDACION;
		}else{
			// Anular verificacion
			inversionService.actualizarEstadoInversionRequisitoCaspio(inversionId, Constantes.DocumentoRequisito.ESTADO_REQUISITO_PENDIENTE);
		}
		return resultado;
	}

	@Override
	public String generarCartaObservacion(String inversionId, String usuarioSAFId) throws Exception {
		LOG.info("###generarCartaObservacion inversionId:"+inversionId);
		
		String msg="";
		boolean verificacionExitosa=false;
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		
		if(!StringUtils.isEmpty(inversionId)){
			 List<InversionRequisito> list= inversionService.obtenerRequisitosPorInversion(inversionId);
			 if(null!=list){
				 List<ObservacionInversion> listObs=new ArrayList<>();
				 for(InversionRequisito irc:list){
					 LOG.info("##irc.getEstadoRequisito():"+irc.getEstadoRequisito());
					 if(Constantes.DocumentoRequisito.ESTADO_REQUISITO_NO_CONFORME.equals(irc.getEstadoRequisito()!=null?irc.getEstadoRequisito():"")){
						 LOG.info("##Por agregar a la lista para el doc obs:"+irc.getObservacion());
						 ObservacionInversion obs=new ObservacionInversion();
						 obs.setObservacion(irc.getObservacion());
						 listObs.add(obs);
					 }
				 }
				 
				 if(listObs.size()==0){
					 verificacionExitosa = true;
					 //Actualizar estado de la inversion como CONFORME
					 
					 msg="No es posible generar la carta de validación por que no existen requisitos con estado NO CONFORME"; 
//					 return msg;
				 }
				 
				String docxGenerado="Carta-de-validacion-de-datos-inversion-inmobiliaria-generado-"+inversionId+".docx";
				String pdfConvertido="Carta-de-validacion-de-datos-inversion-inmobiliaria-generado-"+inversionId+".pdf";
				String gestionInmobiliaria="gestion_inversion_inmobiliaria_desembolso";
				
				/*Generar documento*/
				DocumentoUtil documentoUtil=new DocumentoUtil();
				XWPFDocument docx = documentoUtil.openDocument(rutaDocumentosTemplates+"/"+gestionInmobiliaria+"/"+"Carta-de-validacion-de-datos-inversion-inmobiliaria-TEMPLATE.docx");
				
				/*Obtener datos de la inversion*/
				LOG.info("##Por obtener Inversion Caspio, inversionId:"+inversionId);
				Inversion pic= inversionService.obtenerInversionCaspioPorId(inversionId);
				
				LOG.info("##Obtener Pedido Caspio Por Id, pic.getPedidoId():"+pic.getPedidoId());
				Pedido pedidoCaspio= pedidoService.obtenerPedidoCaspioPorId(String.valueOf(pic.getPedidoId()));
				LOG.info("##Obtener Persona SAF, pedidoCaspio.getAsociadoId():"+pedidoCaspio.getAsociadoId());
				PersonaSAF personaSAF= personaDao.obtenerPersonaSAF(String.valueOf(pedidoCaspio.getAsociadoId()));
				
				LOG.info("##Obtener ContratoxPedido, pedidoCaspio.getPedidoId():"+pedidoCaspio.getPedidoId());
				List<Contrato> listContratos = pedidoService.obtenerContratosxPedidoCaspio(String.valueOf(pedidoCaspio.getPedidoId()));
				//obtener primero contrato para buscar al funcionario de servicios
				Contrato contrato1 = listContratos.get(0);
				LOG.info("##Obtener Contrato SAF, contrato1.getNroContrato():"+contrato1.getNroContrato());
				ContratoSAF contratoSAF= contratoDao.obtenerContratoSAF(contrato1.getNroContrato());
				
				List<Parametro> params=new ArrayList<>();
				Parametro parametro1=new Parametro("$FechaDocumento", Util.getFechaFormateada(Util.getFechaActual(), Constantes.FORMATO_CARTA_VALIDACION_INVERSION) );//fecha
				Parametro parametro2=new Parametro("$NombreCompleto", personaSAF.getNombreCompleto());//asociado
				Parametro parametro3=new Parametro("$TipoInversion", StringUtils.isEmpty(pic.getTipoInversion())?"":pic.getTipoInversion() );//tipo inversion
				Parametro parametro4=new Parametro("$TipoInmueble", StringUtils.isEmpty(pic.getTipoInmuebleNom())?"":pic.getTipoInmuebleNom());//tipo inmbueble
				Parametro parametro5=new Parametro("$Importe", Util.getMontoFormateado(pic.getImporteInversion()));//Importe
				Parametro parametro7=new Parametro("$FuncionarioServYVentas", contratoSAF.getFuncionarioServicioyVentas());//Funcionario de servicios y ventas
				
				params.add(parametro1);
				params.add(parametro2);
				params.add(parametro3);
				params.add(parametro4);
				params.add(parametro5);
				params.add(parametro7);
				
				
				docx=DocumentoUtil.replaceTextCartaValidacion(docx,params,listObs);
				StringBuilder sb=new StringBuilder();
				documentoUtil.saveDocument(docx, sb.append(rutaDocumentosGenerados).append("/").append(docxGenerado).toString());
				
				/*Convertir a pdf*/
				sb=new StringBuilder();
				String strRutaGenerados=sb.append(rutaDocumentosGenerados).append("/").toString();
				DocumentoUtil.convertDocxToPdf(
						strRutaGenerados+docxGenerado,
						strRutaGenerados+pdfConvertido);
				
				
				/*Enviar correo con archivo adjunto*/
				LOG.info("SE GENERO LA CARTA DE OBSERVACION");
		        String asunto = "Resultado de verificación de inversión inmobiliaria - "+personaSAF.getNombreCompleto();
		        String speech = "";
		        boolean enviarArchivo=true;
		        String strNroContratos = Util.getContratosFromList(listContratos);
		        
		        if(verificacionExitosa){
		        	enviarArchivo=false;
		        	speech = DocumentoUtil.getHtmlCartaValidacionConforme(
		        			strNroContratos,
		        			personaSAF.getNombreCompleto(), 
		        			StringUtils.isEmpty(pic.getTipoInmuebleNom())?"":pic.getTipoInmuebleNom(), 
		        			StringUtils.isEmpty(pic.getGravamen())?"":pic.getGravamen(), 
		        			StringUtils.isEmpty(pic.getPartidaRegistral())?"":pic.getPartidaRegistral(), 
		        			Util.getMontoFormateado(pic.getImporteInversion()), 
		        			Util.getMontoFormateado(pic.getAreaTotal()), 
		        			StringUtils.isEmpty(pic.getTipoInversion())?"":pic.getTipoInversion(),
		        			pic.getNroInversion());
		        	
		        }else{
		        	speech = DocumentoUtil.getHtmlCartaValidacionNoConforme(
		        			strNroContratos,
		        			personaSAF.getNombreCompleto(), 
		        			StringUtils.isEmpty(pic.getTipoInmuebleNom())?"":pic.getTipoInmuebleNom(), 
		        			StringUtils.isEmpty(pic.getGravamen())?"":pic.getGravamen(), 
		        			StringUtils.isEmpty(pic.getPartidaRegistral())?"":pic.getPartidaRegistral(), 
		        			Util.getMontoFormateado(pic.getImporteInversion()), 
		        			Util.getMontoFormateado(pic.getAreaTotal()), 
		        			StringUtils.isEmpty(pic.getTipoInversion())?"":pic.getTipoInversion(),
		        			pic.getNroInversion(),
		        			listObs);
		        }
		        
		        Usuario usuario = usuarioDao.obtenerCorreoUsuarioCelula(usuarioSAFId);
		        LOG.info("getCelulaCorreo:: "+usuario.getCelulaCorreo()+" - getEmpleadoCorreo: "+usuario.getEmpleadoCorreo());
		         
		         String emailTo = documentoEmailTo;
		         if(!Util.esVacio(usuario.getCelulaCorreo())){
		        	 emailTo = usuario.getCelulaCorreo();
		         }else if(!Util.esVacio(usuario.getEmpleadoCorreo())){
		        	 emailTo = usuario.getEmpleadoCorreo();
		         }

		         LOG.info("##emailTo: "+emailTo);
		         LOG.info("##Enviar por correo archivo PDF: "+strRutaGenerados+pdfConvertido);
		         LOG.info("##enviarArchivo: "+enviarArchivo);
		         
		         EmailBean emailBean=new EmailBean();
		         emailBean.setEmailFrom(emailDesarrolloPandero);
		         emailBean.setEmailTo(emailTo);
		         emailBean.setSubject(asunto);
		         emailBean.setDocumento(pdfConvertido);
		         emailBean.setTextoEmail(speech);
		         emailBean.setFormatHtml(true);
		         emailBean.setEnviarArchivo(enviarArchivo);
		         mailService.sendMail(emailBean);
		         
		         if(!verificacionExitosa){
		        	 msg="Operación exitosa. Se ha emitido la carta de validación de datos de la inversión inmobiliaria"; 
		         }
				
			 }
		}
		return msg;
	}

	@Override
	public String actualizarEstadoInversionCaspioPorNro(String nroInversion,
			String estadoInversion) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		inversionService.actualizarEstadoInversionCaspioPorNro(nroInversion, estadoInversion);
		
		return "";

	}
	
	public ResultadoBean getImporteComprobante(String inversionNumero, Integer nroArmada) throws Exception {
		LOG.info("###InversionBusinessImpl.getImporteComprobante inversionNumero:"+inversionNumero+", nroArmada:"+nroArmada);
		
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		Double importe=0.00;
		Inversion inversion = inversionService.obtenerInversionCaspioPorNro(inversionNumero);
		
		
		if(null!=inversion){
			List<ComprobanteCaspio> listComprobante=inversionService.getComprobantes(inversion.getInversionId(), nroArmada);
			if(null!=listComprobante){
				for(ComprobanteCaspio comprobante:listComprobante){
					importe += (null!=comprobante.getImporte()?Double.parseDouble(comprobante.getImporte()):0);
				}
			}
		}
		
		ResultadoBean rb=new ResultadoBean();
		rb.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
		rb.setResultado(importe);
		
		return rb;
	}

	@Override
	public String getURLCancelarComprobante(String inversionId) throws Exception {
	LOG.info("###InversionBusinessImpl.getURLCancelarComprobante inversionId:"+inversionId);
		
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		String locationHref="";
		
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
		if(null!=inversion){
			
			if(inversion.getTipoInversion().equalsIgnoreCase(Constantes.TipoInversion.CONSTRUCCION_COD)){
			    if(!inversion.getServicioConstructora()){//No
			      locationHref = "registrar-factura-proveedor.aspx?NroInversion="+inversion.getNroInversion()+"&InversionId="+inversionId;
			    }else{
			    	locationHref = "registrar-factura-proveedor-sin-armadas.aspx?NroInversion="+inversion.getNroInversion()+"&InversionId="+inversionId;
			    }
			 }else if(inversion.getTipoInversion().equalsIgnoreCase(Constantes.TipoInversion.CANCELACION_COD)){
				 locationHref = "registrar-actualizacion-saldo.aspx?NroInversion="+inversion.getNroInversion()+"&InversionId="+inversionId;
			 }else if(inversion.getTipoInversion().equalsIgnoreCase(Constantes.TipoInversion.ADQUISICION_COD) && inversion.getPropietarioTipoDocId().equals(String.valueOf(UtilEnum.TIPO_DOCUMENTO.RUC.getCodigoCaspio()))){
				 locationHref = "registrar-factura-proveedor-sin-armadas.aspx?NroInversion="+inversion.getNroInversion()+"&InversionId="+inversionId;
			 }else{
				 locationHref = "registrar-factura-proveedor-sin-armadas.aspx?NroInversion="+inversion.getNroInversion()+"&InversionId="+inversionId;
			 }
			
		}	
		return locationHref;
	}

	@Override
	public ResultadoBean enviarCargoContabilidad(String inversionId, String nroArmada, String usuarioId)throws Exception {
		LOG.info("###InversionBusinessImpl.enviarCartaContabilidad inversionId:"+inversionId+", nroArmada:"+nroArmada+",usuarioId:"+usuarioId);
		
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		ResultadoBean resultadoBean  = new ResultadoBean();
			
		Date date=Util.getFechaActual();
		String strFecha = Util.getDateFormat(date, Constantes.FORMATO_DATE_YMD);
		LOG.info("##strFecha:"+strFecha);
		inversionService.actualizarComprobanteEnvioCartaContabilidad(inversionId,nroArmada,strFecha,usuarioId,UtilEnum.ESTADO_COMPROBANTE.ENVIADO.getTexto());
		
		resultadoBean = new ResultadoBean();
		resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
		resultadoBean.setResultado("Se enviaron a carta de contabilidad");
		//return resultadoBean;
			
		return resultadoBean;
	}

	@Override
	public ResultadoBean anularCargoContabilidad(String inversionId, String nroArmada, String usuarioId)
			throws Exception {
		LOG.info("###InversionBusinessImpl.anularCartaContabilidad inversionId:"+inversionId+", nroArmada:"+nroArmada+",usuarioId:"+usuarioId);
		
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		ResultadoBean resultadoBean  = new ResultadoBean();
	
		inversionService.actualizarComprobanteEnvioCartaContabilidad(inversionId,nroArmada,"","","");
		
		resultadoBean = new ResultadoBean();
		resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
		resultadoBean.setResultado("Se anulo el envio a carta de contabilidad");
		
		return resultadoBean;
	}

	@Override
	public String recepcionarCargoContabilidad(String inversionId,
			String nroArmada, String fechaRecepcion, String usuarioRecepcion) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		inversionService.recepcionarCargoContabilidad(inversionId, nroArmada, fechaRecepcion, usuarioRecepcion);
		return "";
	}

	@Override
	public String envioCargoContabilidadActualizSaldo(String inversionId, String usuarioEnvio) throws Exception {
		LOG.info("###envioCargoContabilidadActualizSaldo inversionId:"+inversionId+", usuarioEnvio:"+usuarioEnvio);
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		Date date=Util.getFechaActual();
		String strFecha = Util.getDateFormat(date, Constantes.FORMATO_DATE_YMD);
		LOG.info("##strFecha:"+strFecha);
		inversionService.envioCargoContabilidadActualizSaldo(inversionId, strFecha, usuarioEnvio);
		return "";
	}

	@Override
	public String recepcionarCargoContabilidadActualizSaldo(String inversionId,
			String fechaRecepcion, String usuarioRecepcion) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		inversionService.recepcionarCargoContabilidadActualizSaldo(inversionId, fechaRecepcion, usuarioRecepcion);
		return "";
	}
	
	public ResultadoBean verificarRegistrarFacturas(String inversionId, String nroArmada) throws Exception {
		LOG.info("###InversionBusinessImpl.verificarRegistrarFacturas inversionId:"+inversionId+", nroArmada:"+nroArmada);
		
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		ResultadoBean resultadoBean  = new ResultadoBean();
	
		List<ComprobanteCaspio> listComprobantes = inversionService.getComprobantes(Integer.parseInt(inversionId), Integer.parseInt(nroArmada));
		
		if(null!=listComprobantes){
			
			ComprobanteCaspio comprobanteCaspio = listComprobantes.get(0);
			
			if(comprobanteCaspio.getEstadoContabilidad().equalsIgnoreCase(UtilEnum.ESTADO_COMPROBANTE.ENVIADO.getTexto())){
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
				resultadoBean.setResultado("El desembolso fue enviado a cargo de contabilidad.");
			}else{
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
				resultadoBean.setResultado("Proceder al registro de facturas");
			}
			
		}else{
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
			resultadoBean.setResultado("Proceder al registro de facturas");
		}
		
		
		return resultadoBean;
	}

	@Override
	public ResultadoBean grabarComprobantes(String inversionId, String nroArmada, String usuarioId) throws Exception {
		LOG.info("###InversionBusinessImpl.grabarComprobantes inversionId:"+inversionId+", nroArmada:"+nroArmada+",usuarioId:"+usuarioId);
		
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		ResultadoBean resultadoBean  = new ResultadoBean();
		boolean validarTotalMontoDesembolso=true;
		
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
		if(null!=inversion){
			
			if(Constantes.TipoInversion.CONSTRUCCION_COD.equalsIgnoreCase(inversion.getTipoInversion())){
				if(!inversion.getServicioConstructora()){//Sin constructora
					//No validar
					validarTotalMontoDesembolso=false;
				}
			}
			
			if(validarTotalMontoDesembolso){
				//Validar que la suma de los comprobantes cubra el 100% para todas las inversiones menos SC
				Double totalComprobantes=0.00;
				List<ComprobanteCaspio> listComprobantes=inversionService.getComprobantes(Integer.parseInt(inversionId), Integer.parseInt(nroArmada));
				
				if(null!=listComprobantes){
					for(ComprobanteCaspio comprobante:listComprobantes){
						totalComprobantes+= (comprobante.getImporte()!=null?Double.parseDouble(comprobante.getImporte()):0.00);
					}
					
					LOG.info("## Suma de comprobantes de la inversion "+inversionId+", totalComprobantes :"+totalComprobantes);
					
					if(inversion.getImporteInversion()!=totalComprobantes){
						resultadoBean = new ResultadoBean();
						resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
						resultadoBean.setResultado("El total de comprobantes no cubre el monto desembolsado");
						return resultadoBean;
					}
				}else{
					resultadoBean = new ResultadoBean();
					resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
					resultadoBean.setResultado("El total de comprobantes no cubre el monto desembolsado");
					return resultadoBean;
				}
			}
			
			Date date=Util.getFechaActual();
			String strFecha = Util.getDateFormat(date, Constantes.FORMATO_DATE_YMD);
			LOG.info("##strFecha:"+strFecha);
			inversionService.actualizarComprobanteEnvioCartaContabilidad(inversionId,nroArmada,strFecha,usuarioId,UtilEnum.ESTADO_COMPROBANTE.GUARDADO.getTexto());
			
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
			resultadoBean.setResultado("Se grabo el registro de comprobantes para inversionId:"+inversionId);
			
		}else{
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
			resultadoBean.setResultado("No existe la inversion:"+inversionId);
			return resultadoBean;
		}
		
		return resultadoBean;
	}

	
}
