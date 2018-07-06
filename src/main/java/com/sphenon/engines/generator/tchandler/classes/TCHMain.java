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

public class TCHMain implements TCHandler {

    public TCHMain (CallContext context) {
    }

    protected TCHandler source_handler;

    public TCHandler getSourceHandler (CallContext context) {
        return this.source_handler;
    }

    public void setSourceHandler (CallContext context, TCHandler source_handler) {
        this.source_handler = source_handler;
    }

    public TOMNode handle(CallContext context, TCEvent event, TOMNode current_node, BufferedReader reader) throws InvalidTemplateSyntax {
        switch (event) {
            case TEMPLATE_BEGIN:
                current_node = new TOMSkeleton(context, current_node);
                current_node = this.source_handler.handle(context, event, current_node, null);
                break;
            case TEMPLATE_SOURCE:
                current_node = this.source_handler.handle(context, event, current_node, reader);
                break;
            case TEMPLATE_END:
                current_node = this.source_handler.handle(context, event, current_node, null);
                current_node = current_node.getParentNode(context);
                break;
            default:
                current_node = null;
                break;
        }
        return current_node;
    }
}
