package com.pandero.ws.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.business.ContratoBusiness;

@Controller
@RequestMapping("runMeTask")
public class RunMeTask {
	
	private static Logger LOGGER = LoggerFactory.getLogger(RunMeTask.class);
	
	@Autowired
	private ContratoBusiness constroBusiness;
	
	public void sincronizarContratos() {
		LOGGER.info("###sincronizarPedidos execute "+new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()) );
		
		//1.-Obtener contratos con movimientos a la fecha actual del SAF
		List<Contrato> listContratosSAF = null;
	    listContratosSAF = constroBusiness.getListContratoAlDia();
		
		
		if(null!=listContratosSAF){
			//2.-Obtener todos los contratos del caspio
			List<Contrato> listContratosCASPIO=new ArrayList<>();
			
			//3.-Verificar existencia del contrato
			
			//if exists --> update : situacion, fecha de adjudicacion
			// else --> insert todos los datos
			
			//3.1.-Verificar existencia del asociado
			//La llave con el saf es el tipo de documento y el numero de documento
			
			//if not exists --> insert todos los campos
		}else{
			//No existe contrato que sincronizar
		}
		
	}

}
