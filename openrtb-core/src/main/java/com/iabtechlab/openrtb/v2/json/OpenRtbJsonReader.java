/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iabtechlab.openrtb.v2.json;

import static com.iabtechlab.openrtb.v2.json.OpenRtbJsonUtils.endArray;
import static com.iabtechlab.openrtb.v2.json.OpenRtbJsonUtils.endObject;
import static com.iabtechlab.openrtb.v2.json.OpenRtbJsonUtils.getCurrentName;
import static com.iabtechlab.openrtb.v2.json.OpenRtbJsonUtils.peekStructStart;
import static com.iabtechlab.openrtb.v2.json.OpenRtbJsonUtils.readCsvString;
import static com.iabtechlab.openrtb.v2.json.OpenRtbJsonUtils.startArray;
import static com.iabtechlab.openrtb.v2.json.OpenRtbJsonUtils.startObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.io.CharSource;
import com.google.common.io.Closeables;
import com.google.protobuf.ByteString;
import com.iabtechlab.openrtb.v2.Gender;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.App;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.BrandVersion;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Channel;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Content;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Data;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Data.Segment;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Dooh;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Device;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Geo;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Audio;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Banner;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Banner.Format;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Metric;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Native;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Pmp;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Pmp.Deal;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Qty;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Refresh;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Refresh.RefSettings;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Video;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Video.CompanionAd;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Network;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Producer;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Publisher;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Regs;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Site;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Source;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Source.SupplyChain;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Source.SupplyChain.SupplyChainNode;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.User;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.User.EID;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.User.EID.UID;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.UserAgent;
import com.iabtechlab.openrtb.v2.OpenRtb.BidResponse;
import com.iabtechlab.openrtb.v2.OpenRtb.BidResponse.SeatBid;
import com.iabtechlab.openrtb.v2.OpenRtb.BidResponse.SeatBid.Bid;
import com.iabtechlab.openrtb.v2.util.ProtoUtils;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Desserializes OpenRTB {@link BidRequest}/{@link BidResponse} messages from JSON.
 *
 * <p>This class is threadsafe.
 */
public class OpenRtbJsonReader extends AbstractOpenRtbJsonReader {

  protected OpenRtbJsonReader(OpenRtbJsonFactory factory) {
    super(factory);
  }

  /**
   * Desserializes a {@link BidRequest} from a JSON string, provided as a {@link ByteString}.
   */
  public BidRequest readBidRequest(ByteString bs) throws IOException {
    return readBidRequest(bs.newInput());
  }

  /**
   * Desserializes a {@link BidRequest} from a JSON string, provided as a {@link CharSequence}.
   */
  public BidRequest readBidRequest(CharSequence chars) throws IOException {
    return readBidRequest(CharSource.wrap(chars).openStream());
  }

  /**
   * Desserializes a {@link BidRequest} from JSON, streamed from a {@link Reader}.
   */
  public BidRequest readBidRequest(Reader reader) throws IOException {
    return ProtoUtils.built(readBidRequest(factory().getJsonFactory().createParser(reader)));
  }

  /**
   * Desserializes a {@link BidRequest} from JSON, streamed from an {@link InputStream}.
   */
  public BidRequest readBidRequest(InputStream is) throws IOException {
    try {
      return ProtoUtils.built(readBidRequest(factory().getJsonFactory().createParser(is)));
    } finally {
      Closeables.closeQuietly(is);
    }
  }

  /**
   * Desserializes a {@link BidRequest} from JSON, with a provided {@link JsonParser}
   * which allows several choices of input and encoding.
   */
  public final BidRequest.Builder readBidRequest(JsonParser par) throws IOException {
    if (emptyToNull(par)) {
      return null;
    }

    BidRequest.Builder req = BidRequest.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readBidRequestField(par, req, fieldName);
      }
    }
    return req;
  }

  protected void readBidRequestField(JsonParser par, BidRequest.Builder req, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        req.setId(par.getText());
        break;
      case "imp":
        for (startArray(par); endArray(par); par.nextToken()) {
          req.addImp(readImp(par));
        }
        break;
      case "site":
        req.setSite(readSite(par));
        break;
      case "app":
        req.setApp(readApp(par));
        break;
      case "dooh":
        req.setDooh(readDooh(par));
        break;
      case "device":
        req.setDevice(readDevice(par));
        break;
      case "user":
        req.setUser(readUser(par));
        break;
      case "test":
        req.setTest(par.getValueAsBoolean());
        break;
      case "at":
        req.setAt(par.getIntValue());
        break;
      case "tmax":
        req.setTmax(par.getIntValue());
        break;
      case "wseat":
        for (startArray(par); endArray(par); par.nextToken()) {
          req.addWseat(par.getText());
        }
        break;
      case "allimps":
        req.setAllimps(par.getValueAsBoolean());
        break;
      case "cur":
        for (startArray(par); endArray(par); par.nextToken()) {
          req.addCur(par.getText());
        }
        break;
      case "bcat":
        for (startArray(par); endArray(par); par.nextToken()) {
          String cat = par.getText();
          if (checkContentCategory(cat)) {
            req.addBcat(cat);
          }
        }
        break;
      case "cattax":
        req.setCattax(par.getIntValue());
        break;
      case "badv":
        for (startArray(par); endArray(par); par.nextToken()) {
          req.addBadv(par.getText());
        }
        break;
      case "regs":
        req.setRegs(readRegs(par));
        break;
      case "bapp":
        for (startArray(par); endArray(par); par.nextToken()) {
          req.addBapp(par.getText());
        }
        break;
      case "bseat":
        for (startArray(par); endArray(par); par.nextToken()) {
          req.addBseat(par.getText());
        }
        break;
      case "wlang":
        for (startArray(par); endArray(par); par.nextToken()) {
          req.addWlang(par.getText());
        }
        break;
      case "wlangb":
        for (startArray(par); endArray(par); par.nextToken()) {
          req.addWlangb(par.getText());
        }
        break;
      case "source":
        req.setSource(readSource(par));
        break;
      default:
        readOther(req, par, fieldName);
    }
  }

  public final Source.Builder readSource(JsonParser par) throws IOException {
    Source.Builder source = Source.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readSourceField(par, source, fieldName);
      }
    }
    return source;
  }

  protected void readSourceField(JsonParser par, Source.Builder source, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "fd":
        source.setFd(par.getValueAsBoolean());
        break;
      case "tid":
        source.setTid(par.getText());
        break;
      case "pchain":
        source.setPchain(par.getText());
        break;
      case "schain":
        source.setSchain(readSupplyChain(par));
        break;
      default:
        readOther(source, par, fieldName);
    }
  }

  public final SupplyChain.Builder readSupplyChain(JsonParser par) throws IOException {
    SupplyChain.Builder supplyChain = SupplyChain.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readSupplyChainField(par, supplyChain, fieldName);
      }
    }
    return supplyChain;
  }

  protected void readSupplyChainField(JsonParser par, SupplyChain.Builder supplyChain, String fieldName) throws IOException {
    switch (fieldName) {
      case "complete":
        supplyChain.setComplete(par.getValueAsBoolean());
        break;
      case "ver":
        supplyChain.setVer(par.getText());
        break;
      case "nodes":
        for (startArray(par); endArray(par); par.nextToken()) {
          supplyChain.addNodes(readSupplyChainNode(par));
        }
        break;
      default:
        readOther(supplyChain, par, fieldName);
    }
  }

  public final SupplyChainNode.Builder readSupplyChainNode(JsonParser par) throws IOException {
    final SupplyChainNode.Builder supplyChainNode = SupplyChainNode.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readSupplyChainNodeField(par, supplyChainNode, fieldName);
      }
    }
    return supplyChainNode;
  }

  protected void readSupplyChainNodeField(JsonParser par, SupplyChainNode.Builder supplyChainNode, String fieldName) throws IOException {
    switch (fieldName) {
      case "asi":
        supplyChainNode.setAsi(par.getText());
        break;
      case "sid":
        supplyChainNode.setSid(par.getText());
        break;
      case "rid":
        supplyChainNode.setRid(par.getText());
        break;
      case "name":
        supplyChainNode.setName(par.getText());
        break;
      case "domain":
        supplyChainNode.setDomain(par.getText());
        break;
      case "hp":
        supplyChainNode.setHp(par.getValueAsBoolean());
        break;
      default:
        readOther(supplyChainNode, par, fieldName);
    }
  }

  public final Regs.Builder readRegs(JsonParser par) throws IOException {
    Regs.Builder reg = Regs.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readRegsField(par, reg, fieldName);
      }
    }
    return reg;
  }

  protected void readRegsField(JsonParser par, Regs.Builder reg, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "coppa":
        reg.setCoppa(par.getValueAsBoolean());
        break;
      case "gpp":
        reg.setGpp(par.getText());
        break;
      case "gpp_sid":
        for (startArray(par); endArray(par); par.nextToken()) {
          reg.addGppSid(par.getIntValue());
        }
        break;
      case "gdpr":
        reg.setGdpr(par.getValueAsBoolean());
        break;
      case "us_privacy":
        reg.setUsPrivacy(par.getText());
        break;
      default:
        readOther(reg, par, fieldName);
    }
  }

  public final Imp.Builder readImp(JsonParser par) throws IOException {
    Imp.Builder imp = Imp.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readImpField(par, imp, fieldName);
      }
    }
    return imp;
  }

  protected void readImpField(JsonParser par, Imp.Builder imp, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        imp.setId(par.getText());
        break;
      case "banner":
        imp.setBanner(readBanner(par));
        break;
      case "video":
        imp.setVideo(readVideo(par));
        break;
      case "audio":
        imp.setAudio(readAudio(par));
        break;
      case "native":
        imp.setNative(readNative(par));
        break;
      case "displaymanager":
        imp.setDisplaymanager(par.getText());
        break;
      case "displaymanagerver":
        imp.setDisplaymanagerver(par.getText());
        break;
      case "instl":
        imp.setInstl(par.getValueAsBoolean());
        break;
      case "tagid":
        imp.setTagid(par.getText());
        break;
      case "bidfloor":
        imp.setBidfloor(par.getValueAsDouble());
        break;
      case "bidfloorcur":
        imp.setBidfloorcur(par.getText());
        break;
      case "secure":
        imp.setSecure(par.getValueAsBoolean());
        break;
      case "iframebuster":
        for (startArray(par); endArray(par); par.nextToken()) {
          imp.addIframebuster(par.getText());
        }
        break;
      case "pmp":
        imp.setPmp(readPmp(par));
        break;
      case "clickbrowser":
        imp.setClickbrowser(par.getValueAsBoolean());
        break;
      case "exp":
        imp.setExp(par.getIntValue());
        break;
      case "metric":
        for (startArray(par); endArray(par); par.nextToken()) {
          imp.addMetric(readMetric(par));
        }
        break;
      case "rwdd":
        imp.setRwdd(par.getValueAsBoolean());
        break;
      case "ssai":
        imp.setSsai(par.getIntValue());
        break;
      case "qty":
        imp.setQty(readQty(par));
        break;
      case "dt":
        imp.setDt(par.getValueAsDouble());
        break;
      case "refresh":
        imp.setRefresh(readRefresh(par));
        break;
      default:
        readOther(imp, par, fieldName);
    }
  }

  public final Metric.Builder readMetric(JsonParser par) throws IOException {
    Metric.Builder metric = Metric.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readMetricField(par, metric, fieldName);
      }
    }
    return metric;
  }

  protected void readMetricField(JsonParser par, Metric.Builder metric, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "type":
        metric.setType(par.getText());
        break;
      case "value":
        metric.setValue(par.getValueAsDouble());
        break;
      case "vendor":
        metric.setVendor(par.getText());
        break;
      default:
        readOther(metric, par, fieldName);
    }
  }

  public final Qty.Builder readQty(JsonParser par) throws IOException {
    Qty.Builder qty = Qty.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readQtyField(par, qty, fieldName);
      }
    }
    return qty;
  }

  protected void readQtyField(JsonParser par, Qty.Builder qty, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "multiplier":
        qty.setMultiplier(par.getValueAsDouble());
        break;
      case "sourcetype":
        qty.setSourcetype(par.getIntValue());
        break;
      case "vendor":
        qty.setVendor(par.getText());
        break;
      default:
        readOther(qty, par, fieldName);
    }
  }

  public final Refresh.Builder readRefresh(JsonParser par) throws IOException {
    Refresh.Builder refresh = Refresh.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readRefreshField(par, refresh, fieldName);
      }
    }
    return refresh;
  }

  protected void readRefreshField(JsonParser par, Refresh.Builder refresh, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "refsettings":
        for (startArray(par); endArray(par); par.nextToken()) {
          refresh.addRefsettings(readRefSettings(par));
        }
        break;
      case "count":
        refresh.setCount(par.getIntValue());
        break;
      default:
        readOther(refresh, par, fieldName);
    }
  }

  public final RefSettings.Builder readRefSettings(JsonParser par) throws IOException {
    RefSettings.Builder refSettings = RefSettings.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readRefSettingsField(par, refSettings, fieldName);
      }
    }
    return refSettings;
  }

  protected void readRefSettingsField(JsonParser par, RefSettings.Builder refSettings, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "reftype":
        refSettings.setReftype(par.getIntValue());
        break;
      case "minint":
        refSettings.setMinint(par.getIntValue());
        break;
      default:
        readOther(refSettings, par, fieldName);
    }
  }

  public final Native.Builder readNative(JsonParser par) throws IOException {
    Native.Builder nativ = Native.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readNativeField(par, nativ, fieldName);
      }
    }
    return nativ;
  }

  protected void readNativeField(JsonParser par, Native.Builder nativ, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "request":
        if (par.getCurrentToken() == JsonToken.VALUE_STRING) {
          nativ.setRequestNative(factory().newNativeReader().readNativeRequest(new CharArrayReader(
              par.getTextCharacters(), par.getTextOffset(), par.getTextLength())));
        } else { // Object
          nativ.setRequestNative(factory().newNativeReader().readNativeRequest(par));
        }
        break;
      case "ver":
        nativ.setVer(par.getText());
        break;
      case "api":
        for (startArray(par); endArray(par); par.nextToken()) {
          nativ.addApi(par.getIntValue());
        }
        break;
      case "battr":
        for (startArray(par); endArray(par); par.nextToken()) {
            nativ.addBattr(par.getIntValue());
        }
        break;
      default:
        readOther(nativ, par, fieldName);
    }
  }

  public final Pmp.Builder readPmp(JsonParser par) throws IOException {
    Pmp.Builder pmp = Pmp.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readPmpField(par, pmp, fieldName);
      }
    }
    return pmp;
  }

  protected void readPmpField(JsonParser par, Pmp.Builder pmp, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "private_auction":
        pmp.setPrivateAuction(par.getValueAsBoolean());
        break;
      case "deals":
        for (startArray(par); endArray(par); par.nextToken()) {
          pmp.addDeals(readDeal(par));
        }
        break;
      default:
        readOther(pmp, par, fieldName);
    }
  }

  public final Deal.Builder readDeal(JsonParser par) throws IOException {
    Deal.Builder deal = Deal.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readDealField(par, deal, fieldName);
      }
    }
    return deal;
  }

  protected void readDealField(JsonParser par, Deal.Builder deal, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        deal.setId(par.getText());
        break;
      case "bidfloor":
        deal.setBidfloor(par.getValueAsDouble());
        break;
      case "bidfloorcur":
        deal.setBidfloorcur(par.getText());
        break;
      case "wseat":
        for (startArray(par); endArray(par); par.nextToken()) {
          deal.addWseat(par.getText());
        }
        break;
      case "wadomain":
        for (startArray(par); endArray(par); par.nextToken()) {
          deal.addWadomain(par.getText());
        }
        break;
      case "at":
        deal.setAt(par.getIntValue());
        break;
      default:
        readOther(deal, par, fieldName);
    }
  }

  public final Video.Builder readVideo(JsonParser par) throws IOException {
    Video.Builder video = Video.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readVideoField(par, video, fieldName);
      }
    }
    return video;
  }

  @SuppressWarnings("deprecation")
  protected void readVideoField(JsonParser par, Video.Builder video, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "mimes":
        for (startArray(par); endArray(par); par.nextToken()) {
          video.addMimes(par.getText());
        }
        break;
      case "minduration":
        video.setMinduration(par.getIntValue());
        break;
      case "maxduration":
        video.setMaxduration(par.getIntValue());
        break;
      case "protocol":
        video.setProtocol(par.getIntValue());
        break;
      case "protocols":
        for (startArray(par); endArray(par); par.nextToken()) {
          video.addProtocols(par.getIntValue());
        }
        break;
      case "w":
        video.setW(par.getIntValue());
        break;
      case "h":
        video.setH(par.getIntValue());
        break;
      case "startdelay":
        video.setStartdelay(par.getIntValue());
        break;
      case "linearity":
        video.setLinearity(par.getIntValue());
        break;
      case "sequence":
        video.setSequence(par.getIntValue());
        break;
      case "battr":
        for (startArray(par); endArray(par); par.nextToken()) {
          video.addBattr(par.getIntValue());
        }
        break;
      case "maxextended":
        video.setMaxextended(par.getIntValue());
        break;
      case "minbitrate":
        video.setMinbitrate(par.getIntValue());
        break;
      case "maxbitrate":
        video.setMaxbitrate(par.getIntValue());
        break;
      case "boxingallowed":
        video.setBoxingallowed(par.getValueAsBoolean());
        break;
      case "playbackmethod":
        for (startArray(par); endArray(par); par.nextToken()) {
          video.addPlaybackmethod(par.getIntValue());
        }
        break;
      case "delivery":
        for (startArray(par); endArray(par); par.nextToken()) {
          video.addDelivery(par.getIntValue());
        }
        break;
      case "pos":
        video.setPos(par.getIntValue());
        break;
      case "companionad":
        if (peekStructStart(par) == JsonToken.START_ARRAY) {
          // OpenRTB 2.2+
          for (startArray(par); endArray(par); par.nextToken()) {
            video.addCompanionad(readBanner(par));
          }
        } else { // START_OBJECT
          // OpenRTB 2.1-
          video.setCompanionad21(readCompanionAd(par));
        }
        break;
      case "api":
        for (startArray(par); endArray(par); par.nextToken()) {
          video.addApi(par.getIntValue());
        }
        break;
      case "companiontype":
        for (startArray(par); endArray(par); par.nextToken()) {
          video.addCompaniontype(par.getIntValue());
        }
        break;
      case "skip":
        video.setSkip(par.getValueAsBoolean());
        break;
      case "skipmin":
        video.setSkipmin(par.getIntValue());
        break;
      case "skipafter":
        video.setSkipafter(par.getIntValue());
        break;
      case "placement":
        video.setPlacement(par.getIntValue());
        break;
      case "playbackend":
        video.setPlaybackend(par.getIntValue());
        break;
      case "maxseq":
        video.setMaxseq(par.getIntValue());
        break;
      case "poddur":
        video.setPoddur(par.getIntValue());
        break;
      case "podid":
        video.setPodid(par.getText());
        break;
      case "podseq":
        video.setPodseq(par.getIntValue());
        break;
      case "rqddurs":
        for (startArray(par); endArray(par); par.nextToken()) {
          video.addRqddurs(par.getIntValue());
        }
        break;
      case "slotinpod":
        video.setSlotinpod(par.getIntValue());
        break;
      case "mincpmpersec":
        video.setMincpmpersec(par.getValueAsDouble());
        break;
      case "plcmt":
        video.setPlcmt(par.getIntValue());
        break;
      default:
        readOther(video, par, fieldName);
    }
  }

  public final CompanionAd.Builder readCompanionAd(JsonParser par) throws IOException {
    CompanionAd.Builder companionad = CompanionAd.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readCompanionAdField(par, companionad, fieldName);
      }
    }
    return companionad;
  }

  protected void readCompanionAdField(
      JsonParser par, CompanionAd.Builder companionad, String fieldName) throws IOException {
    switch (fieldName) {
      case "banner":
        for (startArray(par); endArray(par); par.nextToken()) {
          companionad.addBanner(readBanner(par));
        }
        break;
      default:
        readOther(companionad, par, fieldName);
    }
  }

  public final Audio.Builder readAudio(JsonParser par) throws IOException {
    Audio.Builder audio = Audio.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readAudioField(par, audio, fieldName);
      }
    }
    return audio;
  }

  protected void readAudioField(JsonParser par, Audio.Builder audio, String fieldName)
      throws IOException {
    // Video & Audio common

    switch (fieldName) {
      case "mimes":
        for (startArray(par); endArray(par); par.nextToken()) {
          audio.addMimes(par.getText());
        }
        break;
      case "minduration":
        audio.setMinduration(par.getIntValue());
        break;
      case "maxduration":
        audio.setMaxduration(par.getIntValue());
        break;
      case "protocols":
        for (startArray(par); endArray(par); par.nextToken()) {
          audio.addProtocols(par.getIntValue());
        }
        break;
      case "startdelay":
        audio.setStartdelay(par.getIntValue());
        break;
      case "sequence":
        audio.setSequence(par.getIntValue());
        break;
      case "battr":
        for (startArray(par); endArray(par); par.nextToken()) {
          audio.addBattr(par.getIntValue());
        }
        break;
      case "maxextended":
        audio.setMaxextended(par.getIntValue());
        break;
      case "minbitrate":
        audio.setMinbitrate(par.getIntValue());
        break;
      case "maxbitrate":
        audio.setMaxbitrate(par.getIntValue());
        break;
      case "delivery":
        for (startArray(par); endArray(par); par.nextToken()) {
          audio.addDelivery(par.getIntValue());
        }
        break;
      case "companionad":
        if (peekStructStart(par) == JsonToken.START_ARRAY) {
          // OpenRTB 2.2+
          for (startArray(par); endArray(par); par.nextToken()) {
            audio.addCompanionad(readBanner(par));
          }
        }
        break;
      case "api":
        for (startArray(par); endArray(par); par.nextToken()) {
          audio.addApi(par.getIntValue());
        }
        break;
      case "companiontype":
        for (startArray(par); endArray(par); par.nextToken()) {
          audio.addCompaniontype(par.getIntValue());
        }
        break;

      // Audio only

      case "maxseq":
        audio.setMaxseq(par.getIntValue());
        break;
      case "feed":
        audio.setFeed(par.getIntValue());
        break;
      case "stitched":
        audio.setStitched(par.getValueAsBoolean());
        break;
      case "nvol":
        audio.setNvol(par.getIntValue());
        break;
      case "poddur":
        audio.setPoddur(par.getIntValue());
        break;
      case "rqddurs":
        for (startArray(par); endArray(par); par.nextToken()) {
          audio.addRqddurs(par.getIntValue());
        }
        break;
      case "podid":
        audio.setPodid(par.getText());
        break;
      case "podseq":
        audio.setPodseq(par.getIntValue());
        break;
      case "slotinpod":
        audio.setSlotinpod(par.getIntValue());
        break;
      case "mincpmpersec":
        audio.setMincpmpersec(par.getValueAsDouble());
        break;
      default:
        readOther(audio, par, fieldName);
    }
  }

  public final Banner.Builder readBanner(JsonParser par) throws IOException {
    Banner.Builder banner = Banner.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readBannerField(par, banner, fieldName);
      }
    }
    return banner;
  }

  @SuppressWarnings("deprecation")
  protected void readBannerField(JsonParser par, Banner.Builder banner, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "w":
        banner.setW(par.getIntValue());
        break;
      case "h":
        banner.setH(par.getIntValue());
        break;
      case "wmax":
        banner.setWmax(par.getIntValue());
        break;
      case "hmax":
        banner.setHmax(par.getIntValue());
        break;
      case "wmin":
        banner.setWmin(par.getIntValue());
        break;
      case "hmin":
        banner.setHmin(par.getIntValue());
        break;
      case "id":
        banner.setId(par.getText());
        break;
      case "btype":
        for (startArray(par); endArray(par); par.nextToken()) {
          banner.addBtype(par.getIntValue());
        }
        break;
      case "battr":
        for (startArray(par); endArray(par); par.nextToken()) {
          banner.addBattr(par.getIntValue());
        }
        break;
      case "pos":
        banner.setPos(par.getIntValue());
        break;
      case "mimes":
        for (startArray(par); endArray(par); par.nextToken()) {
          banner.addMimes(par.getText());
        }
        break;
      case "topframe":
        banner.setTopframe(par.getValueAsBoolean());
        break;
      case "expdir":
        for (startArray(par); endArray(par); par.nextToken()) {
          banner.addExpdir(par.getIntValue());
        }
        break;
      case "api":
        for (startArray(par); endArray(par); par.nextToken()) {
          banner.addApi(par.getIntValue());
        }
        break;
      case "format":
        for (startArray(par); endArray(par); par.nextToken()) {
          banner.addFormat(readFormat(par));
        }
        break;
      case "vcm":
        banner.setVcm(par.getValueAsBoolean());
        break;
      default:
        readOther(banner, par, fieldName);
    }
  }

  public final Format.Builder readFormat(JsonParser par) throws IOException {
    Format.Builder format = Format.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readFormatField(par, format, fieldName);
      }
    }
    return format;
  }

  protected void readFormatField(JsonParser par, Format.Builder format, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "w":
        format.setW(par.getIntValue());
        break;
      case "h":
        format.setH(par.getIntValue());
        break;
      case "wratio":
        format.setWratio(par.getIntValue());
        break;
      case "hratio":
        format.setHratio(par.getIntValue());
        break;
      case "wmin":
        format.setWmin(par.getIntValue());
        break;
      default:
        readOther(format, par, fieldName);
    }
  }

  public final Site.Builder readSite(JsonParser par) throws IOException {
    Site.Builder site = Site.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readSiteField(par, site, fieldName);
      }
    }
    return site;
  }

  protected void readSiteField(JsonParser par, Site.Builder site, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        site.setId(par.getText());
        break;
      case "name":
        site.setName(par.getText());
        break;
      case "domain":
        site.setDomain(par.getText());
        break;
      case "cattax":
        site.setCattax(par.getIntValue());
        break;
      case "cat":
        for (startArray(par); endArray(par); par.nextToken()) {
          String cat = par.getText();
          if (checkContentCategory(cat)) {
            site.addCat(cat);
          }
        }
        break;
      case "sectioncat":
        for (startArray(par); endArray(par); par.nextToken()) {
          String cat = par.getText();
          if (checkContentCategory(cat)) {
            site.addSectioncat(cat);
          }
        }
        break;
      case "pagecat":
        for (startArray(par); endArray(par); par.nextToken()) {
          String cat = par.getText();
          if (checkContentCategory(cat)) {
            site.addPagecat(cat);
          }
        }
        break;
      case "page":
        site.setPage(par.getText());
        break;
      case "ref":
        site.setRef(par.getText());
        break;
      case "search":
        site.setSearch(par.getText());
        break;
      case "mobile":
        site.setMobile(par.getValueAsBoolean());
        break;
      case "privacypolicy":
        site.setPrivacypolicy(par.getValueAsBoolean());
        break;
      case "publisher":
        site.setPublisher(readPublisher(par));
        break;
      case "content":
        site.setContent(readContent(par));
        break;
      case "keywords":
        site.setKeywords(readCsvString(par));
        break;
      default:
        readOther(site, par, fieldName);
    }
  }

  public final App.Builder readApp(JsonParser par) throws IOException {
    App.Builder app = App.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String name = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readAppField(par, app, name);
      }
    }
    return app;
  }

  protected void readAppField(JsonParser par, App.Builder app, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        app.setId(par.getText());
        break;
      case "name":
        app.setName(par.getText());
        break;
      case "bundle":
        app.setBundle(par.getText());
        break;
      case "domain":
        app.setDomain(par.getText());
        break;
      case "cattax":
        app.setCattax(par.getIntValue());
        break;
      case "storeurl":
        app.setStoreurl(par.getText());
        break;
      case "cat":
        for (startArray(par); endArray(par); par.nextToken()) {
          String cat = par.getText();
          if (checkContentCategory(cat)) {
            app.addCat(cat);
          }
        }
        break;
      case "sectioncat":
        for (startArray(par); endArray(par); par.nextToken()) {
          String cat = par.getText();
          if (checkContentCategory(cat)) {
            app.addSectioncat(cat);
          }
        }
        break;
      case "pagecat":
        for (startArray(par); endArray(par); par.nextToken()) {
          String cat = par.getText();
          if (checkContentCategory(cat)) {
            app.addPagecat(cat);
          }
        }
        break;
      case "ver":
        app.setVer(par.getText());
        break;
      case "privacypolicy":
        app.setPrivacypolicy(par.getValueAsBoolean());
        break;
      case "paid":
        app.setPaid(par.getValueAsBoolean());
        break;
      case "publisher":
        app.setPublisher(readPublisher(par));
        break;
      case "content":
        app.setContent(readContent(par));
        break;
      case "keywords":
        app.setKeywords(readCsvString(par));
        break;
      default:
        readOther(app, par, fieldName);
    }
  }

  public final Dooh.Builder readDooh(JsonParser par) throws IOException {
    Dooh.Builder dooh = Dooh.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readDoohField(par, dooh, fieldName);
      }
    }
    return dooh;
  }

  protected void readDoohField(JsonParser par, Dooh.Builder dooh, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        dooh.setId(par.getText());
        break;
      case "name":
        dooh.setName(par.getText());
        break;
      case "domain":
        dooh.setDomain(par.getText());
        break;
      case "venuetype":
        for (startArray(par); endArray(par); par.nextToken()) {
          dooh.addVenuetype(par.getText());
        }
        break;
      case "venuetypetax":
        dooh.setVenuetypetax(par.getIntValue());
        break;
      case "publisher":
        dooh.setPublisher(readPublisher(par));
        break;
      case "content":
        dooh.setContent(readContent(par));
        break;
      case "keywords":
        dooh.setKeywords(readCsvString(par));
        break;
      default:
        readOther(dooh, par, fieldName);
    }
  }

  public final Content.Builder readContent(JsonParser par) throws IOException {
    Content.Builder content = Content.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String name = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readContentField(par, content, name);
      }
    }
    return content;
  }

  @SuppressWarnings("deprecation")
  protected void readContentField(JsonParser par, Content.Builder content, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        content.setId(par.getText());
        break;
      case "episode":
        content.setEpisode(par.getIntValue());
        break;
      case "title":
        content.setTitle(par.getText());
        break;
      case "series":
        content.setSeries(par.getText());
        break;
      case "season":
        content.setSeason(par.getText());
        break;
      case "producer":
        content.setProducer(readProducer(par));
        break;
      case "url":
        content.setUrl(par.getText());
        break;
      case "cattax":
        content.setCattax(par.getIntValue());
        break;
      case "cat":
        for (startArray(par); endArray(par); par.nextToken()) {
          String cat = par.getText();
          if (checkContentCategory(cat)) {
            content.addCat(cat);
          }
        }
        break;
      case "videoquality":
        content.setVideoquality(par.getIntValue());
        break;
      case "context":
        try {
          // JsonParseException may be thrown because value is string in
          // 2.2 and earlier, this allows for backwards compatibility.
          content.setContext(par.getIntValue());
        } catch (JsonParseException jpe) {
        }
        break;
      case "contentrating":
        content.setContentrating(par.getText());
        break;
      case "userrating":
        content.setUserrating(par.getText());
        break;
      case "qagmediarating":
        content.setQagmediarating(par.getIntValue());
        break;
      case "keywords":
        content.setKeywords(readCsvString(par));
        break;
      case "livestream":
        content.setLivestream(par.getValueAsBoolean());
        break;
      case "sourcerelationship":
        content.setSourcerelationship(par.getValueAsBoolean());
        break;
      case "len":
        content.setLen(par.getIntValue());
        break;
      case "language":
        content.setLanguage(par.getText());
        break;
      case "langb":
        content.setLangb(par.getText());
        break;
      case "embeddable":
        content.setEmbeddable(par.getValueAsBoolean());
        break;
      case "artist":
        content.setArtist(par.getText());
        break;
      case "genre":
        content.setGenre(par.getText());
        break;
      case "album":
        content.setAlbum(par.getText());
        break;
      case "isrc":
        content.setIsrc(par.getText());
        break;
      case "prodq":
        content.setProdq(par.getIntValue());
        break;
      case "data":
        for (startArray(par); endArray(par); par.nextToken()) {
          content.addData(readData(par));
        }
        break;
      case "network":
        content.setNetwork(readNetwork(par));
        break;
      case "channel":
        content.setChannel(readChannel(par));
        break;
      default:
        readOther(content, par, fieldName);
    }
  }

  public final Channel.Builder readChannel(JsonParser par) throws IOException {
    Channel.Builder network = Channel.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readChannelField(par, network, fieldName);
      }
    }
    return network;
  }

  protected void readChannelField(JsonParser par, Channel.Builder channel, String fieldName) throws IOException {
    switch (fieldName) {
      case "id":
        channel.setId(par.getText());
        break;
      case "name":
        channel.setName(par.getText());
        break;
      case "domain":
        channel.setDomain(par.getText());
        break;
      default:
        readOther(channel, par, fieldName);
    }
  }

  public final Network.Builder readNetwork(JsonParser par) throws IOException {
    Network.Builder network = Network.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readNetworkField(par, network, fieldName);
      }
    }
    return network;
  }

  protected void readNetworkField(JsonParser par, Network.Builder network, String fieldName) throws IOException {
    switch (fieldName) {
      case "id":
        network.setId(par.getText());
        break;
      case "name":
        network.setName(par.getText());
        break;
      case "domain":
        network.setDomain(par.getText());
        break;
      default:
        readOther(network, par, fieldName);
    }
  }

  public final Producer.Builder readProducer(JsonParser par) throws IOException {
    Producer.Builder producer = Producer.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readProducerField(par, producer, fieldName);
      }
    }
    return producer;
  }

  protected void readProducerField(JsonParser par, Producer.Builder producer, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        producer.setId(par.getText());
        break;
      case "name":
        producer.setName(par.getText());
        break;
      case "cattax":
        producer.setCattax(par.getIntValue());
        break;
      case "cat":
        for (startArray(par); endArray(par); par.nextToken()) {
          String cat = par.getText();
          if (checkContentCategory(cat)) {
            producer.addCat(cat);
          }
        }
        break;
      case "domain":
        producer.setDomain(par.getText());
        break;
      default:
        readOther(producer, par, fieldName);
    }
  }

  public final Publisher.Builder readPublisher(JsonParser par) throws IOException {
    Publisher.Builder publisher = Publisher.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readPublisherField(par, publisher, fieldName);
      }
    }
    return publisher;
  }

  protected void readPublisherField(JsonParser par, Publisher.Builder publisher, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        publisher.setId(par.getText());
        break;
      case "name":
        publisher.setName(par.getText());
        break;
      case "cattax":
        publisher.setCattax(par.getIntValue());
        break;
      case "cat":
        for (startArray(par); endArray(par); par.nextToken()) {
          String cat = par.getText();
          if (checkContentCategory(cat)) {
            publisher.addCat(cat);
          }
        }
        break;
      case "domain":
        publisher.setDomain(par.getText());
        break;
      default:
        readOther(publisher, par, fieldName);
    }
  }

  public final Device.Builder readDevice(JsonParser par) throws IOException {
    Device.Builder device = Device.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readDeviceField(par, device, fieldName);
      }
    }
    return device;
  }

  protected void readDeviceField(JsonParser par, Device.Builder device, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "ua":
        device.setUa(par.getText());
        break;
      case "geo":
        device.setGeo(readGeo(par));
        break;
      case "dnt":
        device.setDnt(par.getValueAsBoolean());
        break;
      case "lmt":
        device.setLmt(par.getValueAsBoolean());
        break;
      case "ip":
        device.setIp(par.getText());
        break;
      case "ipv6":
        device.setIpv6(par.getText());
        break;
      case "devicetype":
        device.setDevicetype(par.getIntValue());
        break;
      case "make":
        device.setMake(par.getText());
        break;
      case "model":
        device.setModel(par.getText());
        break;
      case "os":
        device.setOs(par.getText());
        break;
      case "osv":
        device.setOsv(par.getText());
        break;
      case "hwv":
        device.setHwv(par.getText());
        break;
      case "w":
        device.setW(par.getIntValue());
        break;
      case "h":
        device.setH(par.getIntValue());
        break;
      case "ppi":
        device.setPpi(par.getIntValue());
        break;
      case "pxratio":
        device.setPxratio(par.getValueAsDouble());
        break;
      case "js":
        device.setJs(par.getValueAsBoolean());
        break;
      case "flashver":
        device.setFlashver(par.getText());
        break;
      case "language":
        device.setLanguage(par.getText());
        break;
      case "langb":
        device.setLangb(par.getText());
        break;
      case "carrier":
        device.setCarrier(par.getText());
        break;
      case "connectiontype":
        device.setConnectiontype(par.getIntValue());
        break;
      case "ifa":
        device.setIfa(par.getText());
        break;
      case "didsha1":
        device.setDidsha1(par.getText());
        break;
      case "didmd5":
        device.setDidmd5(par.getText());
        break;
      case "dpidsha1":
        device.setDpidsha1(par.getText());
        break;
      case "dpidmd5":
        device.setDpidmd5(par.getText());
        break;
      case "macsha1":
        device.setMacsha1(par.getText());
        break;
      case "macmd5":
        device.setMacmd5(par.getText());
        break;
      case "geofetch":
        device.setGeofetch(par.getValueAsBoolean());
        break;
      case "mccmnc":
        device.setMccmnc(par.getText());
        break;
      case "sua":
        device.setSua(readSua(par));
        break;
      default:
        readOther(device, par, fieldName);
    }
  }

  public final Geo.Builder readGeo(JsonParser par) throws IOException {
    Geo.Builder geo = Geo.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readGeoField(par, geo, fieldName);
      }
    }
    return geo;
  }

  protected void readGeoField(JsonParser par, Geo.Builder geo, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "lat":
        geo.setLat(par.getValueAsDouble());
        break;
      case "lon":
        geo.setLon(par.getValueAsDouble());
        break;
      case "type":
        geo.setType(par.getIntValue());
        break;
      case "country":
        geo.setCountry(par.getText());
        break;
      case "region":
        geo.setRegion(par.getText());
        break;
      case "regionfips104":
        geo.setRegionfips104(par.getText());
        break;
      case "metro":
        geo.setMetro(par.getText());
        break;
      case "city":
        geo.setCity(par.getText());
        break;
      case "zip":
        geo.setZip(par.getText());
        break;
      case "utcoffset":
        geo.setUtcoffset(par.getIntValue());
        break;
      case "accuracy":
        geo.setAccuracy(par.getIntValue());
        break;
      case "lastfix":
        geo.setLastfix(par.getIntValue());
        break;
      case "ipservice":
        geo.setIpservice(par.getIntValue());
        break;
      default:
        readOther(geo, par, fieldName);
    }
  }

  public final UserAgent.Builder readSua(JsonParser par) throws IOException {
    UserAgent.Builder userAgent = UserAgent.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readSuaField(par, userAgent, fieldName);
      }
    }
    return userAgent;
  }

  protected void readSuaField(JsonParser par, UserAgent.Builder userAgent, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "browsers":
        for (startArray(par); endArray(par); par.nextToken()) {
          userAgent.addBrowsers(readBrandVersion(par));
        }
        break;
      case "platform":
        userAgent.setPlatform(readBrandVersion(par));
        break;
      case "mobile":
        userAgent.setMobile(par.getValueAsBoolean());
        break;
      case "architecture":
        userAgent.setArchitecture(par.getText());
        break;
      case "bitness":
        userAgent.setBitness(par.getText());
        break;
      case "model":
        userAgent.setModel(par.getText());
        break;
      case "source":
        userAgent.setSource(par.getIntValue());
        break;
      default:
        readOther(userAgent, par, fieldName);
    }
  }

  public final BrandVersion.Builder readBrandVersion(JsonParser par) throws IOException {
    BrandVersion.Builder brandVersion = BrandVersion.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readBrandVersionField(par, brandVersion, fieldName);
      }
    }
    return brandVersion;
  }

  protected void readBrandVersionField(JsonParser par, BrandVersion.Builder brandVersion, String fieldName)
          throws IOException {
    switch (fieldName) {
      case "brand":
        brandVersion.setBrand(par.getText());
        break;
      case "version":
        for (startArray(par); endArray(par); par.nextToken()) {
          brandVersion.addVersion(par.getText());
        }
        break;
      default:
        readOther(brandVersion, par, fieldName);
    }
  }

  public final User.Builder readUser(JsonParser par) throws IOException {
    User.Builder user = User.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readUserField(par, user, fieldName);
      }
    }
    return user;
  }

  protected void readUserField(JsonParser par, User.Builder user, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        user.setId(par.getText());
        break;
      case "buyerid":  // Compatibility with broken 2.3 spec; fixed in 2.3.1
      case "buyeruid":
        user.setBuyeruid(par.getText());
        break;
      case "yob":
        user.setYob(par.getIntValue());
        break;
      case "gender": {
        Gender value = Gender.forCode(par.getText());
        if (checkEnum(value)) {
          user.setGender(value.code());
        }
        break;
      }
      case "keywords":
        user.setKeywords(readCsvString(par));
        break;
      case "customdata":
        user.setCustomdata(par.getText());
        break;
      case "geo":
        user.setGeo(readGeo(par));
        break;
      case "data":
        for (startArray(par); endArray(par); par.nextToken()) {
          user.addData(readData(par));
        }
        break;
      case "consent":
        user.setConsent(par.getText());
        break;
      case "eids":
        for (startArray(par); endArray(par); par.nextToken()) {
          user.addEids(readEid(par));
        }
        break;
      default:
        readOther(user, par, fieldName);
    }
  }

  public final EID.Builder readEid(JsonParser par) throws IOException {
    EID.Builder eid = EID.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readEidField(par, eid, fieldName);
      }
    }
    return eid;
  }

  protected void readEidField(JsonParser par, EID.Builder eid, String fieldName) throws IOException {
    switch (fieldName) {
      case "source":
        eid.setSource(par.getText());
        break;
      case "uids":
        for (startArray(par); endArray(par); par.nextToken()) {
          eid.addUids(readUid(par));
        }
        break;
      default:
        readOther(eid, par, fieldName);
    }
  }

  public final UID.Builder readUid(JsonParser par) throws IOException {
    UID.Builder uid = UID.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readUidField(par, uid, fieldName);
      }
    }
    return uid;
  }

  protected void readUidField(JsonParser par, UID.Builder uid, String fieldName) throws IOException {
    switch (fieldName) {
      case "id":
        uid.setId(par.getText());
        break;
      case "atype":
        uid.setAtype(par.getIntValue());
        break;
      default:
        readOther(uid, par, fieldName);
    }
  }

  public final Data.Builder readData(JsonParser par) throws IOException {
    Data.Builder data = Data.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readDataField(par, data, fieldName);
      }
    }
    return data;
  }

  protected void readDataField(JsonParser par, Data.Builder data, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        data.setId(par.getText());
        break;
      case "name":
        data.setName(par.getText());
        break;
      case "segment":
        for (startArray(par); endArray(par); par.nextToken()) {
          data.addSegment(readSegment(par));
        }
        break;
      default:
        readOther(data, par, fieldName);
    }
  }

  public final Segment.Builder readSegment(JsonParser par) throws IOException {
    Segment.Builder segment = Segment.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readSegmentField(par, segment, fieldName);
      }
    }
    return segment;
  }

  protected void readSegmentField(JsonParser par, Segment.Builder segment, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        segment.setId(par.getText());
        break;
      case "name":
        segment.setName(par.getText());
        break;
      case "value":
        segment.setValue(par.getText());
        break;
      default:
        readOther(segment, par, fieldName);
    }
  }

  /**
   * Desserializes a {@link BidResponse} from a JSON string, provided as a {@link ByteString}.
   */
  public BidResponse readBidResponse(ByteString bs) throws IOException {
    return readBidResponse(bs.newInput());
  }

  /**
   * Desserializes a {@link BidResponse} from a JSON string, provided as a {@link CharSequence}.
   */
  public BidResponse readBidResponse(CharSequence chars) throws IOException {
    return readBidResponse(CharSource.wrap(chars).openStream());
  }

  /**
   * Desserializes a {@link BidResponse} from JSON, streamed from a {@link Reader}.
   */
  public BidResponse readBidResponse(Reader reader) throws IOException {
    return ProtoUtils.built(readBidResponse(factory().getJsonFactory().createParser(reader)));
  }

  /**
   * Desserializes a {@link BidResponse} from JSON, streamed from an {@link InputStream}.
   */
  public BidResponse readBidResponse(InputStream is) throws IOException {
    try {
      return ProtoUtils.built(readBidResponse(factory().getJsonFactory().createParser(is)));
    } finally {
      Closeables.closeQuietly(is);
    }
  }

  /**
   * Desserializes a {@link BidResponse} from JSON, with a provided {@link JsonParser}
   * which allows several choices of input and encoding.
   */
  public final BidResponse.Builder readBidResponse(JsonParser par) throws IOException {
    if (emptyToNull(par)) {
      return null;
    }
    BidResponse.Builder resp = BidResponse.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readBidResponseField(par, resp, fieldName);
      }
    }
    return resp;
  }

  protected void readBidResponseField(JsonParser par, BidResponse.Builder resp, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        resp.setId(par.getText());
        break;
      case "seatbid":
        for (startArray(par); endArray(par); par.nextToken()) {
          resp.addSeatbid(readSeatBid(par));
        }
        break;
      case "bidid":
        resp.setBidid(par.getText());
        break;
      case "cur":
        resp.setCur(par.getText());
        break;
      case "customdata":
        resp.setCustomdata(par.getText());
        break;
      case "nbr":
        resp.setNbr(par.getIntValue());
        break;
      default:
        readOther(resp, par, fieldName);
    }
  }

  public final SeatBid.Builder readSeatBid(JsonParser par) throws IOException {
    SeatBid.Builder seatbid = SeatBid.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readSeatBidField(par, seatbid, fieldName);
      }
    }
    return seatbid;
  }

  protected void readSeatBidField(JsonParser par, SeatBid.Builder seatbid, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "bid":
        for (startArray(par); endArray(par); par.nextToken()) {
          seatbid.addBid(readBid(par));
        }
        break;
      case "seat":
        seatbid.setSeat(par.getText());
        break;
      case "group":
        seatbid.setGroup(par.getValueAsBoolean());
        break;
      default:
        readOther(seatbid, par, fieldName);
    }
  }

  public final Bid.Builder readBid(JsonParser par) throws IOException {
    Bid.Builder bid = Bid.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readBidField(par, bid, fieldName);
      }
    }
    return bid;
  }

  protected void readBidField(JsonParser par, Bid.Builder bid, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        bid.setId(par.getText());
        break;
      case "impid":
        bid.setImpid(par.getText());
        break;
      case "price":
        bid.setPrice(par.getValueAsDouble());
        break;
      case "adid":
        bid.setAdid(par.getText());
        break;
      case "nurl":
        bid.setNurl(par.getText());
        break;
      case "adm":
        if (par.getCurrentToken() == JsonToken.VALUE_STRING) {
          String valueString = par.getText();
          if (valueString.startsWith("{")) {
            bid.setAdmNative(factory().newNativeReader().readNativeResponse(valueString));
          } else {
            bid.setAdm(valueString);
          }
        } else { // Object
          bid.setAdmNative(factory().newNativeReader().readNativeResponse(par));
        }
        break;
      case "adomain":
        for (startArray(par); endArray(par); par.nextToken()) {
          bid.addAdomain(par.getText());
        }
        break;
      case "bundle":
        bid.setBundle(par.getText());
        break;
      case "iurl":
        bid.setIurl(par.getText());
        break;
      case "cid":
        bid.setCid(par.getText());
        break;
      case "crid":
        bid.setCrid(par.getText());
        break;
      case "cattax":
        bid.setCattax(par.getIntValue());
        break;
      case "cat":
        for (startArray(par); endArray(par); par.nextToken()) {
          String cat = par.getText();
          if (checkContentCategory(cat)) {
            bid.addCat(cat);
          }
        }
        break;
      case "attr":
        for (startArray(par); endArray(par); par.nextToken()) {
          bid.addAttr(par.getIntValue());
        }
        break;
      case "dealid":
        bid.setDealid(par.getText());
        break;
      case "w":
        bid.setW(par.getIntValue());
        break;
      case "h":
        bid.setH(par.getIntValue());
        break;
      case "api":
        bid.setApi(par.getIntValue());
        break;
      case "protocol":
        bid.setProtocol(par.getIntValue());
        break;
      case "qagmediarating":
        bid.setQagmediarating(par.getIntValue());
        break;
      case "exp":
        bid.setExp(par.getIntValue());
        break;
      case "burl":
        bid.setBurl(par.getText());
        break;
      case "lurl":
        bid.setLurl(par.getText());
        break;
      case "tactic":
        bid.setTactic(par.getText());
        break;
      case "language":
        bid.setLanguage(par.getText());
        break;
      case "langb":
        bid.setLangb(par.getText());
        break;
      case "wratio":
        bid.setWratio(par.getIntValue());
        break;
      case "hratio":
        bid.setHratio(par.getIntValue());
        break;
      case "apis":
        for (startArray(par); endArray(par); par.nextToken()) {
          bid.addApis(par.getIntValue());
        }
        break;
      case "dur":
        bid.setDur(par.getIntValue());
        break;
      case "mtype":
        bid.setMtype(par.getIntValue());
        break;
      case "slotinpod":
        bid.setSlotinpod(par.getIntValue());
        break;
      default:
        readOther(bid, par, fieldName);
    }
  }
}
