package com.iabtechlab.openrtb.v2.json;

import com.fasterxml.jackson.core.JsonParser;
import com.iabtechlab.openrtb.v2.OpenRtb;
import com.iabtechlab.openrtb.v2.OpenRtbExt;

import java.io.IOException;

public class OpenRtbImpExtJsonReader extends OpenRtbJsonExtComplexReader<OpenRtb.BidRequest.Imp.Builder,
        OpenRtbExt.ImpExt.Builder> {

    public static final String AE = "ae";

    public OpenRtbImpExtJsonReader() {
        super(OpenRtbExt.imp, false, AE);
    }

    @Override
    protected void read(OpenRtbExt.ImpExt.Builder builder, JsonParser par) throws IOException {
        if (OpenRtbJsonUtils.getCurrentName(par).equals(AE)) {
            builder.setAe(par.nextIntValue(0));
        }
    }
}
