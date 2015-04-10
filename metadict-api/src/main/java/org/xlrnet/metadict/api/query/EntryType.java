package org.xlrnet.metadict.api.query;

/**
 * The {@link EntryType} enum is used to define the concrete (word) type of an entry. In most cases these types are
 * word
 * classes like nouns, verbs or adjective. However, it is also possible to use whole phrases as a type.
 */
public enum EntryType {

    OTHER_WORD,

    UNKNOWN,

    PHRASE,

    EXAMPLE,

    NOUN,

    VERB,

    ADJECTIVE,

    ADVERB,

    PREPOSITION,

    PRONOUN,

    INTERJECTION, CONJUNCTION

}
