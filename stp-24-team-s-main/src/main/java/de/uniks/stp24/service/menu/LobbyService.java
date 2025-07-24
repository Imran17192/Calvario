package de.uniks.stp24.service.menu;

import de.uniks.stp24.controllers.BasicController;
import de.uniks.stp24.dto.JoinGameDto;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.dto.UpdateMemberDto;
import de.uniks.stp24.model.Empire;
import de.uniks.stp24.rest.GameMembersApiService;
import de.uniks.stp24.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class LobbyService extends BasicController {
    @Inject
    public GameMembersApiService gameMembersApiService;
    @Inject
    public UserApiService userApiService;

    @Inject
    public LobbyService() {}
    public Observable<MemberDto[]> loadPlayers(String gameID) {
        return this.gameMembersApiService.getMembers(gameID);
    }

    public Observable<MemberDto> updateMember(String gameID, String userID, boolean ready, Empire empire) {
        return this.gameMembersApiService.patchMember(gameID, userID, new UpdateMemberDto(ready, empire));
    }

    public Observable<MemberDto> getMember(String gameID, String userID) {
        return this.gameMembersApiService.getMember(gameID, userID);
    }

    public Observable<JoinGameDto> leaveLobby(String gameID, String userID) {
        return this.gameMembersApiService.leaveGame(gameID, userID);
    }

}
