package org.jaun.clubmanager.domain.model.commons;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class StreamId extends ValueObject {

    private final String id;
    private final String category;

    public StreamId(Id id, String category) {
        this(id.getValue(), category);
    }

    private StreamId(String id, String category) {

        this.id = requireNonNull(id);
        this.category = requireNonNull(category);

        if (StringUtils.isBlank(id) || StringUtils.isBlank(category)) {
            throw new IllegalArgumentException("invalid stream id with category '" + category + "' and id '" + id + "'.");
        }
    }


    public static StreamId parse(String value) {

        List<String> elements = Splitter.on('-').limit(2).splitToList(value);
        String id = elements.get(1);
        String category = elements.get(0);
        return new StreamId(id, category);
    }

    public String getValue() {
        return category + "-" + id;
    }


    public String getId() {
        return id;
    }

    public String getCategory() {
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

