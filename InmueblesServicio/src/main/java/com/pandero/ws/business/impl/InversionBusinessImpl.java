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
import com.pandero.ws.bean.DocumentoRequisito;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.InversionRequisitoCaspio;
import com.pandero.ws.bean.ObservacionInversion;
import com.pandero.ws.bean.Parametro;
import com.pandero.ws.bean.PedidoInversionCaspio;
import com.pandero.ws.bean.Usuario;
import com.pandero.ws.business.InversionBusiness;
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
	@Value("${ruta.documentos.templates}")
	private String rutaDocumentosTemplates;
	@Value("${ruta.documentos.generados}")
	private String rutaDocumentosGenerados;
	@Value("${desarrollo.pandero.email}")
	private String emailDesarrolloPandero;
	@Value("${documento.email.to}")
	private String documentoEmailTo;
	@Autowired
	MailService mailService;
	
	@Override
	public String confirmarInversion(String inversionId, String situacionConfirmado) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		constanteService.setTokenCaspio(tokenCaspio);
		
		String resultado = "";
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspio(inversionId);
		
		// Validar datos antes de confirmar
		if(Constantes.Inversion.SITUACION_CONFIRMADO.equals(situacionConfirmado)){
			String resultadoValidacion = validarConfirmarInversion(inversion);
			if(!Util.esVacio(resultadoValidacion)){
				resultado=resultadoValidacion;
			}
		}
		
		// Confirmar-desconfirmar inversion
		if(Util.esVacio(resultado)){						
			if(Constantes.Inversion.SITUACION_CONFIRMADO.equals(situacionConfirmado)){
				resultado=validarDiferenciPrecioExcedenteEnInversion(inversionId, String.valueOf(inversion.getPedidoId()));
			}
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
	
	private String validarConfirmarInversion(Inversion inversion) throws Exception {
		String resultado="";
		if(inversion!=null){
			// Obtener lista de documentos
			List<DocumentoRequisito> listaDocumentosTotal = constanteService.obtenerListaDocumentosPorTipoInversion(inversion.getTipoInversion());
			List<DocumentoRequisito> listaDocumentos = new ArrayList<DocumentoRequisito>();
			// Obtener la lista de documentos por tipo persona
			if(Constantes.TipoInversion.ADQUISICION_COD.equals(inversion.getTipoInversion())){
				for(DocumentoRequisito documentoRequisito : listaDocumentosTotal){
					String propietarioTipoPersona = Util.getTipoPersonaPorDocIden(inversion.getPropietarioTipoDocId());
					if(documentoRequisito.getTipoPersona().equals(propietarioTipoPersona)){
						listaDocumentos.add(documentoRequisito);
					}
				}
			}else{
				listaDocumentos = listaDocumentosTotal;
			}
			
			boolean permiteConfirmar = true;
			// Validar la lista de documentos
			if(inversion.getDocumentosRequeridos()!=null && !inversion.getDocumentosRequeridos().equals("")){
				String[] listaDocuSelec = inversion.getDocumentosRequeridos().split(",");
				if(listaDocuSelec.length!=listaDocumentos.size()){
					permiteConfirmar = false;
				}
			}else{
				permiteConfirmar = false;
			}
			if(permiteConfirmar==false) resultado=Constantes.Service.RESULTADO_PENDIENTE_DOCUMENTOS;
						
			if(permiteConfirmar){
				// Validar datos por tipo de inversion
				if(Constantes.TipoInversion.CANCELACION_COD.equals(inversion.getTipoInversion())){
					if(inversion.getEntidadFinancieraId()==null
							|| Util.esVacio(inversion.getNroCredito())
							|| Util.esVacio(inversion.getSectorista())
							|| Util.esVacio(inversion.getTelefonoContacto())){
						System.out.println("DATOS_PENDIENTES - 1");
						permiteConfirmar = false;
					}
				}else if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())){
					if(inversion.getServicioConstructora()){
						if(!Util.esVacio(inversion.getConstructoraTipoDoc())){
							if(Util.esPersonaJuridica(inversion.getConstructoraTipoDoc())){
								if(Util.esVacio(inversion.getConstructoraNroDoc())
										|| Util.esVacio(inversion.getConstructoraRazonSocial())
										|| Util.esVacio(inversion.getConstructoraTelefono())
										|| Util.esVacio(inversion.getConstructoraContacto())){
									System.out.println("DATOS_PENDIENTES - 2");
								}
							}else{
								if(Util.esVacio(inversion.getConstructoraNroDoc())
										|| Util.esVacio(inversion.getConstructoraNombres())
										|| Util.esVacio(inversion.getConstructoraApePaterno())
										|| Util.esVacio(inversion.getConstructoraApeMaterno())
										|| Util.esVacio(inversion.getConstructoraTelefono())){
									System.out.println("DATOS_PENDIENTES - 3");
									permiteConfirmar = false;
								}
							}
						}else{
							System.out.println("DATOS_PENDIENTES - 4");
							permiteConfirmar = false;
						}
					}
					if(Util.esVacio(inversion.getDescripcionObra())){
						System.out.println("DATOS_PENDIENTES - 5");
						permiteConfirmar = false;
					}
				}
				
				// Validar datos del propietario
				if(permiteConfirmar){
					if(Util.esPersonaJuridica(inversion.getPropietarioTipoDocId())){
						if(Util.esVacio(inversion.getPropietarioRazonSocial())
								|| Util.esVacio(inversion.getRepresentanteTipoDocId())
								|| Util.esVacio(inversion.getRepresentanteNroDoc())
								|| Util.esVacio(inversion.getRepresentanteApePaterno())
								|| Util.esVacio(inversion.getRepresentanteApeMaterno())
								|| Util.esVacio(inversion.getRepresentanteNombres())){
							System.out.println("DATOS_PENDIENTES - 6");
							permiteConfirmar = false;
						}
					}else{
						if(Util.esVacio(inversion.getPropietarioNombres())
								|| Util.esVacio(inversion.getPropietarioApePaterno())
								|| Util.esVacio(inversion.getPropietarioApeMaterno())){
							System.out.println("DATOS_PENDIENTES - 7");
							permiteConfirmar = false;
						}
					}
				}
				
				// Validar datos del beneficiario
				if(permiteConfirmar){
					if(!inversion.getBeneficiarioAsociado()){
						if(Util.esVacio(inversion.getBeneficiarioTipoDocId())
								|| Util.esVacio(inversion.getBeneficiarioNroDoc())
								|| Util.esVacio(inversion.getBeneficiarioNombreCompleto())
								|| inversion.getBeneficiarioRelacionAsociadoId()==null){
							System.out.println("DATOS_PENDIENTES - 8");
							permiteConfirmar = false;
						}
					}
				}
				if(permiteConfirmar==false) resultado=Constantes.Service.RESULTADO_DATOS_PENDIENTES;				
			}						
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
			 List<InversionRequisitoCaspio> list= inversionService.listInversionRequisitoPorIdInversion(inversionId);
			 if(null!=list){
				 List<ObservacionInversion> listObs=new ArrayList<>();
				 for(InversionRequisitoCaspio irc:list){
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
				
				/*Obtener datos del asociado de la inversion*/
				String nombreAsociado = "";
				PedidoInversionCaspio pic= inversionService.obtenerPedidoInversionPorInversion(inversionId);
				List<Parametro> params=new ArrayList<>();
				Parametro parametro1=new Parametro("text_1", "Lima, 09 de Diciembre del 2016");//fecha
				Parametro parametro2=new Parametro("text_2", "Nombre del asociado");//asocaido
				Parametro parametro3=new Parametro("text_3", StringUtils.isEmpty(pic.getTipoInversion())?"":pic.getTipoInversion() );//tipo inversion
				Parametro parametro4=new Parametro("text_4", StringUtils.isEmpty(pic.getTipoInmueble())?"":pic.getTipoInmueble());//tipo inmbueble
				Parametro parametro5=new Parametro("text_5", StringUtils.isEmpty(pic.getImporteInversion())?"":pic.getImporteInversion());//Importe
				Parametro parametro7=new Parametro("text_7", "Nombre del Funcionario");//Funcionario
				
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
