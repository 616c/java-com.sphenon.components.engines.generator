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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.javacode.*;
import com.sphenon.basics.expression.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.returncodes.*;

import java.lang.Package;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

public class Class_GeneratorFactory implements GeneratorFactory {
    static final public Class _class = Class_GeneratorFactory.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    static protected long runtimestep_level;
    static public    long adjustRuntimeStepLevel(long new_level) { long old_level = runtimestep_level; runtimestep_level = new_level; return old_level; }
    static public    long getRuntimeStepLevel() { return runtimestep_level; }
    static { runtimestep_level = RuntimeStepLocationContext.getLevel(_class); };

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public Class_GeneratorFactory(CallContext context) {
        this.full_class_name = null;
        this.text_locator    = null;
        this.template        = null;
    }
           
    public Class_GeneratorFactory(CallContext context, Template template) {
        this.full_class_name = null;
        this.text_locator    = null;
        this.template = template;
    }

    // note: creation of Class_GeneratorFactory instances has to be cheap,
    // since this is done within a synchronized hot spot in GeneratorRegistry
    protected String full_class_name;
    protected String text_locator;

    public Class_GeneratorFactory(CallContext context, String full_class_name) {
        this.full_class_name = full_class_name;
        this.text_locator    = null;
        this.template        = null;
    }

    public Class_GeneratorFactory(CallContext context, String full_class_name, String text_locator) {
        this.full_class_name = full_class_name;
        this.text_locator    = text_locator;
        this.template        = null;
    }

    public String getFullClassName (CallContext context) {
        return this.full_class_name;
    }

    public String getTextLocator (CallContext context) {
        return this.text_locator;
    }

    // Attributes ---------------------------------------------------------------------

    protected Template template;

    public void validate(CallContext context) throws NoSuchTemplate {
        this.prepareTemplate(context);
    }

    protected void prepareTemplate(CallContext context) throws NoSuchTemplate {
        if (    this.template == null
             && (this.text_locator != null || this.full_class_name != null)
           ) {
            synchronized(this) {
                RuntimeStep runtime_step = null;
                if ((runtimestep_level & RuntimeStepLevel.OBSERVATION_CHECKPOINT) != 0) { context = Context.create(context); runtime_step = RuntimeStep.create((Context) context, RuntimeStepLevel.OBSERVATION_CHECKPOINT, _class, "Generator Factory, prepare template '%(id)'", "id", this.text_locator != null + "/" + this.full_class_name); }

                try {
                    if (this.text_locator != null) {
                        this.template = new Class_Template(context, this.full_class_name, this.text_locator);
                    } else if (this.full_class_name != null) {
                        this.template = new Class_Template(context, this.full_class_name);
                    }
                    this.full_class_name = null;
                    this.text_locator    = null;
                } catch (Error e) {
                    if (runtime_step != null) { runtime_step.setFailed(context, "Preparetion failed"); runtime_step = null; }
                    throw e;
                } catch (RuntimeException re) {
                    if (runtime_step != null) { runtime_step.setFailed(context, "Preparetion failed"); runtime_step = null; }
                    throw re;
                }
                if (runtime_step != null) { runtime_step.setCompleted(context, "Preparetion succeeded"); runtime_step = null; }
            }
        }
    }

    public Template getTemplate (CallContext context) {
        try {
            this.prepareTemplate(context);
        } catch (NoSuchTemplate nst) {
            CustomaryContext.create((Context)context).throwProtocolViolation(context, nst, "Template does not exist (this is a safety check, existence should be validated before)");
            throw (ExceptionProtocolViolation) null; // compiler insists
        }
        return this.template;
    }

    public void setTemplate (CallContext context, Template template) {
        this.full_class_name = null;
        this.text_locator    = null;
        this.template        = template;
    }

    protected volatile TemplateParser template_parser;

    public TemplateParser getTemplateParser (CallContext context) {
        return this.template_parser;
    }

    public void setTemplateParser (CallContext context, TemplateParser template_parser) {
        this.template_parser = template_parser;
    }

    public TemplateParser defaultTemplateParser (CallContext context) {
        return null;
    }

    // Internal -----------------------------------------------------------------------

    protected volatile DynamicGeneratorClass dynamic_generator_class;

    protected DynamicGeneratorClass getDynamicGeneratorClass (CallContext context) {
        if (this.template_parser == null) {
            this.template_parser = new BootstrapParser(context, this.getTemplate(context));
        }
        
        if (this.dynamic_generator_class == null) {
            this.dynamic_generator_class = new DynamicGeneratorClass(context, this.getTemplate(context), template_parser);
        }

        return this.dynamic_generator_class;
    }
    // Operations ---------------------------------------------------------------------

    public synchronized Generator create (CallContext context) {
        RuntimeStep runtime_step = null;
        if ((runtimestep_level & RuntimeStepLevel.OBSERVATION_CHECKPOINT) != 0) { context = Context.create(context); runtime_step = RuntimeStep.create((Context) context, RuntimeStepLevel.OBSERVATION_CHECKPOINT, _class, "Generator Factory, create '%(id)'", "id", this.text_locator != null + "/" + this.full_class_name); }

        Generator generator;
        try {
            generator = this.getDynamicGeneratorClass(context).createInstance(context);
        } catch (Error e) {
            if (runtime_step != null) { runtime_step.setFailed(context, "Creation failed"); runtime_step = null; }
            throw e;
        } catch (RuntimeException re) {
            if (runtime_step != null) { runtime_step.setFailed(context, "Creation failed"); runtime_step = null; }
            throw re;
        }
        if (runtime_step != null) { runtime_step.setCompleted(context, "Creation succeeded"); runtime_step = null; }

        return generator;
    }

    public synchronized Class getClass (CallContext context) {
        RuntimeStep runtime_step = null;
        if ((runtimestep_level & RuntimeStepLevel.OBSERVATION_CHECKPOINT) != 0) { context = Context.create(context); runtime_step = RuntimeStep.create((Context) context, RuntimeStepLevel.OBSERVATION_CHECKPOINT, _class, "Generator Factory, load '%(id)'", "id", this.text_locator != null + "/" + this.full_class_name); }
        
        Class generator_class;
        try {
            generator_class = this.getDynamicGeneratorClass(context).loadClass(context);
        } catch (Error e) {
            if (runtime_step != null) { runtime_step.setFailed(context, "Loading failed"); runtime_step = null; }
            throw e;
        } catch (RuntimeException re) {
            if (runtime_step != null) { runtime_step.setFailed(context, "Loading failed"); runtime_step = null; }
            throw re;
        }
        if (runtime_step != null) { runtime_step.setCompleted(context, "Loading succeeded"); runtime_step = null; }

        return generator_class;
    }

    public synchronized void prepareCode(CallContext context) {
        RuntimeStep runtime_step = null;
        if ((runtimestep_level & RuntimeStepLevel.OBSERVATION_CHECKPOINT) != 0) { context = Context.create(context); runtime_step = RuntimeStep.create((Context) context, RuntimeStepLevel.OBSERVATION_CHECKPOINT, _class, "Generator Factory, prepare code '%(id)'", "id", this.text_locator != null + "/" + this.full_class_name); }
        
        try {
            this.getDynamicGeneratorClass(context).createCode(context);
            // this.getDynamicGeneratorClass(context).getJavaCodeManager(context).closeResources(context);
        } catch (Error e) {
            if (runtime_step != null) { runtime_step.setFailed(context, "Preparetion failed"); runtime_step = null; }
            throw e;
        } catch (RuntimeException re) {
            if (runtime_step != null) { runtime_step.setFailed(context, "Preparetion failed"); runtime_step = null; }
            throw re;
        }
        if (runtime_step != null) { runtime_step.setCompleted(context, "Preparetion succeeded"); runtime_step = null; }
    }
}
