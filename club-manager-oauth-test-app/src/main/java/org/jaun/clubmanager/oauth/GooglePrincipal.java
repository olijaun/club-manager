package org.jaun.clubmanager.oauth;

public class GooglePrincipal {
    private final String email;

    public GooglePrincipal(String email) {
        this.email = email;
    }

    public String getId() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        GooglePrincipal that = (GooglePrincipal) o;

        return email != null ? email.equals(that.email) : that.email == null;
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "GooglePrincipal{" + "id=" + email + '}';
    }
}
