// instantiated with jti.pl from WriteMap

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

public interface WriteMap_GeneratorFactory_Type_
{
    // associates index with item, previous association may already exist
    public void     set     (CallContext context, Type index, GeneratorFactory item);

    // associates index with item, previous association must not exist
    public void     add     (CallContext context, Type index, GeneratorFactory item) throws AlreadyExists;

    // associates index with item, previous association must exist
    public void     replace (CallContext context, Type index, GeneratorFactory item) throws DoesNotExist;

    // removes index entry, entry needs not exist
    public void     unset   (CallContext context, Type index);

    // removes index entry, entry must exist
    public void     remove  (CallContext context, Type index) throws DoesNotExist;
}

