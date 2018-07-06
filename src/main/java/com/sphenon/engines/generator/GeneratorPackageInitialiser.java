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
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.data.conversion.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.services.*;

import com.sphenon.engines.generator.services.*;

public class GeneratorPackageInitialiser {

    static protected boolean initialised = false;

    static {
        initialise(RootContext.getRootContext());
    }

    static public void initialise (CallContext context) {
        
        if (initialised == false) {
            initialised = true;

            ExpressionPackageInitialiser.initialise(context);
            com.sphenon.engines.factorysite.FactorySitePackageInitialiser.initialise(context);
            com.sphenon.basics.javacode.JavaCodePackageInitialiser.initialise(context);

            Configuration.loadDefaultProperties(context, com.sphenon.engines.generator.GeneratorPackageInitialiser.class);

            ExpressionEvaluatorRegistry.registerDynamicStringEvaluator(context, new DynamicStringProcessor_Generator(context));

            loadDataConverter (context, getConfiguration(context));

            ServiceRegistry.registerService(context, new RESTResponseEncoder_Generator(context));

            if (getConfiguration(context).get(context, "SaveCacheOnExit", false)) {
                GeneratorRegistry.get(context).saveCacheOnExit(context);
            }

            use_string_cache = getConfiguration(context).get(context, "UseStringCache", false);
        }
    }

    static protected Configuration config;
    static public Configuration getConfiguration (CallContext context) {
        if (config == null) {
            config = Configuration.create(RootContext.getInitialisationContext(), "com.sphenon.engines.generator");
        }
        return config;
    }

    static public boolean use_string_cache;

    static protected void loadDataConverter (CallContext context, Configuration configuration) {
        String id;
        String property;
        int entry_number = 0;
        while ((id = configuration.get(context, (property = "conversion.DataConverter." + ++entry_number) + ".Id", (String) null)) != null) {
            DataConverter dc = processEntry(context, configuration, property, id);
            if (dc != null) {
                DataConversionManager.getSingleton(context).register(context, dc);
            }
        }
    }

    static public DataConverter processEntry(CallContext context, Configuration configuration, String property_prefix, String id) {
        CustomaryContext cc = CustomaryContext.create((Context)context);

        String property;

        property = property_prefix + ".SourceType";
        String source_type = configuration.get(context, property, (String) null);
        if (source_type == null) {
            cc.throwConfigurationError(context, "No property '%(property)' found", "property", property);
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        property = property_prefix + ".TargetType";
        String target_type = configuration.get(context, property, (String) null);
        if (target_type == null) {
            cc.throwConfigurationError(context, "No property '%(property)' found", "property", property);
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        property = property_prefix + ".Filename.RegExp";
        String filename_substitution_regexp = configuration.get(context, property, (String) null);
        if (filename_substitution_regexp == null) {
            cc.throwConfigurationError(context, "No property '%(property)' found", "property", property);
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        property = property_prefix + ".Filename.Substitution";
        String filename_substitution_subst = configuration.get(context, property, (String) null);
        if (filename_substitution_subst == null) {
            cc.throwConfigurationError(context, "No property '%(property)' found", "property", property);
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        return new DataConverter_MediaObject_Generator (context,
                                                        id,
                                                        (TypeImpl_MediaObject) TypeManager.getMediaType(context, source_type),
                                                        (TypeImpl_MediaObject) TypeManager.getMediaType(context, target_type), 
                                                        filename_substitution_regexp, 
                                                        filename_substitution_subst);
    }
}
