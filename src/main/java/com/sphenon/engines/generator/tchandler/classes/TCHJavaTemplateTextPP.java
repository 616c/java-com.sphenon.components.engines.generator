package com.sphenon.engines.generator.tchandler.classes;

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
import com.sphenon.basics.system.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tom.classes.*;
import com.sphenon.engines.generator.tchandler.*;
import com.sphenon.engines.generator.returncodes.*;

import java.io.StringReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.Stack;
import java.util.Vector;

public class TCHJavaTemplateTextPP implements TCHandler {

    protected boolean first;

    public TCHJavaTemplateTextPP (CallContext context) {
        this.first = true;
    }

    protected TCHandler text_handler;

    public TCHandler getTextHandler (CallContext context) {
        return this.text_handler;
    }

    public void setTextHandler (CallContext context, TCHandler text_handler) {
        this.text_handler = text_handler;
    }

    public TOMNode handle(CallContext context, TCEvent event, TOMNode current_node, BufferedReader reader) throws InvalidTemplateSyntax{

        TeeBufferedReader tbr = new TeeBufferedReader(reader, null);
        JavaTemplateTextPP jttpp = new JavaTemplateTextPP (context, tbr, this);
        try {
            if (this.first) {
                this.first = false;
                current_node = jttpp.FirstText(context, event, current_node);
            } else {
                current_node = jttpp.NextText(context, event, current_node);
            }
        } catch (ParseException pe) {
            try { tbr.close(); } catch(IOException ioe) {}
            InvalidTemplateSyntax.createAndThrow(context, pe, "Invalid Java Template Text (JTTPP) at %(position) : %(data)", "position", jttpp.getPosition(context), "data", tbr.getString());
            throw (InvalidTemplateSyntax) null;
        } catch (TokenMgrError tme) {
            try { tbr.close(); } catch(IOException ioe) {}
            InvalidTemplateSyntax.createAndThrow(context, tme, "Invalid Java Template Text (JTTPP) at %(position) : %(data)", "position", jttpp.getPosition(context), "data", tbr.getString());
            throw (InvalidTemplateSyntax) null;
        } catch (InvalidTemplateSyntax its) {
            try { tbr.close(); } catch(IOException ioe) {}
            InvalidTemplateSyntax.createAndThrow(context, its, "Invalid Java Template Text (JTTPP) at %(position) : %(data)", "position", jttpp.getPosition(context), "data", tbr.getString());
            throw (InvalidTemplateSyntax) null;
        }

        return current_node;
    }

    public TOMNode handlePlainText(CallContext context, TCEvent event, TOMNode current_node, String text) throws InvalidTemplateSyntax {
        if (text != null && text.length() > 0) {
            StringReader string_reader = new StringReader(text);
            BufferedReader buffered_reader = new BufferedReader(string_reader);
            current_node = this.text_handler.handle(context, event, current_node, buffered_reader);
        }
        return current_node;
    }

    public TOMNode handleCode(CallContext context, TCEvent event, TOMNode current_node, String code) throws InvalidTemplateSyntax {
        if (code != null && code.length() > 0) {
            new TOMJavaExpression(context, current_node, code);
        }
        return current_node;
    }

    public TOMNode handleIdentifier(CallContext context, TCEvent event, TOMNode current_node, String identifier) throws InvalidTemplateSyntax {
        if (identifier != null && identifier.length() > 0) {
            new TOMJTTIdentifier(context, current_node, identifier);
        }
        return current_node;
    }

    public TOMNode handleJavaTemplateExpression(CallContext context, TCEvent event, TOMNode current_node, JTENodeArgumentList list) throws InvalidTemplateSyntax {
        if (list != null && list.getArguments(context) != null && list.getArguments(context).size() > 0) {
            new TOMJTTTemplateExpression(context, current_node, list);
        }
        return current_node;
    }

    public TOMNode handleJavaTemplateDeclaration(CallContext context, TCEvent event, TOMNode current_node, JTENodeArgumentList list) throws InvalidTemplateSyntax {
        Vector<TCodeArgument> signature = new Vector<TCodeArgument>();
        signature.add(new TCodeArgument(context, "_JAVA_PACKAGE", "String", null));
        signature.add(new TCodeArgument(context, "_JAVA_IMPORTS", "java.util.Vector<String>", null));
        signature.add(new TCodeArgument(context, "_TRAITS", "com.sphenon.engines.generator.JavaTemplateTraits", null));
        for (JTENodeArgument arg : list.getArguments(context)) {
            signature.add(new TCodeArgument(context, arg.getIdentifier(context), "String", arg.getDefaultValue(context)));
        }

        new TOMTemplateSignature_JTT(context, current_node, signature);
        return current_node;
    }

    public TOMNode handleJavaTemplateRequirement(CallContext context, TCEvent event, TOMNode current_node, String template_name, JTENodeArgumentList list) throws InvalidTemplateSyntax {

        new TOMJTTTemplateRequirement(context, current_node, template_name, list);
        return current_node;
    }
}
