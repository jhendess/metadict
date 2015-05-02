package org.xlrnet.metadict.api.query;

import org.jetbrains.annotations.NotNull;

/**
 * The {@link EntryType} enum is used to define the concrete (word) type of an entry. In most cases these types are
 * word
 * classes like nouns, verbs or adjective. However, it is also possible to use whole phrases as a type.
 */
public enum EntryType {

    OTHER_WORD("Other word"),

    UNKNOWN("Unknown"),

    PHRASE("Phrase"),

    EXAMPLE("Example"),

    NOUN("Noun"),

    VERB("Verb"),

    ADJECTIVE("Adjective"),

    ADVERB("Adverb"),

    PREPOSITION("Preposition"),

    PRONOUN("Pronoun"),

    INTERJECTION("Interjection"),

    CONJUNCTION("Conjunction");

    EntryType(String displayname) {
        this.displayname = displayname;
    }

    private String displayname;

    @NotNull
    public String getDisplayname() {
        return displayname;
    }
}
