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

public class OpenRtbBidExtJsonReader extends OpenRtbJsonExtComplexReader<OpenRtb.BidResponse.SeatBid.Bid.Builder,
        OpenRtbExt.BidExt.Builder> {

    public static final String DSA = "dsa";

    private final DsaResponseJsonReader dsaResponseJsonReader;

    public OpenRtbBidExtJsonReader() {
        super(OpenRtbExt.bid, false, DSA);
        this.dsaResponseJsonReader = new DsaResponseJsonReader();
    }

    @Override
    protected void read(OpenRtbExt.BidExt.Builder builder, JsonParser par) throws IOException {
        switch (OpenRtbJsonUtils.getCurrentName(par)) {
            case DSA:
                builder.setDsa(dsaResponseJsonReader.read(par));
                break;
        }
    }
}
