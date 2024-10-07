// instantiated with jti.pl from WriteMap
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

