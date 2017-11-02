package org.xlrnet.metadict.web.db.entities;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Abstract superclass for all persisted entities.
 */
@MappedSuperclass
public abstract class AbstractMetadictEntity {

    @Id
    private String id;

    public AbstractMetadictEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
