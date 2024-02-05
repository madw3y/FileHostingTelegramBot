package ru.madwey.services.enums;

public enum LinkType {

    //URN
    GET_DOC("file/get-doc"),
    GET_PHOTO("file/get-photo");

    private final String link;

    LinkType(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return this.link;
    }
}
