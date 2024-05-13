package com.iabtechlab.openrtb.v2.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.iabtechlab.openrtb.v2.OpenRtbExt;

import static com.iabtechlab.openrtb.v2.json.OpenRtbJsonUtils.writeIntBoolField;

public class DsaResponseJsonWriter {

    private final TransparencyJsonWriter transparencyJsonWriter;

    public DsaResponseJsonWriter() {
        this.transparencyJsonWriter = new TransparencyJsonWriter();
    }

    public void write(OpenRtbExt.DsaResponse dsaResponse, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStartObject();

        writeDsaResponseField(dsaResponse, jsonGenerator);

        jsonGenerator.writeEndObject();
    }

    private void writeDsaResponseField(OpenRtbExt.DsaResponse dsaResponse, JsonGenerator jsonGenerator) throws IOException {
        if (dsaResponse.hasBehalf()) {
            jsonGenerator.writeStringField("behalf", dsaResponse.getBehalf());
        }

        if (dsaResponse.hasPaid()) {
            jsonGenerator.writeStringField("paid", dsaResponse.getPaid());
        }

        transparencyJsonWriter.writeTransparencies(dsaResponse.getTransparencyList(), jsonGenerator);

        if (dsaResponse.hasAdrender()) {
            writeIntBoolField("adrender", dsaResponse.getAdrender(), jsonGenerator);
        }
    }
}
