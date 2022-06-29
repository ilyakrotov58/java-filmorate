package ru.yandex.practicum.filmorate.testUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;

public class GsonConverter {

    public static String convertObjectToJson(Object T){
        var builder = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {

                    @Override
                    public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
                        if (localDate == null) {
                            jsonWriter.value("null");
                            return;
                        }
                        jsonWriter.value(localDate.toString());
                    }

                    @Override
                    public LocalDate read(JsonReader jsonReader) throws IOException {
                        final String text = jsonReader.nextString();
                        if (text.equals("null")) {
                            return null;
                        }
                        return LocalDate.parse(text);
                    }
                }).create();

        return builder.toJson(T);
    }

}
