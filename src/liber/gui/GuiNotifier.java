package liber.gui;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import liber.data.Contact;
import liber.notification.DefaultNotifier;
import liber.notification.Notifier;
import liber.notification.Info;
import liber.notification.Informer;
import liber.notification.info.MessageReceived;

public class GuiNotifier implements Notifier {
	final private Long synchronizer;
	private DefaultNotifier defaultNotifier;
	private Informer informer;
	private Contact currentContact;
	private AudioClip hiddenRing;
	private AudioClip visibleRing;
	public GuiNotifier() {
		synchronizer = 0L;
		defaultNotifier = new DefaultNotifier();
		informer = null;
		currentContact = null;
		hiddenRing = new AudioClip(getClass().getResource("/liber/resource/song/hidden.mp3").toString());
		visibleRing = new AudioClip(getClass().getResource("/liber/resource/song/visible.mp3").toString());
	}
	public void setInformer(Informer im) {
		synchronized (synchronizer) {
			informer = im;
		}
	}
	public void setCurrentContact(Contact contact) {
		synchronized (synchronizer) {
			currentContact = contact;
		}
	}
	@Override
	public void good(String notification) {
		Platform.runLater(() -> {
			try {
				new Alert("Libersaurus", notification);
			} catch (Exception e) {
				defaultNotifier.good(notification);
				e.printStackTrace();
			}
		});
	}
	@Override
	public void bad(String notification) {
		Platform.runLater(() -> {
			try {
				new Alert("Erreur", notification);
			} catch (Exception e) {
				defaultNotifier.bad(notification);
				e.printStackTrace();
			}
		});
	}
	@Override
	public void info(Info info) {
		if(info instanceof MessageReceived) {
			MessageReceived mr = (MessageReceived) info;
			synchronized (synchronizer) {
				if (currentContact != null && mr.get().liberaddress().equals(currentContact.liberaddress())) {
					visibleRing.play();
				} else {
					hiddenRing.play();
				}
			}
		}
		Platform.runLater(() -> {
			synchronized (synchronizer) {
				if (informer != null) try {
					informer.inform(info);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
