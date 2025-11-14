package MapEditor;

import Engine.Config;

public class MapEditor {
    public static void main(String[] args) {
        Config.IS_EDITOR_MODE = true;
        new EditorWindow();
    }
}
