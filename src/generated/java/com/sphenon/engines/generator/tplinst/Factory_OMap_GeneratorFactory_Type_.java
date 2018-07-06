// instantiated with jti.pl from Factory_OMap

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
// please do not modify this file directly
package com.sphenon.engines.generator.tplinst;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.returncodes.*;
import com.sphenon.engines.generator.exceptions.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.tplinst.*;
import com.sphenon.engines.generator.traits.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.tplinst.*;
import com.sphenon.basics.metadata.traits.*;

import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.factory.returncodes.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.many.traits.*;

public class Factory_OMap_GeneratorFactory_Type_
{
    private String[] names;
    private GeneratorFactory[] values;

    public Factory_OMap_GeneratorFactory_Type_ (CallContext context) {
    }

    static public OMap_GeneratorFactory_Type_ construct (CallContext context) {
        Factory_OMap_GeneratorFactory_Type_ factory = new Factory_OMap_GeneratorFactory_Type_(context);
        factory.set_ParametersAtOnce(context, new String[0], new GeneratorFactory[0]);        
        return factory.create(context);
    }

    public OMap_GeneratorFactory_Type_ create (CallContext context) {
        OMap_GeneratorFactory_Type_ omap = new OMapImpl_GeneratorFactory_Type_(context);
        for (int i=0; i<names.length; i++) {
            Type index = ConversionTraits_Type_.tryConvertFromString(context, names[i]);
            omap.set(context, index, values[i]);
            // naja, eigentlich "add" statt "set"
        }
        return omap;
    }

    public void set_ParametersAtOnce(CallContext call_context, String[] names, GeneratorFactory[] values) {
        if (names.length != values.length) {
            Context context = Context.create(call_context);
            CustomaryContext cc = CustomaryContext.create(context);
            cc.throwPreConditionViolation(context, ManyStringPool.get(context, "0.0.0" /* number of names differs from number of values */));
        }
        this.names = names;
        this.values = values;
    }
}
