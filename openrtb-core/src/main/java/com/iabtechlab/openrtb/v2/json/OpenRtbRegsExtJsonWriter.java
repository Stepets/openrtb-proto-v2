package com.iabtechlab.openrtb.v2.json;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.iabtechlab.openrtb.v2.OpenRtbExt;
import com.iabtechlab.openrtb.v2.OpenRtbExt.RegsExt;

public class OpenRtbRegsExtJsonWriter extends OpenRtbJsonExtWriter<RegsExt> {

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
        if (dsaRequest.hasRequired()) {
            gen.writeNumberField("required", dsaRequest.getRequired());
        }

        if (dsaRequest.hasPubrender()) {
            gen.writeNumberField("pubrender", dsaRequest.getPubrender());
        }

        if (dsaRequest.hasDatatopub()) {
            gen.writeNumberField("datatopub", dsaRequest.getDatatopub());
        }

        writeTransparencies(dsaRequest.getTransparencyList(), gen);
    }

    private void writeTransparencies(List<OpenRtbExt.Transparency> transparencies, JsonGenerator gen) throws IOException {
        if (transparencies.isEmpty()) {
            return;
        }

        gen.writeArrayFieldStart("transparency");
        for (OpenRtbExt.Transparency transparency : transparencies) {
            writeTransparency(transparency, gen);
        }
        gen.writeEndArray();
    }

    private void writeTransparency(OpenRtbExt.Transparency transparency, JsonGenerator gen) throws IOException {
        gen.writeStartObject();
        writeTransparencyFields(transparency, gen);
        gen.writeEndObject();
    }

    private void writeTransparencyFields(OpenRtbExt.Transparency transparency, JsonGenerator gen) throws IOException {
        if (transparency.hasDomain()) {
            gen.writeStringField("domain", transparency.getDomain());
        }

        writeParams(transparency.getParamsList(), gen);
    }

    private void writeParams(List<Integer> params, JsonGenerator gen) throws IOException {
        if (params.isEmpty()) {
            return;
        }

        gen.writeArrayFieldStart("params");
        for (Integer param : params) {
            gen.writeNumber(param);
        }
        gen.writeEndArray();
    }
}
