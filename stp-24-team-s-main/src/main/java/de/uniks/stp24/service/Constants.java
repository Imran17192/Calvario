package de.uniks.stp24.service;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Constants {
    // Colors
    public static final String[] colorsArray = {"#DC143C", "#0F52BA", "#50C878", "#9966CC", "#FF7F50",
            "#40E0D0", "#FF00FF", "#FFD700", "#C0C0C0", "#4B0082",
            "#36454F", "#F28500", "#E6E6FA", "#008080", "#800000", "#808000"};

    // Folder
    public static final String DATA_FOLDER_NAME = "data";
    public static final String EVENT_FOLDER_NAME = "src/main/resources/de/uniks/stp24/data/";
    public static final Path DATA_FOLDER = Path.of(DATA_FOLDER_NAME);

    // Description and names for Empires
    public static final Map<String, String[]> empireTemplatesEnglish = Map.ofEntries(
            Map.entry("Prefix", new String[]{
                    "Greedy", "Evil", "Vicious", "Old", "Mischievous", "Dead", "Amateur", "Famous", "Hungry",
                    "Grabby", "Hopeful", "Anxious", "Hideous", "Wicked", "Stinky", "Angry"}),
            Map.entry("Type", new String[]{
                    "Pirates", "Buccaneers", "Thieves", "Traders", "Corsairs", "Explorers", "Adventurers", "Voyagers",
                    "Wanderers", "Heroes", "Charlatans", "Criminals", "Bandits", "Burglars", "Gunmen", "Baddies", "Rogues",
                    "Lords", "Dukes", "Kings", "Rulers", "Barons", "Captains"}),
            Map.entry("Suffix", new String[]{
                    "Iced", "Shadow", "Steel", "Iron", "Sunken", "Chaotic", "Corrupt", "Wicked", "Elder", "Careless", "Reckless",
                    "Red", "Brown", "Crimson", "Azure", "Sapphire", "Golden", "Steamy", "Secret", "Black", "Dark", "Cryptic",
                    "Murky", "Sombre", "Sunless", "Deep", "Misty"}),
            Map.entry("Definition", new String[]{
                    "Cartel", "Gang", "Syndicate", "Ring", "Island", "Reef", "Archipelago", "Triangle", "Enclave", "Empire",
                    "Realm", "Domain", "Reign", "Sanctuary", "Terrain", "Sea", "Lake", "Deeps", "Waters"}),
            Map.entry("Description", new String[]{
                    "A ruthless pirate cartel {NAME}, known for their cutthroat tactics and insatiable greed on the high seas.",
                    "Infamous plunderers who strike fear into the hearts of sailors, amassing wealth through piracy and intimidation",
                    "A shadowy syndicate of swashbucklers, their flag bearing the skull and crossed cutlasses, symbolizing their merciless pursuit of riches.",
                    "Legends of the maritime underworld, their name whispered in fear among sailors, as they command the waves with an iron fist wrapped in gold.",
                    "In the treacherous waters of the Calvario, {NAME} as the most feared pirate alliance, hoarding riches and leaving a trail of chaos in its wake.",
                    "Under the black flag of {NAME}, pirates unite in a fierce brotherhood, seeking fortune and infamy across the boundless expanse of the ocean.",
                    "In the treacherous domain of the Seven Seas, {NAME} stands as the sovereign of plunder, feared for their merciless raids and impenetrable hideouts.",
                    "Under the black flag of {NAME}, the oceans become a stage for daring escapades and untold riches, where every wave whispers tales of their legendary exploits.",
                    "The feared {NAME}, masters of the maritime underworld, ruled with an iron fist and a thirst for gold that knew no bounds.",
                    "With their cunning tactics and insatiable greed, the members of {NAME} strike fear into the hearts of sailors, dominating the oceans with impunity.",
                    "Led by cunning captains, the {NAME} dominate the trade routes, leaving a wake of fear and envy among rival seafarers.",
                    "In the shadowy world of piracy, the name {NAME} strikes dread into the hearts of merchants as they hoard treasures beyond imagination in their hidden island strongholds."
            })
    );

    public static final Map<String, String[]> empireTemplatesGerman = Map.ofEntries(
            Map.entry("Prefix", new String[]{
                    "Gierige", "Böse", "Siegessichere", "Alte", "Schelmische", "Tote", "Amateurhafte", "Berühmte", "Hungrige",
                    "Übergriffige", "Hoffnungsvolle", "Ängstliche", "Abscheuliche", "Gemeine", "Stinkende", "Wütende"}),
            Map.entry("Type", new String[]{
                    "Piraten", "Seeräuber", "Diebe", "Händler", "Korsaren", "Entdecker", "Abenteurer", "Reisende",
                    "Wanderer", "Helden", "Freibeuter", "Kriminelle", "Banditen", "Einbrecher", "Schützen", "Bösewichte", "Schurken",
                    "Fürsten", "Herzöge", "Könige", "Herrscher", "Barone", "Kapitäne"}),
            Map.entry("Suffix", new String[]{
                    "Eisigen", "Schattigen", "Stählernen", "Eisernen", "Versunkenen", "Chaotischen", "Korrupten", "Steinigen", "Zeitlosen", "Gefährlichen", "Untergegangenen",
                    "Roten", "Braunen", "Feuerroten", "Azurblauen", "Saphirblauen", "Goldenen", "Dampfenden", "Geheimen", "Schwarzen", "Dunkelen", "Kryptischen",
                    "Trüben", "Düsteren", "Sonnenlosen", "Tiefen", "Mysteriösen"}),
            Map.entry("Definition", new String[]{
                    "Festungen", "Verstecke", "Buchten", "Strände", "Inseln", "Riffe", "Archipele", "Häfen", "Enklaven", "Imperien",
                    "Reiche", "Klippen", "Herrschaftsgebiete", "Zufluchtsorte", "Felsen", "Meere", "Seen", "Lagunen", "Gewässer"}),
            Map.entry("Description", new String[]{
                    "Dieses rücksichtsloses Piratenkartell ist bekannt für seine skrupellosen Taktiken und unersättliche Gier auf hoher See.",
                    "Berüchtigte Plünderer, die den Seeleuten Angst einjagen und durch Piraterie und Einschüchterung Reichtümer anhäufen.",
                    "Ein schattenhaftes Syndikat von Freibeutern, deren Flagge einen Totenkopf und gekreuzte Krummsäbel trägt und ihre gnadenlose Jagd nach Reichtümern symbolisiert.",
                    "Legenden der maritimen Unterwelt, deren Name unter Seeleuten voller Angst geflüstert wird, während sie die Wellen mit eiserner Faust beherrschen.",
                    "In den gefährlichen Gewässern des Calvario ist diese Piratenallianz dafür bekannt, Reichtümer zu horten und überall eine Schneise der Verwüstung zu hinterlassen.",
                    "Unter dieser schwarzen Flagge vereinen sich Piraten in einer erbitterten Bruderschaft, die nach Reichtum und Ruhm über die grenzenlose Weite des Ozeans strebt.",
                    "Im gefährlichen Reich der Sieben Meere gilt dies Piratenbande als Meister des Plünderns, gefürchtet für ihre gnadenlosen Überfälle und uneinnehmbaren Verstecke.",
                    "Unter dieser schwarzen Flagge wird der Ozean zur Bühne für gewagte Abenteuer und unermessliche Reichtümer, wo jede Welle Geschichten ihrer legendären Taten flüstert.",
                    "Gefürchtete Piraten und Meister der maritimen Unterwelt, regieren mit eiserner Faust und einem unstillbaren Durst nach Gold.",
                    "Mit ihren gerissenen Taktiken und unersättlicher Gier jagen die Mitglieder dieser Freibeutergruppe den Seeleuten Angst ein und beherrschen die Ozeane.",
                    "Angeführt von gerissenen Kapitänen dominieren diese Seeräuber die Handelsrouten und hinterlassen eine Spur von Angst und Neid bei rivalisierenden Seefahrern.",
                    "In der schattenhaften Welt der Piraterie versetzt der Name {NAME} Kaufleute in Angst und Schrecken, während sie in ihren verborgenen Inselverstecken unvorstellbare Schätze horten."
            })
    );

    public static final Map<String, String> resourceTranslation = Map.of(
            "credits", "resource.doubloons",
            "minerals", "resource.sparklingGeode",
            "population", "resource.crewmates",
            "energy", "resource.gunpowder",
            "food", "resource.provisions",
            "fuel","resource.coal",
            "research", "resource.scoutReports",
            "alloys", "resource.gemmyAlloys",
            "consumer_goods","resource.rum"
    );

    public static final Map<String, String> economyProcess = Map.of(
                "cost", "resource.cost",
                "upkeep", "resource.upkeep",
                "production", "resource.production",
                "base", "resource.base",
                "total", "resource.total"
    );

    public static final Map<String, String> resourceImagePath = Map.of(
            "credits", "de/uniks/stp24/icons/resources/credits.png",
            "minerals", "de/uniks/stp24/icons/resources/minerals.png",
            "population", "de/uniks/stp24/icons/resources/population.png",
            "energy", "de/uniks/stp24/icons/resources/energy.png",
            "food", "de/uniks/stp24/icons/resources/food.png",
            "fuel","de/uniks/stp24/icons/resources/fuel.png",
            "research", "de/uniks/stp24/icons/resources/research.png",
            "alloys", "de/uniks/stp24/icons/resources/alloys.png",
            "consumer_goods","de/uniks/stp24/icons/resources/consumer_goods.png"
    );

    public static final Map<String, String> siteTranslation = Map.of(
            "city", "site.village",
            "energy", "site.thaumaturgy",
            "mining","site.mining",
            "agriculture","site.harvesting",
            "industry", "site.production",
            "research", "site.expedition",
            "ancient_foundry","site.epoch",
            "ancient_factory","site.merchant",
            "ancient_refinery", "site.coalmine"
    );

    public static final Map<String, String> islandTranslation = Map.ofEntries(
            Map.entry("uninhabitable_0", "island.plundered"),
            Map.entry("uninhabitable_1", "island.breezy"),
            Map.entry("uninhabitable_2", "island.waveless"),
            Map.entry("uninhabitable_3", "island.serene"),
            Map.entry("regular", "island.bandit"),
            Map.entry("energy", "island.smouldering"),
            Map.entry("mining", "island.echoing"),
            Map.entry("agriculture", "island.lushy"),
            Map.entry("ancient_technology", "island.forlorn"),
            Map.entry("ancient_industry", "island.amazonian"),
            Map.entry("ancient_military", "island.everfrozen")
    );

    public static final Map<String, String> upgradeTranslation = Map.ofEntries(
            Map.entry("unexplored", "update.unexplored"),
            Map.entry("explored", "update.explored"),
            Map.entry("colonized", "update.colonized"),
            Map.entry("upgraded", "update.upgraded"),
            Map.entry("developed", "update.developed")
    );

    public static final Map<String, String> technologyTranslation = Map.ofEntries(
            Map.entry("crew_relations", "society"),
            Map.entry("marine_science", "physics"),
            Map.entry("shipbuilding", "engineering"),
            Map.entry("cartography", "computing"),
            Map.entry("construction", "construction"),
            Map.entry("pirate_codex", "state"),
            Map.entry("power", "energy"),
            Map.entry("sailing", "propulsion"),
            Map.entry("marine_life", "biology"),
            Map.entry("plunder", "materials"),
            Map.entry("hidden_treasure", "rare"),
            Map.entry("navy", "military"),
            Map.entry("production", "production"),
            Map.entry("economy", "economy")
    );

    public static final Map<String, String> sitesIconPathsMap = Map.ofEntries(
            Map.entry("city", "de/uniks/stp24/icons/sites/village_site.png"),
            Map.entry("energy", "de/uniks/stp24/icons/sites/thaumaturgy_site.png"),
            Map.entry("mining", "de/uniks/stp24/icons/sites/mining_site.png"),
            Map.entry("agriculture", "de/uniks/stp24/icons/sites/harvesting_site.png"),
            Map.entry("industry", "de/uniks/stp24/icons/sites/production_site.png"),
            Map.entry("research", "de/uniks/stp24/icons/sites/epoch_site.png"),
            Map.entry("ancient_foundry", "de/uniks/stp24/icons/sites/expedition_site.png"),
            Map.entry("ancient_factory", "de/uniks/stp24/icons/sites/merchant_site.png"),
            Map.entry("ancient_refinery", "de/uniks/stp24/icons/sites/coalmine_site.png")
    );

    public static final Map<String, String> buildingTranslation = Map.of(
            "exchange", "building.seaside",
            "power_plant","building.theurgy",
            "mine","building.resonatingDelves",
            "farm","building.farmside",
            "research_lab","building.scoutHub",
            "foundry","building.alloySmeltery",
            "factory","building.chophouse",
            "refinery","building.coalQuarry",
            "shipyard", "building.shipyard",
            "fortress", "building.stronghold"
    );

    public static final Map<String, String> buildingsIconPathsMap = Map.ofEntries(
            Map.entry("refinery", "de/uniks/stp24/icons/buildings/coal_querry.png"),
            Map.entry("factory", "de/uniks/stp24/icons/buildings/chophouse.png"),
            Map.entry("foundry", "de/uniks/stp24/icons/buildings/alloy_smeltery.png"),
            Map.entry("research_lab", "de/uniks/stp24/icons/buildings/scout_hub.png"),
            Map.entry("farm", "de/uniks/stp24/icons/buildings/farmside.png"),
            Map.entry("mine", "de/uniks/stp24/icons/buildings/resonating_delves.png"),
            Map.entry("power_plant", "de/uniks/stp24/icons/buildings/theurgy_hall.png"),
            Map.entry("exchange", "de/uniks/stp24/icons/buildings/seaside_hut.png"),
            Map.entry("shipyard", "de/uniks/stp24/icons/buildings/shipyard.png"),
            Map.entry("fortress", "de/uniks/stp24/icons/buildings/stronghold.png")
    );

    public static final Map<String, String> buildingsOnQueueMap = Map.ofEntries(
            Map.entry("foundry", "de/uniks/stp24/icons/buildings/jobPause/alloy_smeltery_onQueue.png"),
            Map.entry("power_plant", "de/uniks/stp24/icons/buildings/jobPause/theurgy_hall_onQueue.png"),
            Map.entry("factory", "de/uniks/stp24/icons/buildings/jobPause/chophouse_onQueue.png"),
            Map.entry("mine", "de/uniks/stp24/icons/buildings/jobPause/resonating_delves_onQueue.png"),
            Map.entry("farm", "de/uniks/stp24/icons/buildings/jobPause/farmside_onQueue.png"),
            Map.entry("refinery", "de/uniks/stp24/icons/buildings/jobPause/coal_querry_onQueue.png"),
            Map.entry("research_lab", "de/uniks/stp24/icons/buildings/jobPause/scout_hub_onQueue.png"),
            Map.entry("exchange", "de/uniks/stp24/icons/buildings/jobPause/seaside_hut_onQueue.png"),
            Map.entry("shipyard", "de/uniks/stp24/icons/buildings/jobPause/shipyard_onQueue.png"),
            Map.entry("fortress", "de/uniks/stp24/icons/buildings/jobPause/stronghold_onQueue.png")
    );

    public static final Map<String, String> buildingsJobProgressMap = Map.ofEntries(
            Map.entry("foundry", "de/uniks/stp24/icons/buildings/jobProgress/alloy_smeltery_onConstruction.png"),
            Map.entry("power_plant", "de/uniks/stp24/icons/buildings/jobProgress/theurgy_hall_onConstruction.png"),
            Map.entry("factory", "de/uniks/stp24/icons/buildings/jobProgress/chophouse_onConstruction.png"),
            Map.entry("mine", "de/uniks/stp24/icons/buildings/jobProgress/resonating_delves_onConstruction.png"),
            Map.entry("farm", "de/uniks/stp24/icons/buildings/jobProgress/farmside_onConstruction.png"),
            Map.entry("refinery", "de/uniks/stp24/icons/buildings/jobProgress/coal_querry_onConstruction.png"),
            Map.entry("research_lab", "de/uniks/stp24/icons/buildings/jobProgress/scout_hub_onConstruction.png"),
            Map.entry("exchange", "de/uniks/stp24/icons/buildings/jobProgress/seaside_hut_onConstruction.png"),
            Map.entry("shipyard", "de/uniks/stp24/icons/buildings/jobProgress/shipyard_onConstruction.png"),
            Map.entry("fortress", "de/uniks/stp24/icons/buildings/jobProgress/stronghold_onConstruction.png")
    );

    public static final String[] imagePaths = {
            "de/uniks/stp24/icons/buildings/coal_querry.png",
            "de/uniks/stp24/icons/buildings/chophouse.png",
            "de/uniks/stp24/icons/buildings/alloy_smeltery.png",
            "de/uniks/stp24/icons/buildings/scout_hub.png",
            "de/uniks/stp24/icons/buildings/farmside.png",
            "de/uniks/stp24/icons/buildings/resonating_delves.png",
            "de/uniks/stp24/icons/buildings/theurgy_hall.png",
            "de/uniks/stp24/icons/buildings/seaside_hut.png",
            "de/uniks/stp24/icons/buildings/shipyard.png",
            "de/uniks/stp24/icons/buildings/stronghold.png"
    };
    public static final Map<String, String> technologyIconMap = Map.ofEntries(
            Map.entry("biology", "de/uniks/stp24/assets/technologies/tags/biology.png"),
            Map.entry("computing" , "de/uniks/stp24/assets/technologies/tags/computing.png"),
            Map.entry("construction" , "de/uniks/stp24/assets/technologies/tags/construction.png"),
            Map.entry("economy" , "de/uniks/stp24/assets/technologies/tags/economy.png"),
            Map.entry("energy" , "de/uniks/stp24/assets/technologies/tags/energy.png"),
            Map.entry("engineering" , "de/uniks/stp24/assets/technologies/tags/engineering.png"),
            Map.entry("materials" , "de/uniks/stp24/assets/technologies/tags/materials.png"),
            Map.entry("military" , "de/uniks/stp24/assets/technologies/tags/military.png"),
            Map.entry("physics" , "de/uniks/stp24/assets/technologies/tags/physics.png"),
            Map.entry("production" , "de/uniks/stp24/assets/technologies/tags/production.png"),
            Map.entry("propulsion" , "de/uniks/stp24/assets/technologies/tags/propulsion.png"),
            Map.entry("rare" , "de/uniks/stp24/assets/technologies/tags/rare.png"),
            Map.entry("society" , "de/uniks/stp24/assets/technologies/tags/society.png"),
            Map.entry("state" , "de/uniks/stp24/assets/technologies/tags/state.png"),
            Map.entry("weaponry", "de/uniks/stp24/assets/technologies/tags/weaponry.png"),
            Map.entry("shipmaking", "de/uniks/stp24/assets/technologies/tags/shipmaking.png")
    );

    public static final Map<String, String> shipIconMap = Map.ofEntries(
            Map.entry("explorer", "de/uniks/stp24/icons/ships/ship_Image_1.png"),
            Map.entry("colonizer" , "de/uniks/stp24/icons/ships/ship_Image_1.png"),
            Map.entry("interceptor" , "de/uniks/stp24/icons/ships/ship_Image_1.png"),
            Map.entry("fighter" , "de/uniks/stp24/icons/ships/ship_Galley.png"),
            Map.entry("corvette" , "de/uniks/stp24/icons/ships/buccaneer.png"),
            Map.entry("bomber" , "de/uniks/stp24/icons/ships/ship_Image_1.png"),
            Map.entry("frigate" , "de/uniks/stp24/icons/ships/ship_Image_1.png"),
            Map.entry("destroyer" , "de/uniks/stp24/icons/ships/ship_Marauder.png"),
            Map.entry("cruiser" , "de/uniks/stp24/icons/ships/ship_Image_2.png"),
            Map.entry("vanguard" , "de/uniks/stp24/icons/ships/ship_Image_2.png"),
            Map.entry("sentinel" , "de/uniks/stp24/icons/ships/ship_Image_2.png"),
            Map.entry("battleship" , "de/uniks/stp24/icons/ships/ship_Image_2.png"),
            Map.entry("carrier" , "de/uniks/stp24/icons/ships/ship_Image_2.png"),
            Map.entry("dreadnought" , "de/uniks/stp24/icons/ships/leviathan.png")
    );

    // Sea Backgrounds
    public static final String NIGHT = "-fx-background-image: url('/de/uniks/stp24/assets/backgrounds/eclipse_seamless.png')";
    public static final String DAY = "-fx-background-image: url('/de/uniks/stp24/assets/backgrounds/sea_seamless.png')";

    public static final ArrayList<String> hints = new ArrayList<>(Arrays.asList(
            "hint.alt.s",
            "hint.alt.e",
            "hint.alt.h",
            "hint.shift.mw",
            "hint.space"
    ));

    public enum BUILT_STATUS {
            BUILT,
            QUEUED,
            NOT_BUILT
    }

    public enum POINT_TYPE {
        ISLAND,
        INTER,
        FLEET
    }

    public static final double ISLAND_HEIGHT = 162;
    public static final double ISLAND_WIDTH = 70;
    public static final double FLEET_HW = 30;
    public static final double FLEET_FROM_ISLAND_DISTANCE = FLEET_HW + ISLAND_WIDTH/2 + 60;
    public static final int ISLAND_COLLISION_RADIUS = 150;
    public static final int FLEET_COLLISION_RADIUS = 80;
    public static final double ISLAND_SCALE = 1.25;
    public static final double X_OFFSET = ISLAND_WIDTH/2 * ISLAND_SCALE + 17;
    public static final double Y_OFFSET = ISLAND_HEIGHT/2 * ISLAND_SCALE + 7;
    public static final double FOG_X_OFFSET = ISLAND_WIDTH/2 * 1.75 + 17;
    public static final double FOG_Y_OFFSET = ISLAND_HEIGHT/2 * 1.75 + 7;
}
