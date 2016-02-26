package liber.gui;

import javafx.application.Platform;
import liber.notification.DefaultNotifier;
import liber.notification.Notifier;
import liber.notification.Info;
import liber.notification.Informer;

/**
 * Created by HPPC on 21/02/2016.
 */
public class GuiNotifier implements Notifier {
	final private Long pivot = 0L;
	private DefaultNotifier defaultNotifier;
	private Informer informer;
	public GuiNotifier() {
		defaultNotifier = new DefaultNotifier();
	}
	public void setInformer(Informer im) {
		synchronized (pivot) {
			informer = im;
		}
	}
	public void reset() {
		synchronized (pivot) {
			informer = null;
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
		Platform.runLater(() -> {
			synchronized (pivot) {
				if (informer != null) try {
					informer.inform(info);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
