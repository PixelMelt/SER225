package MapEditor;

import Level.Map;
import Maps.FourthMap;
import Maps.SecondMap;
import Maps.TestMap;
import Maps.ThirdMap;
import Maps.TitleScreenMap;
import java.util.ArrayList;

public class EditorMaps {
    public static ArrayList<String> getMapNames() {
        return new ArrayList<String>() {{
            add("TestMap");
            add("TitleScreen");
            add("SecondMap");
            add("ThirdMap");
            add("FourthMap");
        }};
    }

    public static Map getMapByName(String mapName) {
        switch(mapName) {
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
            default:
                throw new RuntimeException("Unrecognized map name");
        }
    }
}
