package me.alexdevs.solstice.util.data.serializers;

import org.spongepowered.configurate.serialize.ScalarSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;

public final class DateSerializer extends ScalarSerializer<Date> {
    public static final DateSerializer TYPE = new DateSerializer();

    /**
     * ISO 8601 Date format
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);

    DateSerializer() {
        super(Date.class);
    }

    @Override
    public Date deserialize(final Type type, final Object obj) {
        try {
            return DATE_FORMATTER.parse(obj.toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object serialize(final Date item, final Predicate<Class<?>> typeSupported) {
        return DATE_FORMATTER.format(item);
    }

}