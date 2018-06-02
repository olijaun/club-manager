package org.jaun.clubmanager.eventstore;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import org.jaun.clubmanager.domain.model.commons.Id;
import org.jaun.clubmanager.domain.model.commons.ValueObject;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

public class StreamId extends ValueObject {
    private final String id;
    private final Category category;


    public StreamId(Id id, Category category) {
        this(id.getValue(), category);
    }

    private StreamId(String id, Category category) {
        this.id = requireNonNull(Strings.emptyToNull(id));
        this.category = requireNonNull(category);
    }

    public static StreamId parse(String value) {
        List<String> elements = Splitter.on('-').limit(2).splitToList(value);
        String id = elements.get(1);
        String category = elements.get(0);
        return new StreamId(id, new Category(category));
    }

    public String getValue() {
        return category.getName() + "-" + id;
    }

    public String getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        StreamId streamId = (StreamId) o;
        return Objects.equals(id, streamId.id) && Objects.equals(category, streamId.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, category);
    }
}
