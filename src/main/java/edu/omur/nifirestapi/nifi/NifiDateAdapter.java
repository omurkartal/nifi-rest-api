package edu.omur.nifirestapi.nifi;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class NifiDateAdapter extends TypeAdapter<Date> {
    private static final String SHORT_DATE_FORMAT = "HH:mm:ss Z";
    private static final String LONG_DATE_FORMAT = "MM/dd/yyyy HH:mm:ss.SSS Z";

    @Override
    public void write(JsonWriter jsonWriter, Date date) throws IOException {
        if (Objects.isNull(date)) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(date.toString());
        }
    }

    @Override
    public Date read(JsonReader jsonReader) throws IOException {
        try {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            } else {
                String date = jsonReader.nextString();
                if (date.toLowerCase().length() == 12) {
                    return new SimpleDateFormat(SHORT_DATE_FORMAT).parse(date);
                } else {
                    return new SimpleDateFormat(LONG_DATE_FORMAT).parse(date);
                }
            }
        } catch (ParseException ex) {
            throw new JsonParseException(ex);
        }
    }
}
