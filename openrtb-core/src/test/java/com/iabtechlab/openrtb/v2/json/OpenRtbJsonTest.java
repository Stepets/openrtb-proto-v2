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

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iabtechlab.adcom.v1.enums.Enums.APIFramework;
import com.iabtechlab.adcom.v1.enums.Enums.CompanionType;
import com.iabtechlab.adcom.v1.enums.Enums.ConnectionType;
import com.iabtechlab.adcom.v1.enums.Enums.ContentContext;
import com.iabtechlab.adcom.v1.enums.Enums.Creative.Attribute;
import com.iabtechlab.adcom.v1.enums.Enums.Creative.AudioVideoType;
import com.iabtechlab.adcom.v1.enums.Enums.DeliveryMethod;
import com.iabtechlab.adcom.v1.enums.Enums.DeviceType;
import com.iabtechlab.adcom.v1.enums.Enums.ExpandableDirection;
import com.iabtechlab.adcom.v1.enums.Enums.FeedType;
import com.iabtechlab.adcom.v1.enums.Enums.LinearityMode;
import com.iabtechlab.adcom.v1.enums.Enums.LocationService;
import com.iabtechlab.adcom.v1.enums.Enums.LocationType;
import com.iabtechlab.adcom.v1.enums.Enums.MediaRating;
import com.iabtechlab.adcom.v1.enums.Enums.PlacementPosition;
import com.iabtechlab.adcom.v1.enums.Enums.PlaybackCessationMode;
import com.iabtechlab.adcom.v1.enums.Enums.PlaybackMethod;
import com.iabtechlab.adcom.v1.enums.Enums.ProductionQuality;
import com.iabtechlab.adcom.v1.enums.Enums.VideoPlacementSubtype;
import com.iabtechlab.adcom.v1.enums.Enums.VolumeNormalizationMode;
import com.iabtechlab.openrtb.v2.OpenRtb;
import com.iabtechlab.openrtb.v2.OpenRtb.BannerAdType;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.App;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.BrandVersion;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Channel;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Content;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Data;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Data.Segment;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Device;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Dooh;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Geo;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Audio;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Banner;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Metric;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Native;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Pmp;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Qty;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Refresh;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Refresh.RefSettings;
import com.iabtechlab.openrtb.v2.OpenRtb.BidRequest.Imp.Video;
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
import com.iabtechlab.openrtb.v2.OpenRtb.NativeRequest;
import com.iabtechlab.openrtb.v2.OpenRtb.NativeResponse;
import com.iabtechlab.openrtb.v2.OpenRtbExt;
import com.iabtechlab.openrtb.v2.OpenRtbExt.ImpExt;
import com.iabtechlab.openrtb.v2.Test.Test1;
import com.iabtechlab.openrtb.v2.Test.Test2;
import com.iabtechlab.openrtb.v2.TestExt;
import com.iabtechlab.openrtb.v2.TestUtil;
import com.iabtechlab.openrtb.v3.Enums.AuctionType;
import com.iabtechlab.openrtb.v3.Enums.NoBidReason;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.truth.Truth.assertThat;
import static com.iabtechlab.openrtb.v2.json.OpenRtbJsonFactoryHelper.newJsonFactory;
import static java.util.Arrays.asList;

/**
 * Tests for {@link OpenRtbJsonFactory}, {@link OpenRtbJsonReader}, {@link OpenRtbJsonWriter}.
 */
public class OpenRtbJsonTest {
  private static final Logger logger = LoggerFactory.getLogger(OpenRtbJsonTest.class);
  private static final Test1 test1 = Test1.newBuilder().setTest1("data1").build();
  private static final Test2 test2 = Test2.newBuilder().setTest2("data2").build();

  @Test
  public void testJsonGeneratedFiles() throws IOException {
    OpenRtbJsonRequestHelper.testJsonGeneratedFiles();
    OpenRtbJsonResponseHelper.testJsonGeneratedFiles();
  }

  @Test
  public void testJsonFactory() {
    assertThat(OpenRtbJsonFactory.create().getJsonFactory()).isNotNull();
    JsonFactory jf = new JsonFactory();
    assertThat(OpenRtbJsonFactory.create().setJsonFactory(jf).getJsonFactory())
        .isSameInstanceAs(jf);
    TestUtil.testCommonMethods(new Test2Reader<BidRequest.Builder>(TestExt.testRequest2, "x"));
    TestUtil.testCommonMethods(new Test4Writer());
  }

  @Test
  public void testRequest_site() throws IOException {
    testRequest(newJsonFactory(), newBidRequest().setSite(newSite()).build());
  }

  @Test
  public void testRequest_app() throws IOException {
    testRequest(newJsonFactory(), newBidRequest().setApp(newApp()).build());
  }

  @Test
  public void testRequest_dooh() throws IOException {
    testRequest(newJsonFactory(), newBidRequest().setDooh(newDooh()).build());
  }

  @Test
  public void testRequest_AlternateFields() throws IOException {
    testRequest(newJsonFactory()
        .register(new OpenRtbJsonExtWriter<Test1>() {
          @Override protected void write(Test1 ext, JsonGenerator gen) throws IOException {
            gen.writeStringField("test1", "data1");
            gen.writeStringField("test2", "data2");
            gen.writeStringField("test1", "data1");
            gen.writeStringField("test2", "data2");
          }
        }, Test1.class, BidRequest.class),
        newBidRequest().build());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testRequest_emptyMessages() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testRequest(jsonFactory, BidRequest.newBuilder().setId("0").build());
    testRequest(jsonFactory, BidRequest.newBuilder().setId("0")
        .addImp(Imp.newBuilder().setId("0"))
        .setDevice(Device.newBuilder())
        .setApp(App.newBuilder())
        .setUser(User.newBuilder())
        .setRegs(Regs.newBuilder())
        .build());
    testRequest(jsonFactory, BidRequest.newBuilder().setId("0")
        .addImp(Imp.newBuilder().setId("0")
            .setBanner(Banner.newBuilder())
            .setPmp(Pmp.newBuilder()))
        .addImp(Imp.newBuilder().setId("0")
            .setBanner(Banner.newBuilder()
                .addFormat(Banner.Format.newBuilder()))
            .setQty(Qty.newBuilder())
            .setRefresh(Refresh.newBuilder().addRefsettings(RefSettings.newBuilder())))
        .setDevice(Device.newBuilder().setGeo(Geo.newBuilder())
                         .setSua(UserAgent.newBuilder()
                                          .addBrowsers(BrandVersion.newBuilder())
                                          .setPlatform(BrandVersion.newBuilder())))
        .setSite(Site.newBuilder())
        .setUser(User.newBuilder().addData(Data.newBuilder()))
        .build());
    testRequest(jsonFactory, BidRequest.newBuilder().setId("0")
        .addImp(Imp.newBuilder().setId("0")
            .setVideo(Video.newBuilder())
            .setAudio(Audio.newBuilder())
            .setPmp(Pmp.newBuilder().addDeals(Pmp.Deal.newBuilder().setId("0"))))
        .addImp(Imp.newBuilder().setId("0")
            .setVideo(Video.newBuilder()
                .setCompanionad21(Video.CompanionAd.newBuilder()))
            .setQty(Qty.newBuilder())
            .setRefresh(Refresh.newBuilder().addRefsettings(RefSettings.newBuilder())))
        .setSite(Site.newBuilder()
            .setContent(Content.newBuilder())
            .setPublisher(Publisher.newBuilder()))
        .setUser(User.newBuilder()
            .addData(Data.newBuilder()
                .addSegment(Segment.newBuilder()))
            .addEids(EID.newBuilder()
                .addUids(UID.newBuilder())))
        .setSource(Source.newBuilder()
            .setSchain(SupplyChain.newBuilder()
                .addNodes(SupplyChainNode.newBuilder())))
        .build());
    testRequest(jsonFactory, BidRequest.newBuilder().setId("0")
        .addImp(Imp.newBuilder().setId("0")
            .setVideo(Video.newBuilder())
            .setAudio(Audio.newBuilder())
            .setPmp(Pmp.newBuilder().addDeals(Pmp.Deal.newBuilder().setId("0"))))
        .addImp(Imp.newBuilder().setId("0")
            .setVideo(Video.newBuilder().setCompanionad21(Video.CompanionAd.newBuilder()))
            .setQty(Qty.newBuilder())
            .setRefresh(Refresh.newBuilder().addRefsettings(RefSettings.newBuilder())))
        .setDooh(Dooh.newBuilder()
            .setContent(Content.newBuilder())
            .setPublisher(Publisher.newBuilder()))
        .setUser(User.newBuilder().addData(Data.newBuilder().addSegment(Segment.newBuilder())))
        .build());
    testRequest(jsonFactory, BidRequest.newBuilder().setId("0")
        .setSite(Site.newBuilder()
            .setContent(Content.newBuilder()
                    .setProducer(Producer.newBuilder())
                    .setNetwork(Network.newBuilder())
                    .setChannel(Channel.newBuilder())))
        .build());
  }

  @Test
  public void testRequest_emptyToNull() throws IOException {
    OpenRtbJsonReader reader = OpenRtbJsonFactory.create().setStrict(false).newReader();
    assertThat(reader.readBidRequest("")).isNull();
    assertThat(reader.readBidResponse("")).isNull();
  }

  @Test
  public void testRequest_extNoReadersRegistered() throws IOException {
    OpenRtbJsonReader reader = OpenRtbJsonFactory.create().newReader();
    BidRequest req = BidRequest.newBuilder().setId("0").build();
    // Based on Issue #34
    assertThat(reader.readBidRequest("{ \"ext\": { \"x\": 0 }, \"id\": \"0\" }")).isEqualTo(req);
  }

  @Test
  public void testRequest_extNoReadersConsume() throws IOException {
    OpenRtbJsonReader reader = newJsonFactory().newReader();
    BidRequest req = BidRequest.newBuilder().setId("0").build();
    // Based on Issue #34
    assertThat(reader.readBidRequest("{ \"ext\": { \"x\": { } }, \"id\": \"0\" }")).isEqualTo(req);
  }

  @Test
  public void testRequest_extBug() throws IOException {
    OpenRtbJsonReader reader = OpenRtbJsonFactory.create().newReader();
    BidRequest req = BidRequest.newBuilder().setId("0").build();
    // Based on Issue #34
    assertThat(reader.readBidRequest("{ \"ext\": { \"x\": 0, \"y\": {} }, \"id\": \"0\" }"))
        .isEqualTo(req);
  }

  @Test(expected = JsonParseException.class)
  public void testRequest_extNonObject1() throws IOException {
    newJsonFactory().newReader().readBidRequest("{ \"ext\": [ \"x\": { } ], \"id\": \"0\" }");
  }

  @Test(expected = JsonParseException.class)
  public void testRequest_extNonObject2() throws IOException {
    newJsonFactory().newReader().readBidRequest("{ \"ext\": \"x\", \"id\": \"0\" }");
  }

  @Test
  public void testResponse() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    BidResponse resp = newBidResponse(false).build();
    String jsonResp = testResponse(jsonFactory, resp);
    assertThat(jsonFactory.newWriter().writeBidResponse(newBidResponse(false).build()))
        .isEqualTo(jsonResp);
  }

  @Test
  public void testRequestWithNative() throws IOException {
    testRequestWithNative(OpenRtbJsonRequestHelper.REQUEST__SHORT_NOROOT_STRING, false, false);
    testRequestWithNative(OpenRtbJsonRequestHelper.REQUEST__SHORT_NOROOT_OBJECT, false, true);
    testRequestWithNative(OpenRtbJsonRequestHelper.REQUEST__SHORT_ROOT___STRING, true, false);
    testRequestWithNative(OpenRtbJsonRequestHelper.REQUEST__SHORT_ROOT___OBJECT, true, true);

    testRequestWithNative(OpenRtbJsonRequestHelper.REQUEST__FULL__NOROOT_STRING, false, false);
    testRequestWithNative(OpenRtbJsonRequestHelper.REQUEST__FULL__NOROOT_OBJECT, false, true);
    testRequestWithNative(OpenRtbJsonRequestHelper.REQUEST__FULL__ROOT___STRING, true, false);
    testRequestWithNative(OpenRtbJsonRequestHelper.REQUEST__FULL__ROOT___OBJECT, true, true);
  }

  @Test
  public void testRequestWithNativeCrossCheck() throws IOException {
    testRequestWithNative(
        OpenRtbJsonRequestHelper.REQUEST__SHORT_NOROOT_OBJECT,
        OpenRtbJsonRequestHelper.REQUEST__SHORT_NOROOT_STRING,
        false, false, true);
    testRequestWithNative(
        OpenRtbJsonRequestHelper.REQUEST__SHORT_NOROOT_STRING,
        OpenRtbJsonRequestHelper.REQUEST__SHORT_NOROOT_OBJECT,
        false, true, true);
    testRequestWithNative(
        OpenRtbJsonRequestHelper.REQUEST__SHORT_ROOT___OBJECT,
        OpenRtbJsonRequestHelper.REQUEST__SHORT_ROOT___STRING,
        true, false, true);
    testRequestWithNative(
        OpenRtbJsonRequestHelper.REQUEST__SHORT_ROOT___STRING,
        OpenRtbJsonRequestHelper.REQUEST__SHORT_ROOT___OBJECT,
        true, true, true);

    testRequestWithNative(
        OpenRtbJsonRequestHelper.REQUEST__FULL__NOROOT_OBJECT,
        OpenRtbJsonRequestHelper.REQUEST__FULL__NOROOT_STRING,
        false, false, true);
    testRequestWithNative(
        OpenRtbJsonRequestHelper.REQUEST__FULL__NOROOT_STRING,
        OpenRtbJsonRequestHelper.REQUEST__FULL__NOROOT_OBJECT,
        false, true, true);
    testRequestWithNative(
        OpenRtbJsonRequestHelper.REQUEST__FULL__ROOT___OBJECT,
        OpenRtbJsonRequestHelper.REQUEST__FULL__ROOT___STRING,
        true, false, true);
    testRequestWithNative(
        OpenRtbJsonRequestHelper.REQUEST__FULL__ROOT___STRING,
        OpenRtbJsonRequestHelper.REQUEST__FULL__ROOT___OBJECT,
        true, true, true);
  }

  @Test
  public void testResponseWithNative() throws IOException {
    testResponseWithNative(OpenRtbJsonResponseHelper.RESPONSE_SHORT_NOROOT_STRING, false, false);
    testResponseWithNative(OpenRtbJsonResponseHelper.RESPONSE_SHORT_NOROOT_OBJECT, false, true);
    testResponseWithNative(OpenRtbJsonResponseHelper.RESPONSE_SHORT_ROOT___STRING, true, false);
    testResponseWithNative(OpenRtbJsonResponseHelper.RESPONSE_SHORT_ROOT___OBJECT, true, true);

    testResponseWithNative(OpenRtbJsonResponseHelper.RESPONSE_FULL__NOROOT_STRING, false, false);
    testResponseWithNative(OpenRtbJsonResponseHelper.RESPONSE_FULL__NOROOT_OBJECT, false, true);
    testResponseWithNative(OpenRtbJsonResponseHelper.RESPONSE_FULL__ROOT___STRING, true, false);
    testResponseWithNative(OpenRtbJsonResponseHelper.RESPONSE_FULL__ROOT___OBJECT, true, true);
  }

  @Test
  public void testResponseWithNativeCrossCheck() throws IOException {
    testResponseWithNative(
        OpenRtbJsonResponseHelper.RESPONSE_SHORT_NOROOT_OBJECT,
        OpenRtbJsonResponseHelper.RESPONSE_SHORT_NOROOT_STRING,
        false, false, true);
    testResponseWithNative(
        OpenRtbJsonResponseHelper.RESPONSE_SHORT_NOROOT_STRING,
        OpenRtbJsonResponseHelper.RESPONSE_SHORT_NOROOT_OBJECT,
        false, true, true);
    testResponseWithNative(
        OpenRtbJsonResponseHelper.RESPONSE_SHORT_ROOT___OBJECT,
        OpenRtbJsonResponseHelper.RESPONSE_SHORT_ROOT___STRING,
        true, false, true);
    testResponseWithNative(
        OpenRtbJsonResponseHelper.RESPONSE_SHORT_ROOT___STRING,
        OpenRtbJsonResponseHelper.RESPONSE_SHORT_ROOT___OBJECT,
        true, true, true);

    testResponseWithNative(
        OpenRtbJsonResponseHelper.RESPONSE_FULL__NOROOT_OBJECT,
        OpenRtbJsonResponseHelper.RESPONSE_FULL__NOROOT_STRING,
        false, false, true);
    testResponseWithNative(
        OpenRtbJsonResponseHelper.RESPONSE_FULL__NOROOT_STRING,
        OpenRtbJsonResponseHelper.RESPONSE_FULL__NOROOT_OBJECT,
        false, true, true);
    testResponseWithNative(
        OpenRtbJsonResponseHelper.RESPONSE_FULL__ROOT___OBJECT,
        OpenRtbJsonResponseHelper.RESPONSE_FULL__ROOT___STRING,
        true, false, true);
    testResponseWithNative(
        OpenRtbJsonResponseHelper.RESPONSE_FULL__ROOT___STRING,
        OpenRtbJsonResponseHelper.RESPONSE_FULL__ROOT___OBJECT,
        true, true, true);
  }

  @Test
  public void testExt1() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testResponse(jsonFactory, BidResponse.newBuilder()
        .setId("0")
        .setExtension(TestExt.testResponse1, test1)
        .build());
  }

  @Test
  public void testExt2Repeated() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testResponse(jsonFactory, BidResponse.newBuilder()
        .setId("0")
        .addExtension(TestExt.testResponse2, test2)
        .addExtension(TestExt.testResponse2, test2)
        .build());
  }

  @Test
  public void testExt3() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testResponse(jsonFactory, BidResponse.newBuilder()
        .setId("0")
        .setExtension(TestExt.testResponse3, 99)
        .build());
  }

  @Test
  public void testExt4() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testResponse(jsonFactory, BidResponse.newBuilder()
        .setId("0")
        .addExtension(TestExt.testResponse4, 10)
        .addExtension(TestExt.testResponse4, 20)
        .build());
  }

  @Test
  public void testExt2Scalar() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testRequest(jsonFactory, BidRequest.newBuilder()
        .setId("0")
        .setExtension(TestExt.testRequest2, test2)
        .build());
  }

  @Test
  public void testExt2Double() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testResponse(jsonFactory, BidResponse.newBuilder()
        .setId("0")
        .setExtension(TestExt.testResponse2A, Test2.newBuilder().setTest2("data2a").build())
        .setExtension(TestExt.testResponse2B, Test2.newBuilder().setTest2("data2b").build())
        .build());
  }

  @Test
  public void testResponse_emptyMessages() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testResponse(jsonFactory, BidResponse.newBuilder().setId("1").build());
    testResponse(jsonFactory,
        BidResponse.newBuilder().setId("1").addSeatbid(SeatBid.newBuilder()).build());
    testResponse(jsonFactory, BidResponse.newBuilder().setId("1").addSeatbid(SeatBid.newBuilder()
        .addBid(Bid.newBuilder().setId("0").setImpid("0").setPrice(0))).build());
  }

  @Test(expected = JsonParseException.class)
  public void testBadArrayField() throws IOException, JsonParseException {
    String test = // based on Issue #10; sample message from SpotXchange with non-array "cat"
          "{\n \"id\": \"0\",\n \"imp\": [\n {\n \"id\": \"1\",\n \"banner\": "
        + "{\n \"h\": 250,\n \"w\": 300,\n \"pos\": 1\n },\n \"bidfloor\": 0.05\n }\n ],\n "
        + "\"site\": {\n \"id\": \"15047\",\n \"domain\": \"dailymotion.com\",\n "
        + "\"cat\": \"IAB1\",\n "
        + "\"page\": \"http://www.dailymotion.com\",\n "
        + "\"publisher\": {\n \"id\": \"8796\",\n \"name\": \"dailymotion\",\n "
        + "\"cat\": \"IAB3-1\",\n "
        + "\"domain\": \"dailymotion.com\"\n }\n },\n \"user\": {\n \"id\": \"0\"\n },\n "
        + "\"device\": {\n \"ua\": \"Mozilla/4.0\",\n "
        + "\"ip\": \"1.2.3.4\"\n },\n \"at\": 1,\n \"cur\": [\n \"USD\"\n ]\n}";
    newJsonFactory().newReader().readBidRequest(test);
  }

  @Test
  public void testIgnoredFields() throws IOException {
    String test =
          "{ \"id\": \"0\", "
        + "\"x1\": 10, \"at\": 1, "
        + "\"x2\": \"x\", \"x3\": [4], \"test\": 1, "
        + "\"x4\": { \"x5\": [] }, \"tmax\": 100, "
        + "\"ext\": { \"x6\": [ { \"x7\": 100, \"x8\": 3.1415 } ], \"test1\": \"*\" }"
        + "}";
    assertThat(newJsonFactory().newReader().readBidRequest(test))
        .isEqualTo(BidRequest.newBuilder()
            .setId("0")
            .setAt(AuctionType.FIRST_PRICE.getNumber())
            .setTest(true)
            .setTmax(100)
            .setExtension(TestExt.testRequest1, Test1.newBuilder().setTest1("*").build())
            .build());
  }

  @Test
  public void testNulls() throws IOException {
    String test = // based on issue #13
          "{ \"id\": \"0\",\n  \"app\": { \"content\": { "
        + "\"keywords\": null },\n \"id\": \"56600\",\n \"cat\": [\"IAB19\"],\n "
        + "\"keywords\": \"\",\n \"name\": \"Emoji Free!\",\n \"ver\": null\n } \n}";
    newJsonFactory().newReader().readBidRequest(test);
  }

  @Test
  public void testKeywordsAsArray() throws IOException {
    String test =
           "{ \"id\": \"0\",\n  \"site\": { \"content\": { "
        + "\"keywords\":  [\"foo\", \"bar\"]},\n \"id\": \"56600\",\n \"cat\": [\"IAB19\"],\n "
        + "\"keywords\": \"\",\n \"name\": \"Emoji Free!\",\n \"ver\": null\n } \n}";
    BidRequest bidRequest = newJsonFactory().newReader().readBidRequest(test);
    assertThat(bidRequest.getSite().getContent().getKeywords()).isEqualTo("foo,bar");
  }

  static void testRequest(OpenRtbJsonFactory jsonFactory, BidRequest req) throws IOException {
    String jsonReq = jsonFactory.newWriter().writeBidRequest(req);
    logger.info(jsonReq);
    jsonFactory.setStrict(false).newWriter().writeBidRequest(req);
    BidRequest req2 = jsonFactory.newReader().readBidRequest(jsonReq);
    assertThat(req2).isEqualTo(req);
    jsonFactory.setStrict(false).newReader().readBidRequest(jsonReq);
  }

  static void testRequestWithNative(
      String requestString, boolean rootNative, boolean nativeAsObject) throws IOException {
    testRequestWithNative(requestString, requestString, rootNative, nativeAsObject, false);
  }

  static void testRequestWithNative(
      String input, String result,
      boolean rootNative, boolean nativeAsObject, boolean ignoreIdField) throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory().setForceNativeAsObject(nativeAsObject);
    OpenRtb.BidRequest bidRequest = jsonFactory.newReader().readBidRequest(input);
    String jsonRequestNativeStr =
        jsonFactory.setRootNativeField(rootNative).newWriter().writeBidRequest(bidRequest);
    ObjectMapper mapper = new ObjectMapper();
    Object json = mapper.readValue(jsonRequestNativeStr, Object.class);
    jsonRequestNativeStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);

    if (ignoreIdField) {
      assertThat(cleanupIdField(jsonRequestNativeStr)).isEqualTo(cleanupIdField(result));
    } else {
      assertThat(jsonRequestNativeStr).isEqualTo(result);
    }
  }

  private static String cleanupIdField(final String jsonString) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode json = mapper.readValue(jsonString, ObjectNode.class);
    json.put("id", "1");
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
  }

  static String testResponse(OpenRtbJsonFactory jsonFactory, BidResponse resp) throws IOException {
    String jsonResp = jsonFactory.newWriter().writeBidResponse(resp);
    logger.info(jsonResp);
    jsonFactory.setStrict(false).newWriter().writeBidResponse(resp);
    OpenRtb.BidResponse resp2 = jsonFactory.newReader().readBidResponse(jsonResp);
    assertThat(resp2).isEqualTo(resp);
    jsonFactory.setStrict(false).newReader().readBidResponse(jsonResp);
    return jsonResp;
  }

  static void testResponseWithNative(
      String responseString, boolean rootNative, boolean nativeAsObject) throws IOException {
    testResponseWithNative(responseString, responseString, rootNative, nativeAsObject, false);
  }

  static void testResponseWithNative(
      String input, String result,
      boolean rootNative, boolean nativeAsObject, boolean ignoreIdField) throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory().setForceNativeAsObject(nativeAsObject);
    OpenRtb.BidResponse bidResponse1 = jsonFactory.newReader().readBidResponse(input);
    String jsonResponseNativeStr =
        jsonFactory.setRootNativeField(rootNative).newWriter().writeBidResponse(bidResponse1);
    ObjectMapper mapper = new ObjectMapper();
    Object json = mapper.readValue(jsonResponseNativeStr, Object.class);
    jsonResponseNativeStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);

    if (ignoreIdField) {
      assertThat(cleanupIdField(jsonResponseNativeStr)).isEqualTo(cleanupIdField(result));
    } else {
      assertThat(jsonResponseNativeStr).isEqualTo(result);
    }
  }

  @SuppressWarnings("deprecation")
  static BidRequest.Builder newBidRequest() {
    return BidRequest.newBuilder()
        .setId("3031323334353637")
        .addImp(Imp.newBuilder()
            .setId("imp1")
            .setBanner(Banner.newBuilder()
                .setWmax(300)
                .setWmin(200)
                .setHmax(100)
                .setHmin(50)
                .setId("banner1")
                .setPos(PlacementPosition.ATF.getNumber())
                .addBtype(BannerAdType.JAVASCRIPT_AD.getNumber())
                .addBattr(Attribute.TEXT_ONLY.getNumber())
                .addMimes("image/gif")
                .setTopframe(true)
                .addExpdir(ExpandableDirection.RIGHT.getNumber())
                .addApi(APIFramework.MRAID_1_0.getNumber())
                .setVcm(true)
                .addFormat(Banner.Format.newBuilder()
                    .setW(100)
                    .setH(80)
                    .setWratio(120)
                    .setHratio(80)
                    .setWmin(50)
                    .setExtension(TestExt.testFormat, test1))
                .setExtension(TestExt.testBanner, test1))
            .setDisplaymanager("dm1")
            .setDisplaymanagerver("1.0")
            .setInstl(false)
            .setTagid("tag1")
            .setBidfloor(100.0)
            .setBidfloorcur("USD")
            .setSecure(false)
            .addIframebuster("buster1")
            .setPmp(Pmp.newBuilder()
                .setPrivateAuction(false)
                .addDeals(Pmp.Deal.newBuilder()
                    .setId("deal1")
                    .setBidfloor(200.0)
                    .setBidfloorcur("USD")
                    .addWseat("seat2")
                    .addWadomain("goodadv1")
                    .setAt(AuctionType.SECOND_PRICE_PLUS.getNumber())
                    .setExtension(TestExt.testDeal, test1))
                .setExtension(TestExt.testPmp, test1))
            .setClickbrowser(true)
            .setExp(120)
            .addMetric(Metric.newBuilder()
                .setType("coolness")
                .setValue(1.0)
                .setVendor("Google")
                .setExtension(TestExt.testMetric, test1))
            .setRwdd(true)
            .setSsai(3)
            .setQty(Qty.newBuilder()
                .setMultiplier(2.0)
                .setSourcetype(1)
                .setVendor("Vendor"))
            .setRefresh(Refresh.newBuilder()
                .addRefsettings(RefSettings.newBuilder()
                    .setReftype(1)
                    .setMinint(30))
                .setCount(2))
            .setExtension(TestExt.testImp, test1))
        .addImp(Imp.newBuilder()
            .setId("imp2")
            .setVideo(Video.newBuilder()
                .addMimes("video/vp9")
                .setLinearity(LinearityMode.LINEAR.getNumber())
                .setMinduration(15)
                .setMaxduration(60)
                .setProtocol(AudioVideoType.VAST_3_0.getNumber())
                .addProtocols(AudioVideoType.VAST_2_0.getNumber())
                .setW(200)
                .setH(50)
                .setStartdelay(0)
                .setSequence(1)
                .addBattr(Attribute.TEXT_ONLY.getNumber())
                .setMaxextended(120)
                .setMinbitrate(1000)
                .setMaxbitrate(2000)
                .setBoxingallowed(false)
                .addPlaybackmethod(PlaybackMethod.CLICK_SOUND_ON.getNumber())
                .addDelivery(DeliveryMethod.STREAMING.getNumber())
                .setPos(PlacementPosition.ATF.getNumber())
                .addCompanionad(Banner.newBuilder()
                    .setId("compad1")
                    .setW(100)
                    .setH(50))
                .setCompanionad21(Video.CompanionAd.newBuilder()
                    .addBanner(Banner.newBuilder()
                    .setId("compad2")
                    .setW(110)
                    .setH(60))
                    .setExtension(TestExt.testCompanionAd, test1))
                .addApi(APIFramework.VAPID_2_0.getNumber())
                .addCompaniontype(CompanionType.HTML_RSRC.getNumber())
                .setSkip(true)
                .setSkipmin(45)
                .setSkipafter(10)
                .setPlacement(VideoPlacementSubtype.IN_FEED.getNumber())
                .setPlaybackend(PlaybackCessationMode.ON_EXIT_FLOAT.getNumber())
                .setMaxseq(4)
                .setPoddur(60)
                .setPodid("pod_1")
                .setPodseq(1)
                .addRqddurs(15)
                .setSlotinpod(2)
                .setMincpmpersec(60.0)
                .setPlcmt(34)
                .setExtension(TestExt.testVideo, test1)))
        .addImp(Imp.newBuilder()
            .setId("imp3")
            .setAudio(Audio.newBuilder()
                // Video/Audio common
                .addMimes("video/vp9")
                .setMinduration(15)
                .setMaxduration(60)
                .addProtocols(AudioVideoType.VAST_2_0.getNumber())
                .setStartdelay(0)
                .setSequence(1)
                .addBattr(Attribute.TEXT_ONLY.getNumber())
                .setMaxextended(120)
                .setMinbitrate(1000)
                .setMaxbitrate(2000)
                .addDelivery(DeliveryMethod.STREAMING.getNumber())
                .addCompanionad(Banner.newBuilder()
                    .setId("compad1")
                    .setW(100)
                    .setH(50))
                .addApi(APIFramework.VAPID_2_0.getNumber())
                .addCompaniontype(CompanionType.HTML_RSRC.getNumber())
                .setMaxseq(4)
                .setFeed(FeedType.PODCAST.getNumber())
                .setStitched(true)
                .setNvol(VolumeNormalizationMode.LOUDNESS_NORMALIZATION.getNumber())
                .setPoddur(60)
                .addRqddurs(15)
                .setPodid("pod_1")
                .setPodseq(1)
                .setSlotinpod(2)
                .setMincpmpersec(45.0)
                .setExtension(TestExt.testAudio, OpenRtbJsonFactoryHelper.test1))
                .setExtension(OpenRtbExt.imp,
                              ImpExt.newBuilder().setAe(ImpExt.AuctionEnvironment.SERVER_SIDE_AUCTION_VALUE).build()))
        .addImp(Imp.newBuilder()
            .setId("imp4")
            .setNative(Native.newBuilder()
                .setRequestNative(NativeRequest.newBuilder().setVer("1"))
                .setVer("1.0")
                .addApi(APIFramework.MRAID_1_0.getNumber())
                .addBattr(Attribute.TEXT_ONLY.getNumber())
                .setExtension(TestExt.testNative, test1))
            .setExtension(TestExt.testImp, test1)
            .setExtension(OpenRtbExt.imp,
                          ImpExt.newBuilder().setAe(ImpExt.AuctionEnvironment.ON_DEVICE_INTEREST_GROUP_AUCTION_VALUE)
                                .build()))
        .setDevice(Device.newBuilder()
            .setUa("Chrome")
            .setGeo(Geo.newBuilder()
                .setLat(90.0)
                .setLon(45.0)
                .setType(LocationType.GPS_LOC_SERVICE.getNumber())
                .setCountry("USA")
                .setRegion("New York")
                .setRegionfips104("US36")
                .setMetro("New York")
                .setCity("New York City")
                .setZip("10000")
                .setUtcoffset(3600)
                .setAccuracy(10)
                .setLastfix(15)
                .setIpservice(LocationService.IP2LOCATION.getNumber())
                .setExtension(TestExt.testGeo, test1))
                .setExtension(OpenRtbExt.device, OpenRtbExt.DeviceExt.newBuilder().setCdep("cdepValue").build())
            .setDnt(false)
            .setLmt(false)
            .setIp("192.168.1.0")
            .setIpv6("1:2:3:4:5:6:0:0")
            .setDevicetype(DeviceType.MOBILE_TABLET.getNumber())
            .setMake("Motorola")
            .setModel("MotoX")
            .setOs("Android")
            .setOsv("3.2.1")
            .setHwv("X")
            .setW(640)
            .setH(1024)
            .setPpi(300)
            .setPxratio(1.0)
            .setJs(true)
            .setFlashver("11")
            .setLanguage("en")
            .setLangb("enb")
            .setCarrier("77777")
            .setConnectiontype(ConnectionType.CELL_4G.getNumber())
            .setIfa("999")
            .setDidsha1("1234")
            .setDidmd5("4321")
            .setDpidsha1("5678")
            .setDpidmd5("8765")
            .setMacsha1("abc")
            .setMacmd5("xyz")
            .setGeofetch(true)
            .setMccmnc("310-005")
            .setSua(UserAgent.newBuilder()
                .addBrowsers(BrandVersion.newBuilder()
                    .setBrand("Chrome")
                    .addAllVersion(asList("1", "2"))
                    .setExtension(TestExt.testBrandVersion, test1))
                .addBrowsers(BrandVersion.newBuilder()
                    .setBrand("Windows")
                    .addAllVersion(asList("1", "2"))
                    .setExtension(TestExt.testBrandVersion, test1))
                .setPlatform(BrandVersion.newBuilder()
                    .setBrand("Chrome")
                    .addAllVersion(asList("1", "2"))
                    .setExtension(TestExt.testBrandVersion, test1))
                .setMobile(true)
                .setArchitecture("arm")
                .setBitness("64")
                .setModel("Pixel 3 XL")
                .setSource(1)
                .setExtension(TestExt.testUserAgent, test1))
            .setExtension(TestExt.testDevice, test1))
        .setUser(User.newBuilder()
            .setId("user1")
            .setBuyeruid("Picard")
            .setYob(1973)
            .setGender("M")
            .setKeywords("boldly,going")
            .setCustomdata("data1")
            .setGeo(Geo.newBuilder().setZip("12345"))
            .addData(Data.newBuilder()
                .setId("data1")
                .setName("dataname1")
                .addSegment(Segment.newBuilder()
                    .setId("seg1")
                    .setName("segname1")
                    .setValue("segval1")
                    .setExtension(TestExt.testSegment, test1))
                .setExtension(TestExt.testData, test1))
            .setConsent("consent")
            .addEids(EID.newBuilder()
                .setSource("source")
                .addUids(UID.newBuilder()
                    .setId("id")
                    .setAtype(12)
                    .setExtension(TestExt.testUID, test1))
                .setExtension(TestExt.testEID, test1))
            .setExtension(TestExt.testUser, test1))
        .setAt(AuctionType.SECOND_PRICE_PLUS.getNumber())
        .setTmax(100)
        .addWseat("seat1")
        .setAllimps(false)
        .addCur("USD")
        .addAllBcat(asList("IAB11", "IAB11-4"))
        .addBadv("badguy")
        .setCattax(200)
        .setRegs(Regs.newBuilder()
            .setCoppa(true)
            .setGpp("gpp")
            .addGppSid(1)
            .addGppSid(2)
            .setGdpr(true)
            .setUsPrivacy("us_privacy")
            .setExtension(TestExt.testRegs, test1)
            .setExtension(OpenRtbExt.regs, OpenRtbExt.RegsExt.newBuilder()
                                                             .setGpc("gpcValue")
                                                             .setDsa(newDsaRequest())
                                                             .build()))
        .setTest(false)
        .addAllBapp(asList("app1", "app2"))
        .addAllBseat(asList("seat3", "seat4"))
        .addAllWlang(asList("en", "pt"))
        .addAllWlangb(asList("enb", "ptb"))
        .setSource(Source.newBuilder()
            .setFd(true)
            .setTid("tid1")
            .setPchain("XYZ01234:ABCD56789")
            .setSchain(SupplyChain.newBuilder()
                .setComplete(true)
                .setVer("1.0")
                .addNodes(SupplyChainNode.newBuilder()
                    .setAsi("asi")
                    .setSid("sid")
                    .setRid("rid")
                    .setName("name")
                    .setDomain("domain")
                    .setHp(true)
                    .setExtension(TestExt.testSupplyChainNode, test1))
                .setExtension(TestExt.testSupplyChain, test1))
            .setExtension(TestExt.testSource, test1))
        .setExtension(TestExt.testRequest2, test2)
        .setExtension(TestExt.testRequest1, test1);
  }

  static OpenRtbExt.DsaRequest.Builder newDsaRequest() {
    return OpenRtbExt.DsaRequest.newBuilder()
                                .setDsarequired(1)
                                .setDatatopub(1)
                                .setPubrender(2)
                                .addAllTransparency(Arrays.asList(OpenRtbExt.Transparency.getDefaultInstance(),
                                                                  OpenRtbExt.Transparency.newBuilder()
                                                                                         .setDomain("someDomain")
                                                                                         .addAllDsaparams(Arrays.asList(1, 2, 3))
                                                                                         .build()));
  }

  @SuppressWarnings("deprecation")
  static Site.Builder newSite() {
    return Site.newBuilder()
        .setId("88")
        .setName("CNN")
        .setDomain("cnn.com")
        .setCattax(100)
        .addCat("IAB1")
        .addSectioncat("IAB1-2")
        .addPagecat("IAB1-2")
        .setPage("http://cnn.com/news/elections.html")
        .setPrivacypolicy(true)
        .setRef("http://referrer.com")
        .setSearch("http://google.com?q=elections")
        .setMobile(false)
        .setPublisher(Publisher.newBuilder()
            .setId("pub1")
            .setName("Turner")
            .setCattax(100)
            .addCat("IAB1")
            .setDomain("tbs.com")
            .setExtension(TestExt.testPublisher, test1))
        .setContent(Content.newBuilder()
            .setId("cont1")
            .setEpisode(1)
            .setTitle("Godzilla")
            .setSeries("Dr. Who")
            .setSeason("S4")
            .setUrl("http://who.com")
            .addCat("IAB10-2")
            .setVideoquality(ProductionQuality.PROFESSIONAL.getNumber())
            .setKeywords("sci-fi,aliens")
            .setContentrating("R")
            .setUserrating("Awesome!")
            .setContext(ContentContext.OTHER.getNumber())
            .setLivestream(false)
            .setSourcerelationship(false)
            .setProducer(Producer.newBuilder()
                .setId("prod1")
                .setName("Warner")
                .setCattax(100)
                .addCat("IAB10")
                .setDomain("http://bros.com")
                .setExtension(TestExt.testProducer, test1))
            .setLen(240)
            .setQagmediarating(MediaRating.MATURE.getNumber())
            .setEmbeddable(false)
            .setLanguage("en")
            .setLangb("enb")
            .setArtist("Beethoven")
            .setGenre("Classical")
            .setAlbum("9th")
            .setIsrc("1234")
            .setCattax(100)
            .setProdq(ProductionQuality.PROFESSIONAL.getNumber())
            .setNetwork(Network.newBuilder()
              .setId("netId")
              .setName("netName")
              .setDomain("netDomain")
              .setExtension(TestExt.testNetwork, test1))
            .setChannel(Channel.newBuilder()
              .setId("channelId")
              .setName("channelName")
              .setDomain("channelDomain")
              .setExtension(TestExt.testChannel, test1))
            .setExtension(TestExt.testContent, test1))
        .setKeywords("news,politics")
        .setExtension(TestExt.testSite, test1);
  }

  static App.Builder newApp() {
    return App.newBuilder()
        .setId("PewDiePie")
        .setName("CNN App")
        .setDomain("cnn.com")
        .setCattax(100)
        .addCat("IAB1")
        .addSectioncat("IAB1-1")
        .addPagecat("IAB1-2")
        .setVer("1.0")
        .setBundle("com.cnn.app")
        .setPrivacypolicy(true)
        .setPaid(false)
        .setPublisher(Publisher.newBuilder().setId("pub9"))
        .setContent(Content.newBuilder().setId("cont9"))
        .setKeywords("news,politics")
        .setStoreurl("http://appstore.com/cnn")
        .setExtension(TestExt.testApp, test1);
  }

  static Dooh.Builder newDooh() {
    return Dooh.newBuilder()
        .setId("Board-1")
        .setName("Brand name")
        .addVenuetype("Airport")
        .setVenuetypetax(1)
        .setPublisher(Publisher.newBuilder().setId("pub9"))
        .setDomain("billboard.com")
        .setContent(Content.newBuilder().setId("cont9"))
        .setKeywords("news,politics")
        .setExtension(TestExt.testDooh, test1);
  }

  static BidResponse.Builder newBidResponse(boolean admNative) {
    Bid.Builder bid = Bid.newBuilder()
        .setId("bid1")
        .setImpid("imp1")
        .setPrice(19.95)
        .setAdid("adid1")
        .setNurl("http://iwon.com")
        .addAdomain("http://myads.com")
        .setIurl("http://mycdn.com/ad.gif")
        .setCid("cid1")
        .setCrid("crid1")
        .setCattax(200)
        .addAttr(Attribute.TEXT_ONLY.getNumber())
        .setDealid("deal1")
        .setW(100)
        .setH(80)
        .setBundle("com.google.testapp")
        .addCat("IAB10-2")
        .setApi(APIFramework.VPAID_1_0.getNumber())
        .setProtocol(AudioVideoType.VAST_4_0.getNumber())
        .setQagmediarating(MediaRating.OVER_12.getNumber())
        .setExp(250)
        .setBurl("http://billing.com/")
        .setLurl("http://losing.com/")
        .setTactic("abc")
        .setLanguage("en")
        .setLanguage("enb")
        .setWratio(100)
        .setHratio(85)
        .addApis(1)
        .setDur(30)
        .setMtype(2)
        .setSlotinpod(1)
        .setExtension(TestExt.testBid, test1);
    if (admNative) {
      bid.setAdmNative(NativeResponse.newBuilder()
          .setVer("1.0")
          .setLink(NativeResponse.Link.newBuilder()));
    } else {
      bid.setAdm("<test></test>");
    }
    return BidResponse.newBuilder()
        .setId("resp1")
        .addSeatbid(SeatBid.newBuilder()
            .addBid(bid)
            .setSeat("seat1")
            .setGroup(false)
            .setExtension(TestExt.testSeat, test1))
        .setBidid("bid1")
        .setCur("USD")
        .setCustomdata("mydata")
        .setNbr(NoBidReason.TECHNICAL_ERROR.getNumber())
        .setExtension(TestExt.testResponse1, test1)
        .addExtension(TestExt.testResponse2, test2)
        .addExtension(TestExt.testResponse2, test2)
        .setExtension(TestExt.testResponse2A, test2)
        .setExtension(TestExt.testResponse2B, test2)
        .setExtension(TestExt.testResponse3, 99)
        .addExtension(TestExt.testResponse4, 10)
        .addExtension(TestExt.testResponse4, 20);
  }
}
