package com.iabtechlab.openrtb.v2.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.iabtechlab.openrtb.v2.OpenRtbExt.DeviceExt;

import java.io.IOException;

public class OpenRtbDeviceExtJsonWriter extends OpenRtbJsonExtWriter<DeviceExt> {

    @Override
    protected void write(DeviceExt deviceExt, JsonGenerator gen) throws IOException {
        if (deviceExt.hasCdep()) {
            gen.writeStringField("cdep", deviceExt.getCdep());
        }
    }
}
