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
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.graph.*;
import com.sphenon.engines.factorysite.gates.*;
import com.sphenon.engines.factorysite.returncodes.*;

import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.returncodes.*;

import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;

import java.io.*;

public class GeneratorRegistry {

    static final public Class _class = GeneratorRegistry.class;

    static protected Configuration config;
    static { config = Configuration.create(RootContext.getInitialisationContext(), _class); };

    static volatile protected GeneratorRegistry singleton;

    static public GeneratorRegistry get(CallContext context) {

        // ToDo: registry aus GeneratorContext holen

        if (singleton == null) {
            synchronized(GeneratorRegistry.class) {
                if (singleton == null) {
                    singleton = new GeneratorRegistry (context);
                }
            }
        }
        return singleton;
    }

    protected GeneratorRegistry (CallContext context) {
        loaded_generator_factories = new Hashtable<String,GeneratorFactory>();
    }

    protected Hashtable<String,GeneratorFactory> loaded_generator_factories;

    // --------------------------------------------------------------------------------------------------------------

    public GeneratorFactory getGeneratorFactory (CallContext context, String full_class_name, String text_locator) throws NoSuchTemplate {
        Class_Template ct = null;
        if (full_class_name == null || full_class_name.indexOf('.') == -1) {
            ct = new Class_Template(context, full_class_name, text_locator);
            full_class_name = ct.getFullClassName(context);
        }

        GeneratorFactory generator_factory;
        synchronized (loaded_generator_factories) {
            generator_factory = loaded_generator_factories.get(full_class_name);
            if (generator_factory == null) {
                if (ct != null) {
                    generator_factory = new Class_GeneratorFactory(context, ct);
                } else {
                    generator_factory = new Class_GeneratorFactory(context, full_class_name, text_locator);
                }
                loaded_generator_factories.put(full_class_name, generator_factory);
            }
        }
        generator_factory.validate(context);
        return generator_factory;
    }

    public Template getTemplate(CallContext context, String full_class_name, String text_locator) throws NoSuchTemplate {
        return this.getGeneratorFactory(context, full_class_name, text_locator).getTemplate(context);
    }

    public void prepareCode(CallContext context, String full_class_name, String text_locator) throws NoSuchTemplate {
        this.getGeneratorFactory(context, full_class_name, text_locator).prepareCode(context);
    }

    public Class getGeneratorClass (CallContext context, String full_class_name, String text_locator) throws NoSuchTemplate {
        return this.getGeneratorFactory(context, full_class_name, text_locator).getClass(context);
    }

    public Generator getGenerator (CallContext context, String full_class_name, String text_locator) throws NoSuchTemplate {
        return this.getGeneratorFactory(context, full_class_name, text_locator).create(context);
    }

    // --------------------------------------------------------------------------------------------------------------

    public GeneratorFactory getGeneratorFactory (CallContext context, TreeLeaf leaf) {
        Class_Template ct = new Class_Template(context, null, leaf);
        String full_class_name = ct.getFullClassName(context);

        GeneratorFactory generator_factory;
        synchronized (loaded_generator_factories) {
            generator_factory = loaded_generator_factories.get(full_class_name);
            if (generator_factory == null) {
                generator_factory = new Class_GeneratorFactory(context, ct);
                loaded_generator_factories.put(full_class_name, generator_factory);
            }
        }
        return generator_factory;
    }

    public Template getTemplate(CallContext context, TreeLeaf leaf) {
        return this.getGeneratorFactory(context, leaf).getTemplate(context);
    }

    public void prepareCode(CallContext context, TreeLeaf leaf) {
        this.getGeneratorFactory(context, leaf).prepareCode(context);
    }

    public Class getGeneratorClass (CallContext context, TreeLeaf leaf) {
        return this.getGeneratorFactory(context, leaf).getClass(context);
    }

    public Generator getGenerator (CallContext context, TreeLeaf leaf) {
        return this.getGeneratorFactory(context, leaf).create(context);
    }

    // --------------------------------------------------------------------------------------------------------------

    public GeneratorFactory getGeneratorFactory (CallContext context, String full_class_name) throws NoSuchTemplate {
        return this.getGeneratorFactory (context, full_class_name, null);
    }

    public Template getTemplate(CallContext context, String full_class_name) throws NoSuchTemplate {
        return this.getTemplate(context, full_class_name, null);
    }

    public void prepareCode(CallContext context, String full_class_name) throws NoSuchTemplate {
        this.prepareCode(context, full_class_name, null);
    }

    public Class getGeneratorClass (CallContext context, String full_class_name) throws NoSuchTemplate {
        return this.getGeneratorClass(context, full_class_name, null);
    }

    public Generator getGenerator (CallContext context, String full_class_name) throws NoSuchTemplate {
        return this.getGenerator(context, full_class_name, null);
    }

    // --------------------------------------------------------------------------------------------------------------

    public GeneratorFactory getGeneratorFactory_InMemoryTemplate (CallContext context, String template_code) {

        Class_Template_InMemory ctim = new Class_Template_InMemory(context, template_code);
        String full_class_name = ctim.getFullClassName(context);

        GeneratorFactory generator_factory;
        synchronized (loaded_generator_factories) {
            generator_factory = loaded_generator_factories.get(full_class_name);
            if (generator_factory == null) {
                generator_factory = new Class_GeneratorFactory(context, ctim);
                loaded_generator_factories.put(full_class_name, generator_factory);
            }
        }
        return generator_factory;
    }

    public Class getGeneratorClass_InMemoryTemplate (CallContext context, String template_code) {
        return this.getGeneratorFactory_InMemoryTemplate(context, template_code).getClass(context);
    }

    public Generator getGenerator_InMemoryTemplate (CallContext context, String template_code) {
        return this.getGeneratorFactory_InMemoryTemplate(context, template_code).create(context);
    }

    // --------------------------------------------------------------------------------------------------------------

    static public Generator mustGetGenerator (CallContext context, String any_name) {
        return mustGetGenerator (context, any_name, null);
    }

    static public Generator mustGetGenerator (CallContext context, String any_name, String prefix_path) {
        try {
            boolean is_class_name = (any_name != null && any_name.indexOf('/') == -1);
            if (is_class_name && prefix_path != null && any_name.indexOf('.') == -1 && prefix_path.isEmpty() == false) {
                // welchen sinn macht das???
                any_name = prefix_path + "." + any_name;
            }
            Generator generator = GeneratorRegistry.get(context).getGenerator(context, is_class_name ? any_name : null, is_class_name ? null : any_name);
            return generator;
        } catch (NoSuchTemplate nst) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, nst, "Could not find generator template");
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    // --------------------------------------------------------------------------------------------------------------

    protected String cache_file;

    public void saveCacheOnExit(CallContext context) {
        cache_file = config.get(context, "CacheFile", (String) null);
        java.lang.Runtime.getRuntime().addShutdownHook(new Thread() { public void run() { saveCache(RootContext.getDestructionContext()); } });
    }

    protected Map<String,String> getGeneratorCache(CallContext context) {
        Map<String,String> cache = new HashMap<String,String>();
        String cache_content = config.get(context, "Cache", (String) null);
        if (cache_content != null) {
            for (String entry : cache_content.split(",")) {
                String[] ep = entry.split("\\+",-1);
                cache.put(Encoding.recode(context, ep[0], Encoding.URI, Encoding.UTF8),
                          Encoding.recode(context, ep[1], Encoding.URI, Encoding.UTF8));
            }
        }
        return cache;
    }

    public void saveCache(CallContext context) {
        try {
            if (cache_file != null) {
                File f = new File(cache_file);
                f.setWritable(true);
                FileOutputStream fos = new FileOutputStream(f);
                OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                BufferedWriter bw = new BufferedWriter(osw);
                PrintWriter pw = new PrintWriter(bw);

                pw.print("com.sphenon.engines.generator.GeneratorRegistry.Cache=");

                Map<String,String> cache = getGeneratorCache(context);

                if (loaded_generator_factories != null) {
                    synchronized (loaded_generator_factories) {
                        for (String full_class_name : loaded_generator_factories.keySet()) {
                            if (full_class_name.matches(".*SHA1_[A-Fa-f0-9]+") == false) {
                                String text_locator = loaded_generator_factories.get(full_class_name).getTextLocator(context);
                                cache.put(full_class_name, (text_locator == null ? "" : text_locator));
                            }
                        }
                    }
                }

                boolean first = true;

                for (String fcn : cache.keySet()) {
                    if (first) { first = false; } else { pw.print(","); }
                    pw.print(Encoding.recode(context, fcn, Encoding.UTF8, Encoding.URI));
                    pw.print("+");
                    pw.print(Encoding.recode(context, cache.get(fcn), Encoding.UTF8, Encoding.URI));
                }

                pw.println("");

                pw.close();
                bw.close();
                osw.close();
                fos.close();
            }
        } catch (FileNotFoundException fnfe) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, fnfe, "Cannot write to file '%(filename)'", "filename", cache_file);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        } catch (UnsupportedEncodingException uee) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, uee, "Cannot write to file '%(filename)'", "filename", cache_file);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ioe, "Cannot write to file '%(filename)'", "filename", cache_file);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    public void loadCachedTemplates(CallContext context) {
        Map<String,String> cache = getGeneratorCache(context);

        for (String fcn : cache.keySet()) {
            System.err.println("Loading: " + fcn);
            try {
                String tl = cache.get(fcn);
                getGeneratorClass(context, fcn, tl.isEmpty() ? null : tl);
            } catch (NoSuchTemplate nst) {
                CustomaryContext.create((Context)context).throwConfigurationError(context, nst, "Cached template could not be loaded");
                throw (ExceptionConfigurationError) null; // compiler insists
            }
        }
    }
}
