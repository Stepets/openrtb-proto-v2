package com.iabtechlab.openrtb.v2.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.iabtechlab.openrtb.v2.OpenRtb;
import com.iabtechlab.openrtb.v2.OpenRtbExt;

import static com.iabtechlab.openrtb.v2.json.OpenRtbJsonUtils.endObject;
import static com.iabtechlab.openrtb.v2.json.OpenRtbJsonUtils.getCurrentName;
import static com.iabtechlab.openrtb.v2.json.OpenRtbJsonUtils.startObject;

public class OpenRtbRegsExtJsonReader extends OpenRtbJsonExtComplexReader<OpenRtb.BidRequest.Regs.Builder,
        OpenRtbExt.RegsExt.Builder> {

    public static final String GPC = "gpc";
    public static final String DSA = "dsa";

    private final TransparencyJsonReader transparencyJsonReader;

    public OpenRtbRegsExtJsonReader() {
        super(OpenRtbExt.regs, false, GPC, DSA);
        this.transparencyJsonReader = new TransparencyJsonReader();
    }

    @Override
    protected void read(OpenRtbExt.RegsExt.Builder builder, JsonParser par) throws IOException {
        switch (OpenRtbJsonUtils.getCurrentName(par)) {
            case GPC:
                builder.setGpc(par.nextTextValue());
                break;
            case DSA:
                builder.setDsa(readDsa(par));
                break;
        }
    }

    private OpenRtbExt.DsaRequest.Builder readDsa(JsonParser jsonParser) throws IOException {
        OpenRtbExt.DsaRequest.Builder builder = OpenRtbExt.DsaRequest.newBuilder();

        for (startObject(jsonParser); endObject(jsonParser); jsonParser.nextToken()) {
            final String fieldName = getCurrentName(jsonParser);
            if (jsonParser.nextToken() != JsonToken.VALUE_NULL) {
                readDsaFields(builder, jsonParser, fieldName);
            }
        }

        return builder;
    }

    private void readDsaFields(OpenRtbExt.DsaRequest.Builder builder,
                               JsonParser jsonParser,
                               String fieldName) throws IOException {
        switch (fieldName) {
            case "required":
                builder.setRequired(jsonParser.getIntValue());
                break;
            case "pubrender":
                builder.setPubrender(jsonParser.getIntValue());
                break;
            case "datatopub":
                builder.setDatatopub(jsonParser.getIntValue());
                break;
            case "transparency":
                builder.addAllTransparency(readTrancparencyList(jsonParser));
                break;
        }
    }

    private List<OpenRtbExt.Transparency> readTrancparencyList(JsonParser jsonParser) throws IOException {
        List<OpenRtbExt.Transparency> transparencies = new ArrayList<>();

        for (OpenRtbJsonUtils.startArray(jsonParser); OpenRtbJsonUtils.endArray(jsonParser); jsonParser.nextToken()) {
            transparencies.add(transparencyJsonReader.readTransparency(jsonParser));
        }

        return transparencies;
    }
}
