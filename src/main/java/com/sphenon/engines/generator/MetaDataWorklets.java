package com.sphenon.engines.generator;

/****************************************************************************
  Copyright 2001-2018 Sphenon GmbH

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*****************************************************************************/

import com.sphenon.basics.context.*;
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.worklet.*;

import com.sphenon.engines.generator.*;

import java.util.Vector;

/**
   A {@link Worklet} associated with a generator.
*/
public class MetaDataWorklets implements MetaData {

    public MetaDataWorklets(CallContext context, Vector<Worklet> worklets) {
        this.worklets = worklets;
    }

    protected Vector<Worklet> worklets;

    public Vector<Worklet> getWorklets (CallContext context) {
        return this.worklets;
    }

    public void setWorklets (CallContext context, Vector<Worklet> worklets) {
        this.worklets = worklets;
    }
}
