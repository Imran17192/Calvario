package de.uniks.stp24.service;

import de.uniks.stp24.App;
import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.LoginResult;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

public class AppServicesTest extends ApplicationTest {

    @Spy
    public final App app = spy(App.class);
    @Inject
    TokenStorage tokenStorage;
    @Inject
    ImageCache imageCache;
    @Inject
    PrefService prefService;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        app.start(stage);
        stage.requestFocus();
        stage.getScene().getStylesheets().clear();
    }
    @Test
    public void imageCacheTest() {
        assertNull(imageCache);
        imageCache = new ImageCache();

        imageCache.get("test/911.png");
        //fail
        imageCache.get("test/910.png");
        imageCache.get("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==");
    }

    @Test
    public void prefTest() {
        assertNull(prefService);
        prefService = new PrefService();
        prefService.removeRefreshToken();
        String token = "token";
        assertNull(prefService.getRefreshToken());
        prefService.setRefreshToken(token);
        assertNotNull(prefService.getRefreshToken());
        assertEquals("token", prefService.getRefreshToken());
        prefService.removeRefreshToken();
    }

    @Test
    public void dtoTest(){
        String name = "alice";
        String avatar = "test/911.png";
        String password = "alice999";
        String _id = "12345";
        String access = "accessToken";
        String refresh = "refreshToken";
        String created = "prior";
        String updated = "justNow";
        Map<String,Integer> _public = new HashMap<>();


        CreateUserDto userDto = new CreateUserDto(name,avatar,password,_public);
        assertEquals(name,userDto.name());
        assertEquals(avatar,userDto.avatar());
        assertEquals(password,userDto.password());

        LoginDto loginDto = new LoginDto(name,password);
        assertEquals(name,loginDto.name());
        assertEquals(password,loginDto.password());

        LoginResult loginResult = new LoginResult(_id,name,avatar,_public,access,refresh);
        assertEquals(_id,loginResult._id());
        assertEquals(name,loginResult.name());
        assertEquals(avatar,loginResult.avatar());
        assertEquals(access,loginResult.accessToken());
        assertEquals(refresh,loginResult.refreshToken());

        LogoutDto logoutDto = new LogoutDto("");
        assertEquals("",logoutDto.any());

        RefreshDto refreshDto = new RefreshDto(refresh);
        assertEquals(refresh,refreshDto.refreshToken());

        SignUpResultDto signUpResultDto = new SignUpResultDto(created,updated,_id,name,avatar);
        assertEquals(created,signUpResultDto.createdAt());
        assertEquals(updated,signUpResultDto.updatedAt());
        assertEquals(_id,signUpResultDto._id());
        assertEquals(name,signUpResultDto.name());
        assertEquals(avatar,signUpResultDto.avatar());

        UpdateUserDto updateUserDto = new UpdateUserDto(name,avatar,password);
        assertEquals(name,updateUserDto.name());
        assertEquals(avatar,updateUserDto.avatar());
        assertEquals(password,updateUserDto.password());

    }


}
