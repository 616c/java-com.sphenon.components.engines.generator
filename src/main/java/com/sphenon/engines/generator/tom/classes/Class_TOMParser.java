package com.sphenon.engines.generator.tom.classes;

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
import com.sphenon.basics.many.tplinst.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tchandler.*;
import com.sphenon.engines.generator.returncodes.*;

public class Class_TOMParser implements TOMParser {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public Class_TOMParser (CallContext context) {        
    }

    // Attributes ---------------------------------------------------------------------

    protected TCHandler begin_handler;

    public TCHandler getBeginHandler (CallContext context) {
        return this.begin_handler;
    }

    public void setBeginHandler (CallContext context, TCHandler begin_handler) {
        this.begin_handler = begin_handler;
    }

    protected TCHandler source_handler;

    public TCHandler getSourceHandler (CallContext context) {
        return this.source_handler;
    }

    public void setSourceHandler (CallContext context, TCHandler source_handler) {
        this.source_handler = source_handler;
    }

    protected TCHandler end_handler;

    public TCHandler getEndHandler (CallContext context) {
        return this.end_handler;
    }

    public void setEndHandler (CallContext context, TCHandler end_handler) {
        this.end_handler = end_handler;
    }

    protected Vector_String_long_ include_modules;

    public Vector_String_long_ getIncludeModules (CallContext context) {
        return this.include_modules;
    }

    public Vector_String_long_ defaultIncludeModules (CallContext context) {
        return null;
    }

    public void setIncludeModules (CallContext context, Vector_String_long_ include_modules) {
        this.include_modules = include_modules;
    }

    // Internal -----------------------------------------------------------------------

    // Operations ---------------------------------------------------------------------

    public TOMNode parse(CallContext context, Template template, TOMNode current_node) throws InvalidTemplateSyntax {

        boolean top_level = false;
        if (current_node == null) {
            top_level = true;
            current_node = new TOMRoot(context, template);
            current_node = this.begin_handler.handle(context, TCEvent.TEMPLATE_BEGIN, current_node, null);
        }

        boolean have_tails = false;
        if (include_modules != null) {
            for (String include_module : new VectorIterable_String_long_(context, include_modules)) {
                if (include_module.matches(".*Tail$") == false) {
                    if (current_node.getRootNode(context).checkAndAddIncludedModule(context, include_module) == false) {
                        Template template_module = null;
                        try {
                            template_module = new Class_Template(context, include_module, true);
                        } catch (NoSuchTemplate nst) {
                            CustomaryContext.create((Context)context).throwConfigurationError(context, nst, "Template module to include does not exist");
                            throw (ExceptionConfigurationError) null; // compiler insists
                        }
                        TemplateParser template_module_parser = new BootstrapParser(context, template_module);
                        current_node = template_module_parser.parse(context, template_module, null, current_node);
                    }
                } else {
                    have_tails = true;
                }
            }
        }

        current_node = this.source_handler.handle(context, TCEvent.TEMPLATE_SOURCE, current_node, template.getReader(context));

        if (include_modules != null && have_tails) {
            for (String include_module : new VectorIterable_String_long_(context, include_modules)) {
                if (current_node.getRootNode(context).checkAndAddIncludedModule(context, include_module) == false) {
                    if (include_module.matches(".*Tail$") == true) {
                        Template template_module = null;
                        try {
                            template_module = new Class_Template(context, include_module, true);
                        } catch (NoSuchTemplate nst) {
                            CustomaryContext.create((Context)context).throwConfigurationError(context, nst, "Template module to include does not exist");
                            throw (ExceptionConfigurationError) null; // compiler insists
                        }
                        TemplateParser template_module_parser = new BootstrapParser(context, template_module);
                        current_node = template_module_parser.parse(context, template_module, null, current_node);
                    }
                }
            }
        }

        if (top_level) {
            current_node = this.end_handler.handle(context, TCEvent.TEMPLATE_END, current_node, null);
        }

        return current_node;
    }
}
