package com.iabtechlab.openrtb.v2.json;

import com.fasterxml.jackson.core.JsonParser;
import com.iabtechlab.openrtb.v2.OpenRtb;
import com.iabtechlab.openrtb.v2.OpenRtbExt;

import java.io.IOException;

public class OpenRtbDeviceExtJsonReader extends OpenRtbJsonExtComplexReader<OpenRtb.BidRequest.Device.Builder,
        OpenRtbExt.DeviceExt.Builder> {

    public static final String CDEP = "cdep";

    public OpenRtbDeviceExtJsonReader() {
        super(OpenRtbExt.device, false, CDEP);
    }

    @Override
    protected void read(OpenRtbExt.DeviceExt.Builder builder, JsonParser par) throws IOException {
        if (OpenRtbJsonUtils.getCurrentName(par).equals(CDEP)) {
            builder.setCdep(par.nextTextValue());
        }
    }
}
