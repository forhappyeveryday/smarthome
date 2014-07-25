package com.mobile.server.sendmail;
import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.mobile.server.config.Config;

import com.mobile.server.utils.Log;


public class SimpleSendMail {
	private static final String TAG = "SimpleSendMail";
	public static String mailFrom ;
	public static String mailFromPwd;  
    static Session session = null;
    public static void init(Config config){
	    mailFrom = config.getEmailFrom();
	    mailFromPwd = config.getEmailFromPwd();
	    
		Properties props = new Properties();
		String emailHost = config.getEmailHost();
		String emailPort = config.getEmailPort();
		boolean emailSmtpAuth = config.getEmailSmtpAuth();
		Log.d(TAG, "host:" + emailHost +
					" port:"+ emailPort +
					" smtpAuth:" + emailSmtpAuth +
					" mailFrom:" + mailFrom +
					" mailFromPwd:" + mailFromPwd);
	    props.put("mail.smtp.host", config.getEmailHost());
	    props.put("mail.smtp.port", config.getEmailPort());
	    props.put("mail.smtp.auth", String.valueOf(config.getEmailSmtpAuth()) );  

	    MyAuthenticator authenticator = new MyAuthenticator(mailFrom, mailFromPwd);
	    session = Session.getInstance(props, authenticator);
    }
	public static  boolean send(String mailTo, 
				String subject, String content, String fileFullPath
				){
	    try {
	    	Log.d(TAG, "enter send(mailTo:" + mailTo + " subject:" + subject +
	    			" content:" + content + " fileFullPath:" + fileFullPath);
	    	long startTime = System.currentTimeMillis();
	    
	        MimeMessage msg = new MimeMessage(session);
	        msg.setFrom(new InternetAddress(mailFrom));
//	        msg.setSender(address);
	        msg.setRecipients(Message.RecipientType.TO, mailTo);
	        msg.setSubject(subject);
	        msg.setSentDate(new Date());
	        
	        Multipart multipart = new MimeMultipart();	        
	        BodyPart bodypart = createContent(content, fileFullPath);
	        multipart.addBodyPart(bodypart);
	        msg.setContent(multipart);
	        
	        Transport.send(msg);
	        Log.d(TAG, "send mail cause:" + (System.currentTimeMillis()- startTime) + " msecs");
	        return true;
	    } catch (MessagingException mex) {
	        Log.e(TAG, "send failed, exception: " + mex);
	    } catch (Exception e) {
	    	Log.e(TAG, e.toString(), e);
		}
	    return false;
	}
	 
    /**  
     * ���ݴ�����ʼ�����body���ļ�·������ͼ�Ĳ�ï�����Ĳ���  
     */ 
    private static MimeBodyPart createContent(String body, String fileName)  
            throws Exception {  
        // ���ڱ����������Ĳ���  
        MimeBodyPart contentBody = new MimeBodyPart();  
        // ��������ı���ͼƬ��"related"�͵�MimeMultipart����  
        MimeMultipart contentMulti = new MimeMultipart("related");  
 
        // ���ĵ��ı�����  
        MimeBodyPartUtils.setTextContent(contentMulti, body);
        // ���ĵ�ͼƬ����  
//        contentMulti.addBodyPart(MimeBodyPartUtils.AddAttachFile(fileName));  
        MimeBodyPartUtils.AddAttachFile(contentMulti, fileName);
 
        // ������"related"�͵� MimeMultipart ������Ϊ�ʼ�������  
        contentBody.setContent(contentMulti);  
        return contentBody;  
    }
    private static class MimeBodyPartUtils {
    	public static MimeBodyPart setTextContent(MimeMultipart mimeContent, String text) throws MessagingException{
    		if(null == text || text.length() == 0){
    			Log.e(TAG, "text content is null, need set!");
    			return null;
    		}
            MimeBodyPart textBody = new MimeBodyPart();  
            textBody.setContent(text, "text/html;charset=gbk");
            mimeContent.addBodyPart(textBody);
            return textBody;
    	}
    	
    	public static MimeBodyPart AddAttachFile(MimeMultipart mimeContent, String fileName) throws MessagingException {
    		if(null == fileName || fileName.length() == 0){
    			Log.e(TAG, "fileName is empty, cannot create file");
    			return null;
    		}
    		
    		File f = new File(fileName);
    		if(!f.exists()){
    			Log.e(TAG, "file:" + fileName + " is not exsit!");
    			return null;
    		}
    		
    		if(!f.canRead()){
    			Log.e(TAG, "file:" + fileName + " cannot read, cannot attach it");
    			return null;
    		}
    		
    		
            MimeBodyPart jpgBody = new MimeBodyPart();  
            FileDataSource fds = new FileDataSource(fileName);  
            jpgBody.setDataHandler(new DataHandler(fds));  
            jpgBody.setFileName(fds.getName());
//            jpgBody.setContentID("logo_jpg");
            mimeContent.addBodyPart(jpgBody);
            return jpgBody;
    	}
    }
	 
	private static class MyAuthenticator extends Authenticator{
		String userName=null;   
	    String password=null;  
	    public MyAuthenticator(String username, String password) {    
	        this.userName = username;    
	        this.password = password;    
	    }    
	    protected PasswordAuthentication getPasswordAuthentication(){   
	        return new PasswordAuthentication(userName, password);   
	    }   
	}
}
