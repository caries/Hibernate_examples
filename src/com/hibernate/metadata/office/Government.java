package com.hibernate.metadata.office;

/**
 * Contains government features.
 */
public class Government {
    private long id;
    private Person chief;

    public Government() {
    }

    public Person getChief() {
        return chief;
    }

    public void setChief(Person chief) {
        this.chief = chief;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Government that = (Government) o;

        return id == that.id && chief.equals(that.chief);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + chief.hashCode();
        return result;
    }
}