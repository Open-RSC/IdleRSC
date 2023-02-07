package controller;

public class InteractableId {

    private final int id;

    public InteractableId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InteractableId that = (InteractableId) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
