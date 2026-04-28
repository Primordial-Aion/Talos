package engine.behavior;

public interface ISelectable {
    void onSelect();
    void onDeselect();
    boolean isSelected();
}