/*
 * Copyright (c) 2015 Ericsson Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.sfc.l2renderer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.opendaylight.sfc.provider.api.SfcProviderServiceForwarderAPI;
import org.opendaylight.sfc.provider.api.SfcProviderServiceFunctionAPI;
import org.opendaylight.sfc.provider.api.SfcProviderServiceFunctionGroupAPI;
import org.opendaylight.sfc.sfc_ovs.provider.SfcOvsUtil;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.common.rev151017.SfName;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.common.rev151017.SffName;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sf.rev140701.service.functions.ServiceFunction;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.ServiceFunctionForwarder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfg.rev150214.service.function.groups.ServiceFunctionGroup;

public class SfcL2ProviderUtils extends SfcL2BaseProviderUtils {

    // Since this class can be called by multiple threads,
    // store these objects per RSP id to avoid collisions
    private class RspContext {

        // store the SFs and SFFs internally so we dont have to
        // query the DataStore repeatedly for the same thing
        private Map<SfName, ServiceFunction> serviceFunctions;
        private Map<String, ServiceFunctionGroup> serviceFunctionGroups;
        private Map<SffName, ServiceFunctionForwarder> serviceFunctionFowarders;

        public RspContext() {
            serviceFunctions = Collections.synchronizedMap(new HashMap<SfName, ServiceFunction>());
            serviceFunctionGroups = Collections.synchronizedMap(new HashMap<String, ServiceFunctionGroup>());
            serviceFunctionFowarders = Collections.synchronizedMap(new HashMap<SffName, ServiceFunctionForwarder>());
        }
    }

    private Map<Long, RspContext> rspIdToContext;

    public SfcL2ProviderUtils() {
        rspIdToContext = new HashMap<Long, RspContext>();
    }

    @Override
    public void addRsp(long rspId) {
        rspIdToContext.put(rspId, new RspContext());
    }

    @Override
    public void removeRsp(long rspId) {
        rspIdToContext.remove(rspId);
    }

    /**
     * Return the named ServiceFunction
     * Acts as a local cache to not have to go to DataStore so often
     * First look in internal storage, if its not there
     * get it from the DataStore and store it internally
     *
     * @param sfName - The SF Name to search for
     * @return - The ServiceFunction object, or null if not found
     */
    @Override
    public ServiceFunction getServiceFunction(final SfName sfName, long rspId) {
        RspContext rspContext = rspIdToContext.get(rspId);

        ServiceFunction sf = rspContext.serviceFunctions.get(sfName);
        if (sf == null) {
            sf = SfcProviderServiceFunctionAPI.readServiceFunction(sfName);
            if (sf != null) {
                rspContext.serviceFunctions.put(sfName, sf);
            }
        }

        return sf;
    }

    /**
     * Return the named ServiceFunctionForwarder
     * Acts as a local cache to not have to go to DataStore so often
     * First look in internal storage, if its not there
     * get it from the DataStore and store it internally
     *
     * @param sffName - The SFF Name to search for
     * @return The ServiceFunctionForwarder object, or null if not found
     */
    @Override
    public ServiceFunctionForwarder getServiceFunctionForwarder(final SffName sffName, long rspId) {
        RspContext rspContext = rspIdToContext.get(rspId);

        ServiceFunctionForwarder sff = rspContext.serviceFunctionFowarders.get(sffName);
        if (sff == null) {
            sff = SfcProviderServiceForwarderAPI.readServiceFunctionForwarder(sffName);
            if (sff != null) {
                sff = SfcOvsUtil.augmentSffWithOpenFlowNodeId(sff);
                rspContext.serviceFunctionFowarders.put(sffName, sff);
            }
        }

        return sff;
    }

    @Override
    public ServiceFunctionGroup getServiceFunctionGroup(final String sfgName, long rspId) {
        RspContext rspContext = rspIdToContext.get(rspId);

        ServiceFunctionGroup sfg = rspContext.serviceFunctionGroups.get(sfgName);
        if (sfg == null) {
            sfg = SfcProviderServiceFunctionGroupAPI.readServiceFunctionGroup(sfgName);
            if (sfg != null) {
                rspContext.serviceFunctionGroups.put(sfgName, sfg);
            }
        }

        return sfg;
    }

}
