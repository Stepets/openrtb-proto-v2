package com.iabtechlab.openrtb.v2.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.iabtechlab.openrtb.v2.OpenRtbExt;
import com.iabtechlab.openrtb.v2.OpenRtbExt.ImpExt;

import java.io.IOException;

public class OpenRtbImpExtJsonWriter extends OpenRtbJsonExtWriter<OpenRtbExt.ImpExt> {

    @Override
    protected void write(ImpExt impExt, JsonGenerator gen) throws IOException {
        if (impExt.hasAe()) {
            gen.writeNumberField("ae", impExt.getAe());
        }
    }
}
