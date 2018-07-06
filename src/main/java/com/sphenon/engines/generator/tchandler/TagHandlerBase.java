package com.sphenon.engines.generator.tchandler;

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

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tom.classes.*;
import com.sphenon.engines.generator.tchandler.*;
import com.sphenon.engines.generator.returncodes.*;

import java.util.Vector;
import java.util.HashMap;

import java.io.StringReader;
import java.io.BufferedReader;

public class TagHandlerBase implements TagHandler {

    public TagHandlerBase(CallContext context) {
        this.handled_tags = new Vector<String>();
        this.handler = new HashMap<String,InternalTagHandler>();
    }

    // -----------------------------------------------------------------------------------
    abstract static protected class InternalTagHandler {

        public InternalTagHandler (CallContext context, String name, String... argument_names) {
            this.name = name;
            this.argument_names = argument_names;
            this.minimum_arguments = 0;
            this.maximum_arguments = 0;
            int index = 0;
            if (this.argument_names != null) {
                for (String argument_name : argument_names) {
                    if (this.maximum_arguments != -1) {
                        this.maximum_arguments++;
                    }
                    if (argument_name.matches("[^:]+:required:.*")) {
                        this.minimum_arguments++;
                    }
                    if (argument_name.matches("\\.\\.\\.:.*") && index == argument_names.length - 1) {
                        this.maximum_arguments = -1;
                    }
                    index++;
                }
            }
        }

        protected String name;

        public String getName (CallContext context) {
            return this.name;
        }

        protected String[] argument_names;

        public String[] getArgumentNames (CallContext context) {
            return this.argument_names;
        }

        protected int minimum_arguments;
        
        public int getMinimumArguments (CallContext context) {
            return this.minimum_arguments;
        }

        protected int maximum_arguments;
        
        public int getMaximumArguments (CallContext context) {
            return this.maximum_arguments;
        }

        public TOMNode handleTagBegin(CallContext context, TCEvent event, TOMNode current_node, String tag_name, Vector<String> arguments, TCHandler ascii_source_handler, StringBuffer out) throws InvalidTemplateSyntax {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Tag '%(name)' cannot be used as a start tag", "name", tag_name);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }

        public TOMNode handleTagEnd(CallContext context, TCEvent event, TOMNode current_node, String tag_name, Vector<String> arguments, TCHandler ascii_source_handler, StringBuffer out) throws InvalidTemplateSyntax {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Tag '%(name)' cannot be used as an end tag", "name", tag_name);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }
    // -----------------------------------------------------------------------------------

    static protected InternalTagHandler                 universal_handler;
    static protected HashMap<String,InternalTagHandler> handler;
    static protected Vector<String>                     handled_tags;

    protected void registerHandler(CallContext context, InternalTagHandler internal_tag_handler) {
        this.handled_tags.add(internal_tag_handler.getName(context));
        this.handler.put(internal_tag_handler.getName(context), internal_tag_handler);
    }

    protected void checkArguments(CallContext context, String tag_name, InternalTagHandler internal_tag_handler, Vector<String> current_arguments) throws InvalidTemplateSyntax {
        String error = null;
        String[] argument_names = internal_tag_handler.getArgumentNames(context);
        int number_of_arguments = (current_arguments == null ? 0 : current_arguments.size());
        if (    number_of_arguments < internal_tag_handler.getMinimumArguments(context)
             || (    internal_tag_handler.getMaximumArguments(context) != -1
                  && number_of_arguments > internal_tag_handler.getMaximumArguments(context)
                )
           ) {
            String expected = "";
            String got = "";
            for (String argument_name : argument_names) {
                expected += (expected.length() == 0 ? "" : ",") + argument_name;
            }
            for (String current_argument : current_arguments) {
                got += (got.length() == 0 ? "" : ",") + "'" + current_argument + "'";
            }
            InvalidTemplateSyntax.createAndThrow(context, "Invalid number of arguments for tag '%(tag)', expected (%(expected)), got (%(got))", "tag", tag_name, "expected", expected, "got", got);
            throw (InvalidTemplateSyntax) null;
        }
        int index = 0;
        String ellipsis = null;
        for (String current_argument : current_arguments) {
            String argument_name = index < argument_names.length ? argument_names[index] : ellipsis;
            String[] anparts = argument_name.split(":",3);
            if (anparts[0].equals("...") && index == argument_names.length - 1) {
                ellipsis = argument_name;
            }
            if (anparts.length == 3 && anparts[2] != null && current_argument.matches(anparts[2]) == false) {
                InvalidTemplateSyntax.createAndThrow(context, "Argument '%(name)' = '%(value)' of tag '%(name)' must match '%(regexp)'", "name", anparts[0], "value", current_argument, "tag", tag_name, "regexp", anparts[2]);
                throw (InvalidTemplateSyntax) null;
            }
            index++;
        }
    }

    public TOMNode handleTagBegin(CallContext context, TCEvent event, TOMNode current_node, String tag_name, Vector<String> arguments, TCHandler ascii_source_handler, StringBuffer out) throws InvalidTemplateSyntax {
        InternalTagHandler internal_tag_handler = this.handler.get(tag_name);

        if (internal_tag_handler != null) {
            this.checkArguments(context, tag_name, internal_tag_handler, arguments);
            return internal_tag_handler.handleTagBegin(context, event, current_node, tag_name, arguments, ascii_source_handler, out);
        }
        if (universal_handler != null) {
            this.checkArguments(context, tag_name, universal_handler, arguments);
            return universal_handler.handleTagBegin(context, event, current_node, tag_name, arguments, ascii_source_handler, out);
        }

        CustomaryContext.create((Context)context).throwProtocolViolation(context, "Tag '%(name)' is not handled by '%(handler)'", "name", tag_name, "handler", this.getClass().getName());
        throw (ExceptionProtocolViolation) null; // compiler insists
    }

    public TOMNode handleTagEnd(CallContext context, TCEvent event, TOMNode current_node, String tag_name, Vector<String> arguments, TCHandler ascii_source_handler, StringBuffer out) throws InvalidTemplateSyntax {
        InternalTagHandler internal_tag_handler = this.handler.get(tag_name);

        if (internal_tag_handler != null) {
            // this.checkArguments : no, end tag does accept any
            return internal_tag_handler.handleTagEnd(context, event, current_node, tag_name, arguments, ascii_source_handler, out);
        }
        if (universal_handler != null) {
            // this.checkArguments : no, end tag does accept any
            return universal_handler.handleTagEnd(context, event, current_node, tag_name, arguments, ascii_source_handler, out);
        }

        CustomaryContext.create((Context)context).throwProtocolViolation(context, "Tag '%(name)' is not handled by '%(handler)'", "name", tag_name, "handler", this.getClass().getName());
        throw (ExceptionProtocolViolation) null; // compiler insists
    }

    public Vector<String> getHandledTags(CallContext context) {
        return handled_tags;
    }

    public boolean getAcceptsAllTags(CallContext context) {
        return universal_handler != null;
    }
}
