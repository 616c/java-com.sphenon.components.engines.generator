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
import com.sphenon.basics.system.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.javacode.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tchandler.*;
import com.sphenon.engines.generator.returncodes.*;

import java.io.BufferedWriter;
import java.io.IOException;

import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class TOMRoot extends Class_TOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    static protected StringCache string_cache;

    public TOMRoot(CallContext context, Template template) {
        super(context, null);
        this.template = template;
        if (this.string_cache == null) {
            this.string_cache = StringCache.getSingleton(context);
        }
    }

    // Attributes ---------------------------------------------------------------------

    protected Template template;

    public Template getTemplate (CallContext context) {
        return this.template;
    }

    protected int gom_id;

    public int getNextGOMId (CallContext context) {
        return this.gom_id++;
    }

    public void resetNextGOMId (CallContext context) {
        this.gom_id = 0;
    }

    protected Stack<Integer> local_gom_indices;
    protected int            local_gom_index;

    public int getNextLocalGOMIndex (CallContext context) {
        return this.local_gom_index++;
    }

    public void pushLocalGOMIndex (CallContext context) {
        if (this.local_gom_indices == null) {
            this.local_gom_indices = new Stack<Integer>();
        }
        this.local_gom_indices.push(this.local_gom_index);
        this.local_gom_index = 0;
    }

    public void popLocalGOMIndex (CallContext context) {
        assert this.local_gom_indices != null;
        assert ! this.local_gom_indices.empty();
        this.local_gom_index = this.local_gom_indices.pop();
    }

    protected List<String>        texts;
    protected Map<String,Integer> text_indices;
    protected List<Integer>       string_cache_indices;

    public List<Integer> getStringCacheIndices(CallContext context) {
        return this.string_cache_indices;
    }

    public int getTextIndex(CallContext context, String text) {
        Integer text_index = null;
        if (this.texts == null) {
            this.texts = new ArrayList<String>();
            this.text_indices = new HashMap<String,Integer>();
            this.string_cache_indices = new ArrayList<Integer>();
        } else {
            text_index = this.text_indices.get(text);
        }
        if (text_index == null) {
            text_index = this.texts.size();
            int sci = this.string_cache.putText(context, text);
            this.texts.add(text);
            this.text_indices.put(text, text_index);
            this.string_cache_indices.add(sci);
        }
        return text_index;
    }

    protected Set<String> included_modules;

    public boolean checkAndAddIncludedModule(CallContext context, String module) {
        boolean contained = false;
        if (this.included_modules == null) {
            this.included_modules = new HashSet<String>();
        } else {
            contained = this.included_modules.contains(module);
        }
        if (! contained) {
            this.included_modules.add(module);
        }
        return contained;
    }

    protected Map<String,TagHandler> tag_handler_registry;
    protected TagHandler universal_tag_handler;

    public void registerTagHandler (CallContext context, String tag_name, String tch_class) throws InvalidTemplateSyntax {
        if (this.tag_handler_registry == null) {
            this.tag_handler_registry = new HashMap<String,TagHandler>();
        }

        ReflectionUtilities ru = new ReflectionUtilities(context);
        TagHandler th = (TagHandler) ru.tryNewInstance(context, ru.tryGetConstructor(context, tch_class, CallContext.class), context);
        if (th == null) {
            InvalidTemplateSyntax.createAndThrow(context, ru.getThrowable(context), "Tag handler '%(handler)' could not be instantiated", "handler", tch_class);
            throw (InvalidTemplateSyntax) null;
        }
        if (tag_name.equals("*")) {
            if (th.getHandledTags(context) != null) {
                for (String tn : th.getHandledTags(context)) {
                    this.tag_handler_registry.put(tn, th);
                }
            }
            if (th.getAcceptsAllTags(context)) {
                if (this.universal_tag_handler != null) {
                    InvalidTemplateSyntax.createAndThrow(context, "Cannot register universal tag handler '%(new)', there is already one '%(old)'", "new", this.universal_tag_handler.getClass().getName(), "old", th.getClass().getName());
                    throw (InvalidTemplateSyntax) null;
                }
                this.universal_tag_handler = th;
            }
        } else {
            this.tag_handler_registry.put(tag_name, th);
        }
    }

    public TagHandler getTagHandler (CallContext context, String tag_name) throws InvalidTemplateSyntax {
        TagHandler th = (this.tag_handler_registry == null ? null : this.tag_handler_registry.get(tag_name));
        if (th == null) {
            th = this.universal_tag_handler;
        }
        if (th == null) {
            InvalidTemplateSyntax.createAndThrow(context, "No tag handler defined fo tag '%(name)'", "name", tag_name);
            throw (InvalidTemplateSyntax) null;
        }
        return th;
    }

    // Internal -----------------------------------------------------------------------

    // Operations ---------------------------------------------------------------------

    // --------------------------------------------------------------------------------

    public void createJavaCode(CallContext context, Section section, JavaCodeManager java_code_manager, BufferedWriter current_writer, String indent) {
        // Generator generator_generator = GeneratorRegistry.getGenerator(context, "com.sphenon.engines.generator.classes.GeneratorGenerator");
        // generator_generator.generate(context, this, java_code_sink);

        BufferedWriter java_code_writer = java_code_manager.getDefaultResource(context).getWriter(context);
        this.gom_id = 0;
        this.local_gom_index = 0;

        for (TOMNode tom_node : this.getChildNodes(context)) {
            tom_node.createJavaCode(context, Section.NO_SECTION, java_code_manager, java_code_writer, indent);
        }
    }
}
