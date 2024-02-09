package com.iabtechlab.openrtb.v2.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.iabtechlab.openrtb.v2.OpenRtbExt;
import com.iabtechlab.openrtb.v2.OpenRtbExt.RegsExt;

public class OpenRtbRegsExtJsonWriter extends OpenRtbJsonExtWriter<RegsExt> {

    private final TransparencyJsonWriter transparencyJsonWriter;

    public OpenRtbRegsExtJsonWriter() {
        this.transparencyJsonWriter = new TransparencyJsonWriter();
    }

    @Override
    protected void write(RegsExt regsExt, JsonGenerator gen) throws IOException {
        if (regsExt.hasGpc()) {
            gen.writeStringField("gpc", regsExt.getGpc());
        }

        if (regsExt.hasDsa()) {
            gen.writeFieldName("dsa");
            writeDsa(regsExt.getDsa(), gen);
        }
    }

    private void writeDsa(OpenRtbExt.DsaRequest dsaRequest, JsonGenerator gen) throws IOException {
        gen.writeStartObject();
        writeDsaFields(dsaRequest, gen);
        gen.writeEndObject();
    }

    private void writeDsaFields(OpenRtbExt.DsaRequest dsaRequest, JsonGenerator gen) throws IOException {
        if (dsaRequest.hasDsarequired()) {
            gen.writeNumberField("dsarequired", dsaRequest.getDsarequired());
        }

        if (dsaRequest.hasPubrender()) {
            gen.writeNumberField("pubrender", dsaRequest.getPubrender());
        }

        if (dsaRequest.hasDatatopub()) {
            gen.writeNumberField("datatopub", dsaRequest.getDatatopub());
        }

        transparencyJsonWriter.writeTransparencies(dsaRequest.getTransparencyList(), gen);
    }
}
