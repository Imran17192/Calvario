package de.uniks.stp24.service.menu;

import de.uniks.stp24.dto.JoinGameDto;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.rest.GameMembersApiService;
import de.uniks.stp24.service.TokenStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class JoinGameService {
    @Inject
    TokenStorage tokenStorage;
    @Inject
    public GameMembersApiService gameMembersApiService;

    @Inject
    public JoinGameService() {
    }

    public Observable<JoinGameDto> joinGame(String gameID, String userID, String password) {
        return this.gameMembersApiService.joinGame(gameID, this.loadUserLobbyDto(password, userID));
    }

    private MemberDto loadUserLobbyDto(String password, String userID) {
        return new MemberDto(false,
                userID,
                null,
                password
        );
    }
}
