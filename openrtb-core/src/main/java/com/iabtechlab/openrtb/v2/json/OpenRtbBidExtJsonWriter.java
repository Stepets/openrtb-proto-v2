package com.iabtechlab.openrtb.v2.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.iabtechlab.openrtb.v2.OpenRtbExt.BidExt;

public class OpenRtbBidExtJsonWriter extends OpenRtbJsonExtWriter<BidExt> {

    private final DsaResponseJsonWriter dsaResponseJsonWriter;

    public OpenRtbBidExtJsonWriter() {
        this.dsaResponseJsonWriter = new DsaResponseJsonWriter();
    }

    @Override
    protected void write(BidExt bidExt, JsonGenerator gen) throws IOException {
        if (bidExt.hasDsa()) {
            gen.writeFieldName("dsa");
            dsaResponseJsonWriter.write(bidExt.getDsa(), gen);
        }
    }
}
