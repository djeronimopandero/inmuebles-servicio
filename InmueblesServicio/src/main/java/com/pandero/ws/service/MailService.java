package com.pandero.ws.service;


public interface MailService {

	public void sendMail(String emailFrom, String emailTo, String asunto, String documento, String textoEmail);
	
}
