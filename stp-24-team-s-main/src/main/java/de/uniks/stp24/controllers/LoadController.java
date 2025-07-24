package de.uniks.stp24.controllers;

import de.uniks.stp24.service.menu.LoginService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Map;

@Title("Calvario")
@Controller
public class LoadController extends BasicController{
    public Label press_any_key;
    @FXML
    HBox backgroundHBox;
    @FXML
    Image calvarioLogoLoad;
    @FXML
    Image spiderbyteLogoLoad;
    @FXML
    Image deadBirdsLogo;

    @Inject
    Subscriber subscriber;
    @Inject
    LoginService loginService;

    private String autologinText = "normalLogin";
    public boolean autologin;

    @Inject
    public LoadController() {
    }

    @OnInit
    public void init() {
        // checks if user wants to login automatically: there is a refreshToken if the user selected remember me before
        final String refreshToken = prefService.getRefreshToken();
        if (refreshToken == null || System.getenv("DISABLE_AUTO_LOGIN") != null) {
            // no automatic login possible
            this.autologin = false;
        }else {
            try {
                // login with saved refreshToken
                subscriber.subscribe(loginService.autologin(refreshToken),
                        result -> this.autologin = true,
                        error -> {
                            autologinText = "autologinFailed";
                            prefService.removeRefreshToken();
                        });
            } catch (Exception e) {
                this.autologin = false;
            }
        }
    }

    @OnKey
    public void showLoginScreen() {
        if(autologin){
            app.show("/browseGames");
        }else {
            app.show("/login", Map.of("info",autologinText));
        }
    }

    public void mouseClicked() {
        if(autologin){
            app.show("/browseGames");
        }else {
            app.show("/login", Map.of("info",autologinText));
        }
    }

    @OnDestroy
    public void destroy(){
        subscriber.dispose();
        backgroundHBox = null;
        deadBirdsLogo = null;
        calvarioLogoLoad = null;
        spiderbyteLogoLoad = null;
    }

}
