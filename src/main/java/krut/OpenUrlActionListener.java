package krut;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class OpenUrlActionListener implements ActionListener {

    private final static Logger logger = Logger.getLogger(OpenUrlActionListener.class.getName());

    private URI uri;

    public OpenUrlActionListener(String url) {
        try {
            this.uri = new URI(url);
        } catch (URISyntaxException e) {
            logger.throwing("OpenUrlActionListener", "OpenUrlActionListener", e);
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                logger.throwing("OpenUrlActionListener", "actionPerformed", e);
            }
        }
    }
}