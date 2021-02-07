package scripting.idlescript;

import orsc.ORSCharacter;

public class ManButBetter extends IdleScript {
    private final int manNpcId = 11;

    @Override
    public void start(String[] parameters) {
        thieveMan();
    }

    private void thieveMan() {
        if(controller.isBatching()) {
            return;
        }

        ORSCharacter manCharacter = controller.getNearestNpcById(manNpcId, controller.isInCombat());

        controller.thieveNpc(manCharacter.serverIndex);
    }
}
