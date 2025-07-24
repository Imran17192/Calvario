package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.SeasonalTradeDto;
import de.uniks.stp24.dto.UpdateEmpireMarketDto;
import de.uniks.stp24.model.SeasonComponent;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Objects;

@Singleton
public class MarketService {
    @Inject
    public PresetsApiService presetsApiService;
    @Inject
    public EmpireApiService empireApiService;
    @Inject
    public Subscriber subscriber;
    @Inject
    TokenStorage tokenStorage;

    private ObservableList<SeasonComponent> seasonComponents = FXCollections.observableArrayList();

    @Inject
    public MarketService() {
    }


    public Observable<EmpireDto> getEmpire(String gameID, String empireID) {
        return this.empireApiService.getEmpire(gameID, empireID);
    }

    public Observable<UpdateEmpireMarketDto> updateEmpireMarket(String gameID, String empireID, UpdateEmpireMarketDto updateEmpireMarketDto) {
        return this.empireApiService.updateEmpireMarket(gameID, empireID, updateEmpireMarketDto);
    }

    public Observable<SeasonalTradeDto> getSeasonalTrades(String gameID, String empireID) {
        return this.empireApiService.getSeasonalTrades(gameID, empireID);
    }

    public void setSeasonComponents(ObservableList<SeasonComponent> seasonComponents) {
        this.seasonComponents = seasonComponents;
    }

    public void cancelSeasonalTrade(SeasonComponent seasonComponent) {
        seasonComponents.remove(seasonComponent);
        saveSeasonalTrades();
    }

    public void saveSeasonalTrades() {
        subscriber.subscribe(this.empireApiService.getSeasonalTrades(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                seasonalTradeDto->{
                    if (Objects.nonNull(seasonalTradeDto._private()) && Objects.nonNull(seasonalTradeDto._private().get("allSeasonalTrades"))) {
                        seasonalTradeDto._private().put("allSeasonalTrades", seasonComponents);
                        subscriber.subscribe(this.empireApiService.saveSeasonalComponents(tokenStorage.getGameId(), tokenStorage.getEmpireId(), seasonalTradeDto));
                    } else{
                        subscriber.subscribe(this.empireApiService.saveSeasonalComponents(tokenStorage.getGameId(), tokenStorage.getEmpireId(), new SeasonalTradeDto(Map.of("allSeasonalTrades", seasonComponents))));
                    }
                }, error -> System.out.println("errorSaveSeasonalTrades:" + error.getMessage())
        );
    }

    public void dispose() {
        this.seasonComponents.clear();
    }
}