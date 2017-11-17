/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Jakob Hendeß
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.xlrnet.metadict.web.history.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.core.api.query.QueryRequest;
import org.xlrnet.metadict.core.services.aggregation.group.GroupingType;
import org.xlrnet.metadict.core.services.aggregation.order.OrderType;
import org.xlrnet.metadict.core.util.BilingualDictionaryUtils;
import org.xlrnet.metadict.web.auth.entities.PersistedUser;
import org.xlrnet.metadict.web.db.converter.BilingualDictionaryConverter;
import org.xlrnet.metadict.web.db.converter.LanguageConverter;
import org.xlrnet.metadict.web.db.entities.AbstractMetadictEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

/**
 * Single entry of the query log. .
 */
@Entity
@Table(name = "querylog")
@AttributeOverride(name = "id", column = @Column(name = "qlog_id", unique = true, nullable = false, length = 36))
public class QueryLogEntry extends AbstractMetadictEntity {

    private static final long serialVersionUID = -8686903484223403622L;

    /** The query that was sent by the user. */
    @Column(name = "qlog_request", nullable = false, length = 250)
    private String queryString;

    /** Time when the request was performed. */
    @Column(name = "qlog_request_time", nullable = false)
    private Instant requestTime;

    @OneToOne
    @JoinColumn(name = "qlog_user_id")
    private PersistedUser user;

    /** The requested bilingual dictionaries. */
    @ElementCollection(fetch = FetchType.EAGER, targetClass = String.class)
    @CollectionTable(name = "qlog_bilingual", joinColumns = @JoinColumn(name = "qlbd_request_id"))
    @Column(name = "qlbd_dictionary", nullable = false)
    @Convert(converter = BilingualDictionaryConverter.class)
    private List<BilingualDictionary> bilingualDictionaries;

    /** The requested monolingual languages. */
    @ElementCollection(fetch = FetchType.LAZY, targetClass = String.class)
    @CollectionTable(name = "qlog_monolingual", joinColumns = @JoinColumn(name = "qlml_request_id"))
    @Column(name = "qlml_language", nullable = false)
    @Convert(converter = LanguageConverter.class)
    private List<Language> monolingualLanguages;

    /** The requested grouping. */
    @Column(name = "qlog_grouping")
    @Enumerated(EnumType.STRING)
    private GroupingType groupingType;

    /** The requested order. */
    @Column(name = "qlog_order")
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    public QueryLogEntry() {
        super();
    }

    public QueryLogEntry(String id, QueryRequest queryRequest, PersistedUser persistedUser, Instant requestTime) {
        this.setId(id);
        this.requestTime = requestTime;
        this.bilingualDictionaries = queryRequest.getBilingualDictionaries();
        this.monolingualLanguages = queryRequest.getMonolingualLanguages();
        this.groupingType = queryRequest.getQueryGrouping();
        this.orderType = queryRequest.getQueryOrdering();
        this.queryString = queryRequest.getOriginalQueryString();
        this.user = persistedUser;
    }

    @JsonProperty("requestTime")
    public Instant getRequestTime() {
        return requestTime;
    }

    @JsonProperty("queryString")
    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @JsonIgnore
    public PersistedUser getUser() {
        return user;
    }

    public void setUser(PersistedUser user) {
        this.user = user;
    }

    @JsonProperty("dictionaries")
    public List<BilingualDictionary> getBilingualDictionaries() {
        return bilingualDictionaries;
    }

    public void setBilingualDictionaries(List<BilingualDictionary> bilingualDictionaries) {
        this.bilingualDictionaries = bilingualDictionaries;
    }

    @JsonProperty("languages")
    public List<Language> getMonolingualLanguages() {
        return monolingualLanguages;
    }

    public void setMonolingualLanguages(List<Language> monolingualLanguages) {
        this.monolingualLanguages = monolingualLanguages;
    }

    @JsonProperty("groupingType")
    public GroupingType getGroupingType() {
        return groupingType;
    }

    public void setGroupingType(GroupingType groupingType) {
        this.groupingType = groupingType;
    }

    @JsonProperty("orderType")
    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    @JsonProperty("dictionaryString")
    public String getDictionaryString() {
        return BilingualDictionary.buildQueryString(this.bilingualDictionaries);
    }
}
