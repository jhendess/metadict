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

    private static EntryType[] ALL_BUT_UNKNOWN;

    /**
     * Returns all entry types except for {@link #UNKNOWN}.
     */
    public static EntryType[] allButUnknown() {
        if (ALL_BUT_UNKNOWN == null) {
            synchronized (UNKNOWN) {
                ALL_BUT_UNKNOWN = new EntryType[values().length - 1];
                int i = 0;
                for (EntryType entryType : EntryType.values()) {
                    if (entryType == UNKNOWN) {
                        continue;
                    }
                    ALL_BUT_UNKNOWN[i++] = entryType;
                }
            }
        }
        return ALL_BUT_UNKNOWN;
    }

    @NotNull
    public String getDisplayname() {
        return displayname;
    }
}
