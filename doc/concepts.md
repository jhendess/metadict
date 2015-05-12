# Concepts

## Languages and Dictionaries

The main concept of Metadict is built around the idea of querying multiple dictionaries at the same time. In Metadict,
a dictionary is like a rule that says which language can be translated to another. The languages of a dictionary are
therefore divided in *input* and *output*, where "input" is the input language and "output" is the output language.
 
A language itself is usually a spoken language that has to contain an identifier (e.g. "en" for English) and can
optionally have a dialect. In fact, "dialect" might be wrong in a linguistic sense, since it is only used to describe a
subtype of a language. For english a valid dialect might therefore be "gb" to reference EN_GB (i.e. spoken English in
Great Britain).

Furthermore, dictionaries are described by their direction. A dictionary can either be unidirected or bidirected. If it
is unidirected it can only be looked up in one direction (input -> output), whereas bidirected dictionaries indicate
support for querying in both ways (input -> output and output -> input).  
      
## Engines and providers

Since Metadict is a meta search engine, it relies on various other engines that can be plugged into the core component.
Based on incoming queries, Metadict decides automatically which engines should be called and how. Each engine is
provided by a provider component, that provides not only an instance of the engine itself but also metadata like
information about the author and a list of dictionaries that the engine supports. 

When Metadict decides to call an engine, the engine calls its backend which is usually a webservice but can also be a
website that has to be parsed manually. The task of the engine is now to build an ``EngineQueryResult`` which contains
the extracted data from the backend.

## Result

The result set is divided in different types of objects: 

  - DictionaryEntry:
  Dictionary entries contain a certain word in two different languages. Each dictionary has an entry
  type (e.g. noun or verb) and contains two ``DictionaryObject`` objects. These contain the different words for a
  single entry in different languages (remember the input and output types for dictionaries?).  
 
  - Similar recommendations:
  These are basically recommendations for the user. The user should see them *after* his query to find similar sounding
  or looking words. Note that these entries are *not* intended for autocompletion. Internally, a recommendation is
  represented as a DictionaryObject, too.
 
  - External contents:
  This entry type can be used to provide links to external contents for the user. These links might be interesting
  web articles concerning the current query.  
 
  - Lexicographic entries:
  (coming soon)

## Phases

### Startup

The latest version of Metadict relies on CDI (Context and Dependency Injection) for discovering search engines. This
decision was made to provide an easier integration with existing Java EE application servers and to avoid a rather
complex configuration.

To make a search engine visible in Metadict, you have to implement the ``SearchProvider`` and ``SearchEngine``
interfaces. The SearchProvider is used for meta information about the implemented SearchEngine and has to provide a new
instance of the engine. Since Metadict uses CDI you also have to create an (empty) *beans.xml* file. 

Upon booting Metadict, the core component will discover all available SearchProvider implementations and register
their provided meta data and engine in the EngineRegistry. From now on the engine is loaded and can be used for
querying.

### Query processing

Since Metadict is a meta search engine that coordinates and aggregates various search engines according to a user's
query, each query has to be processed in different steps:

  - QueryPlanning:
  After a basic validation, the QueryManager tries to resolve which engines should be queried according to the requested
  dictionaries. During this phase the QueryPlanner will lookup which dictionaries are supported by each engine according
  to the meta data provided by the SearchProvider.
   
  - QueryExecution:
  In this phase the previously calculated QueryPlan will be executed with a chosen ExecutionStrategy. The strategy can
  decide how it calls the chosen engines and may decide whether caching or parallelism should be used. 
  
  - Grouping:
  During the grouping phase, the results from the single engines will be grouped into several groups. Currently,
  grouping is only supported for bilingual entries. Grouping types include e.g. grouping by entry types, by
  dictionaries or no grouping at all. The type of grouping can be chosen by  the user.  
  
  - Sorting:
  After the grouping phase, the results in each group will be sorted according to a selected grouping strategy.
    
  - Collecting:
  This is the last phase where all results and performance statistics are being collected and returned to the user. 