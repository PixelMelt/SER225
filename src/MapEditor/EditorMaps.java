package MapEditor;

import Level.Map;
import Maps.FifthMap;
import Maps.FourthMap;
import Maps.SecondMap;
import Maps.TestMap;
import Maps.TutorialMap;
import Maps.ThirdMap;
import Maps.SeventhMap;
import Maps.SixthMap;
import Maps.TitleScreenMap;
import java.util.ArrayList;

public class EditorMaps {
    public static ArrayList<String> getMapNames() {
        return new ArrayList<String>() {{
            add("TutorialMap");
            add("TestMap");
            add("TitleScreen");
            add("SecondMap");
            add("ThirdMap");
            add("FourthMap");
            add("FifthMap");
            add("SixthMap");
            add("SeventhMap");
        }};
    }

    public static Map getMapByName(String mapName) {
        switch(mapName) {
            case "TutorialMap":
                return new TutorialMap();
            case "TestMap":
                return new TestMap();
            case "TitleScreen":
                return new TitleScreenMap();
            case "SecondMap":
                return new SecondMap();
            case "ThirdMap":
                return new ThirdMap();
            case "FourthMap":
                return new FourthMap();
            case "FifthMap":
                return new FifthMap();
            case "SixthMap":
                return new SixthMap();
            case "SeventhMap":
                return new SeventhMap();
            default:
                throw new RuntimeException("Unrecognized map name");
        }
    }
}
