/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.sfc.provider.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.sfc.provider.AbstractDataStoreManager;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.rsp.rev140701.RenderedServicePaths;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.rsp.rev140701.rendered.service.paths.RenderedServicePath;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.rsp.rev140701.rendered.service.paths.RenderedServicePathBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.rsp.rev140701.rendered.service.paths.RenderedServicePathKey;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.rsp.rev140701.rendered.service.paths.rendered.service.path.RenderedServicePathHop;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.rsp.rev140701.rendered.service.paths.rendered.service.path.RenderedServicePathHopBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.rsp.rev140701.rendered.service.paths.rendered.service.path.RenderedServicePathHopKey;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sf.rev140701.service.function.entry.SfDataPlaneLocator;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sf.rev140701.service.function.entry.SfDataPlaneLocatorBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sf.rev140701.service.function.entry.SfDataPlaneLocatorKey;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sf.rev140701.service.functions.ServiceFunction;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sf.rev140701.service.functions.ServiceFunctionBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sf.rev140701.service.functions.ServiceFunctionKey;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.Open;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.ServiceFunctionForwarders;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.ServiceFunctionForwardersBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.ServiceFunctionForwarder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.ServiceFunctionForwarderBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.ServiceFunctionForwarderKey;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.service.function.forwarder.ServiceFunctionDictionary;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.service.function.forwarder.ServiceFunctionDictionaryBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.service.function.forwarder.ServiceFunctionDictionaryKey;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.service.function.forwarder.SffDataPlaneLocator;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.service.function.forwarder.SffDataPlaneLocatorBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.service.function.forwarder.SffDataPlaneLocatorKey;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.service.function.forwarder.service.function.dictionary.SffSfDataPlaneLocator;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.service.function.forwarder.service.function.dictionary.SffSfDataPlaneLocatorBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.service.function.forwarder.sff.data.plane.locator.DataPlaneLocatorBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.state.service.function.forwarder.state.SffServicePath;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfp.rev140701.service.function.paths.ServiceFunctionPathBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sft.rev140701.Firewall;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sl.rev140701.VxlanGpe;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sl.rev140701.data.plane.locator.locator.type.IpBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.IpAddress;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.PortNumber;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

/**
 * Tests for simple datastore operations on SFFs (i.e. Service Functions are created first)
 */
public class SfcProviderServiceForwarderAPISimpleTest extends AbstractDataStoreManager {
    private final ServiceFunctionForwarderBuilder sffBuilder = new ServiceFunctionForwarderBuilder();
    private final RenderedServicePathHopBuilder renderedServicePathHopBuilder = new RenderedServicePathHopBuilder();
    private final RenderedServicePathHop renderedServicePathHop = renderedServicePathHopBuilder.setKey(new RenderedServicePathHopKey((short) 3))
            .setServiceFunctionForwarder("SFF1").build();
    SfcProviderServiceForwarderAPI sfcProviderServiceForwarderAPI = new SfcProviderServiceForwarderAPI();
    ServiceFunctionForwarder sff;

    @Before
    public void before() {
        setOdlSfc();

        RenderedServicePathBuilder renderedServicePathBuilder = new RenderedServicePathBuilder();

        long pathId = SfcServicePathId.check_and_allocate_pathid();

        assertNotEquals("Must be not equal", pathId, -1);

        renderedServicePathBuilder.setName("RSP1")
                .setKey(new RenderedServicePathKey("RSP1"))
                .setPathId(pathId);

        List<RenderedServicePathHop> renderedServicePathHopList = new ArrayList<>();
        renderedServicePathHopList.add(renderedServicePathHop);



        renderedServicePathBuilder.setRenderedServicePathHop(renderedServicePathHopList);
        InstanceIdentifier<RenderedServicePath> rspIID =
                InstanceIdentifier.builder(RenderedServicePaths.class)
                        .child(RenderedServicePath.class, new RenderedServicePathKey("RSP1"))
                        .build();
        SfcDataStoreAPI.writePutTransactionAPI(rspIID, renderedServicePathBuilder.build(), LogicalDatastoreType.OPERATIONAL);

        ServiceFunctionForwarderBuilder sffBuilder = new ServiceFunctionForwarderBuilder();
        sffBuilder.setName("SFF1")
                .setKey(new ServiceFunctionForwarderKey("SFF1"));
        ServiceFunctionForwarder sff = sffBuilder.build();

        ServiceFunctionForwarderKey serviceFunctionForwarderKey = new ServiceFunctionForwarderKey("SFF1");
        InstanceIdentifier<ServiceFunctionForwarder> sffEntryIID = InstanceIdentifier.builder(ServiceFunctionForwarders.class).
                child(ServiceFunctionForwarder.class, serviceFunctionForwarderKey).build();

        SfcDataStoreAPI.writePutTransactionAPI(sffEntryIID, sff, LogicalDatastoreType.CONFIGURATION);
    }

    @Test
    public void testCreateReadUpdateServiceFunctionForwarder() {
        String name = "SFF1";
        String[] sfNames = {"unittest-fw-1", "unittest-dpi-1", "unittest-napt-1", "unittest-http-header-enrichment-1", "unittest-qos-1"};
        IpAddress[] ipMgmtAddress = new IpAddress[sfNames.length];

        List<SffDataPlaneLocator> locatorList = new ArrayList<>();

        IpBuilder ipBuilder = new IpBuilder();
        ipBuilder.setIp(new IpAddress(new Ipv4Address("10.1.1.101")))
                .setPort(new PortNumber(555));

        DataPlaneLocatorBuilder sffLocatorBuilder = new DataPlaneLocatorBuilder();
        sffLocatorBuilder.setLocatorType(ipBuilder.build())
                .setTransport(VxlanGpe.class);

        SffDataPlaneLocatorBuilder locatorBuilder = new SffDataPlaneLocatorBuilder();
        locatorBuilder.setName("locator-1").setKey(new SffDataPlaneLocatorKey("locator-1"))
                .setDataPlaneLocator(sffLocatorBuilder.build());

        locatorList.add(locatorBuilder.build());


        List<ServiceFunctionDictionary> dictionary = new ArrayList<>();

        SfDataPlaneLocatorBuilder sfDataPlaneLocatorBuilder = new SfDataPlaneLocatorBuilder();
        sfDataPlaneLocatorBuilder.setName("unittest-fw-1").setKey(new SfDataPlaneLocatorKey("unittest-fw-1"));

        SfDataPlaneLocator sfDataPlaneLocator = sfDataPlaneLocatorBuilder.build();
        List<ServiceFunction> sfList = new ArrayList<>();

        ServiceFunctionBuilder sfBuilder = new ServiceFunctionBuilder();
        List<SfDataPlaneLocator> dataPlaneLocatorList = new ArrayList<>();
        dataPlaneLocatorList.add(sfDataPlaneLocator);
        sfBuilder.setName(sfNames[0]).setKey(new ServiceFunctionKey("unittest-fw-1"))
                .setType(Firewall.class)
                .setIpMgmtAddress(ipMgmtAddress[0])
                .setSfDataPlaneLocator(dataPlaneLocatorList);
        sfList.add(sfBuilder.build());

        ServiceFunction sf = sfList.get(0);
        SfDataPlaneLocator sfDPLocator = sf.getSfDataPlaneLocator().get(0);
        SffSfDataPlaneLocatorBuilder sffSfDataPlaneLocatorBuilder = new SffSfDataPlaneLocatorBuilder(sfDPLocator);
        SffSfDataPlaneLocator sffSfDataPlaneLocator = sffSfDataPlaneLocatorBuilder.build();
        ServiceFunctionDictionaryBuilder dictionaryEntryBuilder = new ServiceFunctionDictionaryBuilder();
        dictionaryEntryBuilder
                .setName(sf.getName()).setKey(new ServiceFunctionDictionaryKey(sf.getName()))
                .setType(sf.getType())
                .setSffSfDataPlaneLocator(sffSfDataPlaneLocator)
                .setFailmode(Open.class)
                .setSffInterfaces(null);

        ServiceFunctionDictionary dictionaryEntry = dictionaryEntryBuilder.build();
        dictionary.add(dictionaryEntry);

        ServiceFunctionForwarderBuilder sffBuilder = new ServiceFunctionForwarderBuilder();

        ServiceFunctionForwarder sff =
                sffBuilder.setName(name).setKey(new ServiceFunctionForwarderKey(name))
                        .setSffDataPlaneLocator(locatorList)
                        .setServiceFunctionDictionary(dictionary)
                        .setServiceNode(null) // for consistency only; we are going to get rid of ServiceNodes in the future
                        .build();

        SfcProviderServiceForwarderAPI.putServiceFunctionForwarder(sff);

        ServiceFunctionForwarder sff2 = SfcProviderServiceForwarderAPI.readServiceFunctionForwarder(name);

        assertNotNull("Must be not null", sff2);
        assertEquals("Must be equal", sff2.getSffDataPlaneLocator(), locatorList);
        assertEquals("Must be equal", sff2.getServiceFunctionDictionary(), dictionary);

    }

    /*
     * test creates service function forwarder and four rendered service paths
     * these paths are added to service function forwarder state with different methods
     * all paths are then checked whether they are already in sff state or not
     * all paths are removed with different ways and checked whether they has been really removed
     */
    @Test
    public void testAddDeletePathsFromServiceForwarderState() {
        String sff = "sff";
        String rsp = "rsp";

        //create service function forwarder and write it into data store
        ServiceFunctionForwarderBuilder serviceFunctionForwarderBuilder = new ServiceFunctionForwarderBuilder();
        serviceFunctionForwarderBuilder.setName(sff)
                .setKey(new ServiceFunctionForwarderKey(sff));

        boolean transactionSuccessful = SfcProviderServiceForwarderAPI.putServiceFunctionForwarder(serviceFunctionForwarderBuilder.build());
        assertTrue("Must be true", transactionSuccessful);

        //create list of path for testing purposes
        List<RenderedServicePath> sffServicePathTestList = new ArrayList<>();
        sffServicePathTestList.add(createRenderedServicePath(rsp + 1, sff, (short) 1));
        sffServicePathTestList.add(createRenderedServicePath(rsp + 2, sff, (short) 2));
        sffServicePathTestList.add(createRenderedServicePath(rsp + 3, sff, (short) 3));
        sffServicePathTestList.add(createRenderedServicePath(rsp + 4, sff, (short) 4));

        //add two paths to service function forwarder state via rsp objects
        transactionSuccessful = SfcProviderServiceForwarderAPI.addPathToServiceForwarderState(sffServicePathTestList.get(0));
        assertTrue("Must be true", transactionSuccessful);
        transactionSuccessful = SfcProviderServiceForwarderAPI.addPathToServiceForwarderState(sffServicePathTestList.get(1));
        assertTrue("Must be true", transactionSuccessful);

        //add another two paths to service function forwarder state via rsp name
        transactionSuccessful = SfcProviderServiceForwarderAPI.addPathToServiceForwarderState(sffServicePathTestList.get(2).getName());
        assertTrue("Must be true", transactionSuccessful);
        transactionSuccessful = SfcProviderServiceForwarderAPI.addPathToServiceForwarderState(sffServicePathTestList.get(3).getName());
        assertTrue("Must be true", transactionSuccessful);

        //read service function forwarder state from data store, it should return list of all four paths
        List<SffServicePath> sffServicePaths = SfcProviderServiceForwarderAPI.readSffState(sff);
        assertNotNull("Must be not null", sffServicePaths);
        assertEquals("Must be equal", sffServicePaths.size(), 4);

        //remove path 1 via service path object
        ServiceFunctionPathBuilder serviceFunctionPathBuilder = new ServiceFunctionPathBuilder();
        serviceFunctionPathBuilder.setName("rsp1");
        transactionSuccessful = SfcProviderServiceForwarderAPI.deletePathFromServiceForwarderState(serviceFunctionPathBuilder.build());
        assertTrue("Must be true", transactionSuccessful);

        //remove path 2 via rendered service path name
        transactionSuccessful = SfcProviderServiceForwarderAPI.deletePathFromServiceForwarderState("rsp2");
        assertTrue("Must be true", transactionSuccessful);

        //remove paths 3 & 4 via list of rendered service path names
        List<String> rspNames = new ArrayList<>();
        rspNames.add("rsp3");
        rspNames.add("rsp4");
        transactionSuccessful = SfcProviderServiceForwarderAPI.deletePathFromServiceForwarderState(rspNames);
        assertTrue("Must be true", transactionSuccessful);

        //read service function forwarder state from data store, paths are removed, should return null
        sffServicePaths = SfcProviderServiceForwarderAPI.readSffState(sff);
        assertNull("Must be null", sffServicePaths);

        //remove written forwarder
        transactionSuccessful = SfcProviderServiceForwarderAPI.deleteServiceFunctionForwarder(sff);
        assertTrue("Must be true", transactionSuccessful);
    }


    @Test
    public void testUpdateServiceFunctionForwarderExecutor() {

        sffBuilder.setName("SFF1")
                .setKey(new ServiceFunctionForwarderKey("SFF1"));
        ServiceFunctionForwarder sff = sffBuilder.build();
        assertTrue(SfcProviderServiceForwarderAPI.updateServiceFunctionForwarder(sff));
    }

    /*
     * test creates service function forwarder and rendered service path, then add path to sff state
     * after this, path will be removed and checked if deletion was successful
     */
    @Test
    public void testDeleteRenderedPathsUsedByServiceForwarder() {
        String sffName = "sffName";
        String sffPath = "sffPath";

        //create service function forwarder and write into data store
        ServiceFunctionForwarderBuilder serviceFunctionForwarderBuilder = new ServiceFunctionForwarderBuilder();
        serviceFunctionForwarderBuilder.setName(sffName)
                .setKey(new ServiceFunctionForwarderKey(sffName));
        ServiceFunctionForwarder serviceFunctionForwarder = serviceFunctionForwarderBuilder.build();

        boolean transactionSuccessful = SfcProviderServiceForwarderAPI.putServiceFunctionForwarder(serviceFunctionForwarder);
        assertTrue("Must be true", transactionSuccessful);

        //create rendered service path and add it to service function forwarder state
        transactionSuccessful = SfcProviderServiceForwarderAPI.addPathToServiceForwarderState(createRenderedServicePath(sffPath, sffName, (short) 1));
        assertTrue("Must be true", transactionSuccessful);

        //check if rendered service path exists
        RenderedServicePath renderedServicePath = SfcProviderRenderedPathAPI.readRenderedServicePath(sffPath);
        assertNotNull("Must not be null", renderedServicePath);

        //delete rendered path used by service function forwarder
        transactionSuccessful = SfcProviderServiceForwarderAPI.deleteRenderedPathsUsedByServiceForwarder(serviceFunctionForwarder);
        assertTrue("Must be true", transactionSuccessful);

        //check if rendered service path has been deleted
        renderedServicePath = SfcProviderRenderedPathAPI.readRenderedServicePath(sffPath);
        assertNull("Must be null", renderedServicePath);
    }

    @Test
    public void testPutServiceFunctionForwarderExecutor() {
        ServiceFunctionForwarderBuilder sffBuilder = new ServiceFunctionForwarderBuilder();
        sffBuilder.setName("SFF1")
                .setKey(new ServiceFunctionForwarderKey("SFF1"));
        ServiceFunctionForwarder sff = sffBuilder.build();
        assertTrue(SfcProviderServiceForwarderAPI.putServiceFunctionForwarder(sff));
    }

    @Test
    public void testDeleteSffDataPlaneLocator() {
        SffDataPlaneLocatorBuilder sffDataPlaneLocatorBuilder = new SffDataPlaneLocatorBuilder();

        ServiceFunctionForwarderKey serviceFunctionForwarderKey = new ServiceFunctionForwarderKey("SFF1");
        SffDataPlaneLocatorKey sffDataPlaneLocatorKey = new SffDataPlaneLocatorKey("SFFLoc1");
        SffDataPlaneLocator sffDataPlaneLocator = sffDataPlaneLocatorBuilder.setName("SFDP1")
                .setKey(sffDataPlaneLocatorKey).build();

        InstanceIdentifier<SffDataPlaneLocator> sffDataPlaneLocatorIID = InstanceIdentifier
                .builder(ServiceFunctionForwarders.class)
                .child(ServiceFunctionForwarder.class, serviceFunctionForwarderKey)
                .child(SffDataPlaneLocator.class, sffDataPlaneLocatorKey).build();

        SfcDataStoreAPI.writePutTransactionAPI(sffDataPlaneLocatorIID, sffDataPlaneLocator, LogicalDatastoreType.CONFIGURATION);

        SfcProviderServiceForwarderAPI sfcProviderServiceForwarderAPI = new SfcProviderServiceForwarderAPI();
        assertTrue(sfcProviderServiceForwarderAPI.deleteSffDataPlaneLocator("SFF1", "SFFLoc1"));
    }

    @Test
    public void testDeleteSffDataPlaneLocatorExecutor() {
        SffDataPlaneLocatorBuilder sffDataPlaneLocatorBuilder = new SffDataPlaneLocatorBuilder();

        ServiceFunctionForwarderKey serviceFunctionForwarderKey = new ServiceFunctionForwarderKey("SFF1");
        SffDataPlaneLocatorKey sffDataPlaneLocatorKey = new SffDataPlaneLocatorKey("SFFLoc1");
        SffDataPlaneLocator sffDataPlaneLocator = sffDataPlaneLocatorBuilder.setName("SFDP1")
                .setKey(sffDataPlaneLocatorKey).build();

        InstanceIdentifier<SffDataPlaneLocator> sffDataPlaneLocatorIID = InstanceIdentifier
                .builder(ServiceFunctionForwarders.class)
                .child(ServiceFunctionForwarder.class, serviceFunctionForwarderKey)
                .child(SffDataPlaneLocator.class, sffDataPlaneLocatorKey).build();

        SfcDataStoreAPI.writePutTransactionAPI(sffDataPlaneLocatorIID, sffDataPlaneLocator, LogicalDatastoreType.CONFIGURATION);

        assertTrue(SfcProviderServiceForwarderAPI.deleteSffDataPlaneLocator("SFF1", "SFFLoc1"));
    }

    @Test
    public void testDeleteServiceFunctionForwarder() {
        SfcProviderServiceForwarderAPI sfcProviderServiceForwarderAPI = new SfcProviderServiceForwarderAPI();
        assertTrue(sfcProviderServiceForwarderAPI.deleteServiceFunctionForwarder("SFF1"));
    }

    @Test
    public void testDeleteServiceFunctionForwarderExecutor() {
        assertTrue(SfcProviderServiceForwarderAPI.deleteServiceFunctionForwarder("SFF1"));
    }

    @Test
    public void testPutAllServiceFunctionForwarders() {
        ServiceFunctionForwarderBuilder sffBuilder = new ServiceFunctionForwarderBuilder();
        sffBuilder.setName("SFF1")
                .setKey(new ServiceFunctionForwarderKey("SFF1"));
        ServiceFunctionForwarder sff = sffBuilder.build();

        List<ServiceFunctionForwarder> serviceFunctionForwarderList = new ArrayList<>();
        serviceFunctionForwarderList.add(sff);
        ServiceFunctionForwardersBuilder serviceFunctionForwardersBuilder = new ServiceFunctionForwardersBuilder();

        serviceFunctionForwardersBuilder.setServiceFunctionForwarder(serviceFunctionForwarderList);
        ServiceFunctionForwarders sffs = serviceFunctionForwardersBuilder.build();

        SfcProviderServiceForwarderAPI sfcProviderServiceForwarderAPI = new SfcProviderServiceForwarderAPI();
        assertTrue(sfcProviderServiceForwarderAPI.putAllServiceFunctionForwarders(sffs));
    }

    @Test
    public void testDeleteServiceFunctionFromForwarder() {
        SfDataPlaneLocatorBuilder sfDataPlaneLocatorBuilder = new SfDataPlaneLocatorBuilder();
        SfDataPlaneLocator sfDataPlaneLocator = sfDataPlaneLocatorBuilder.setName("SFDPL1")
                .setKey(new SfDataPlaneLocatorKey("SFDPL1")).setServiceFunctionForwarder("SFF1").build();
        List<SfDataPlaneLocator> sfDataPlaneLocatorList = new ArrayList<>();
        sfDataPlaneLocatorList.add(sfDataPlaneLocator);

        ServiceFunctionBuilder serviceFunctionBuilder = new ServiceFunctionBuilder();
        serviceFunctionBuilder.setName("SFD1")
                .setKey(new ServiceFunctionKey("SFD1")).setSfDataPlaneLocator(sfDataPlaneLocatorList);
        ServiceFunction serviceFunction = serviceFunctionBuilder.build();

        SffSfDataPlaneLocatorBuilder sffSfDataPlaneLocatorBuilder = new SffSfDataPlaneLocatorBuilder();
        SffSfDataPlaneLocator sffSfDataPlaneLocator = sffSfDataPlaneLocatorBuilder.build();

        ServiceFunctionDictionaryBuilder serviceFunctionDictionaryBuilder = new ServiceFunctionDictionaryBuilder();
        ServiceFunctionDictionary serviceFunctionDictionary = serviceFunctionDictionaryBuilder.setName("SFD1")
                .setKey(new ServiceFunctionDictionaryKey("SFD1")).setSffSfDataPlaneLocator(sffSfDataPlaneLocator).build();

        InstanceIdentifier<ServiceFunctionDictionary> sffIID;
        ServiceFunctionDictionaryKey serviceFunctionDictionaryKey =
                new ServiceFunctionDictionaryKey(serviceFunction.getName());
        ServiceFunctionForwarderKey serviceFunctionForwarderKey =
                new ServiceFunctionForwarderKey("SFF1");
        sffIID = InstanceIdentifier.builder(ServiceFunctionForwarders.class)
                .child(ServiceFunctionForwarder.class, serviceFunctionForwarderKey)
                .child(ServiceFunctionDictionary.class, serviceFunctionDictionaryKey)
                .build();

        SfcDataStoreAPI.writePutTransactionAPI(sffIID, serviceFunctionDictionary, LogicalDatastoreType.CONFIGURATION);

        SfcProviderServiceForwarderAPI sfcProviderServiceForwarderAPI = new SfcProviderServiceForwarderAPI();
        assertTrue(sfcProviderServiceForwarderAPI.deleteServiceFunctionFromForwarder(serviceFunction));
    }

    private RenderedServicePath createRenderedServicePath(String pathName, String sffName, short pathKey) {

        //create rendered service path and write to data store
        List<RenderedServicePathHop> renderedServicePathHops = new ArrayList<>();
        RenderedServicePathHopBuilder renderedServicePathHopBuilder = new RenderedServicePathHopBuilder();
        renderedServicePathHopBuilder.setKey(new RenderedServicePathHopKey(pathKey))
                .setServiceFunctionForwarder(sffName);
        renderedServicePathHops.add(renderedServicePathHopBuilder.build());


        long pathId = SfcServicePathId.check_and_allocate_pathid();

        assertNotEquals("Must be not equal", pathId, -1);

        RenderedServicePathBuilder renderedServicePathBuilder = new RenderedServicePathBuilder();
        renderedServicePathBuilder.setName(pathName)
                .setKey(new RenderedServicePathKey(pathName))
                .setRenderedServicePathHop(renderedServicePathHops)
                .setPathId(pathId);

        InstanceIdentifier<RenderedServicePath> rspIID =
                InstanceIdentifier.builder(RenderedServicePaths.class)
                        .child(RenderedServicePath.class, new RenderedServicePathKey(pathName))
                        .build();

        boolean transactionSuccessful = SfcDataStoreAPI.writePutTransactionAPI(rspIID, renderedServicePathBuilder.build(), LogicalDatastoreType.OPERATIONAL);
        assertTrue("Must be true", transactionSuccessful);


        return renderedServicePathBuilder.build();
    }

}
