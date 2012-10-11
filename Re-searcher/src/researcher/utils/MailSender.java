package researcher.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import researcher.beans.BlastQuery;
import researcher.beans.Configuration;
import researcher.beans.User;
import researcher.cache.Cache;
import researcher.db.GlobalDAO;

public class MailSender {
	
	public static void sendNotifyOnNewHits(long queryId) {
		BlastQuery query;
		User user;
		GlobalDAO dao = null;
		try {
			dao = new GlobalDAO();
			query = dao.getQuery(queryId);
			user = query.getUser();
			String uuid = user.getUuid();
			// actually, all users should have uuids
			// they are assigned when the user is created
			if (uuid == null) {
				uuid = UUID.randomUUID().toString();
				user.setUuid(uuid);
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		StringBuffer text = new StringBuffer();
		text.append("Re-searcher has found new hits.\n\n");
		text.append("Query name: ");
		text.append(query.getSequenceName());
		text.append("\nQuery description:" + query.getShortDetails());
		String link = Cache.getConfig().getAppLink();

		if (link != null) {
			try {
				StringBuffer s = new StringBuffer();
				s.append(link);
				s.append("?service=external&page=HitList");
				s.append("&uid=");
				String uuid = user.getUuid();
				s.append(URLEncoder.encode(uuid, "UTF-8"));
				s.append("&queryid=");
				s.append(query.getId());
				text
						.append("\n\nYou can follow this link to see the hits table:\n");
				text.append(s);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		Thread mailer = new Thread(new Mailer(user.getEmail(), "New hits found: "
				+ query.getSequenceName(), text.toString()));
		mailer.start();
	}
	
	public static void sendNotifyOnError(long queryId){
		BlastQuery query;
		User user;
		GlobalDAO dao = null;
		try {
			dao = new GlobalDAO();
			query = dao.getQuery(queryId);
			user = query.getUser();
			String uuid = user.getUuid();
			// actually, all users should have uuids
			// they are assigned when the user is created
			if (uuid == null) {
				uuid = UUID.randomUUID().toString();
				user.setUuid(uuid);
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		StringBuffer text = new StringBuffer();
		text.append("Re-searcher encountered an error when executing the query.\n\n");
		text.append("Query name: ");
		text.append(query.getSequenceName());
		text.append("\nQuery description:" + query.getShortDetails());
		text.append("\nError message:" + query.getBlastErrors());
		String link = Cache.getConfig().getAppLink();

		if (link != null) {
			try {
				StringBuffer s = new StringBuffer();
				s.append(link);
				s.append("?service=external&page=QueryDetails");
				s.append("&uid=");
				String uuid = user.getUuid();
				s.append(URLEncoder.encode(uuid, "UTF-8"));
				s.append("&queryid=");
				s.append(query.getId());
				text
						.append("\n\nYou can follow this link to see query details:\n");
				text.append(s);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		Thread mailer = new Thread(new Mailer(user.getEmail(), "Error encountered: "
				+ query.getSequenceName(), text.toString()));
		mailer.start();
	}
	
	private static class Mailer implements Runnable {
		
		String email, subject, text;
		
		public Mailer(String email, String subject, String text) {
			this.email = email;
			this.subject = subject;
			this.text = text;
		}
		
		public void run() {
			sendMail(email, subject, text);
		}
	}
	
	private static void sendMail(String email, String subject, String text) {
		Configuration cfg = Cache.getConfig();
		if (cfg.getSmtpHostname() == null)
			throw new NullPointerException("smtp hostname is null");
		if (cfg.getEmailFrom() == null)
			throw new NullPointerException("from mail is null");
		SimpleEmail sender = new SimpleEmail();
		sender.setHostName(cfg.getSmtpHostname());
		try {
			sender.addTo(email);
			sender.setFrom(cfg.getEmailFrom(), "Automatic notification");
			sender.setSubject(subject);
			sender.setMsg(text);
			sender.send();
		} catch (EmailException e) {
			e.printStackTrace();
		}
	}
	
	
	/*
	public static void sendNotify(long queryId, Mailer mailer) {
		GlobalDAO dao = null;
		try {
			dao = new GlobalDAO();
			BlastQuery query = dao.getQuery(queryId);
			User user = query.getUser();
			String uuid = user.getUuid();
			// actually, all users should have uuids
			// they are assigned when the user is created
			if (uuid == null) {
				uuid = UUID.randomUUID().toString();
				user.setUuid(uuid);
			}
			Thread thread = new Thread(mailer);
			thread.start();
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	public static void sendNotifyOnNewhits(long queryId) {
		GlobalDAO dao = null;
		try {
			dao = new GlobalDAO();
			BlastQuery query = dao.getQuery(queryId);
			User user = query.getUser();
			String uuid = user.getUuid();
			// actually, all users should have uuids
			// they are assigned when the user is created
			if (uuid == null) {
				uuid = UUID.randomUUID().toString();
				user.setUuid(uuid);
			}
			Thread thread = new Thread(new NewHitsMailer(query, user));
			thread.start();
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	private static abstract class Mailer implements Runnable {
		
		BlastQuery query;
		User user;

		Mailer(BlastQuery query, User user) {
			this.query = query;
			this.user = user;
		} 
		
	}

	private static class NewHitsMailer extends Mailer {

		public NewHitsMailer(BlastQuery query, User user) {
			super(query, user);
		}

		public void run() {
			StringBuffer text = new StringBuffer();
			text.append("Re-searcher has found new hits.\n\n");
			text.append("Query name: ");
			text.append(query.getSequenceName());
			text.append("\nQuery description:" + query.getShortDetails());

			String link = Cache.getConfig().getAppLink();

			if (link != null) {
				try {
					StringBuffer s = new StringBuffer();
					s.append(link);
					s.append("?service=external&page=HitList");
					s.append("&uid=");
					String uuid = user.getUuid();
					s.append(URLEncoder.encode(uuid, "UTF-8"));
					s.append("&queryid=");
					s.append(query.getId());
					text
							.append("\n\nYou can follow this link to see the hits table:\n");
					text.append(s);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			sendMail(user.getEmail(), "New hits found: "
					+ query.getSequenceName(), text.toString());
		}
	}

	public static void sendNotifyError(long id) {
	}
	*/
	
	public static void main(String[] args) {
		SimpleEmail sender = new SimpleEmail();
		sender.setHostName("mail.zebra.lt");
//		sender.setAuthentication("posso@hotpop.com", "gnosis1");
		try {
			sender.addTo("valdemaras@gmail.com");
			sender.setFrom("posso@HotPOP.com", "Automatic notification");
			sender.setSubject("subject");
			sender.setMsg("text");
			sender.send();
		} catch (EmailException e) {
			e.printStackTrace();
		}
	}
}
