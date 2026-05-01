package engine.behavior;

import org.joml.Vector3f;

public interface IInteractable {
    void onInteract(org.joml.Vector3f interactorPos);
    float getInteractionRange();
    String getActionPrompt();
    boolean canInteract();
}