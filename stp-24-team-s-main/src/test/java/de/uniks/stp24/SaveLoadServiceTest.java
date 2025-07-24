package de.uniks.stp24;

import de.uniks.stp24.service.SaveLoadService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SaveLoadServiceTest {

//    @Mock
//    ObjectMapper objectMapper;

    @InjectMocks
    final
    SaveLoadService saveLoadService = new SaveLoadService();

    @Test
    public void testSaveFile() {
        saveLoadService.setUserId("Ashkan");
        assertTrue(saveLoadService.fileName.contains("Ashkan"));
    }

//    @Test
//    public void testLoadGangs() {
//        try {
//            doReturn("").when(objectMapper).readValue(Paths.get(saveLoadService.fileName).toFile(), Gang[].class);
//            ObservableList<Gang> gangs = saveLoadService.loadGangs();
//            assertTrue(gangs.isEmpty());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
