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

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tom.classes.*;
import com.sphenon.engines.generator.tchandler.*;
import com.sphenon.engines.generator.returncodes.*;

import java.util.Vector;

import java.io.StringReader;
import java.io.BufferedReader;

public class JavaTemplateTextTagHandler implements TagHandler {

    public JavaTemplateTextTagHandler (CallContext context) {
    }

    public TOMNode handleTagBegin(CallContext context, TCEvent event, TOMNode current_node, String tag_name, Vector<String>  arguments, TCHandler ascii_source_handler, StringBuffer out) throws InvalidTemplateSyntax {
        String dynargs = "";
        boolean first_dynarg = true;
        if (arguments != null && arguments.size() != 0) {
            for (String dynarg : arguments) {
                dynargs += (first_dynarg ? " + \"[\"" : "") + " + " + dynarg;
            }
            dynargs += " + \"]\"";
        }
        String code = "(_TRAITS.get(context, \""+tag_name+"\", getId(context)" + dynargs + ", arguments))";
        if (out != null) {
            out.append(code);
        }
        if (ascii_source_handler != null) {
            StringReader string_reader = new StringReader("@@"+code+"@@");
            BufferedReader buffered_reader = new BufferedReader(string_reader);
            ascii_source_handler.handle(context, TCEvent.TEMPLATE_SOURCE, current_node, buffered_reader);
        }
         return current_node;
    }

    public TOMNode handleTagEnd(CallContext context, TCEvent event, TOMNode current_node, String tag_name, Vector<String> arguments, TCHandler ascii_source_handler, StringBuffer out) throws InvalidTemplateSyntax {
        if (out != null) {
        }
        if (ascii_source_handler != null) {
        }
        return current_node;
    }

    public Vector<String> getHandledTags(CallContext context) {
        return null;
    }

    public boolean getAcceptsAllTags(CallContext context) {
        return true;
    }
}
