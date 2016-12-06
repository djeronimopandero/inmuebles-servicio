package com.pandero.ws.business.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.DocumentoRequisito;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.InversionRequisitoCaspio;
import com.pandero.ws.business.InversionBusiness;
import com.pandero.ws.service.ConstanteService;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.util.Constantes;
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
	
}
