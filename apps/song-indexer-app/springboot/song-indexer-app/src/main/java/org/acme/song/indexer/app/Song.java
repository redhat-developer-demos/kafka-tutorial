package org.acme.song.indexer.app;

public record Song(int id, String name, String author, Operation op) {

    public enum Operation {
        ADD, MODIFY
    }

    public Song(int id, String name, String author, Operation op) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.op = (op == null ? Operation.ADD : op);
    }

    public Song(int id, String name, String author) {
        this(id, name, author, Operation.ADD);
    }

}
