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
import com.sphenon.basics.data.*;
import com.sphenon.basics.javacode.*;
import com.sphenon.basics.javacode.classes.*;
import com.sphenon.basics.expression.*;

import com.sphenon.basics.configuration.annotations.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.returncodes.*;

public class DynamicGeneratorClass extends Class_DynamicClass<Generator> {

    // Configuration ------------------------------------------------------------------

    @Configuration public interface Config {
        @DefaultValue("true")
        boolean getDoGeneration(CallContext context);
        @DefaultValue("true")
        boolean getDoCompilation(CallContext context);
        @DefaultValue("true")
        boolean getTryToLoadAsResource(CallContext context);
        @DefaultValue("false")
        boolean getUseExistingResourceUnconditionally(CallContext context);
    }
    static public Config config = Configuration_DynamicGeneratorClass.get(RootContext.getInitialisationContext());

    // Construction -------------------------------------------------------------------

    public DynamicGeneratorClass(CallContext context, Template template, TemplateParser template_parser) {
        super(context, template.getFullClassName(context), Generator.class);
        this.template = template;
        this.template_parser = template_parser;
    }

    // Attributes -----------------------------------------------------------------

    protected Template template;

    public Template getTemplate (CallContext context) {
        return this.template;
    }

    protected TemplateParser template_parser;

    public TemplateParser getTemplateParser (CallContext context) {
        return this.template_parser;
    }

    // Overloads ------------------------------------------------------------------

    protected boolean doAutoGeneration(CallContext context) {
        return true;
    }

    protected boolean doGeneration(CallContext context) {
        return DynamicGeneratorClass.config.getDoGeneration(context);
    }

    protected boolean doCompilation(CallContext context) {
        return DynamicGeneratorClass.config.getDoCompilation(context);
    }

    protected boolean tryToLoadAsResource(CallContext context) {
        return DynamicGeneratorClass.config.getTryToLoadAsResource(context);
    }

    protected boolean useExistingResourceUnconditionally(CallContext context) {
        return DynamicGeneratorClass.config.getUseExistingResourceUnconditionally(context);
    }

    protected String getAdditionalMetaData(CallContext context) {
        return "Template:" + this.getTemplate(context).tryGetOrigin(context).getPartialTextLocator(context);
    }

    protected String getCodeGeneratorPackage(CallContext context) {
        return "com.sphenon.engines.generator";
    }

    protected long getLastModificationOfCodeGeneratorSource(CallContext context) {
        return this.getTemplate(context).getLastModification(context);
    }

    protected long getLastModificationOfCodeGeneratorConfiguration(CallContext context) {
        try {
            return this.getTemplateParser(context).getLastModification(context);
        } catch (InvalidTemplateSyntax its) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, its, "Could not parse associated template");
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    protected void generateCode(CallContext context) {
        try {
            this.getTemplateParser(context).parse(context, this.getTemplate(context), this.getJavaCodeManager(context), null);
            for (String imp : this.getJavaCodeManager(context).getImports(context)) {
                GeneratorRegistry.get(context).prepareCode(context, imp);
            }
            this.notifyCodeGenerationCompleted(context);
        } catch (InvalidTemplateSyntax its) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, its, "Could not parse template '%(template)'", "template", template.getFullClassName(context));
            throw (ExceptionConfigurationError) null; // compiler insists
        } catch (NoSuchTemplate nst) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, nst, "Imported template class was not found");
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }
}
