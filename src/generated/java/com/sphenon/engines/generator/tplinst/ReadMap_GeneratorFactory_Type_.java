// instantiated with jti.pl from ReadMap

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

import com.sphenon.basics.many.returncodes.*;

public interface ReadMap_GeneratorFactory_Type_
{
    // retrieves item at index; item must exist
    public GeneratorFactory get     (CallContext context, Type index) throws DoesNotExist;

    // retrieves item at index; returns null if item does not exist
    public GeneratorFactory tryGet  (CallContext context, Type index);

    // returns true if item at index exists, otherwise false
    public boolean  canGet  (CallContext context, Type index);
}

