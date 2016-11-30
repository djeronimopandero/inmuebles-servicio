package com.pandero.ws.business.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pandero.ws.bean.DocumentoRequisito;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.business.InversionBusiness;
import com.pandero.ws.service.ConstanteService;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.Util;

@Component
public class InversionBusinessImpl implements InversionBusiness{

	private static final Logger LOG = LoggerFactory.getLogger(PedidoBusinessImpl.class);

	@Autowired
	InversionService inversionService;
	@Autowired
	ConstanteService constanteService;
	
	@Override
	public String confirmarInversion(String inversionId, String situacionConfirmado) throws Exception {
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
			inversionService.actualizarSituacionConfirmadoInversionCaspio(inversionId, situacionConfirmado);
			resultado = Constantes.Service.RESULTADO_EXITOSO;
		}
							
		return resultado;
	}
	
	private String validarConfirmarInversion(Inversion inversion){
		String resultado="";
		if(inversion!=null){
			// Obtener lista de documentos
			List<DocumentoRequisito> listaDocumentos = constanteService.obtenerListaDocumentosPorTipoInversion(inversion.getTipoInversion());
			
			// Obtener la lista de documentos por tipo persona
			if(Constantes.TipoInversion.ADQUISICION_COD.equals(inversion.getTipoInversion())){
				for(DocumentoRequisito documentoRequisito : listaDocumentos){
					String propietarioTipoPersona = Util.getTipoPersonaPorDocIden(inversion.getPropietarioTipoDocId());
					if(!documentoRequisito.getTipoPersona().equals(propietarioTipoPersona)){
						listaDocumentos.remove(documentoRequisito);
					}
				}
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
						permiteConfirmar = false;
					}
				}else if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())){
					if(!inversion.getServicioConstructora()){
						if(!Util.esVacio(inversion.getConstructoraTipoDoc())){
							if(Util.esPersonaJuridica(inversion.getConstructoraTipoDoc())){
								if(Util.esVacio(inversion.getConstructoraNroDoc())
										|| Util.esVacio(inversion.getConstructoraRazonSocial())
										|| Util.esVacio(inversion.getConstructoraTelefono())
										|| Util.esVacio(inversion.getConstructoraContacto())){
									permiteConfirmar = false;
								}
							}else{
								if(Util.esVacio(inversion.getConstructoraNroDoc())
										|| Util.esVacio(inversion.getConstructoraNombres())
										|| Util.esVacio(inversion.getConstructoraApePaterno())
										|| Util.esVacio(inversion.getConstructoraApeMaterno())
										|| Util.esVacio(inversion.getConstructoraTelefono())){
									permiteConfirmar = false;
								}
							}
						}else{
							permiteConfirmar = false;
						}
					}
					if(Util.esVacio(inversion.getDescripcionObra())){
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
							permiteConfirmar = false;
						}
					}else{
						if(Util.esVacio(inversion.getPropietarioNombres())
								|| Util.esVacio(inversion.getPropietarioApePaterno())
								|| Util.esVacio(inversion.getPropietarioApeMaterno())){
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
							permiteConfirmar = false;
						}
					}
				}
				if(permiteConfirmar==false) resultado=Constantes.Service.RESULTADO_DATOS_PENDIENTES;				
			}						
		}
				
		return resultado;
	}
	
}
