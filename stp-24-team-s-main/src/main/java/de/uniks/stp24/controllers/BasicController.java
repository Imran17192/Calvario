package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.service.ErrorService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.PrefService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.menu.LanguageService;
import de.uniks.stp24.utils.ResponseConstants;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.ResourceBundle;

/*
doesn't need @Controller annotation!
contains important @Inject annotations
provides some useful methods
*/
public class BasicController {
    @Inject
    public App app;
    @Inject
    PrefService prefService;
    @Inject
    LanguageService languageService;
    @Inject
    @Resource
    public ResourceBundle resources;
    @Inject
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;
    @Inject
    @Named("variablesResourceBundle")
    public ResourceBundle variablesResourceBundle;
    @Inject
    public ErrorService errorService;
    @Inject
    ResponseConstants responseConstants;
    @Inject
    public Subscriber subscriber;
    @Inject
    public ImageCache imageCache;
    @Inject
    public TokenStorage tokenStorage;

    public Map<Integer, String> controlResponses;

    @Inject
    public BasicController() {
    }

    // check if one or more string aren't empty or blank
    public boolean checkIt(String... texts) {
        if (texts.length == 0) return false;
        boolean cond = true;
        for (String str : texts) {
            cond &= checkIfInputNotBlankOrEmpty(str);
        }
        return cond;
    }

    private boolean checkIfInputNotBlankOrEmpty(String text) {
        return (!text.isBlank() && !text.isEmpty());
    }

    // look for a text (info or error) in a respective dictionary
    public String getErrorInfoText(Map<Integer, String> map, int code) {
        if (map == null || map.isEmpty())
            return resources.getString(responseConstants.resStdText.getOrDefault(code, "no.dict"));
        return resources.getString(
                map.getOrDefault(code, "no.entry.dict")
        );
    }

    public String getErrorInfoText(int code) {
        return getErrorInfoText(this.controlResponses, code);
    }

    /*
    use this methode when making a request like with
     subscriber.subscribe(method,
                          result -> {...},
                          error -> {
                          ...getErrorInfoText(error);
                          ...});
     */
    public String getErrorInfoText(Throwable error) {
        int code = errorService.getStatus(error);
        return getErrorInfoText(this.controlResponses, code);
    }

    public void changeNodesVisibility(boolean show, Node... nodes) {
        for (Node node : nodes) {
            node.setVisible(show);
        }
    }

    public void changeNodesDisability(boolean disable, Node... nodes) {
        for (Node node : nodes) {
            node.setDisable(disable);
        }
    }

    public void setTextEditable(boolean editable, TextInputControl... textFields) {
        for (TextInputControl textField : textFields) {
            textField.setEditable(editable);
        }
    }
}
