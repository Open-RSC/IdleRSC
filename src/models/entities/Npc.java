package models.entities;

public enum Npc implements Id {
    BANKER_95(95),
    BANKER_224(224),
    BANKER_268(268),
    BANKER_540(540),
    BANKER_617(617),
    BANKER_792(792);

    private final int id;

    Npc(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
