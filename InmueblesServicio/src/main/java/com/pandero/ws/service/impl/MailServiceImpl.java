package com.pandero.ws.service.impl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.pandero.ws.bean.EmailBean;
import com.pandero.ws.service.MailService;

@Service
public class MailServiceImpl implements MailService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);
	
	@Value("${ruta.documentos.generados}")
	private String rutaDocumentosGenerados;
	
	@Autowired
	private JavaMailSender mailSender;

	
//	public void sendMail(String emailFrom, String emailTo, String asunto, String documento, String textoEmail,boolean isFormatHtml) {
	public void sendMail(EmailBean email) {
		LOGGER.info("##sendMail... email:"+email);
	   MimeMessage message = mailSender.createMimeMessage();

	   try{
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			
			helper.setFrom(email.getEmailFrom());
			helper.setTo(email.getEmailToArray());
			helper.setCc("ichuyes@pandero.com.pe");
			helper.setBcc("djeronimo@pandero.com.pe");
			helper.setSubject(email.getSubject());
			helper.setText(email.getTextoEmail(),email.isFormatHtml());
			
			LOGGER.info("##email.isEnviarArchivo():"+email.isEnviarArchivo());
			if(email.isEnviarArchivo()){
				if(email.getAttachment()!=null){
					FileSystemResource file = new FileSystemResource(email.getAttachment());
					helper.addAttachment(file.getFilename(), file);										
				}
				else{
					String rutaDocumento = rutaDocumentosGenerados+"/"+email.getDocumento();
					LOGGER.info("###rutaDocumento:"+rutaDocumento);
					FileSystemResource file = new FileSystemResource(rutaDocumento);
					helper.addAttachment(file.getFilename(), file);					
				}
			}
		
	     }catch (MessagingException e) {
	    	 LOGGER.error("###sendMail:",e);
	    	 throw new MailParseException(e);
	     }
	   
	     mailSender.send(message);
    }
	
}
