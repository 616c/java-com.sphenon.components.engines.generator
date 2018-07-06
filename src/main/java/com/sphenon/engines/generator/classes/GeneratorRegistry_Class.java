package com.sphenon.engines.generator.classes;

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
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.validation.returncodes.*;

import com.sphenon.basics.configuration.annotations.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.returncodes.*;
import com.sphenon.engines.generator.tplinst.*;

import java.util.Vector;

public class GeneratorRegistry_Class {

    public GeneratorRegistry_Class(CallContext context, GeneratorInternal root_generator) {
        this.root_generator = root_generator;
    }

    protected GeneratorInternal root_generator;

    protected OMap_GeneratorFactory_Type_ oo_registry;

    public void registerGenerator(CallContext context, GeneratorFactory generator_factory) {
        if (oo_registry == null) {
            oo_registry = Factory_OMap_GeneratorFactory_Type_.construct(context);
        }
        Class generator_class = generator_factory.getClass(context);
        Vector<FormalArgument> generator_signature = null;
        String method_name = "getSignature_" + generator_class.getName().replaceFirst("^.*\\.", "");
        try {
            generator_signature = (Vector<FormalArgument>) generator_class.getMethod(method_name, CallContext.class).invoke(null, context);
        } catch (NoSuchMethodException nsme) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, nsme, "Generator '%(id)' has no '%(method)' method", "id", generator_class.getName(), "method", method_name);
            throw (ExceptionConfigurationError) null; // compiler insists
        } catch (IllegalAccessException iae) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, iae, "Generator '%(id)' has no accessible '%(method)' method", "id", generator_class.getName(), "method", method_name);
            throw (ExceptionConfigurationError) null; // compiler insists
        } catch (java.lang.reflect.InvocationTargetException ite) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, ite, "Generator '%(id)', generic method invocation '%(method)' failed", "id", generator_class.getName(), "method", method_name);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
        if (generator_signature == null || generator_signature.size() == 0) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "Generator '%(id)' to be registered as polymorphic handler must provide a signature with at least one argument", "id", generator_factory.getClass(context).getName());
            throw (ExceptionConfigurationError) null; // compiler insists
        }
        FormalArgument formal_argument = generator_signature.get(0);
        oo_registry.set(context, formal_argument.getType(context), generator_factory);
    }

    public GeneratorFactory tryGet(CallContext context, Object... arguments) {
        if (oo_registry == null) { return null; }
        GeneratorFactory generator_factory = oo_registry.tryGet(context, TypeManager.get(context, arguments[0].getClass()));
        return generator_factory;
    }
}
