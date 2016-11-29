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
import com.pandero.ws.util.MetodoUtil;

@Component
public class InversionBusinessImpl implements InversionBusiness{

	private static final Logger LOG = LoggerFactory.getLogger(PedidoBusinessImpl.class);

	@Autowired
	InversionService inversionService;
	@Autowired
	ConstanteService constanteService;
	
	@Override
	public String confirmarInversion(String inversionId) throws Exception {
		String resultado = "";
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspio(inversionId);
		
		// Obtener lista de documentos
		List<DocumentoRequisito> listaDocumentos = constanteService.obtenerListaDocumentosPorTipoInversion(inversion.getTipoInversion());
		
		// Obtener la lista de documentos por tipo persona
		if(Constantes.TipoInversion.ADQUISICION_COD.equals(inversion.getTipoInversion())){
			for(DocumentoRequisito documentoRequisito : listaDocumentos){
				String propietarioTipoPersona = MetodoUtil.getTipoPersonaPorDocIden(inversion.getPropietarioTipoDocId());
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
		if(permiteConfirmar==false) resultado="PENDIENTE_DOCUMENTOS";
				
		return resultado;
	}
	
	
}
