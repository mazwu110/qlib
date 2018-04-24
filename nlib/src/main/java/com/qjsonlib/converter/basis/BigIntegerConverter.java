package com.qjsonlib.converter.basis;

import com.qjsonlib.JsonException;
import com.qjsonlib.Log;
import com.qjsonlib.converter.TypeConverter;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

import java.math.BigInteger;

/**
 * BigInteger数据转换工具
 *
 * @author mzw
 *
 */
public final class BigIntegerConverter extends TypeConverter<BigInteger> {
    private static final String TAG = "com.mzw.library.json.converter.basis.BigDecimalConverter";

    public static final BigIntegerConverter INSTANCE = new BigIntegerConverter();

    @Override
    public BigInteger read(JsonReader jsonReader) throws JsonException {
        if (jsonReader.next() != JsonToken.NUMBER
                && jsonReader.next() != JsonToken.STRING) {
            jsonReader.skipValue();
            Log.w(TAG, "Expected a NUMBER, but was " + jsonReader.next());
            return null;
        }

        try {
            return new BigInteger(jsonReader.nextString());
        } catch (Exception ignore) {
            Log.w(TAG, "Expected a NUMBER, but was STRING", ignore);
        }
        return null;
    }

    @Override
    public void write(JsonWriter jsonWriter, BigInteger value)
            throws JsonException {
        jsonWriter.nextValue(value);
    }

}
