package com.pandero.ws.business.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.ContratoSAF;
import com.pandero.ws.bean.DocumentoRequisito;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.InversionRequisito;
import com.pandero.ws.bean.ObservacionInversion;
import com.pandero.ws.bean.Parametro;
import com.pandero.ws.bean.Pedido;
import com.pandero.ws.bean.PedidoInversionSAF;
import com.pandero.ws.bean.PersonaSAF;
import com.pandero.ws.bean.Usuario;
import com.pandero.ws.business.InversionBusiness;
import com.pandero.ws.dao.ContratoDao;
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
		Inversion inversion = inversionService.obtenerInversionCaspio(inversionId);
		
		// Si se va a confirmar la inversion
		if(Constantes.Inversion.SITUACION_CONFIRMADO.equals(situacionConfirmado)){
			// Obtener lista de documentos
			List<DocumentoRequisito> listaDocumentos = obtenerDocumentosTipoInversion(inversion.getTipoInversion(), inversion.getPropietarioTipoDocId());
			// Validar datos antes de confirmar
			String resultadoValidacion = DocumentoUtil.validarConfirmarInversion(listaDocumentos, inversion);
			if(!Util.esVacio(resultadoValidacion)){
				resultado=resultadoValidacion;
			}
		}
		
		// Confirmar-desconfirmar inversion
		if(Util.esVacio(resultado)){						
			if(Constantes.Inversion.SITUACION_CONFIRMADO.equals(situacionConfirmado)){
				// Verificar si existe excedente certificado o diferencia de precio
				resultado=validarDiferenciPrecioExcedenteEnInversion(inversionId, String.valueOf(inversion.getPedidoId()));
				
				try{
				// Obtener datos del pedido	
					Pedido pedido = pedidoService.obtenerPedidoCaspioPorId(String.valueOf(inversion.getPedidoId()));
					
				// Obtener tipo de proveedor
				Integer proveedorId = null, personaId = null;
				String tipoDocuProv="", nroDocuProv="", tipoProveedor="";
				if(Constantes.TipoInversion.CANCELACION_COD.equals(inversion.getTipoInversion())){
					tipoProveedor = Constantes.Proveedor.TIPO_ENTIDAD_FINANCIERA_COD;
					proveedorId = inversion.getEntidadFinancieraId();					
				}else if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())){
					if(inversion.getServicioConstructora()){
						tipoProveedor = Constantes.Proveedor.TIPO_CONSTRUCTORA_COD;
						tipoDocuProv = inversion.getConstructoraTipoDoc();
						nroDocuProv = inversion.getConstructoraNroDoc();
					}else{
						tipoProveedor = Constantes.Proveedor.TIPO_PERSONA_COD;
						personaId = pedido.getAsociadoId();
					}
				}else{
					tipoProveedor = Constantes.Proveedor.TIPO_PERSONA_COD;
					tipoDocuProv = inversion.getPropietarioTipoDocId();
					nroDocuProv = inversion.getPropietarioNroDoc();
				}
				// Obtener proveedor SAF
				PersonaSAF proveedor = personaDao.obtenerProveedorSAF(proveedorId, tipoProveedor, personaId, tipoDocuProv, nroDocuProv);
				if(proveedor==null){
					PersonaSAF proveedorSAF = new PersonaSAF();
					proveedorSAF.setPersonaID(personaId);
					proveedorSAF.setTipoDocumentoID(tipoDocuProv);
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
				// Registrar pedido-inversion
				PedidoInversionSAF pedidoInversionSAF = new PedidoInversionSAF();
				pedidoInversionSAF.setNroPedido(pedido.getNroPedido());
				pedidoInversionSAF.setProveedorID(String.valueOf(proveedor.getProveedorID().intValue()));
				pedidoInversionSAF.setPedidoInversionNumero(inversion.getNroInversion());
				pedidoInversionSAF.setPedidoTipoInversionID(Util.obtenerTipoInversionID(inversion.getTipoInversion()));
				pedidoInversionSAF.setConfirmarID("1");
				pedidoInversionSAF.setUsuarioIDCreacion(usuarioId);
				
				pedidoDao.agregarPedidoInversionSAF(pedidoInversionSAF);
				
				}catch(Exception e){
					LOG.error("ERROR pedido-inversion:: "+e.getMessage());
					e.printStackTrace();
				}
				
			}
			
			// Registrar inversion en caspio
			inversionService.actualizarSituacionConfirmadoInversionCaspio(inversionId, situacionConfirmado);
		}
							
		return resultado;
	}
	
	private String validarDiferenciPrecioExcedenteEnInversion(String inversionId, String pedidoId) throws Exception{		
		String resultado = "";
		// Obtener inversiones del pedido
		List<Inversion> listaInversiones = pedidoService.obtenerInversionesxPedidoCaspio(pedidoId);
		double montoTotalCertificados = 0, montoCertificadoUsado = 0, montoInversion = 0;		
		if(listaInversiones!=null && listaInversiones.size()>0){
			for(Inversion inversion : listaInversiones){
				System.out.println("inversion.getConfirmado():: "+inversion.getConfirmado());
				if(Constantes.Inversion.SITUACION_CONFIRMADO.equals(inversion.getConfirmado())){
					System.out.println("suma111");
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
			Inversion inversion = inversionService.obtenerInversionCaspio(inversionId);
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
	public void anularVerificacion(String inversionId) throws Exception {
		LOG.info("###anularVerificacion inversionId:"+inversionId);
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		if(null!=inversionId){
			inversionService.actualizarEstadoInversionRequisitoCaspio(inversionId, Constantes.InversionRequisito.PENDIENTE);
		}
	}

	@Override
	public void generarCartaObservacion(String inversionId, String usuarioSAFId) throws Exception {
		LOG.info("###generarCartaObservacion inversionId:"+inversionId);
		
		if(!StringUtils.isEmpty(inversionId)){
			 List<InversionRequisito> list= inversionService.obtenerRequisitosPorInversion(inversionId);
			 if(null!=list){
				 List<ObservacionInversion> listObs=new ArrayList<>();
				 for(InversionRequisito irc:list){
					 ObservacionInversion obs=new ObservacionInversion();
					 obs.setObservacion(irc.getObservacion());
					 listObs.add(obs);
				 }
				 
				String docxGenerado="Carta-de-validacion-de-datos-inversion-inmobiliaria-generado-"+inversionId+".docx";
				String pdfConvertido="Carta-de-validacion-de-datos-inversion-inmobiliaria-generado-"+inversionId+".pdf";
				String gestionInmobiliaria="gestion_inversion_inmobiliaria_desembolso";
				
				/*Generar documento*/
				DocumentoUtil documentoUtil=new DocumentoUtil();
				XWPFDocument docx = documentoUtil.openDocument(rutaDocumentosTemplates+"/"+gestionInmobiliaria+"/"+"Carta-de-validacion-de-datos-inversion-inmobiliaria-TEMPLATE.docx");
				
				/*Obtener datos de la inversion*/
				String nombreAsociado = "";
				Inversion pic= inversionService.obtenerInversionCaspio(inversionId);
				
				Pedido pedidoCaspio= pedidoService.obtenerPedidoCaspioPorId(String.valueOf(pic.getPedidoId().intValue()));
				PersonaSAF personaSAF= personaDao.obtenerPersonaSAF(String.valueOf(pedidoCaspio.getAsociadoId()));
				
				List<Contrato> listContratos = pedidoService.obtenerContratosxPedidoCaspio(String.valueOf(pedidoCaspio.getPedidoId()));
				//obtener primero contrato para buscar al funcionario de servicios
				Contrato contrato1 = listContratos.get(0);
				ContratoSAF contratoSAF= contratoDao.obtenerContratoSAF(contrato1.getNroContrato());
				
				List<Parametro> params=new ArrayList<>();
				Parametro parametro1=new Parametro("$FechaDocumento", Util.getFechaFormateada(Util.getFechaActual(), Constantes.FORMATO_CARTA_VALIDACION_INVERSION) );//fecha
				Parametro parametro2=new Parametro("$NombreCompleto", personaSAF.getNombreCompleto());//asociado
				Parametro parametro3=new Parametro("$TipoInversion", StringUtils.isEmpty(pic.getTipoInversion())?"":pic.getTipoInversion() );//tipo inversion
				Parametro parametro4=new Parametro("$TipoInmueble", StringUtils.isEmpty(pic.getTipoInmuebleNom())?"":pic.getTipoInmuebleNom());//tipo inmbueble
				Parametro parametro5=new Parametro("$Importe", String.valueOf(pic.getImporteInversion()) );//Importe
				Parametro parametro7=new Parametro("$FuncionarioServYVentas", contratoSAF.getFuncionarioServicioyVentas());//Funcionario de servicios y ventas
				
				params.add(parametro1);
				params.add(parametro2);
				params.add(parametro3);
				params.add(parametro4);
				params.add(parametro5);
				params.add(parametro7);
				
				
				XWPFDocument docxEdit=DocumentoUtil.replaceTextCartaValidacion(docx,params,listObs);
				StringBuilder sb=new StringBuilder();
				documentoUtil.saveDocument(docxEdit, sb.append(rutaDocumentosGenerados).append("/").append(docxGenerado).toString());
				
				/*Convertir a pdf*/
				DocumentoUtil.convertDocxToPdf(
						sb.append(rutaDocumentosGenerados).append("/").append(docxGenerado).toString(),
						sb.append(rutaDocumentosGenerados).append("/").append(pdfConvertido).toString());
				
				
				/*Enviar correo con archivo adjunto*/
				LOG.info("SE GENERO LA CARTA DE OBSERVACION");
		        String asunto = "Carta de Validación de Inversión - "+nombreAsociado;
		        Usuario usuario = usuarioDao.obtenerCorreoUsuarioCelula(usuarioSAFId);
		         
		        LOG.info("getCelulaCorreo:: "+usuario.getCelulaCorreo()+" - getEmpleadoCorreo: "+usuario.getEmpleadoCorreo());
		         
		         String emailTo = documentoEmailTo;
		         if(!Util.esVacio(usuario.getCelulaCorreo())){
		        	 emailTo = usuario.getCelulaCorreo();
		         }else if(!Util.esVacio(usuario.getEmpleadoCorreo())){
		        	 emailTo = usuario.getEmpleadoCorreo();
		         }
		         mailService.sendMail(emailDesarrolloPandero, emailTo, asunto, sb.append(rutaDocumentosGenerados).append("/").append(pdfConvertido).toString());
				
			 }
		}
		
	}
}
