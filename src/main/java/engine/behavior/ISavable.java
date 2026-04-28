package engine.behavior;

public interface ISavable {
    void onSave(java.util.Map<String, Object> data);
    void onLoad(java.util.Map<String, Object> data);
}