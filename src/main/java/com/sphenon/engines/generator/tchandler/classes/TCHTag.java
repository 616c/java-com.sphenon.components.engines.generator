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

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tom.classes.*;
import com.sphenon.engines.generator.tchandler.*;
import com.sphenon.engines.generator.returncodes.*;

import java.io.BufferedReader;

import java.util.Vector;

public class TCHTag implements TCHandler {

    public TCHTag (CallContext context) {
    }

    protected TCHandler ascii_source_handler;

    public TCHandler getASCIISourceHandler (CallContext context) {
        return this.ascii_source_handler;
    }

    public void setASCIISourceHandler (CallContext context, TCHandler ascii_source_handler) {
        this.ascii_source_handler = ascii_source_handler;
    }

    public TOMNode handle(CallContext context, TCEvent event, TOMNode current_node, BufferedReader reader) throws InvalidTemplateSyntax{

        Tag tag = new Tag (context, reader, this, event);
        try {
            current_node = tag.Tag(context, current_node);
        } catch (ParseException pe) {
            InvalidTemplateSyntax.createAndThrow(context, pe, "Invalid Template Syntax (Tag) at " + tag.getPosition(context));
            throw (InvalidTemplateSyntax) null;
        } catch (TokenMgrError tme) {
            InvalidTemplateSyntax.createAndThrow(context, tme, "Invalid Template Syntax (Tag) at " + tag.getPosition(context));
            throw (InvalidTemplateSyntax) null;
        } catch (InvalidTemplateSyntax its) {
            InvalidTemplateSyntax.createAndThrow(context, its, "Invalid Template Syntax (Tag) at " + tag.getPosition(context));
            throw (InvalidTemplateSyntax) null;
        }

        return current_node;
    }

    public TOMNode handleInvocation(CallContext context, TCEvent event, TOMNode current_node, String tag_name, Vector arguments, boolean is_start) throws InvalidTemplateSyntax {
        TagHandler th = current_node.getRootNode(context).getTagHandler(context, tag_name);
        if (is_start) {
            th.handleTagBegin(context, event, current_node, tag_name, (Vector<String>) arguments, this.ascii_source_handler, null);
        } else {
            th.handleTagEnd(context, event, current_node, tag_name, (Vector<String>) arguments, this.ascii_source_handler, null);
        }
        return current_node;
    }

    public TOMNode handleDeclaration(CallContext context, TCEvent event, TOMNode current_node, String tag_name, String tch_class) throws InvalidTemplateSyntax {
        current_node.getRootNode(context).registerTagHandler(context, tag_name, tch_class);
        return current_node;
    }
}
